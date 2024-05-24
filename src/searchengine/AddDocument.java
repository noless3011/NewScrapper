package searchengine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

import model.Article;
import model.Facebook;
import model.Tweet;

public class AddDocument {
	private TokenNameFinderModel personModel;
	
	public void tweet(IndexWriter writer,Tweet tweet) throws IOException {
		Document doc = new Document();
		doc.add(new TextField("author", tweet.getAuthor(), Field.Store.YES));
		doc.add(new TextField("content", tweet.getContent().toString(), Field.Store.YES));
		doc.add(new TextField("view", tweet.getNumber_of_view(), Field.Store.YES));
		doc.add(new TextField("like", tweet.getNumber_of_liked(), Field.Store.YES));
		doc.add(new TextField("comment", tweet.getNumber_of_comment(),Field.Store.YES));
		long date = DateRange.formatterTimeToEpochSecond(tweet.getPublishedAt());
		doc.add(new TextField("date", Long.toString(date), Field.Store.YES));
		doc.add(new TextField("url", tweet.getSourceUrl(), Field.Store.YES));
		List <String> hashtags = tweet.getHashtags();
		for (String hashtag : hashtags ) {
			doc.add(new TextField("hashtag", hashtag, Field.Store.YES));
		}
		doc.add(new TextField("indexType", "Tweet", Field.Store.YES));
//		Set <String> entities = detectEntities(tweet.getContent().toString());
//		for (String entity: entities) {
//			doc.add(new TextField("entity", entity, Field.Store.YES));
//		}
		writer.addDocument(doc);
	}
	
	public void article(IndexWriter writer, Article article) throws IOException {
		Document doc = new Document();
		doc.add(new TextField("author", article.getAuthor(), Field.Store.YES));
		if (article.getTitle() == null) {
			doc.add(new TextField("title", "", Field.Store.YES));
		} else 	doc.add(new TextField("title", article.getTitle(), Field.Store.YES));
		doc.add(new TextField("content", article.getContent().toString(), Field.Store.YES));
		long date = DateRange.formatterTimeToEpochSecond(article.getPublishedAt());
		doc.add(new TextField("date", Long.toString(date), Field.Store.YES));
		doc.add(new TextField("url", article.getSourceUrl(), Field.Store.YES));
		doc.add(new TextField("indexType", "Article", Field.Store.YES));
//		Set <String> entities = detectEntities(article.getContent().toString());
//		for (String entity: entities) {
//			doc.add(new TextField("entity", entity, Field.Store.YES));
//		}
		writer.addDocument(doc);
	}
	
	public void facebook(IndexWriter writer, Facebook facebook) throws IOException {
		Document doc = new Document();
		doc.add(new TextField("author", facebook.getAuthor(), Field.Store.YES));
		doc.add(new TextField("content", facebook.getContent().toString(), Field.Store.YES));
		long date = DateRange.formatterTimeToEpochSecond(facebook.getPublishedAt());
		doc.add(new TextField("date", Long.toString(date), Field.Store.YES));
		doc.add(new TextField("url", facebook.getSourceUrl(), Field.Store.YES));
		doc.add(new TextField("comment", facebook.getNumber_of_comment(), Field.Store.YES));
		doc.add(new TextField("reaction", facebook.getNumber_of_reaction(), Field.Store.YES));
		doc.add(new TextField("share", facebook.getNumber_of_share(), Field.Store.YES));
		doc.add(new TextField("urlimg", facebook.getImgUrl(), Field.Store.YES));
		doc.add(new TextField("indexType", "Facebook", Field.Store.YES));
//		Set <String> entities = detectEntities(facebook.getContent().toString());
//		for (String entity: entities) {
//			doc.add(new TextField("entity", entity, Field.Store.YES));
//		}
		writer.addDocument(doc);
	}
	
	private Set <String> detectEntities(String content) throws FileNotFoundException, IOException {
		personModel = new TokenNameFinderModel(new FileInputStream("en-ner-person.bin"));
		Set <String> entities = new HashSet<>();
		String[] tokens = tokenize(content);
		
		//Thêm thực thể người
		NameFinderME personFinder = new NameFinderME(personModel);
	    Span[] personSpans = personFinder.find(tokens);
	    addEntities(entities, personSpans, tokens);
		return entities;
	}
	public String[] tokenize(String content) throws IOException{
		TokenizerModel tokenModel = new TokenizerModel(new FileInputStream("opennlp-en-ud-ewt-tokens-1.0-1.9.3.bin"));
		TokenizerME tokenizer = new TokenizerME(tokenModel);
		return tokenizer.tokenize(content);
	}
	
	private void addEntities(Set <String> entities, Span[] spans, String[] tokens) {
		 for (Span span : spans) {
			 	StringBuilder entity = new StringBuilder();
			 	for (int i = span.getStart(); i < span.getEnd(); i++) {
			 		entity.append(tokens[i]).append(" ");
			 	}
			 	entities.add(entity.toString().trim());
		}
	}
}
