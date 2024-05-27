package datamanager.crawler;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import adapter.LocalDateTimeAdapter;
import adapter.ProgressCallback;
import javafx.util.Pair;
import model.Article;
import model.Content;
import model.Image;

public class TheBlockchainCrawler implements ICrawler<Article>{
	private static final String WEB_URL = "https://www.the-blockchain.com";
	private static List <Article> articles = new ArrayList<>();
	private int timeout = 10000000;
	private Document accessPage(int page) {
		try {
			Document document;
			if(page == 1) {
				System.out.println(WEB_URL + "/?amp");
				document = Jsoup.connect(WEB_URL + "/?amp").timeout(timeout).get();
				
			}else {
				System.out.println(WEB_URL + "/page/" + page + "/?amp");
				document = Jsoup.connect(WEB_URL + "/page/" + page + "/?amp").timeout(timeout).get();
				
			}
			
			return document;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void crawlList(int amount, ProgressCallback callback) {
		articles = new ArrayList<>();//getListFromJson();
		HashSet <String> uniqueArticles = new HashSet<String>();
		for (Article article: articles) {
			uniqueArticles.add(article.getSourceUrl());
		}
		
		int page = 1;
		int index = 0;
		while(index < amount && !Thread.currentThread().isInterrupted()) {
			Document doc = accessPage(page);
			if (doc != null) {
		        
		        Elements removedAmp = doc.select("amp-sidebar");
		        for(Element e : removedAmp) {
		        	e.remove();
		        }
		        Elements newsCards = doc.select("div.td_module_mob_1.td_module_wrap.td-animation-stack");
		        
		        for (Element newsCard : newsCards) {
		        	
		        	Element titleElement = newsCard.selectFirst(".entry-title a");
		            String title = titleElement.attr("title");
		            
		            String url = titleElement.attr("href");
		            
		            Element datetimeElement = newsCard.selectFirst(".td-post-date time");
		            String datetimeString = datetimeElement.attr("datetime");
		            LocalDateTime publishedDateTime = dateTimeParser(datetimeString);
		            System.out.println(title + ", " + datetimeString);
		            Element authorElement = newsCard.selectFirst(".td-post-author-name");
		        
		            String author = authorElement.text();
		            
		        	Content content = getContent(url);
		        	
					// Create object is a Article
					Article article = new Article(title, author, content, publishedDateTime, url);
					articles.add(article);
					uniqueArticles.add(url);
					//Update index and page
					index++;
					callback.updateProgress(index);
					if (index == amount) break;			
		        }
			}else {
		        System.out.println("Document is null. Error accessing the page.");
		    }
			page++;
		}
		saveToJson();
	}
	
	LocalDateTime dateTimeParser(String dateTimeString) {
		// Define the formatter to parse the string
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        // Parse the string into an OffsetDateTime
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(dateTimeString, formatter);

        // Convert OffsetDateTime to LocalDateTime
        LocalDateTime localDateTime = offsetDateTime.toLocalDateTime();

        return localDateTime;
	}
	
	private Content getContent(String url) {
		Content content = new Content();
		System.out.println(url);
		Document doc;
		try {
			doc = Jsoup.connect(url).timeout(timeout).get();
			Elements removedAmp = doc.select("amp-sidebar");
	        for(Element e : removedAmp) {
	        	e.remove();
	        }
			Elements imageElements = doc.select(".td-post-featured-image a");
			Elements paragraphWrapper = doc.select("div.td-post-content");
			for(Element imageElement : imageElements) {
				String imgUrl = imageElement.attr("href");
				Image image = new Image(imgUrl);
				content.AddElement(image);
			}
			Elements paragraphElements = paragraphWrapper.select("p");
			for(Element paragraph : paragraphElements) {
				if(paragraph.tagName().equalsIgnoreCase("p")) {
					content.AddElement(paragraph.text());
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return content;
	}
	
	@Override
	public void saveToJson() {
		Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).setPrettyPrinting().create();
		try (FileWriter writer = new FileWriter("Theblockchain_data.json")){
			gson.toJson(articles, writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Đã lưu thành công vào file json");
		
	}

	@Override
	public List<Article> getListFromJson() {
		Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).setPrettyPrinting().create();
		try (Reader reader = new FileReader("Theblockchain_data.json")){
			Type listType = new TypeToken<List<Article>>() {}.getType();
            List<Article> tweets = gson.fromJson(reader, listType);
            return tweets;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	

}
