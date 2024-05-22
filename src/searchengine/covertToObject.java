package searchengine;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.document.Document;

import model.Article;
import model.Content;
import model.Facebook;
import model.Image;
import model.Tweet;

public class covertToObject {
	public Tweet toTweet(Document doc) {
		String author = doc.get("author");
		
		//Lấy nội dung
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
	    
	    String numberOfLiked = doc.get("like");
		String numberOfView = doc.get("view");
		String numberOfComment = doc.get("comment");
		List<String> hashtags = Arrays.asList(doc.getValues("hashtag"));
		String sourceUrl = doc.get("url");
		List<String> entities = Arrays.asList(doc.getValues("entity"));
	    Set<String> uniqueEntities = new HashSet<>(entities); // Sử dụng Set để loại bỏ các thực thể trùng lặp
	    return new Tweet(author, incres, publishAt, sourceUrl, hashtags, numberOfComment, numberOfLiked, numberOfView, uniqueEntities);
	}
	
	public long toInteger(String value) {
        // Tạo một biểu thức chính quy để tìm các ký tự số
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(value);
        if (matcher.find()) {
            int number = Integer.parseInt(matcher.group());
            // Kiểm tra nếu giá trị kèm theo "K", "M", vv.
            if (value.contains("K")) {
                number *= 1000;
            } else if (value.contains("M")) {
                number *= 1000000;
            }
            return (long) number;
        }
        return 0; // Trả về 0 nếu không tìm thấy số
    }
	
	public Facebook toFacebook(Document doc) {
		String author = doc.get("author");
		String numberOfReaction = doc.get("reaction");
		String numberOfComment = doc.get("comment");
		String numberOfShare = doc.get("share");
		
		//Chuyển đổi thời gian từ numbergeric sang dạng ngày tháng năm
		String publish = doc.get("date");
		long published = Long.parseLong(publish);
		LocalDateTime publishAt = DateRange.formatterEpochSecondTotime(published);
		
		String sourceUrl = doc.get("url");
		String urlImg = doc.get("urlimg");
		Content content = new Content();
		String element = doc.get("content");
		content.AddElement(element);
		List<String> entities = Arrays.asList(doc.getValues("entity"));
	    Set<String> uniqueEntities = new HashSet<>(entities); // Sử dụng Set để loại bỏ các thực thể trùng lặp
		return new Facebook(author, content, publishAt, sourceUrl, numberOfReaction, numberOfComment, numberOfShare, urlImg, uniqueEntities);

	}
	
	public Article toArticle(Document doc) {
		String author = doc.get("author");
		String title = doc.get("title");
		
		String publish = doc.get("date");
		long published = Long.parseLong(publish);
		LocalDateTime publishAt = DateRange.formatterEpochSecondTotime(published);
		
		String sourceUrl = doc.get("url");
		Content content = restoreContentFromString(doc.get("content"));
		List<String> entities = Arrays.asList(doc.getValues("entity"));
	    Set<String> uniqueEntities = new HashSet<>(entities); // Sử dụng Set để loại bỏ các thực thể trùng lặp
		return new Article(title, author, content, publishAt, sourceUrl,uniqueEntities);
		
	}
	
	public Content restoreContentFromString(String contentString) {
	    Content content = new Content();
	    
	    // Phân tách chuỗi thành các đoạn văn bản và URL mô tả
	    String[] parts = contentString.split("\\{url=");
	    if (parts.length == 1) {
	    	content.AddElement(parts);
	    	return content;
	    }
	    for (String part : parts) {
	        // Phân tách đoạn thành URL và mô tả
	        String[] subParts = part.split(", description=");
	        // Nếu mảng không có ít nhất 2 phần tử, thì bỏ qua và tiếp tục vòng lặp
	        if (subParts.length < 2) {
	            continue;
	        }
	        
	        String url = subParts[0];
	        String description = subParts[1].replace("}", ""); // Xóa ký tự '}' ở cuối
	        
	        // Tạo đối tượng Image từ URL và mô tả
	        Image image = new Image(url);
	        
	        // Thêm đối tượng Image và đoạn văn bản vào đối tượng Content
	        content.AddElement(image);
	        content.AddElement(description);
	    }
	    
	    return content;
	}	
}
