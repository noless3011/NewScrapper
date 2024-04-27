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
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
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
				doc.add(new TextField("comment", tweet.getNumber_of_comment(), Field.Store.YES));
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
	public static List<Tweet> searchTweet(String inputTitle, String inputAuthor, DateRange range, String inputContent,List <String> inputHashtag, SortOptionTweet option, boolean direction) throws ParseException {
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
				long startDate = DateRange.formatterTimeToEpochSecond(range.getStartDate());
				long endDate = DateRange.formatterTimeToEpochSecond(range.getEndDate());
				Query dateQuery = new TermRangeQuery("date", new BytesRef(Long.toString(startDate)), new BytesRef(Long.toString(endDate)), true, true);
                queryBuilder.add(dateQuery, BooleanClause.Occur.MUST);
			}
			
			//Bộ trả về các kết quả
			Query query = queryBuilder.build();
			TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE);
			List <Tweet> searchResults = new ArrayList<>();
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				Document doc = searcher.doc(scoreDoc.doc);
				searchResults.add(covertToObject.tweet(doc));
			}
			return searchResults;
		} catch(IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	
	public static void main(String[] args) throws ParseException, IOException {
	   indexTweet();
	   DateRange daterange = new DateRange();
	   daterange.setStartDate(LocalDateTime.of(2024, 04, 26, 06, 00,00));
	   daterange.setEndDate(LocalDateTime.of(2024, 04, 26, 07, 10, 00));
	   List <Tweet> tweets=searchTweet("", "", daterange , "",null,null, true);
	        System.out.println(tweets); // In ra một đối tượng Tweet
	}
}
