package demoapache;

import java.io.IOException;
import java.util.List;

import crawler.TwitterCrawler;
import model.Article;

public class MainApp {
	public static void main(String [] args) {
		String indexPath = "index";
		TwitterCrawler twittercrawler = new TwitterCrawler();
		try {
			Indexer indexer = new Indexer(indexPath);
			List <Article> list = twittercrawler.getTweetFromJson();
			for (Article test : list) {
				indexer.addDocument(test.getAuthor(),test.getTitle(), test.getContent());
			}
			indexer.close();
			System.out.println("Indexing Complete");
		} catch (IOException e) {
			System.err.println("Error Indexing" + e.getMessage());
		}
		try {
			Searcher searcher = new Searcher(indexPath);
			String queryString = "blockchain";
			try {
				searcher.searchAndDisplay(queryString);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			System.err.println("Error Searching index: " + e.getMessage());
		}
	}
}
