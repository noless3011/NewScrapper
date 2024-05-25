package crawler;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import adapter.LocalDateTimeAdapter;
import adapter.ProgressCallback;
import model.Image;
import model.Article;
import model.Content;

public class Blockchain101Crawler implements ICrawler<Article> {

	private static final String WEB_URL = "https://101blockchains.com/blog/";
	private static List<Article> articles = new ArrayList<>();

	// Connect to Web, prepare to crawl
	public Document accessPage(int page) {
		try {
			if (page == 1) {
				return Jsoup.connect(WEB_URL).timeout(5000).get();
			} else {
				return Jsoup.connect(WEB_URL + "page" + "/" + page + "/").timeout(5000).get();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void crawlList(int amount, ProgressCallback callback) {
		articles = getListFromJson();
		HashSet <String> uniqueArticles = new HashSet<String>();
		for (Article article: articles) {
			uniqueArticles.add(article.getSourceUrl());
		}
		int page = 1;
		int index = 0;
		while (index < amount) {
			// Crawl, get title author and time of page
			Document doc = accessPage(page);		    
		    if (doc != null) {
		        Elements accessOutUrl = doc.select("div[class=pho-blog-part-content]");
		        
		        loop:
		        for (Element element : accessOutUrl) {
		            // Tiếp tục xử lý các phần tử HTML
		        	// Get URL
					String url = element.select("h2 a").attr("href");
					if(uniqueArticles.contains(url)){
						System.out.println("Skip");
						continue loop;
					}
					// Get title
					String title = element.select("h2 a").text();
					// Get author
					String author = element.select("h5").text();

					// Get date and time
					String dateTimeString = element.select("p").text();

					// Specify the format of the date string without the year
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d yyyy");

					// Parse the date string using the formatter
					Year currentYear = Year.now();
					LocalDate publishedDate = LocalDate.parse(dateTimeString + " " + currentYear.getValue(), formatter);
					// Set the time components to default values
					LocalDateTime publishedDateTime = publishedDate.atStartOfDay();
					// Get tag
					String tag = element.select("a").first().text();

					// Get contents
					try {
						Document document = Jsoup.connect(url).timeout(5000).get();
						Element accessInUrl = document.select("article").first();
						Elements getElement = accessInUrl.select("p, h2, h3, picture");
					
						Content content = new Content();
						boolean pic_number = false;
						for (Element contentElement: getElement) {
							String tagName = contentElement.tagName().toLowerCase();
							if (tagName.equals("picture")) {
								if(!pic_number) {
									Image image = new Image(contentElement.select("img").attr("src"));
									content.AddElement(image);
									pic_number = true;
								}
								
							} else {
								content.AddElement(contentElement.text() + '\n');
							}
						}
					
						// Create object is a Article
						Article article = new Article(title, author, content, publishedDateTime, url);
						article.setTags(tag);
						articles.add(article);
						uniqueArticles.add(url);
						//Update index and page
						System.out.println(index);
						index++;
						callback.updateProgress(index);
						if (index == amount) break;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else {
				System.out.println("Document is null. Error accessing the page.");
			}
			page++;
		}
		saveToJson(articles);
	}

	@Override
	public void saveToJson(List<Article> list) {
		Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
				.setPrettyPrinting().create();
		try (FileWriter writer = new FileWriter("Blockchain101_data.json")) {
			gson.toJson(list, writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Đã lưu thành công vào file json");
	}

	@Override
	public List<Article> getListFromJson() {
		Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
				.setPrettyPrinting().create();
		try (Reader reader = new FileReader("Blockchain101_data.json")) {
			Type listType = new TypeToken<List<Article>>() {
			}.getType();
			List<Article> tweets = gson.fromJson(reader, listType);
			return tweets;
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<Article>();
		}
	}

	public static void main(String[] args) {
		Blockchain101Crawler test = new Blockchain101Crawler();
		test.crawlList(10, null);
	}
}
