package demoapache;

import java.io.IOException;
import java.nio.file.Paths;

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
		String [] fields = {"title", "content", "author"};
		Directory dir = FSDirectory.open(Paths.get(indexPath));
		IndexReader reader = DirectoryReader.open(dir); 
		searcher = new IndexSearcher(reader);
		parser = new MultiFieldQueryParser(fields, new StandardAnalyzer());
	}
	
	public void searchAndDisplay(String queryString) throws Exception {
		Query query = parser.parse(queryString);
		TopDocs hits = searcher.search(query, 10);
		dislaySearchResults(hits);
	}
	
	private void dislaySearchResults(TopDocs hits)  throws IOException {
		System.out.println("Found " + hits.totalHits + "document.");
		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = searcher.doc(scoreDoc.doc);
			System.out.println("Author: " + doc.get("author"));
			System.out.println("Title: " + doc.get("title"));
			System.out.println("Content: " + doc.get("content"));
			System.out.println("--------------------------------------");
		}
		
	}

	public Document getDocument(ScoreDoc scoreDoc) throws IOException {
		return searcher.doc(scoreDoc.doc);
	}
}
