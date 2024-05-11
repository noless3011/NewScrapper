package searchengine;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import model.Article;
import model.Facebook;
import model.Tweet;

public class DefaultSearch {
	private static final String ARTICLE_INDEX_DIR = "articles_index";
	private static final String TWEET_INDEX_DIR = "tweet_index";
	private static final String FACEBOOK_INDEX_DIR = "facebook_index";
	private static final String ALL_INDEX_DIR = "all_index";
	
	public static List<Tweet> searchDefaultTweet(String queryString) throws ParseException {
		try {
			IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(TWEET_INDEX_DIR))));
			String [] fields = {"content", "author"};
			QueryParser parser = new MultiFieldQueryParser(fields, new StandardAnalyzer());
			Query query = parser.parse(queryString);
			TopDocs topdocs = searcher.search(query, Integer.MAX_VALUE);
			List <Tweet> searchResults = new ArrayList<>();
			for (ScoreDoc scoreDoc : topdocs.scoreDocs) {
				Document doc = searcher.doc(scoreDoc.doc);
				searchResults.add(covertToObject.toTweet(doc));
			}
			return searchResults;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<Article> searchDefaultArticle(String queryString) throws ParseException {
		try {
			IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(ARTICLE_INDEX_DIR))));
			String [] fields = {"content", "author", "title"};
			QueryParser parser = new MultiFieldQueryParser(fields, new StandardAnalyzer());
			Query query = parser.parse(queryString);
			TopDocs topdocs = searcher.search(query, Integer.MAX_VALUE);
			List <Article> searchResults = new ArrayList<>();
			for (ScoreDoc scoreDoc : topdocs.scoreDocs) {
				Document doc = searcher.doc(scoreDoc.doc);
				searchResults.add(covertToObject.toArticle(doc));
			}
			return searchResults;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<Facebook> searchDefaultFacebook(String queryString) throws ParseException {
		try {
			IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(FACEBOOK_INDEX_DIR))));
			String [] fields = {"content", "author", "title"};
			QueryParser parser = new MultiFieldQueryParser(fields, new StandardAnalyzer());
			Query query = parser.parse(queryString);
			TopDocs topdocs = searcher.search(query, Integer.MAX_VALUE);
			List <Facebook> searchResults = new ArrayList<>();
			for (ScoreDoc scoreDoc : topdocs.scoreDocs) {
				Document doc = searcher.doc(scoreDoc.doc);
				searchResults.add(covertToObject.toFacebook(doc));
			}
			return searchResults;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static List<Object> searchDefaultAll(String queryString) throws ParseException {
		try {
			IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(ALL_INDEX_DIR))));
			String [] fields = {"content", "author", "title"};
			QueryParser parser = new MultiFieldQueryParser(fields, new StandardAnalyzer());
			Query query = parser.parse(queryString);
			TopDocs topdocs = searcher.search(query, Integer.MAX_VALUE);
			List <Object> searchResults = new ArrayList<>();
			for (ScoreDoc scoreDoc : topdocs.scoreDocs) {
				Document doc = searcher.doc(scoreDoc.doc);
				String indexType = doc.get("indexType"); 
	            switch (indexType) {
	                    case "Article":
	                        searchResults.add(covertToObject.toArticle(doc));
	                        break;
	                    case "Facebook":
	                    	 searchResults.add(covertToObject.toFacebook(doc));
	                        break;
	                    case "Tweet":
	                    	 searchResults.add(covertToObject.toTweet(doc));
	                        break;
	                   
	                    default: 
	            }
			}
			return searchResults;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static void main(String [] args) throws ParseException, IOException, InterruptedException {
		Index.indexAll();
		List <Object> tweets = searchDefaultAll("bonus");
		for (Object tweet: tweets) {
			Thread.sleep(1000);
			System.out.println(tweet);
		}
	}
}