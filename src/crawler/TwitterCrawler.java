package crawler;

import adapter.LocalDateTimeAdapter;
import adapter.ProgressCallback;

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


public class TwitterCrawler implements ICrawler<Tweet> {
	private static List<Tweet> tweetList = new ArrayList<Tweet>();
	private ChromeOptions driverOptions;
	private WebDriver driver;
	private JavascriptExecutor js;
	private final String USER_NAME = "Hoang583899460";
	private final String PASSWORD = "ngocha3792";
	Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).setPrettyPrinting().create();
	
	//Delay
	public void threadSleep(int miliseconds) {
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
	
	public void loginTwitter() throws InterruptedException {
		//Truy cập vào trang đăng nhập của twitter
		driverOptions = new ChromeOptions();
		//driverOptions.addArguments("--headless");
		driver = new ChromeDriver(driverOptions);
		js = (JavascriptExecutor) driver;
		
		driver.get("https://twitter.com/i/flow/login");
		
		driver.manage().window().maximize();
		System.out.println("get in");
		threadSleep(5000);
		// Chờ cho tới khi trường nhập tên người dùng hiển thị và nhập tên đăng nhập 
		WebElement usernameInput = driver.findElement(By.xpath("//input[@name='text']"));
		usernameInput.sendKeys(USER_NAME);
		
		// Click vào nút next
		WebElement next_button = driver.findElement(By.xpath("//span[contains(text(),'Next')]"));
		next_button.click();
		Thread.sleep(10000);
		
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
		Thread.sleep(5000);
//		WebElement latest_button = driver.findElement(By.xpath("//span[contains(text(), 'Latest')]"));
//		latest_button.click();
//		Thread.sleep(5000);

	}
	
	 public boolean isAtEndOfPage() {
	        long previousPageHeight = (long) js.executeScript("return document.body.scrollHeight");
	        js.executeScript("window.scrollBy(0, 1000);");
	        long currentPageHeight = (long) js.executeScript("return document.body.scrollHeight");
	        threadSleep(3000);
	        return currentPageHeight == previousPageHeight;
	    }
	
	//Tìm kiếm các thông tin về tác giả, thời gian, url của các tweet
	
	public void crawlList(int amount, ProgressCallback callback) {
		tweetList = getListFromJson();
		HashSet<String> uniqueTweets = new HashSet<>();
		for (Tweet tweet : tweetList) {
			uniqueTweets.add(tweet.getSourceUrl());
		}
		
		int i = 0, index = 0;
		
		try {
			loginTwitter();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		 // HashSet để kiểm tra xem tweet đã có trong danh sách chưa để tránh crawl hai bài giống nhau
		while (tweetList.size() < amount) {
//		while(!isElementPresent(driver, By.xpath("//span[contains(text(), 'Something went wrong. Try reloading.')]"))) {
			i++;
			if (i > 2) {
				i = 0;
				//document.body.scrollHeight
				if (isAtEndOfPage()) {
					js.executeScript("window.scrollBy(0, -4000);");//document.body.scrollHeight
					js.executeScript("window.scrollBy(0, 5000);");//document.body.scrollHeight
				}
				threadSleep(2000);
			}
			
			try {
				// Truy nhập vào từng đối tượng bài viết
				List<WebElement> tweets = null;
				tweets = driver.findElements(By.xpath("//div[@class='css-175oi2r r-1iusvr4 r-16y2uox r-1777fci r-kzbkwu']"));
				
				try {
					
					for (WebElement tweet : tweets) {
						//Lấy tên tác giả
						String author = tweet.findElement(By.xpath(".//span[@class='css-1jxf684 r-bcqeeo r-1ttztb7 r-qvutc0 r-poiln3']")).getText();
			
						// Lấy url
						String sourceUrl = tweet.findElement(By.xpath(".//div[@class='css-175oi2r r-18u37iz r-1q142lx']/a")).getAttribute("href");
				
						//Lấy số reply
						String number_of_comment = tweet.findElement(By.xpath(".//button[@data-testid='reply']")).getText();
						if (number_of_comment.equals("")) number_of_comment = "0";
						
						//Lấy số lượt thích
						String number_of_liked = tweet.findElement(By.xpath(".//button[@data-testid='like']")).getText();
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
						//Kiểm tra xem nội dung có bị trùng lặp không
						if (!uniqueTweets.contains(sourceUrl)) {
							Content content = crawlTweetContent(tweet);

							Tweet test = new Tweet(author, content, publishedAt, sourceUrl, hashtags, number_of_comment, number_of_liked, number_of_view);
							tweetList.add(test);
							test.setContent(crawlTweetContent(tweet));
							System.out.println(index);
							index++;
//							callback.updateProgress(index);
							uniqueTweets.add(sourceUrl);
						}
					}
					
				} catch (org.openqa.selenium.NoSuchElementException e) {
					e.printStackTrace();
					continue;
				}	
				
				if (isAtEndOfPage()) {
					js.executeScript("window.scrollBy(0, -4000);");//document.body.scrollHeight
					js.executeScript("window.scrollBy(0, 5000);");//document.body.scrollHeight
				}
				threadSleep(2000);
			} catch (org.openqa.selenium.NoSuchElementException e) {
				e.printStackTrace();
				continue;
			}
		}
		saveToJson(tweetList);

		driver.quit();
	}
	
	// Lấy nội dung của các tweet bao gồm cả ảnh và chữ
	// Hàm này có thể không phải sử dụng
	
	public Content crawlTweetContent(WebElement tweet) {
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

	@Override
	public void saveToJson(List<Tweet> tweetsList) {
		try (FileWriter writer = new FileWriter("tweets.json")){
			gson.toJson(tweetList, writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Đã lưu thành công vào file json");
	}
	
	// Lấy danh sách đối tượng từ file Json
	

	public List<Tweet> getListFromJson(){
		List <Tweet> tweets = null;
		try (Reader reader = new FileReader("tweets.json")){
			Type listType = new TypeToken<List<Tweet>>() {}.getType();
			tweets = gson.fromJson(reader, listType);
            return tweets;
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<Tweet>();
		}
	}
	
	public static void main(String [] args) {
		TwitterCrawler test = new TwitterCrawler();
		test.crawlList(10, null);
	}
}