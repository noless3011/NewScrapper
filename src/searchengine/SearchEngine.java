package searchengine;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.SortedNumericDocValuesField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import crawler.Blockchain101Crawler;
import crawler.CNBCCrawler;
import crawler.CoindeskCrawler;
import crawler.FacebookCrawler;
import crawler.TwitterCrawler;
import model.Content;
import model.Facebook;
import model.Image;
import model.Tweet;
import model.Article;

public class SearchEngine {
	private static final String ARTICLE_INDEX_DIR = "articles_index";
	private static final String TWEET_INDEX_DIR = "tweet_index";
	private static final String FACEBOOK_INDEX_DIR = "facebook_index";
	private static StandardAnalyzer analyzer = new StandardAnalyzer();
	private static Directory articleIndexDirectory;
	private static Directory tweetIndexDirectory;
	private static Directory facebookIndexDirectory;
	private static TwitterCrawler twittercrawler = new TwitterCrawler();
	private static FacebookCrawler facebookcrawler = new FacebookCrawler();
	private static CNBCCrawler cnbccrawler = new CNBCCrawler();
	private static Blockchain101Crawler blockchain101crawler = new Blockchain101Crawler();
	private static CoindeskCrawler coindeskcrawler = new CoindeskCrawler();
	
	
	public SearchEngine() {
		
	}
	
	public static enum SortOptionArticle {
        TIME, RELEVANT
    }
	public static enum SortOptionTweet {
	    TIME, RELEVANT, VIEW, LIKE, COMMENT
	    
	}

	public static enum SortOptionFacebook {
	    TIME, RELEVANT, REACTION, SHARE, COMMENT
	}
	public static void indexTweet() throws IOException {
		// Khởi tạo index để chuẩn bị
			tweetIndexDirectory = FSDirectory.open(Path.of(TWEET_INDEX_DIR));
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
		    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
			IndexWriter writer = new IndexWriter(tweetIndexDirectory, config);
			
			// Thêm các trường vào index
			List <Tweet> tweets = twittercrawler.getListFromJson();
			for (Tweet tweet : tweets) {
				Document doc = new Document();
				doc.add(new TextField("author", tweet.getAuthor(), Field.Store.YES));
				doc.add(new TextField("content", tweet.getContent().toString(), Field.Store.YES));
				doc.add(new TextField("view", tweet.getNumber_of_view(), Field.Store.YES));
				doc.add(new TextField("like", tweet.getNumber_of_liked(), Field.Store.YES));
				doc.add(new TextField("comment", tweet.getNumber_of_comment(),Field.Store.YES));
				long date = DateRange.formatterTimeToEpochSecond(tweet.getPublishedAt());
				doc.add(new TextField("date", Long.toString(date), Field.Store.YES));
				doc.add(new TextField("url", tweet.getSourceUrl(), Field.Store.YES));
				List <String> hashtags = tweet.getHashtags();
				for (String hashtag : hashtags ) {
					doc.add(new TextField("hashtag", hashtag, Field.Store.YES));
				}
				writer.addDocument(doc);
				}
			writer.close();
	}
	
	public static void indexArticle() throws IOException {
		// Khởi tạo index để chuẩn bị
			articleIndexDirectory = FSDirectory.open(Path.of(ARTICLE_INDEX_DIR));
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
		    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
			IndexWriter writer = new IndexWriter(articleIndexDirectory, config);
			
			// Thêm các trường vào index
			List <Article> articles = cnbccrawler.getListFromJson();
			articles.addAll(blockchain101crawler.getListFromJson());
//			articles.addAll(coindeskcrawler.getListFromJson());
			for (Article article : articles) {
				Document doc = new Document();
				doc.add(new TextField("author", article.getAuthor(), Field.Store.YES));
				doc.add(new TextField("title", article.getTitle(), Field.Store.YES));
				doc.add(new TextField("content", article.getContent().toString(), Field.Store.YES));
				long date = DateRange.formatterTimeToEpochSecond(article.getPublishedAt());
				doc.add(new TextField("date", Long.toString(date), Field.Store.YES));
				doc.add(new TextField("url", article.getSourceUrl(), Field.Store.YES));
				writer.addDocument(doc);
			}
			writer.close();
	}
	
