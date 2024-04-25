package crawler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import adapter.LocalDateTimeAdapter;
import adapter.ProgressCallback;
import javafx.util.Pair;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;

import model.Content;
import model.Article;

public class CNBCCrawler implements ICrawler<Article> {
	private static final Map<String, Integer> STRING_TO_MONTH = new HashMap<>(){{
        put("JAN", 1);
        put("FEB", 2);
        put("MAR", 3);
        put("APR", 4);
        put("MAY", 5);
        put("JUN", 6);
        put("JUL", 7);
        put("AUG", 8);
        put("SEP", 9);
        put("OCT", 10);
        put("NOV", 11);
        put("DEC", 12);
    }};
	private static List<Article> articles = new ArrayList<Article>();
	//Các thành phần của driver để truy cập trang chính
	private WebDriver mainDriver;
	private JavascriptExecutor mainJsExecutor;
	private WebDriverWait mainWaiter;
	private ChromeOptions mainOptions;
	//Các thành phần của driver để truy cập vào từng bài viết
	private WebDriver articleDriver;
	private JavascriptExecutor articleJsExecutor;
	private WebDriverWait articleWaiter;
	private ChromeOptions articleOptions;
	public CNBCCrawler(){
	}
	//Hàm này setup các thành phần cần thiết của driver từng bài viết
	public void setUpArticleDriver() {
		articleOptions = new ChromeOptions();
		//Added options for the driver here ->
		//option này để chạy mà không mở 1 cửa số chrome
		articleOptions.addArguments("--headless");
		
		articleDriver = new ChromeDriver(articleOptions);
		try {
			//Các waiter này tạm thời không dùng đến
			articleWaiter = new WebDriverWait(mainDriver, Duration.ofSeconds(10));
			articleJsExecutor = (JavascriptExecutor)articleDriver;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	//Hàm này để chuyển driver của từng bài sang bài mới
	public void updateArticleDriver(String url) {
		try {
			articleDriver.get(url);
			articleWaiter = new WebDriverWait(mainDriver, Duration.ofSeconds(10));
			articleJsExecutor = (JavascriptExecutor)articleDriver;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	//TƯơng tư với driver của từng bài
	public void setUpMainDriver(String url) {
		mainOptions = new ChromeOptions();
		//Added options for the driver here ->
		mainOptions.addArguments("--headless");
		
		mainDriver = new ChromeDriver(mainOptions);
		try {
			mainDriver.get(url);
			mainWaiter = new WebDriverWait(mainDriver, Duration.ofSeconds(10));
			mainJsExecutor = (JavascriptExecutor)mainDriver;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void crawlList(int amount, ProgressCallback callback) {
		
		setUpMainDriver("https://www.cnbc.com/blockchain/");
		setUpArticleDriver();
		//Tìm nút loadmore
		WebElement loadmoreButton = mainDriver.findElement(By.className("LoadMoreButton-loadMore"));
		List<WebElement> newsList = mainDriver.findElements(By.xpath("//div[@data-test='Card']"));
		//Click vào nút loadmore đến khi đủ amount bài viết
		while(newsList.size() < amount) {
			loadmoreButton.click();
			newsList = mainDriver.findElements(By.xpath("//div[@data-test='Card']"));
		}
		//Biến index để theo dõi progress của crawler
		int index = 0;
		for(WebElement news : newsList) {
			Article article;
			String title = news.findElement(By.className("Card-title")).getText();
			
			String datetimeString = news.findElement(By.className("Card-time")).getText();
			
			String[] datetimeParts = datetimeString.split(" ");
			int date = Integer.parseInt(datetimeParts[2].replaceAll("[a-zA-Z]", ""));
			
			int month = STRING_TO_MONTH.get(datetimeParts[1]);
			int year = Integer.parseInt(datetimeParts[3]);
			LocalDateTime publishedDateTime = LocalDateTime.of(year,month,date, 0, 0);			
			String sourcesURL = news.findElement(By.className("Card-title")).getAttribute("href");
			
			Pair<Content, String> contentAndAuthor = crawlContentAndAuthor(sourcesURL);
			
			Content content = contentAndAuthor.getKey();
			
			String author = contentAndAuthor.getValue();
			
			
			article = new Article(title, author, content, publishedDateTime, sourcesURL);
			articles.add(article);
			//Đoạn này để update progress
			index++;
			callback.updateProgress(index);
			//Đủ ảticle thì ngắt
			if(index == amount) break;
		}
		mainDriver.close();
		articleDriver.close();
		saveToJson(articles);
		articles.clear();
	}

	@Override
	public void saveToJson(List<Article> list) {
		Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).setPrettyPrinting().create();
		try (FileWriter writer = new FileWriter("articles.json")){
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
		try (Reader reader = new FileReader("articles.json")){
			Type listType = new TypeToken<List<Article>>() {}.getType();
            List<Article> tweets = gson.fromJson(reader, listType);
            return tweets;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<Article> getArticlesFromJsonTest() {
		Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).setPrettyPrinting().create();
		try (Reader reader = new FileReader("articles.json")){
			Type listType = new TypeToken<List<Article>>() {}.getType();
            List<Article> tweets = gson.fromJson(reader, listType);
            return tweets;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private Pair<Content, String> crawlContentAndAuthor(String url) {
		Content content = new Content();
		String author = "";
		System.out.println(url);
		updateArticleDriver(url);
		author = articleDriver.findElement(By.className("Author-authorName")).getText();
		List<WebElement> groups = articleDriver.findElements(By.className("group"));
		for(WebElement group : groups) {
			List<WebElement> paragraphs = group.findElements(By.tagName("p"));
			for(WebElement paragraph : paragraphs) {
				content.AddElement(paragraph.getText());
			}
		}
		
		return new Pair<Content, String>(content,author);
	}



}
