package demoapache;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import crawler.CNBCCrawler;
import crawler.TwitterCrawler;
import model.Tweet;
import model.Article;

public class MainApp {
	public static void main(String [] args) {
		
		String indexPath = "index";
		CNBCCrawler twittercrawler = new CNBCCrawler();
		try {
			Indexer indexer = new Indexer(indexPath);
			List <Article> list = twittercrawler.getListFromJson();
			for (Article test : list) {
				indexer.addDocument(test.getAuthor(),test.getTitle(), test.getContent().toString());
			}
			indexer.close();
			System.out.println("Indexing Complete");
		} catch (IOException e) {
			System.err.println("Error Indexing" + e.getMessage());
		}
		try {
			Searcher searcher = new Searcher(indexPath);
			try (Scanner scanner = new Scanner(System.in)) {
				System.out.print("Nhập từ cần tìm: ");
				String queryString = scanner.next();
				
				try {
					searcher.searchAndDisplay(queryString);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			System.err.println("Error Searching index: " + e.getMessage());
		}
	}
}
