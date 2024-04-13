package crawler;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import model.Article;
import model.Content;

public class Blockchain101 extends ICrawler{
	
	private static final String WEB_URL = "https://101blockchains.com/blog/";
	static List <Article> articles = new ArrayList<>();
	
	// Connect to Web, prepare to crawl
	public Document accessPage(int page) {
		try {
			if (page == 1) {
				 return Jsoup.connect(WEB_URL).timeout(2000).get();
			}
			else {
				return Jsoup.connect(WEB_URL + "page" + "/" + page + "/"  ).timeout(2000).get();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// Crawl, get title author and time of all page
		public List<Article> crawlPage(int page){
				Document doc = accessPage(page);
				Elements accessOutUrl = doc.select("div [class=pho-blog-part-content]");
				// get title author and time of all article in page
				for (Element element : accessOutUrl) {
					// Get URL
					String url = element.select("h2 a").attr("href");
					
					// Get title
					String title = element.select("h2 a").text();
					// Get author
					String author = element.select("h5").text();
					
					// Get date and time
					// Get date and time
					String dateTimeString = element.select("p").text();
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
					LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);
					
					// Get tag
					String tag = element.select("a").first().text();
					
					//Get contents
					try {
					Document document = Jsoup.connect(url).timeout(2000).get();
					Element accessInUrl = document.select("article").first();
					Elements getAllTextElement = accessInUrl.select("p, h2, h3");
					StringBuilder text = new StringBuilder();
					for (Element getText: getAllTextElement) {
						text.append(getText.text());
						
					}
					Content content = new Content(text.toString());
					
					// Create object is a Article
					Article article = new Article(title, author, content, dateTime, url);
					articles.add(article);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return articles;
		}
		
		public static void main(String [] agrs) throws IOException {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			FileWriter fileWriter = new FileWriter("Blockchain101_data.json");
			for (int page = 1; page < 150; page++) {
				Blockchain101 main = new Blockchain101();
				main.crawlPage(page);
				String json = gson.toJson(articles);
				fileWriter.write(json);
				
			}
			
			fileWriter.close();
			System.out.println("Done");
		}
	
	@Override
	public void CrawlArticleList() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Content CrawlArticleContent(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void SaveToJson(List<Article> list) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Article> GetArticlesFromJson() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
