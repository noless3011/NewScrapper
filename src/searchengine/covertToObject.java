package searchengine;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.document.Document;

import model.Content;
import model.Image;
import model.Tweet;

public class covertToObject {
	
	public static Tweet tweet(Document doc) {
		String author = doc.get("author");
		//Lưu nội dung
		Content incres = new Content();
		String content = doc.get("content");
		String[] value = content.split("\\{");
	    String beforeCurlyBrace = value[0];
	    incres.AddElement(beforeCurlyBrace);
		//Lấy ảnh từ doc
		String[] parts = content.split("\\{url=");
	    for (int i = 1; i < parts.length; i++) {
	    	String url = parts[i].split(", description=")[0];
	    	Image image = new Image(url);
	        incres.AddElement(image);
	    }
	    // Lấy ngày tháng năm
	    String publish = doc.get("date");
	    long published = Long.parseLong(publish);
	    LocalDateTime publishAt = DateRange.formatterEpochSecondTotime(published);
	    
		String numberOfView = doc.get("view");
		String numberOfLiked = doc.get("like");
		String numberOfComment = doc.get("comment");
		List<String> hashtags = Arrays.asList(doc.getValues("hashtag"));
		String sourceUrl = doc.get("url");
	    return new Tweet(author, incres, publishAt, sourceUrl, hashtags, numberOfComment, numberOfLiked, numberOfView);
	}
	
	
}
