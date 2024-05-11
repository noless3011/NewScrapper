package searchengine;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
	
	public AdvanceSearch() {
		
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
	
	public static List<Tweet> searchAdvanceTweet(String inputTitle, String inputAuthor, DateRange range, String inputContent,List <String> inputHashtag, SortOptionTweet option, boolean direction) throws ParseException, IOException {
		try {
			// Khởi tạo bộ truy vấn theo nhiều trường dữ liệu nhập vào
			IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(TWEET_INDEX_DIR))));
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
	
	public static List<Article> searchAdvanceArticle(String inputTitle, String inputAuthor, DateRange range, String inputContent, SortOptionArticle option, boolean direction) throws ParseException, IOException {
		try {
			// Khởi tạo bộ truy vấn theo nhiều trường dữ liệu nhập vào
			IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(ARTICLE_INDEX_DIR))));
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
	
	public static List<Facebook> searchAdvanceFacebook(String inputTitle, String inputAuthor, DateRange range, String inputContent, SortOptionFacebook option, boolean direction) throws ParseException, IOException {
		try {
			// Khởi tạo bộ truy vấn theo nhiều trường dữ liệu nhập vào
			IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(FACEBOOK_INDEX_DIR))));
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
		Index.indexArticle();
		DateRange daterange = new DateRange();
		daterange.setStartDate(LocalDateTime.of(2019, 04, 20, 06, 00,00));
		daterange.setEndDate(LocalDateTime.of(2025, 04, 29, 07, 10, 00));
	   	List <Article> tweets = searchAdvanceArticle("", "", daterange , "crypto",SortOptionArticle.TIME, false);
	   	System.out.println(tweets); // In ra một đối tượng Tweet
	}
}
