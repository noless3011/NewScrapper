package crawler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import model.Content;
import model.Article;


public class CoindeskCrawler implements ICrawler<Article> {
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
  	public CoindeskCrawler(){
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
  		setUpMainDriver("https://www.coindesk.com/search?s=blockchain");
  		setUpArticleDriver();
  		int i = 0;
  		List<WebElement> elements = mainDriver.findElements(By.cssSelector("div.Box-sc-1hpkeeg-0.fyqwAv"));
  		//Truy cập các trang
  		while(elements.size() < amount) {
  			i = i + 1;
  			setUpMainDriver("https://www.coindesk.com/search?s=blockchain" + "&i=" + i);
			elements = mainDriver.findElements(By.cssSelector("div.Box-sc-1hpkeeg-0.fyqwAv"));
  		}
  		//Lấy các nội dung cần tìm
  		int index = 0;
  		for (WebElement element : elements){

            //Lấy url của bài viết
            List<WebElement> geng = element.findElements(By.cssSelector("a.Box-sc-1hpkeeg-0.hmLHIZ"));
            WebElement drx = geng.get(1);
            String url = drx.getAttribute("href");

            //Lấy tiêu đề của bài viết
            WebElement dtitle = drx.findElement(By.tagName("h6"));
            String title = dtitle.getText();

            //Lấy tác giả của bài viết
            List<WebElement> dauthors = element.findElements(By.cssSelector("a.author"));
            WebElement dauthor;
            if (dauthors.size() > 0) {
                dauthor = dauthors.get(0);
            } else {
                dauthor = element.findElement(By.cssSelector("div.typography__StyledTypography-sc-owin6q-0.gzrhkO"));
            }
            String author = dauthor.getText();

            //Lấy thời gian tung ra của bài viết
            WebElement ddate = element.findElement(By.cssSelector("h6.typography__StyledTypography-sc-owin6q-0.jjSgIS"));
            String dateString = ddate.getText();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
            LocalDate date = LocalDate.parse(dateString, formatter);
            LocalDateTime dateTime = date.atStartOfDay();

            //Dùng hàm lấy nội dung bài viết
            Content mem = new Content();
            Content content = new Content();
            mem.AddElement(extractContentFromUrl(url));
            content	= mem;

            Article article = new Article(title, author, content, dateTime, url);
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
  	
  	private String extractContentFromUrl(String url) {
        	updateArticleDriver(url);
            StringBuilder text = new StringBuilder();
            List<WebElement> contentElements = articleDriver.findElements(By.cssSelector("div.typography__StyledTypography-sc-owin6q-0.eycWal.at-text"));
            for (WebElement contents : contentElements){
                WebElement dcontent = contents.findElement(By.tagName("p"));
                text.append(dcontent.getText());
            }
            String ddcontent = text.toString();
            return ddcontent;

    }
	
	

}