package searchengine;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;

import model.Article;
import model.Facebook;
import model.Tweet;

public class AddDocument {
	private EntityRecognition entityrecognition = new EntityRecognition();

	private void addCommonFields(Document doc, String author, String content, long date, String url,
			List<String> entities) {
		doc.add(new TextField("author", author, Field.Store.YES));
		doc.add(new TextField("content", content, Field.Store.YES));
		doc.add(new TextField("date", Long.toString(date), Field.Store.YES));
		doc.add(new TextField("url", url, Field.Store.YES));

		for (String entity : entities) {
			doc.add(new TextField("entity", entity, Field.Store.YES));
		}
	}

	public void tweet(IndexWriter writer, Tweet tweet) throws IOException {
		Document doc = new Document();
		long date = DateRange.formatterTimeToEpochSecond(tweet.getPublishedAt());
		List<String> entities = entityrecognition.SimpleEntityRecognition(tweet.getContent().toString());

		addCommonFields(doc, tweet.getAuthor(), tweet.getContent().toString(), date, tweet.getSourceUrl(), entities);

		doc.add(new TextField("view", tweet.getNumber_of_view(), Field.Store.YES));
		doc.add(new TextField("like", tweet.getNumber_of_liked(), Field.Store.YES));
		doc.add(new TextField("comment", tweet.getNumber_of_comment(), Field.Store.YES));

		for (String hashtag : tweet.getHashtags()) {
			doc.add(new TextField("hashtag", hashtag, Field.Store.YES));
		}

		doc.add(new TextField("indexType", "Tweet", Field.Store.YES));
		writer.addDocument(doc);
	}

	public void article(IndexWriter writer, Article article) throws IOException {
		Document doc = new Document();
		long date = DateRange.formatterTimeToEpochSecond(article.getPublishedAt());
		List<String> entities = entityrecognition.SimpleEntityRecognition(article.getContent().toString());

		addCommonFields(doc, article.getAuthor(), article.getContent().toString(), date, article.getSourceUrl(),
				entities);

		doc.add(new TextField("title", article.getTitle(), Field.Store.YES));
		for (String element : article.getContent().getElements()) {
			doc.add(new TextField("content", element, Field.Store.YES));
		}

		doc.add(new TextField("indexType", "Article", Field.Store.YES));
		writer.addDocument(doc);
	}

	public void facebook(IndexWriter writer, Facebook facebook) throws IOException {
		Document doc = new Document();
		long date = DateRange.formatterTimeToEpochSecond(facebook.getPublishedAt());
		List<String> entities = entityrecognition.SimpleEntityRecognition(facebook.getContent().toString());

		addCommonFields(doc, facebook.getAuthor(), facebook.getContent().toString(), date, facebook.getSourceUrl(),
				entities);

		doc.add(new TextField("comment", facebook.getNumber_of_comment(), Field.Store.YES));
		doc.add(new TextField("reaction", facebook.getNumber_of_reaction(), Field.Store.YES));
		doc.add(new TextField("share", facebook.getNumber_of_share(), Field.Store.YES));
		doc.add(new TextField("urlimg", facebook.getImgUrl(), Field.Store.YES));
		doc.add(new TextField("indexType", "Facebook", Field.Store.YES));
		writer.addDocument(doc);
	}

}
