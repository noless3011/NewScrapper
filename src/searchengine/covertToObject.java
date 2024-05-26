package searchengine;

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

	@SuppressWarnings("unchecked")
	public <T extends Article> T to(Class<T> type, Document doc) {
		if (type.equals(Article.class)) {
			return (T) this.toArticle(doc);
		}
		;
		if (type.equals(Tweet.class)) {
			return (T) this.toTweet(doc);
		}
		;
		if (type.equals(Facebook.class)) {
			return (T) this.toFacebook(doc);
		}
		;
		return null;
	}

	public Tweet toTweet(@SuppressWarnings("exports") Document doc) {
		String author = doc.get("author");

		// Lấy nội dung
		Content incres = new Content();
		String content = doc.get("content");
		incres.AddElement(content);

		// Lấy ngày tháng năm
		String publish = doc.get("date");
		long published = Long.parseLong(publish);
		LocalDateTime publishAt = DateRange.formatterEpochSecondTotime(published);

		String numberOfLiked = doc.get("like");
		String numberOfView = doc.get("view");
		String numberOfComment = doc.get("comment");
		List<String> hashtags = Arrays.asList(doc.getValues("hashtag"));
		String sourceUrl = doc.get("url");
		
		List<String> entities = Arrays.asList(doc.get("entity"));
		Set<String> uniqueEntities = new HashSet<>(entities);
		return new Tweet(author, incres, publishAt, sourceUrl, hashtags, numberOfComment, numberOfLiked, numberOfView,
				uniqueEntities);
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

		// Chuyển đổi thời gian từ numbergeric sang dạng ngày tháng năm
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
		return new Facebook(author, content, publishAt, sourceUrl, numberOfReaction, numberOfComment, numberOfShare,
				urlImg, uniqueEntities);

	}

	public Article toArticle(Document doc) {
		String author = doc.get("author");
		String title = doc.get("title");

		String publish = doc.get("date");
		long published = Long.parseLong(publish);
		LocalDateTime publishAt = DateRange.formatterEpochSecondTotime(published);
		String sourceUrl = doc.get("url");

		List<String> elements = Arrays.asList(doc.getValues("content"));
		Content content = new Content();
		for (String element: elements) {
			if(element.contains("url=")) {
				Image image = new Image(element);
				content.AddElement(image);
			} else {
				content.AddElement(element);
			}
		}

		List<String> entities = Arrays.asList(doc.getValues("entity"));
		Set<String> uniqueEntities = new HashSet<>(entities);
		return new Article(title, author, content, publishAt, sourceUrl, uniqueEntities);

	}
}