	public static void indexFacebook() throws IOException {
		// Khởi tạo index để chuẩn bị
			facebookIndexDirectory = FSDirectory.open(Path.of(ARTICLE_INDEX_DIR));
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
		    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
			IndexWriter writer = new IndexWriter(facebookIndexDirectory, config);

			// Thêm các trường vào index
			List <Facebook> facebooks = facebookcrawler.getListFromJson();
			for (Facebook facebook : facebooks) {
				Document doc = new Document();
				doc.add(new TextField("author", facebook.getAuthor(), Field.Store.YES));
				doc.add(new TextField("content", facebook.getContent().toString(), Field.Store.YES));
				long date = DateRange.formatterTimeToEpochSecond(facebook.getPublishedAt());
				doc.add(new TextField("date", Long.toString(date), Field.Store.YES));
				doc.add(new TextField("url", facebook.getSourceUrl(), Field.Store.YES));
				doc.add(new TextField("comment", facebook.getNumber_of_comment(), Field.Store.YES));
				doc.add(new TextField("reaction", facebook.getNumber_of_reaction(), Field.Store.YES));
				doc.add(new TextField("share", facebook.getNumber_of_share(), Field.Store.YES));
				doc.add(new TextField("urlimg", facebook.getImgUrl(), Field.Store.YES));
				writer.addDocument(doc);
			}
			writer.close();
	}
	
	
	public static List<Tweet> searchTweet(String inputTitle, String inputAuthor, DateRange range, String inputContent,List <String> inputHashtag, SortOptionTweet option, boolean direction) throws ParseException, IOException {
		indexTweet();
		try {
			// Khởi tạo bộ truy vấn theo nhiều trường dữ liệu nhập vào
			IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(tweetIndexDirectory));
			BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
			
			//Đổi tất cả chữ hoa thành chữ thường
			inputTitle = inputTitle.toLowerCase();
		    inputAuthor = inputAuthor.toLowerCase();
		    inputContent = inputContent.toLowerCase();
		    List<String> normalizedHashtags = new ArrayList<>();
		    if (inputHashtag != null) {
		    	for (String hashtag : inputHashtag) {
		    		normalizedHashtags.add(hashtag.toLowerCase());
		    	}
		    }   
		    // Khởi tạo các truy vấn được thêm vào tìm kiếm
		    if (!inputTitle.equals("")) {
		    	PhraseQuery.Builder titlePhraseQueryBuilder = new PhraseQuery.Builder();
				String[] titleTerms = inputTitle.split("\\s+");
				for (String term : titleTerms) {
					titlePhraseQueryBuilder.add(new Term("title", term));
				}
				queryBuilder.add(titlePhraseQueryBuilder.build(), BooleanClause.Occur.MUST);
			}
			if (!inputAuthor.equals("")) {
				PhraseQuery.Builder authorPhraseQueryBuilder = new PhraseQuery.Builder();
				String[] authorTerms = inputAuthor.split("\\s+");
				for (String term : authorTerms) {
					authorPhraseQueryBuilder.add(new Term("author", term));
				}
				queryBuilder.add(authorPhraseQueryBuilder.build(), BooleanClause.Occur.MUST);
			}
			if (!inputContent.equals("")) {
				PhraseQuery.Builder contentPhraseQueryBuilder = new PhraseQuery.Builder();
				String[] contentTerms = inputContent.split("\\s+");
				for (String term : contentTerms) {
						contentPhraseQueryBuilder.add(new Term("content", term));
				}
				queryBuilder.add(contentPhraseQueryBuilder.build(), BooleanClause.Occur.MUST);		
			}
			if (normalizedHashtags != null) {
				for (String hashtag : normalizedHashtags) {
					queryBuilder.add(new TermQuery(new Term("hashtag",hashtag)), BooleanClause.Occur.MUST);
				}
			}
			if (range != null) {
				long startDate = DateRange.formatterTimeToEpochSecond(range.getStartDate());
				long endDate = DateRange.formatterTimeToEpochSecond(range.getEndDate());
				Query dateQuery = new TermRangeQuery("date", new BytesRef(Long.toString(startDate)), new BytesRef(Long.toString(endDate)), true, true);
                queryBuilder.add(dateQuery, BooleanClause.Occur.MUST);
			}
			//Bộ trả về các kết quả
			TopDocs topDocs =  searcher.search(queryBuilder.build(), Integer.MAX_VALUE);
			List <Tweet> searchResults = new ArrayList<>();
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				Document doc = searcher.doc(scoreDoc.doc);
				searchResults.add(covertToObject.toTweet(doc));
			}
			
