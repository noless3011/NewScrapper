package model;

import java.time.LocalDateTime;
import java.time.Month;

import javafx.util.converter.LocalDateStringConverter;

public class Article {
	private int id;
    private String title;
    private String sourceUrl;
    private String sourceName;
    private String author;
    private String tags;
    private Content content;
    private LocalDateTime publishedDate;

    // Constructor
    public Article(String title, String author, Content content, LocalDateTime publishedAt, String sourceUrl) {
        this.title = title;
        this.author = author;
        this.content = content;
        this.publishedDate = publishedAt;
        this.sourceUrl = sourceUrl;
        this.id = this.toString().hashCode();
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

    public String getContent() {
        return content.toString();
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
    	if(other == this) return true;
    	else return false;
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
	
	  // toString method for printing article details
    @Override
    public String toString() {
        return "Title: " + title +
                "\nAuthor: " + author +
                "\nPublished At: " + publishedDate +
                "\nSource URL: " + sourceUrl;
    }

}