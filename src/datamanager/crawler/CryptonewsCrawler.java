package datamanager.crawler;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
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

public class CryptonewsCrawler implements ICrawler<Article>{
	private static final String WEB_URL = "https://cryptonews.com/news/";
	private static List <Article> articles = new ArrayList<>();
	
	public Document accessPage(int page) {
		try {
			if (page == 0) {
				 return Jsoup.connect(WEB_URL).timeout(5000).get();
			}
			else {
				return Jsoup.connect(WEB_URL + "page" + "/" + page + "/"   ).timeout(5000).get();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	

	@Override
	public void crawlList(int amount, ProgressCallback callback)   {
		articles = getListFromJson();
		HashSet <String> uniqueArticles = new HashSet<String>();
		for (Article article: articles) {
			uniqueArticles.add(article.getSourceUrl());
		}
		
		int page = 0;
		int index = 0;
		while(index < amount && !Thread.currentThread().isInterrupted()) {
			Document doc = accessPage(page);
			 if (doc != null) {
			        Elements accessOutUrl = doc.select("div[class = news-one]");
			        
			        loop:
			        for (Element element : accessOutUrl) {
			        	// Get URL
						String url = element.select(" a").attr("href");
						if(uniqueArticles.contains(url)){
							System.out.println("Skip");
							continue loop;
						}
						// Get title
						String title = element.select("div.news-one-title > a").text();
						
						// Get date and time
						String dateTimeString = element.select("div.article__badge-date").attr("data-utctime");
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
						LocalDateTime publishedDateTime = LocalDateTime.parse(dateTimeString, formatter);
						
						
						// Get tag
						String tag = element.select("a.news-one-category").text();
						
						Pair<Content, String> contentAndAuthor;
						try {
							contentAndAuthor = crawlContentAndAuthor(url);
							Content content = contentAndAuthor.getKey();
							
							String author = contentAndAuthor.getValue();
							// Create object is a Article
							Article article = new Article(title, author, content, publishedDateTime, url);
							article.setTags(tag);
							articles.add(article);
							uniqueArticles.add(url);
							//Update index and page
							index++;
							callback.updateProgress(index);
							if (index == amount) break;
						} catch (IOException e) {
							e.printStackTrace();
						}			
			        }
			 }else {
			        System.out.println("Document is null. Error accessing the page.");
			    }
				page++;
			}
			saveToJson();
			 
		}
	
	private Pair<Content, String> crawlContentAndAuthor(String url) throws IOException {
		
		Document connectUrl = Jsoup.connect(url).timeout(5000).get();
		Element getcontent = connectUrl.select("div[class = article-single__content category_contents_details]").first();
		
		//Get content
		Content content = new Content();
		Elements getAllTextElement = getcontent.select("h1, p, h2, img");
		int picNumber = 0;
		for (Element contentElement: getAllTextElement) {
			String tagName = contentElement.tagName();
			if (tagName.equals("img")) {
				picNumber++;
				if(picNumber == 2) {
					Image image = new Image(contentElement.select("img").attr("src"));
					content.AddElement(image);
				}
				
			} else {
				content.AddElement(contentElement.text() + '\n');
			}
		}
		
		// Get author
		String author = getcontent.select("div.author-title > a").text();
		return new Pair<Content, String>(content,author);
		
	}


	@Override
	public void saveToJson() {
		Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).setPrettyPrinting().create();
		try (FileWriter writer = new FileWriter("CryptoNews_data.json")){
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
		try (Reader reader = new FileReader("CryptoNews_data.json")){
			Type listType = new TypeToken<List<Article>>() {}.getType();
            List<Article> tweets = gson.fromJson(reader, listType);
            return tweets;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
}