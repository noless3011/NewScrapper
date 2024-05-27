package datamanager.searchengine;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
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
		try {
			IndexSearcher searcher = new IndexSearcher(
					DirectoryReader.open(FSDirectory.open(Paths.get(TWEET_INDEX_DIR))));
			String[] fields = { "content", "author" };
			MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, analyzer);

			Query mainQuery = parser.parse(queryString);

			BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
			booleanQueryBuilder.add(mainQuery, BooleanClause.Occur.SHOULD);

			for (String field : fields) {
				
				FuzzyQuery fuzzyQuery = new FuzzyQuery(new Term(field, queryString));
				booleanQueryBuilder.add(fuzzyQuery, BooleanClause.Occur.SHOULD);

				PrefixQuery prefixQuery = new PrefixQuery(new Term(field, queryString));
				booleanQueryBuilder.add(prefixQuery, BooleanClause.Occur.SHOULD);
			}

			BooleanQuery combinedQuery = booleanQueryBuilder.build();

			TopDocs topDocs = searcher.search(combinedQuery, Integer.MAX_VALUE);
			List<Tweet> searchResults = new ArrayList<>();
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
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
			IndexSearcher searcher = new IndexSearcher(
					DirectoryReader.open(FSDirectory.open(Paths.get(ARTICLE_INDEX_DIR))));
			String[] fields = { "content", "author", "title" };
			MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, analyzer);

			Query mainQuery = parser.parse(queryString);

			BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
			booleanQueryBuilder.add(mainQuery, BooleanClause.Occur.SHOULD);

			for (String field : fields) {
	
				FuzzyQuery fuzzyQuery = new FuzzyQuery(new Term(field, queryString));
				booleanQueryBuilder.add(fuzzyQuery, BooleanClause.Occur.SHOULD);

				PrefixQuery prefixQuery = new PrefixQuery(new Term(field, queryString));
				booleanQueryBuilder.add(prefixQuery, BooleanClause.Occur.SHOULD);
			}

			BooleanQuery combinedQuery = booleanQueryBuilder.build();

			TopDocs topDocs = searcher.search(combinedQuery, Integer.MAX_VALUE);
			List<Article> searchResults = new ArrayList<>();
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
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
			IndexSearcher searcher = new IndexSearcher(
					DirectoryReader.open(FSDirectory.open(Paths.get(FACEBOOK_INDEX_DIR))));
			String[] fields = { "content", "author", "title" };
			MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, analyzer);

			Query mainQuery = parser.parse(queryString);

			BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
			booleanQueryBuilder.add(mainQuery, BooleanClause.Occur.SHOULD);

			for (String field : fields) {
				FuzzyQuery fuzzyQuery = new FuzzyQuery(new Term(field, queryString));
				booleanQueryBuilder.add(fuzzyQuery, BooleanClause.Occur.SHOULD);

				PrefixQuery prefixQuery = new PrefixQuery(new Term(field, queryString));
				booleanQueryBuilder.add(prefixQuery, BooleanClause.Occur.SHOULD);
			}

			BooleanQuery combinedQuery = booleanQueryBuilder.build();

			TopDocs topDocs = searcher.search(combinedQuery, Integer.MAX_VALUE);
			List<Facebook> searchResults = new ArrayList<>();
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
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
			IndexSearcher searcher = new IndexSearcher(
					DirectoryReader.open(FSDirectory.open(Paths.get(ALL_INDEX_DIR))));
			String[] fields = { "content", "author", "title" };
			MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, analyzer);

			Query mainQuery = parser.parse(queryString);

			BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
			booleanQueryBuilder.add(mainQuery, BooleanClause.Occur.SHOULD);

			for (String field : fields) {

				FuzzyQuery fuzzyQuery = new FuzzyQuery(new Term(field, queryString));
				booleanQueryBuilder.add(fuzzyQuery, BooleanClause.Occur.SHOULD);
				
				PrefixQuery prefixQuery = new PrefixQuery(new Term(field, queryString));
				booleanQueryBuilder.add(prefixQuery, BooleanClause.Occur.SHOULD);
			}

			BooleanQuery combinedQuery = booleanQueryBuilder.build();

			TopDocs topDocs = searcher.search(combinedQuery, Integer.MAX_VALUE);
			List<Object> searchResults = new ArrayList<>();
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
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
		Index index = new Index();
		index.indexArticle();
		DefaultSearch search = new DefaultSearch();
		List<Article> results = search.searchArticle("blockchain");
		for (Article result : results) {
			System.out.println(result);
		}
	}
}