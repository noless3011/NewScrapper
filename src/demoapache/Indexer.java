package demoapache;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import model.Content;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;

import org.apache.lucene.store.Directory;

public class Indexer {
	
	private IndexWriter writer;
	private Tokenizer tokenizer;
	private TokenNameFinderModel personModel;
	private TokenNameFinderModel organizationModel;
	private TokenNameFinderModel localModel;
	
	// Tạo index
	public Indexer(String indexPath) throws IOException {
		Directory dir = FSDirectory.open(Paths.get(indexPath));
		IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
		config.setOpenMode(IndexWriterConfig.OpenMode.CREATE); 
		writer = new IndexWriter(dir, config);
		
		
		// Tạo các token để phát hiện, tìm kiếm các thực thể
		tokenizer = SimpleTokenizer.INSTANCE;
        personModel = new TokenNameFinderModel(new FileInputStream("en-ner-person.bin"));
        organizationModel = new TokenNameFinderModel(new FileInputStream("en-ner-organization.bin"));
        localModel = new TokenNameFinderModel(new FileInputStream("en-ner-location.bin"));
	}
	
	//Thêm các nội dung các thuộc tính vào trong các trường
	public void addDocument(String author,String title, String content) throws IOException {
		Document doc = new Document();
		doc.add(new TextField("author" ,author, Field.Store.YES));
		doc.add(new TextField("title" ,title, Field.Store.YES));
		doc.add(new TextField("content", content, Field.Store.YES));
		List <String> entities = detectEntities(content);
		for (String entity: entities) {
			doc.add(new TextField("entity", entity, Field.Store.YES));
		}
		writer.addDocument(doc);
	}
	
	//Lấy ra danh sách các thực thể
	private List<String> detectEntities(String content) {
		List <String> entities = new ArrayList<>();
		String[] tokens = tokenizer.tokenize(content);
		
		//Thêm thực thể người
		NameFinderME personFinder = new NameFinderME(personModel);
	    Span[] personSpans = personFinder.find(tokens);
	    addEntities(entities, personSpans, tokens);
	    
	    //Thêm thực thể địa chỉ
	    NameFinderME locationFinder = new NameFinderME(localModel);
	    Span[] locationSpans = locationFinder.find(tokens);
	    addEntities(entities, locationSpans, tokens);
	    
	    //Thêm thực thể tổ chức
	    NameFinderME organizationFinder = new NameFinderME(organizationModel);
	    Span[] organizationSpans = organizationFinder.find(tokens);
	    addEntities(entities, organizationSpans, tokens);
		return entities;
	}
	
	//Thêm thực thể vào danh sách
	private void addEntities(List<String> entities, Span[] spans, String[] tokens) {
		 for (Span span : spans) {
	            StringBuilder entity = new StringBuilder();
	            for (int i = span.getStart(); i < span.getEnd(); i++) {
	                entity.append(tokens[i]).append(" ");
	            }
	            entities.add(entity.toString().trim());
	        }
	}
		

	public void close() throws IOException {
		writer.close();
	}
	
}
