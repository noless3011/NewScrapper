package searchengine;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import crawler.TwitterCrawler;
import model.Content;
import model.Image;
import model.Tweet;

public class SearchEngine {
	private static final String ARTICLE_INDEX_DIR = "articles_index";
	private static final String TWEET_INDEX_DIR = "tweet_index";
	private static final String FACEBOOK_INDEX_DIR = "facebook_index";
	private static StandardAnalyzer analyzer = new StandardAnalyzer();
	private static Directory articleIndexDirectory;
	private static Directory tweetIndexDirectory;
	private static Directory facebookIndexDirectory;
	private static TwitterCrawler twittercrawler = new TwitterCrawler();
	
	
	public SearchEngine() {
		
	}
	
	public static enum SortOptionArticle {
        TIME, RELEVANT
    }
	public static enum SortOptionTweet {
	    TIME, RELEVANT, VIEW, LIKE, COMMENT
	    
	}

	public static enum SortOptionFacebook {
	    TIME, RELEVANT, LIKE, SHARE, COMMENT
	}
	
	
	public static void indexTweet() throws IOException {
			tweetIndexDirectory = FSDirectory.open(Path.of(TWEET_INDEX_DIR));
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
		    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
			IndexWriter writer = new IndexWriter(tweetIndexDirectory, config);
			List <Tweet> tweets = twittercrawler.getListFromJson();
			for (Tweet tweet : tweets) {
				Document doc = new Document();
				doc.add(new TextField("author", tweet.getAuthor(), Field.Store.YES));
				doc.add(new TextField("content", tweet.getContent().toString(), Field.Store.YES));
				doc.add(new TextField("view", tweet.getNumber_of_view(), Field.Store.YES));
				doc.add(new TextField("like", tweet.getNumber_of_liked(), Field.Store.YES));
				doc.add(new TextField("comment", tweet.getNumber_of_comment(), Field.Store.YES));
				doc.add(new TextField("date", tweet.getPublishedAt().toString(), Field.Store.YES));
				doc.add(new TextField("url", tweet.getSourceUrl(), Field.Store.YES));
				List <String> hashtags = tweet.getHashtags();
				for (String hashtag : hashtags ) {
					doc.add(new TextField("hashtag", hashtag, Field.Store.YES));
				}
				writer.addDocument(doc);
				}
			writer.close();
	}
	
	public static List<Tweet> searchTweet(String inputTitle, String inputAuthor, DateRange range, String inputContent,List <String> inputHashtag, SortOptionTweet option, boolean direction) throws ParseException {
		try {
			IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(tweetIndexDirectory));
			BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
			inputTitle = inputTitle.toLowerCase();
		    inputAuthor = inputAuthor.toLowerCase();
		    inputContent = inputContent.toLowerCase();
		    List<String> normalizedHashtags = new ArrayList<>();
		    if (inputHashtag != null) {
		    	for (String hashtag : inputHashtag) {
		    		normalizedHashtags.add(hashtag.toLowerCase());
		    	}
		    }   
		    if (!inputTitle.equals("")) {
				queryBuilder.add(new TermQuery(new Term("title", inputTitle)), BooleanClause.Occur.MUST);
			}
			if (!inputAuthor.equals("")) {
				queryBuilder.add(new TermQuery(new Term("author", inputAuthor)), BooleanClause.Occur.MUST);
			}
			if (!inputContent.equals("")) {
				queryBuilder.add(new TermQuery(new Term("content", inputContent)), BooleanClause.Occur.MUST);
			}
			if (normalizedHashtags != null) {
				for (String hashtag : normalizedHashtags) {
					queryBuilder.add(new TermQuery(new Term("hashtag",hashtag)), BooleanClause.Occur.MUST);
				}
			}
			if (range != null) {
				DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd['T'HH:mm[:ss]]").toFormatter();
				String startRange = range.getStartDate().format(formatter);
                String endRange = range.getEndDate().format(formatter);
                Query query = TermRangeQuery.newStringRange("date", startRange, endRange, true, true);
                queryBuilder.add(query, BooleanClause.Occur.MUST);
			}
			Query query = queryBuilder.build();
			TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE);
			List <Tweet> searchResults = new ArrayList<>();
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				Document doc = searcher.doc(scoreDoc.doc);
				searchResults.add(covertToTweet(doc));
			}
			return searchResults;
		} catch(IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	
	public static Tweet covertToTweet(Document doc) {
		String author = doc.get("author");
		//Lưu nội dung
		Content incres = new Content();
		String content = doc.get("content");
		String[] value = content.split("\\{");
	    String beforeCurlyBrace = value[0];
	    incres.AddElement(beforeCurlyBrace);
		//Lấy ảnh từ doc
		String[] parts = content.split("\\{url=");
	    for (int i = 1; i < parts.length; i++) {
	    	String url = parts[i].split(", description=")[0];
	    	Image image = new Image(url);
	        incres.AddElement(image);
	    }
	    // Lấy ngày tháng năm
	    String publish = doc.get("date");
	    DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd['T'HH:mm[:ss]]").toFormatter();
	    LocalDateTime publishAt = LocalDateTime.parse(publish, formatter);
		String numberOfView = doc.get("view");
		String numberOfLiked = doc.get("like");
		String numberOfComment = doc.get("comment");
		List<String> hashtags = Arrays.asList(doc.getValues("hashtag"));
		String sourceUrl = doc.get("url");
	    return new Tweet(author, incres, publishAt, sourceUrl, hashtags, numberOfComment, numberOfLiked, numberOfView);
	}
	
	public static void main(String[] args) throws ParseException, IOException {
	   indexTweet();
	   DateRange daterange = new DateRange();
	   daterange.setStartDate(LocalDateTime.of(2024, 04, 23, 00, 00,00));
	   daterange.setEndDate(LocalDateTime.of(2024, 04, 26, 00, 00,00));
	   List <Tweet> tweets=searchTweet("", "",null , "blockchain",null,null, true);
	        System.out.println(tweets); // In ra một đối tượng Tweet
	}
}
