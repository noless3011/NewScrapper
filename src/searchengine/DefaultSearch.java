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

    private StandardAnalyzer analyzer = new StandardAnalyzer();
    private covertToObject converter = new covertToObject();
    
    public DefaultSearch() {
       
    }

    public List<Tweet> searchTweet(String queryString) throws ParseException {
        try {
            IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(TWEET_INDEX_DIR))));
            String[] fields = {"content", "author"};
            QueryParser parser = new MultiFieldQueryParser(fields, analyzer);
            Query query = parser.parse(queryString);
            TopDocs topdocs = searcher.search(query, Integer.MAX_VALUE);
            List<Tweet> searchResults = new ArrayList<>();
            for (ScoreDoc scoreDoc : topdocs.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                searchResults.add(converter.toTweet(doc));
            }
            return searchResults;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Article> searchArticle(String queryString) throws ParseException {
        try {
            IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(ARTICLE_INDEX_DIR))));
            String[] fields = {"content", "author", "title"};
            QueryParser parser = new MultiFieldQueryParser(fields, analyzer);
            Query query = parser.parse(queryString);
            TopDocs topdocs = searcher.search(query, Integer.MAX_VALUE);
            List<Article> searchResults = new ArrayList<>();
            for (ScoreDoc scoreDoc : topdocs.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                searchResults.add(converter.toArticle(doc));
            }
            return searchResults;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Facebook> searchFacebook(String queryString) throws ParseException {
        try {
            IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(FACEBOOK_INDEX_DIR))));
            String[] fields = {"content", "author", "title"};
            QueryParser parser = new MultiFieldQueryParser(fields, analyzer);
            Query query = parser.parse(queryString);
            TopDocs topdocs = searcher.search(query, Integer.MAX_VALUE);
            List<Facebook> searchResults = new ArrayList<>();
            for (ScoreDoc scoreDoc : topdocs.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                searchResults.add(converter.toFacebook(doc));
            }
            return searchResults;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Object> searchAll(String queryString) throws ParseException {
        try {
            IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(ALL_INDEX_DIR))));
            String[] fields = {"content", "author", "title"};
            QueryParser parser = new MultiFieldQueryParser(fields, analyzer);
            Query query = parser.parse(queryString);
            TopDocs topdocs = searcher.search(query, Integer.MAX_VALUE);
            List<Object> searchResults = new ArrayList<>();
            for (ScoreDoc scoreDoc : topdocs.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                String indexType = doc.get("indexType");
                switch (indexType) {
                    case "Article":
                        searchResults.add(converter.toArticle(doc));
                        break;
                    case "Facebook":
                        searchResults.add(converter.toFacebook(doc));
                        break;
                    case "Tweet":
                        searchResults.add(converter.toTweet(doc));
                        break;
                    default:
                        break;
                }
            }
            return searchResults;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void main(String[] args) throws ParseException, IOException, InterruptedException {
        // Create an instance of Index and run indexing
        // Perform search
    	Index index = new Index();
    	index.indexAll();
        DefaultSearch search = new DefaultSearch();
        List<Object> results = search.searchAll("Georgia");
        for (Object result : results) {
            Thread.sleep(1000);
            System.out.println(result);
        }
    }
}