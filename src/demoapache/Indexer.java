package demoapache;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import model.Content;

import org.apache.lucene.store.Directory;

public class Indexer {
	
	private IndexWriter writer;
	
	public Indexer(String indexPath) throws IOException {
		Directory dir = FSDirectory.open(Paths.get(indexPath));
		IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
		 config.setOpenMode(IndexWriterConfig.OpenMode.CREATE); 
		writer = new IndexWriter(dir, config);
	}
	
	
	
	
	public void addDocument(String author, String title, String content) throws IOException {
		Document doc = new Document();
		doc.add(new TextField("author" ,author, Field.Store.YES));
		doc.add(new TextField("title" ,title, Field.Store.YES));
		doc.add(new TextField("content", content, Field.Store.YES));
		writer.addDocument(doc);
	}
	
	public void close() throws IOException {
		writer.close();
	}
	
}
