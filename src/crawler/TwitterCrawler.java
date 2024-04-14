package crawler;

import adapter.LocalDateTimeAdapter;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.Keys;
import org.openqa.selenium.JavascriptExecutor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.io.Reader;
import java.lang.reflect.Type;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.HashSet;

public class TwitterCrawler {
	public static List<Tweet> tweetList = new ArrayList();
	private final static WebDriver driver = new ChromeDriver();
	private final static JavascriptExecutor js = (JavascriptExecutor) driver;
	private final String USER_NAME = "Hoang583899460";
	private final String PASSWORD = "ngocha2007";
	
	//Delay
	public void threadsleep(int miliseconds) {
		try {
			Thread.sleep(miliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	//Kiểm tra xem trường đó có tồn tại không
	public boolean isElementPresent(WebDriver driver, By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }
	
	public WebDriver loginTwitter() throws InterruptedException {
		//Truy cập vào trang đăng nhập của twitter
		driver.get("https://twitter.com/i/flow/login");
		
		////phóng to màn hình
		driver.manage().window().maximize();
		Thread.sleep(5000);
   
		// Chờ cho tới khi trường nhập tên người dùng hiển thị và nhập tên đăng nhập 
		WebElement usernameInput = driver.findElement(By.xpath("//input[@name='text']"));
		usernameInput.sendKeys(USER_NAME);
		
		// Click vào nút next
		WebElement next_button = driver.findElement(By.xpath("//span[contains(text(),'Next')]"));
		next_button.click();
		Thread.sleep(20000);
		
		// Chờ cho tới khi trường nhập mật khẩu xuất hiện và nhập mật khẩu
		WebElement passwordInput = driver.findElement(By.xpath("//input[@name='password']"));
		passwordInput.sendKeys(PASSWORD);
		
		//Click vào nút login
		WebElement login_button = driver.findElement(By.xpath("//span[contains(text(),'Log in')]"));
		login_button.click();
		Thread.sleep(5000);
		//Kiểm tra xem có phải nhập số điện thoại để xác minh không  
		boolean phoneNumberRequired = isElementPresent(driver, By.xpath("//input[@data-testid='ocfEnterTextTextInput']"));
		 if (phoneNumberRequired) {
			 WebElement phoneNumberInput = driver.findElement(By.xpath("//input[@data-testid='ocfEnterTextTextInput']"));
			 phoneNumberInput.sendKeys("0971015032");
	         WebElement clicknext = driver.findElement(By.xpath("//span[contains(text(),'Next')]"));
	         clicknext.click();
	         Thread.sleep(5000);
	        }
		//Search từ khóa Blockchain
		WebElement search_button = driver.findElement(By.xpath("//input[@data-testid='SearchBox_Search_Input']"));
		search_button.sendKeys("blockchain" + Keys.ENTER);
		Thread.sleep(1000);
		WebElement latest_button = driver.findElement(By.xpath("//span[contains(text(), 'Latest')]"));
		latest_button.click();
		Thread.sleep(5000);
		return driver;
	}
	
	 public boolean isAtEndOfPage() {
	        long previousPageHeight = (long) js.executeScript("return document.body.scrollHeight");
	        js.executeScript("window.scrollBy(0, 1000);");
	        long currentPageHeight = (long) js.executeScript("return document.body.scrollHeight");
	        threadsleep(2000);
	        return currentPageHeight == previousPageHeight;
	    }
	
	//Tìm kiếm các thông tin về tác giả, thời gian, url của các tweet
	
	public void CrawlArticleList() {
		int i = 0,k=0;
		HashSet<String> uniqueTweets = new HashSet<>(); // HashSet để kiểm tra xem tweet đã có trong danh sách chưa để tránh crawl hai bài giống nhau
		while(tweetList.size() < 200) {
			i++;
			if (i > 2) {
				i = 0;
				;//document.body.scrollHeight
				if (isAtEndOfPage()) {
					js.executeScript("window.scrollBy(0, -4000);");//document.body.scrollHeight
					js.executeScript("window.scrollBy(0, 5000);");//document.body.scrollHeight
				}
				threadsleep(2000);
				}
			try {
				// Truy nhập vào từng đối tượng bài viết
				List<WebElement> tweets = null;
				tweets = driver.findElements(By.xpath("//div[@class='css-175oi2r r-1iusvr4 r-16y2uox r-1777fci r-kzbkwu']"));
				try {
					for (WebElement tweet : tweets) {
						//Lấy tên tác giả
				
						String author = tweet.findElement(By.xpath(".//span[@class='css-1qaijid r-bcqeeo r-qvutc0 r-poiln3']")).getText();
			
						// Lấy url
						String sourceUrl = tweet.findElement(By.xpath(".//div[@class='css-175oi2r r-18u37iz r-1q142lx']/a")).getAttribute("href");
				
						//Lấy số reply
						String number_of_comment = tweet.findElement(By.xpath(".//div[@data-testid='reply']")).getText();
						if (number_of_comment.equals("")) number_of_comment = "0";
						
						//Lấy số lượt thích
						String number_of_liked = tweet.findElement(By.xpath(".//div[@data-testid='like']")).getText();
						if (number_of_liked.equals("")) number_of_liked = "0";
						
						// Lấy số lượt xem
						String number_of_view = tweet.findElement(By.xpath(".//a[@class='css-175oi2r r-1777fci r-bt1l66 r-bztko3 r-lrvibr r-1ny4l3l r-1loqt21']")).getText();
						if (number_of_view.equals("")) number_of_view = "0";
						
						//Lấy list HashTag
						List <WebElement> findHashTag = tweet.findElements(By.xpath(".//span[@class='r-18u37iz']"));
						List <String> hashtags = new ArrayList<>();
						for (WebElement hashtag : findHashTag) {
							hashtags.add(hashtag.getText());
						}
				
						//Lấy thời gian
				
						String timeStringFromTwitter = tweet.findElement(By.xpath(".//div[@class='css-175oi2r r-18u37iz r-1q142lx']//a/time")).getAttribute("datetime");
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						LocalDateTime publishedAt = LocalDateTime.parse(timeStringFromTwitter, formatter);
				
						// Lấy nội dung content
						Content content = null;
				
						//Kiểm tra xem nội dung có bị trùng lặp không
						if (!uniqueTweets.contains(sourceUrl)) {
							Tweet test = new Tweet(author, content, publishedAt, sourceUrl, hashtags, number_of_comment, number_of_liked, number_of_view);
							tweetList.add(test);
							test.setContent(CrawlArticleContent(tweet));
							uniqueTweets.add(sourceUrl);	
							System.out.println(k + "\n");k++;
							System.out.println(test.toString());
						}
					}
				}catch (org.openqa.selenium.NoSuchElementException e) {
					continue;
			}	
				
				if (isAtEndOfPage()) {
					js.executeScript("window.scrollBy(0, -4000);");//document.body.scrollHeight
					js.executeScript("window.scrollBy(0, 5000);");//document.body.scrollHeight
				}
				threadsleep(2000);
			}	catch (org.openqa.selenium.NoSuchElementException e) {
				continue;
				}
		}
	}
	
	// Lấy nội dung của các tweet bao gồm cả ảnh và chữ
	
	public Content CrawlArticleContent(WebElement tweet) {
		try {
		// Lấy chữ
		Content content = new Content();
		String text = tweet.findElement(By.xpath(".//div[@data-testid='tweetText']")).getText();
		content.AddElement(text);
		// Lấy url ảnh
		List<WebElement> imgElements = tweet.findElements(By.xpath(".//img[@alt='Image' and not(contains(@src,'profile_images'))]"));
		for (WebElement imgElement: imgElements ) {
			 Image image = new Image(imgElement.getAttribute("src"));
			 content.AddElement(image);
			 }
		return content;
		}  catch (NoSuchElementException e) {
           System.out.println("Some elements were not found in this tweet.");
           return null; // Or handle the exception as needed
        }
	}
	
	// Lưu list gồm các tweet vào file json
	
	public void SaveToJson(List<Tweet> tweetList) {
		Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).setPrettyPrinting().create();
		try (FileWriter writer = new FileWriter("tweets.json")){
			gson.toJson(tweetList, writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Đã lưu thành công vào file json");
	}
	
	// Lấy danh sách đối tượng từ file Json
	
	public List<Tweet> GetArticlesFromJson(){
		try (Reader reader = new FileReader("tweets.json")){
			Type listType = new TypeToken<List<Tweet>>() {}.getType();
            List<Tweet> tweets = new Gson().fromJson(reader, listType);
            return tweets;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String [] agrs) throws InterruptedException {
		//Tạo đối tượng TwitterCrawler để truyền thông điệp thực hiện các hành vi
		TwitterCrawler twittercrawler = new TwitterCrawler();
		// Tạo trình duyệt chromedriver và mở trang twitter
		twittercrawler.loginTwitter();
		twittercrawler.CrawlArticleList();
		twittercrawler.SaveToJson(tweetList);
	}
}