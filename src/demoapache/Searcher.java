package demoapache;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;


public class Searcher {
	private IndexSearcher searcher;
	private QueryParser parser;
	
	public Searcher(String indexPath) throws IOException {
		Directory dir = FSDirectory.open(Paths.get(indexPath));
		IndexReader reader = DirectoryReader.open(dir); 
		String [] fields = {"title", "content", "author", "entity"};
		searcher = new IndexSearcher(reader);
		parser = new MultiFieldQueryParser(fields, new StandardAnalyzer());
	}
	
	public void searchAndDisplay(String queryString) throws Exception {
		Query query = parser.parse(queryString);
		TopDocs hits = searcher.search(query, 100);
		dislaySearchResults(hits);
	}
	
	@SuppressWarnings("deprecation")
	private void dislaySearchResults(TopDocs hits)  throws IOException {
		System.out.println("Found " + hits.totalHits + "document.");
		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = searcher.doc(scoreDoc.doc);
			System.out.println("Author: " + doc.get("author"));
			System.out.println("Title: " + doc.get("title"));
			System.out.println("Content: " + doc.get("content"));
			System.out.print("Entities: ");
	        List<String> entities = Arrays.asList(doc.getValues("entity"));
	        Set<String> uniqueEntities = new HashSet<>(entities); // Sử dụng Set để loại bỏ các thực thể trùng lặp
            boolean firstEntity = true;
            for (String entity : uniqueEntities) {
                if (!firstEntity) {
                    System.out.print(", ");
                }
                System.out.print(entity);
                firstEntity = false;
            }
			System.out.println("\n--------------------------------------");
		}
	}

	@SuppressWarnings("deprecation")
	public Document getDocument(ScoreDoc scoreDoc) throws IOException {
		return searcher.doc(scoreDoc.doc);
	}
}