			//In ra danh sách theo option
			switch (option) {
				case TIME: Collections.sort(searchResults, new Comparator<Tweet>() {

					@Override
					public int compare(Tweet o1, Tweet o2) {
					return Long.compare(DateRange.formatterTimeToEpochSecond(o1.getPublishedAt()), DateRange.formatterTimeToEpochSecond(o2.getPublishedAt()));
					}
				});
					break;  
				case LIKE:  Collections.sort(searchResults, new Comparator<Tweet>() {

					@Override
					public int compare(Tweet o1, Tweet o2) {
						return Long.compare(covertToObject.toInteger(o1.getNumber_of_liked()), covertToObject.toInteger(o2.getNumber_of_liked()));
					}
				});
					break; 
				case VIEW:  Collections.sort(searchResults, new Comparator<Tweet>() {

					@Override
					public int compare(Tweet o1, Tweet o2) {
						return Long.compare(covertToObject.toInteger(o1.getNumber_of_view()), covertToObject.toInteger(o2.getNumber_of_view()));
					}
				});
					break; 
				case COMMENT:  Collections.sort(searchResults, new Comparator<Tweet>() {

					@Override
					public int compare(Tweet o1, Tweet o2) {
						return Long.compare(covertToObject.toInteger(o1.getNumber_of_comment()), covertToObject.toInteger(o2.getNumber_of_comment()));
					}
				});
					break; 
				case RELEVANT:
					break;		
			}
			if (direction == false) {
				  Collections.reverse(searchResults);
			}
			
