package demoapache;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import crawler.FacebookCrawler;
import model.Facebook;

public class MainApp {
	public static void main(String [] args) {
		
		String indexPath = "index";
		FacebookCrawler twittercrawler = new FacebookCrawler();
		try {
			Indexer indexer = new Indexer(indexPath);
			List <Facebook> list = twittercrawler.getListFromJson();
			for (Facebook test : list) {
				indexer.addDocument(test.getAuthor(), test.getContent().toString());
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
