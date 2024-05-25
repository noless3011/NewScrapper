package model;

import java.time.LocalDateTime;
import java.util.Set;

public class Article {
	private int id;
    private String title;
    private String sourceUrl;
    private String sourceName;
    private String author;
    private String tags;
    protected Content content;
    private LocalDateTime publishedDate;
    private Set <String> entity;

    // Constructor
    public Article(String title, String author, Content content, LocalDateTime publishedAt, String sourceUrl) {
        this.title = title;
        this.author = author;
        this.content = content;
        this.publishedDate = publishedAt;
        this.sourceUrl = sourceUrl;
        this.id = this.toString().hashCode();
    }
    
    public Article(String title, String author, Content content, LocalDateTime publishedAt, String sourceUrl, Set <String> entity) {
        this.title = title;
        this.author = author;
        this.content = content;
        this.publishedDate = publishedAt;
        this.sourceUrl = sourceUrl;
        this.id = this.toString().hashCode();
        this.entity = entity;
    }

	public Article(String title, String author, Content content, LocalDateTime publishedAt, String sourceUrl,
			Set<String> entity) {
		this.title = title;
		this.author = author;
		this.content = content;
		this.publishedDate = publishedAt;
		this.sourceUrl = sourceUrl;
		this.id = this.toString().hashCode();
		this.entity = entity;
	}

	// Getters and setters
	public int getID() {
		this.id = this.toString().hashCode();
		return id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	public LocalDateTime getPublishedAt() {
		return publishedDate;
	}

	public void setPublishedAt(LocalDateTime publishedAt) {
		this.publishedDate = publishedAt;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public boolean isEquals(Article other) {
		if (other == this)
			return true;
		else
			return false;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}
	
	public void setEntity(Set <String> entity) {
		this.entity = entity;
	}
	
	public Set <String> getEntity(){
		return entity;
	}
	
	// toString method for printing article details
    @Override
    public String toString() {
        return "Title: " + title +
        		"\nContent: " + content.toString() +
                "\nAuthor: " + author +
                "\nPublished At: " + publishedDate +
                "\nSource URL: " + sourceUrl + 
                "\nEntities: " + entity;
    }

	public void setEntity(Set<String> entity) {
		this.entity = entity;
	}

	public Set<String> getEntity() {
		return entity;
	}

	// toString method for printing article details
	@Override
	public String toString() {
		return "Title: " + title + "\nContent: " + content.toString() + "\nAuthor: " + author + "\nPublished At: "
				+ publishedDate + "\nSource URL: " + sourceUrl + "\nEntities: " + entity;
	}

}