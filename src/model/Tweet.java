package model;

import java.util.List;
import java.util.Set;
import java.time.LocalDateTime;



public class Tweet extends Article{
	private String number_of_comment;
	private String number_of_liked;
	private String number_of_view;
	private List<String> hashtags;
	
	//Khởi tạo đối tượng tweet
	public Tweet(String author, Content content, LocalDateTime publishedAt, String sourceUrl,List<String> hashtags, String number_of_comment, String number_of_liked, String number_of_view) {
		super(null, author, content, publishedAt, sourceUrl);
		this.number_of_liked = number_of_liked;
		this.hashtags = hashtags;
		this.number_of_comment = number_of_comment;
		this.number_of_view = number_of_view;
	}
	
	public Tweet(String author, Content content, LocalDateTime publishedAt, String sourceUrl,List<String> hashtags, 
			String number_of_comment, String number_of_liked, String number_of_view, Set <String> entity) {
		super(null, author, content, publishedAt, sourceUrl, entity);
		this.number_of_liked = number_of_liked;
		this.hashtags = hashtags;
		this.number_of_comment = number_of_comment;
		this.number_of_view = number_of_view;
	}
	
	public List<String> getHashtags() {
		return hashtags;
	}

	public void setHashtags(List<String> hashtags) {
		this.hashtags = hashtags;
	}

	public String getNumber_of_comment() {
		return number_of_comment;
	}

	public void setNumber_of_comment(String number_of_comment) {
		this.number_of_comment = number_of_comment;
	}

	public String getNumber_of_liked() {
		return number_of_liked;
	}

	public void setNumber_of_liked(String number_of_liked) {
		this.number_of_liked = number_of_liked;
	}

	public String getNumber_of_view() {
		return number_of_view;
	}

	public void setNumber_of_view(String number_of_view) {
		this.number_of_view = number_of_view;
	}

	 @Override
	    public String toString() {
	        return "Author: " + getAuthor() +
	        		"\nContent: " + getContent() +
	                "\nPublished At: " + getPublishedAt() +
	                "\nSource URL: " + getSourceUrl() + 
	                "\nHashTag: " + getHashtags() +
	        		"\nNumber of View: " + getNumber_of_view() + 
	        		"\nNumber of Liked:" + getNumber_of_liked() + 
	        		"\nNumber of Comment: " + getNumber_of_comment() + 
	        		 "\nEntities: " + getEntity();
	    }	
}
