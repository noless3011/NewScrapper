package model;

import java.time.LocalDateTime;
import java.util.Set;

public class Facebook extends Article {
	private String number_of_comment;
	private String number_of_reaction;
	private String number_of_share;
	private String imgUrl;

    //Khởi tạo đối tượng tweet
    public Facebook(String author, Content content, LocalDateTime publishedAt, String sourceUrl, 
    		String number_of_reaction, String number_of_comment, String number_of_share, String imgUrl) {
        super(null, author, content, publishedAt, sourceUrl);
        this.number_of_reaction = number_of_reaction;
        this.number_of_comment = number_of_comment;
        this.number_of_share = number_of_share;
        this.imgUrl = imgUrl;
    }
    public Facebook(String author, Content content, LocalDateTime publishedAt, String sourceUrl, 
			String number_of_reaction, String number_of_comment, 
			String number_of_share, String imgUrl, Set <String> entity) {
    	super(null, author, content, publishedAt, sourceUrl, entity);
    	this.number_of_reaction = number_of_reaction;
    	this.number_of_comment = number_of_comment;
    	this.number_of_share = number_of_share;
    	this.imgUrl = imgUrl;
}
    

	public Facebook(String author, Content content, LocalDateTime publishedAt, String sourceUrl,
			String number_of_reaction, String number_of_comment, String number_of_share, String imgUrl,
			Set<String> entity) {
		super(null, author, content, publishedAt, sourceUrl, entity);
		this.number_of_reaction = number_of_reaction;
		this.number_of_comment = number_of_comment;
		this.number_of_share = number_of_share;
		this.imgUrl = imgUrl;
	}

	public Facebook(String title, String author, Content content, LocalDateTime publishedAt, String sourceUrl) {
		super(title, author, content, publishedAt, sourceUrl);
	}

	public String getNumber_of_comment() {
		return number_of_comment;
	}

	public void setNumber_of_comment(String number_of_comment) {
		this.number_of_comment = number_of_comment;
	}

	public String getNumber_of_reaction() {
		return number_of_reaction;
	}

	public void setNumber_of_reaction(String number_of_liked) {
		this.number_of_reaction = number_of_liked;
	}

	public String getNumber_of_share() {
		return number_of_share;
	}

	public void setNumber_of_share(String number_of_view) {
		this.number_of_share = number_of_view;
	}

	public String getImgUrl() {
		return imgUrl;
	}

    @Override
    public String toString() {
        return "Author: " + getAuthor() +
                "\nContent: " + content +
                "\nPublished At: " + getPublishedAt() +
                "\nSource URL: " + getSourceUrl() +
                "\nNumber of Comment: " + number_of_comment +
                "\nNumber of Reaction: " + number_of_reaction +
                "\nNumber of Share: " + number_of_share +
                "\nEntities: " + getEntity();
        
    }

	@Override
	public String toString() {
		return "Author: " + getAuthor() + "\nContent: " + content + "\nPublished At: " + getPublishedAt()
				+ "\nSource URL: " + getSourceUrl() + "\nNumber of Comment: " + number_of_comment
				+ "\nNumber of Reaction: " + number_of_reaction + "\nNumber of Share: " + number_of_share
				+ "\nEntities: " + getEntity();

	}

}