			return searchResults;
		} catch(IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	
	public static List<Article> searchArticle(String inputTitle, String inputAuthor, DateRange range, String inputContent, SortOptionArticle option, boolean direction) throws ParseException, IOException {
		indexArticle();
		try {
			// Khởi tạo bộ truy vấn theo nhiều trường dữ liệu nhập vào
			IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(articleIndexDirectory));
			BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
			
			//Đổi tất cả chữ hoa thành chữ thường
			inputTitle = inputTitle.toLowerCase();
		    inputAuthor = inputAuthor.toLowerCase();
		    inputContent = inputContent.toLowerCase();
		 
		    // Khởi tạo các truy vấn được thêm vào tìm kiếm
		    if (!inputTitle.equals("")) {
		    	PhraseQuery.Builder titlePhraseQueryBuilder = new PhraseQuery.Builder();
				String[] titleTerms = inputTitle.split("\\s+");
				for (String term : titleTerms) {
					titlePhraseQueryBuilder.add(new Term("title", term));
				}
				queryBuilder.add(titlePhraseQueryBuilder.build(), BooleanClause.Occur.MUST);
			}
			if (!inputAuthor.equals("")) {
				PhraseQuery.Builder authorPhraseQueryBuilder = new PhraseQuery.Builder();
				String[] authorTerms = inputAuthor.split("\\s+");
				for (String term : authorTerms) {
					authorPhraseQueryBuilder.add(new Term("author", term));
				}
				queryBuilder.add(authorPhraseQueryBuilder.build(), BooleanClause.Occur.MUST);
			}
			if (!inputContent.equals("")) {
				PhraseQuery.Builder contentPhraseQueryBuilder = new PhraseQuery.Builder();
				String[] contentTerms = inputContent.split("\\s+");
				for (String term : contentTerms) {
						contentPhraseQueryBuilder.add(new Term("content", term));
				}
				queryBuilder.add(contentPhraseQueryBuilder.build(), BooleanClause.Occur.MUST);		
			}
			if (range != null) {
				long startDate = DateRange.formatterTimeToEpochSecond(range.getStartDate());
				long endDate = DateRange.formatterTimeToEpochSecond(range.getEndDate());
				Query dateQuery = new TermRangeQuery("date", new BytesRef(Long.toString(startDate)), new BytesRef(Long.toString(endDate)), true, true);
                queryBuilder.add(dateQuery, BooleanClause.Occur.MUST);
			}
			//Bộ trả về các kết quả
			TopDocs topDocs =  searcher.search(queryBuilder.build(), Integer.MAX_VALUE);
			List <Article> searchResults = new ArrayList<>();
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				Document doc = searcher.doc(scoreDoc.doc);
				searchResults.add(covertToObject.toArticle(doc));
			}
			
			//In ra danh sách theo option
			switch (option) {
				case TIME: Collections.sort(searchResults, new Comparator<Article>() {

					@Override
					public int compare(Article o1, Article o2) {
					return Long.compare(DateRange.formatterTimeToEpochSecond(o1.getPublishedAt()), DateRange.formatterTimeToEpochSecond(o2.getPublishedAt()));
					}
				});
					break;  
				case RELEVANT:
					break;		
			}
			if (direction == false) {
				  Collections.reverse(searchResults);
			}
			
			return searchResults;
		} catch(IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	
	public static List<Facebook> searchFacebook(String inputTitle, String inputAuthor, DateRange range, String inputContent, SortOptionFacebook option, boolean direction) throws ParseException, IOException {
		indexFacebook();
		try {
			// Khởi tạo bộ truy vấn theo nhiều trường dữ liệu nhập vào
			IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(facebookIndexDirectory));
			BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
			
			//Đổi tất cả chữ hoa thành chữ thường
			inputTitle = inputTitle.toLowerCase();
		    inputAuthor = inputAuthor.toLowerCase();
		    inputContent = inputContent.toLowerCase();
		 
		    // Khởi tạo các truy vấn được thêm vào tìm kiếm
		    if (!inputTitle.equals("")) {
		    	PhraseQuery.Builder titlePhraseQueryBuilder = new PhraseQuery.Builder();
				String[] titleTerms = inputTitle.split("\\s+");
				for (String term : titleTerms) {
					titlePhraseQueryBuilder.add(new Term("title", term));
				}
				queryBuilder.add(titlePhraseQueryBuilder.build(), BooleanClause.Occur.MUST);
			}
			if (!inputAuthor.equals("")) {
				PhraseQuery.Builder authorPhraseQueryBuilder = new PhraseQuery.Builder();
				String[] authorTerms = inputAuthor.split("\\s+");
				for (String term : authorTerms) {
					authorPhraseQueryBuilder.add(new Term("author", term));
				}
				queryBuilder.add(authorPhraseQueryBuilder.build(), BooleanClause.Occur.MUST);
			}
			if (!inputContent.equals("")) {
				PhraseQuery.Builder contentPhraseQueryBuilder = new PhraseQuery.Builder();
				String[] contentTerms = inputContent.split("\\s+");
				for (String term : contentTerms) {
						contentPhraseQueryBuilder.add(new Term("content", term));
				}
				queryBuilder.add(contentPhraseQueryBuilder.build(), BooleanClause.Occur.MUST);		
			}
			if (range != null) {
				long startDate = DateRange.formatterTimeToEpochSecond(range.getStartDate());
				long endDate = DateRange.formatterTimeToEpochSecond(range.getEndDate());
				Query dateQuery = new TermRangeQuery("date", new BytesRef(Long.toString(startDate)), new BytesRef(Long.toString(endDate)), true, true);
                queryBuilder.add(dateQuery, BooleanClause.Occur.MUST);
			}
			//Bộ trả về các kết quả
			TopDocs topDocs =  searcher.search(queryBuilder.build(), Integer.MAX_VALUE);
			List <Facebook> searchResults = new ArrayList<>();
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				Document doc = searcher.doc(scoreDoc.doc);
				searchResults.add(covertToObject.toFacebook(doc));
			}
			
			//In ra danh sách theo option
			switch (option) {
				case TIME: Collections.sort(searchResults, new Comparator<Facebook>() {

					@Override
					public int compare(Facebook o1, Facebook o2) {
					return Long.compare(DateRange.formatterTimeToEpochSecond(o1.getPublishedAt()), DateRange.formatterTimeToEpochSecond(o2.getPublishedAt()));
					}
				});
					break;  
				case REACTION:  Collections.sort(searchResults, new Comparator<Facebook>() {

					@Override
					public int compare(Facebook o1, Facebook o2) {
						return Long.compare(covertToObject.toInteger(o1.getNumber_of_reaction()), covertToObject.toInteger(o2.getNumber_of_reaction()));
					}
				});
					break; 
				case SHARE:  Collections.sort(searchResults, new Comparator<Facebook>() {

					@Override
					public int compare(Facebook o1, Facebook o2) {
						return Long.compare(covertToObject.toInteger(o1.getNumber_of_share()), covertToObject.toInteger(o2.getNumber_of_share()));
					}
				});
					break; 
				case COMMENT:  Collections.sort(searchResults, new Comparator<Facebook>() {

					@Override
					public int compare(Facebook o1, Facebook o2) {
						return Long.compare(covertToObject.toInteger(o1.getNumber_of_comment()), covertToObject.toInteger(o2.getNumber_of_comment()));
					}
				});
					break; 
				case RELEVANT:
					break;		
			}
			if (direction == false) {
				  Collections.reverse(searchResults);
			}
			
			return searchResults;
		} catch(IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	
	public static void main(String[] args) throws ParseException, IOException {
	   DateRange daterange = new DateRange();
	   daterange.setStartDate(LocalDateTime.of(2024, 04, 20, 06, 00,00));
	   daterange.setEndDate(LocalDateTime.of(2024, 04, 29, 07, 10, 00));
	   List <Article> tweets = searchArticle("", "", null , "crypto",SortOptionArticle.TIME, false);
	        System.out.println(tweets); // In ra một đối tượng Tweet
	}
}
