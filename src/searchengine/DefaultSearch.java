package searchengine;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
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
	    return search(queryString, TWEET_INDEX_DIR, new String[]{"content", "author"}, doc -> converter.toTweet(doc));
	}

	public List<Article> searchArticle(String queryString) throws ParseException {
	    return search(queryString, ARTICLE_INDEX_DIR, new String[]{"content", "author", "title"}, doc -> converter.toArticle(doc));
	}

	public List<Facebook> searchFacebook(String queryString) throws ParseException {
	    return search(queryString, FACEBOOK_INDEX_DIR, new String[]{"content", "author", "title"}, doc -> converter.toFacebook(doc));
	}

	public List<Object> searchAll(String queryString) throws ParseException {
	    return search(queryString, ALL_INDEX_DIR, new String[]{"content", "author", "title"}, doc -> {
	        String indexType = doc.get("indexType");
	        switch (indexType) {
	            case "Article": return converter.toArticle(doc);
	            case "Facebook": return converter.toFacebook(doc);
	            case "Tweet": return converter.toTweet(doc);
	            default: return null;
	        }
	    });
	}

	private <T> List<T> search(String queryString, String indexDir, String[] fields, Function<Document, T> converter) throws ParseException {
	    try {
	        IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(indexDir))));
	        MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, analyzer);
	        Query mainQuery = parser.parse(queryString);

	        BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
	        booleanQueryBuilder.add(mainQuery, BooleanClause.Occur.SHOULD);

	        for (String field : fields) {
	            booleanQueryBuilder.add(new FuzzyQuery(new Term(field, queryString)), BooleanClause.Occur.SHOULD);
	            booleanQueryBuilder.add(new PrefixQuery(new Term(field, queryString)), BooleanClause.Occur.SHOULD);
	        }

	        BooleanQuery combinedQuery = booleanQueryBuilder.build();
	        TopDocs topDocs = searcher.search(combinedQuery, Integer.MAX_VALUE);

	        List<T> searchResults = new ArrayList<>();
	        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
	            Document doc = searcher.doc(scoreDoc.doc);
	            T result = converter.apply(doc);
	            if (result != null) {
	                searchResults.add(result);
	            }
	        }
	        return searchResults;
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
}