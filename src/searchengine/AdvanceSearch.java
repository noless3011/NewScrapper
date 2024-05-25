package searchengine;

import java.io.IOException;
import java.nio.file.Paths;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import model.Facebook;
import model.Tweet;
import model.Article;

public class AdvanceSearch {
	private static final String ARTICLE_INDEX_DIR = "articles_index";
	private static final String TWEET_INDEX_DIR = "tweet_index";
	private static final String FACEBOOK_INDEX_DIR = "facebook_index";

	private covertToObject converter = new covertToObject();

	public AdvanceSearch() {

	}

	private void addPhraseQuery(BooleanQuery.Builder queryBuilder, String field, String value) {
		PhraseQuery.Builder phraseQueryBuilder = new PhraseQuery.Builder();
		String[] terms = value.split("\\s+");
		for (String term : terms) {
			phraseQueryBuilder.add(new Term(field, term));
		}
		queryBuilder.add(phraseQueryBuilder.build(), BooleanClause.Occur.MUST);
	}

	public <T extends Article> List<T> searchAdvance(Class<T> type, String inputTitle, String inputAuthor,
			DateRange range, String inputContent) throws ParseException, IOException {
		try {
			String pathString = "";
			if (type.equals(Article.class))
				pathString = ARTICLE_INDEX_DIR;
			if (type.equals(Tweet.class))
				pathString = TWEET_INDEX_DIR;
			if (type.equals(Facebook.class))
				pathString = FACEBOOK_INDEX_DIR;

			IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(pathString))));
			BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();

			inputTitle = inputTitle.toLowerCase();
			inputAuthor = inputAuthor.toLowerCase();
			inputContent = inputContent.toLowerCase();

			if (!inputTitle.equals("")) {
				addPhraseQuery(queryBuilder, "title", inputTitle);
			}
			if (!inputAuthor.equals("")) {
				addPhraseQuery(queryBuilder, "author", inputAuthor);
			}
			if (!inputContent.equals("")) {
				addPhraseQuery(queryBuilder, "content", inputContent);
			}
			if (range != null) {
				long startDate = DateRange.formatterTimeToEpochSecond(range.getStartDate());
				long endDate = DateRange.formatterTimeToEpochSecond(range.getEndDate());
				Query dateQuery = new TermRangeQuery("date", new BytesRef(Long.toString(startDate)),
						new BytesRef(Long.toString(endDate)), true, true);
				queryBuilder.add(dateQuery, BooleanClause.Occur.MUST);
			}

			TopDocs topDocs = searcher.search(queryBuilder.build(), Integer.MAX_VALUE);

			List<T> searchResults = new ArrayList<>();
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				Document doc = searcher.doc(scoreDoc.doc);
				searchResults.add(converter.to(type, doc));
			}
			return searchResults;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	public List<Tweet> searchAdvanceTweet(String inputTitle, String inputAuthor, DateRange range, String inputContent,
			List<String> inputHashtag) throws ParseException, IOException {
		try {
			IndexSearcher searcher = new IndexSearcher(
					DirectoryReader.open(FSDirectory.open(Paths.get(TWEET_INDEX_DIR))));
			BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();

			inputTitle = inputTitle.toLowerCase();
			inputAuthor = inputAuthor.toLowerCase();
			inputContent = inputContent.toLowerCase();
			List<String> normalizedHashtags = (inputHashtag != null)
					? inputHashtag.stream().map(String::toLowerCase).collect(Collectors.toList())
					: null;

			if (!inputTitle.isEmpty()) {
				addPhraseQuery(queryBuilder, "title", inputTitle);
			}
			if (!inputAuthor.isEmpty()) {
				addPhraseQuery(queryBuilder, "author", inputAuthor);
			}
			if (!inputContent.isEmpty()) {
				addPhraseQuery(queryBuilder, "content", inputContent);
			}
			if (normalizedHashtags != null) {
				normalizedHashtags.forEach(hashtag -> queryBuilder.add(new TermQuery(new Term("hashtag", hashtag)),
						BooleanClause.Occur.MUST));
			}
			if (range != null) {
				long startDate = DateRange.formatterTimeToEpochSecond(range.getStartDate());
				long endDate = DateRange.formatterTimeToEpochSecond(range.getEndDate());
				Query dateQuery = new TermRangeQuery("date", new BytesRef(Long.toString(startDate)),
						new BytesRef(Long.toString(endDate)), true, true);
				queryBuilder.add(dateQuery, BooleanClause.Occur.MUST);
			}

			TopDocs topDocs = searcher.search(queryBuilder.build(), Integer.MAX_VALUE);
			List<Tweet> searchResults = new ArrayList<>();
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				Document doc = searcher.doc(scoreDoc.doc);
				searchResults.add(converter.toTweet(doc));
			}
			return searchResults;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	public List<Article> searchAdvanceArticle(String inputTitle, String inputAuthor, DateRange range,
			String inputContent) throws ParseException, IOException {
		try {
			IndexSearcher searcher = new IndexSearcher(
					DirectoryReader.open(FSDirectory.open(Paths.get(ARTICLE_INDEX_DIR))));
			BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();

			inputTitle = inputTitle.toLowerCase();
			inputAuthor = inputAuthor.toLowerCase();
			inputContent = inputContent.toLowerCase();

			if (!inputTitle.equals("")) {
				addPhraseQuery(queryBuilder, "title", inputTitle);
			}
			if (!inputAuthor.equals("")) {
				addPhraseQuery(queryBuilder, "author", inputAuthor);
			}
			if (!inputContent.equals("")) {
				addPhraseQuery(queryBuilder, "content", inputContent);
			}
			if (range != null) {
				long startDate = DateRange.formatterTimeToEpochSecond(range.getStartDate());
				long endDate = DateRange.formatterTimeToEpochSecond(range.getEndDate());
				Query dateQuery = new TermRangeQuery("date", new BytesRef(Long.toString(startDate)),
						new BytesRef(Long.toString(endDate)), true, true);
				queryBuilder.add(dateQuery, BooleanClause.Occur.MUST);
			}

			TopDocs topDocs = searcher.search(queryBuilder.build(), Integer.MAX_VALUE);
			List<Article> searchResults = new ArrayList<>();
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				Document doc = searcher.doc(scoreDoc.doc);
				searchResults.add(converter.toArticle(doc));
			}
			return searchResults;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	public List<Facebook> searchAdvanceFacebook(String inputTitle, String inputAuthor, DateRange range,
			String inputContent) throws ParseException, IOException {
		try {
			IndexSearcher searcher = new IndexSearcher(
					DirectoryReader.open(FSDirectory.open(Paths.get(FACEBOOK_INDEX_DIR))));
			BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();

			inputTitle = inputTitle.toLowerCase();
			inputAuthor = inputAuthor.toLowerCase();
			inputContent = inputContent.toLowerCase();

			if (!inputTitle.equals("")) {
				addPhraseQuery(queryBuilder, "title", inputTitle);
			}
			if (!inputAuthor.equals("")) {
				addPhraseQuery(queryBuilder, "author", inputAuthor);
			}
			if (!inputContent.equals("")) {
				addPhraseQuery(queryBuilder, "content", inputContent);
			}
			if (range != null) {
				long startDate = DateRange.formatterTimeToEpochSecond(range.getStartDate());
				long endDate = DateRange.formatterTimeToEpochSecond(range.getEndDate());
				Query dateQuery = new TermRangeQuery("date", new BytesRef(Long.toString(startDate)),
						new BytesRef(Long.toString(endDate)), true, true);
				queryBuilder.add(dateQuery, BooleanClause.Occur.MUST);
			}

			TopDocs topDocs = searcher.search(queryBuilder.build(), Integer.MAX_VALUE);
			List<Facebook> searchResults = new ArrayList<>();
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				Document doc = searcher.doc(scoreDoc.doc);
				searchResults.add(converter.toFacebook(doc));
			}
			return searchResults;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	public static void main(String[] args) throws ParseException, IOException {
		AdvanceSearch advanceSearch = new AdvanceSearch();
		Arrange arrange = new Arrange();
		DateRange dateRange = new DateRange();
		dateRange.setStartDate(LocalDateTime.of(2024, 4, 20, 6, 0, 0));
		dateRange.setEndDate(LocalDateTime.of(2024, 4, 29, 7, 10, 0));

		List<Tweet> tweets = advanceSearch.searchAdvanceTweet("", "", null, "crypto", null);
		arrange.arrangeTweet(tweets, Arrange.SortOptionTweet.TIME, false);
		System.out.println(tweets);
	}
}