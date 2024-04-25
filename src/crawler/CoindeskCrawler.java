package crawler;

import model.Content;
import model.Article;
import adapter.LocalDateTimeAdapter;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSerializer;
//TODO: This crawler is absolute unusable, fixing later
public class CoindeskCrawler{
    private static final String Base_URL = "https://www.coindesk.com/search?s=blockchain";
    static List <Article> articles = new ArrayList<>();

    public List<Article> crawlPage(int page) {
        System.setProperty("webdriver.chrome.driver", "C:\\Tailieu\\ForJava\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        WebDriver driver = new ChromeDriver(options);
        WebDriver driver1 = new ChromeDriver(options);

        //Kết nối đến trang web
        try{
            if (page == 1) {
                driver.navigate().to(Base_URL);
            } else {
                int trang = page - 1;
                driver.navigate().to(Base_URL + "&i=" + trang);
            }
        
            //Lấy hàng loạt các phần tử cần tìm
            List<WebElement> elements = driver.findElements(By.cssSelector("div.Box-sc-1hpkeeg-0.fyqwAv"));
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
                Content content = extractContentFromUrl(url, driver1);

                Article article = new Article(title, author, content, dateTime, url);
                articles.add(article);
            }
        } finally {
            driver.quit();
            driver1.quit();
        }
        return articles;
    }

    //Ham lay noi dung bai viet
    private static Content extractContentFromUrl(String url, WebDriver driver) {
        try {
            driver.navigate().to(url);
            Content paragraphs = new Content();
            List<WebElement> contentElements = driver.findElements(By.cssSelector("div.typography__StyledTypography-sc-owin6q-0.eycWal.at-text"));
            for (WebElement contents : contentElements){
                WebElement dcontent = contents.findElement(By.tagName("p"));
                String paragraph = dcontent.getText();
                paragraphs.AddElement(paragraph);
            }
            
            return paragraphs;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws IOException {
        Gson gson = new GsonBuilder()
                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                        .setPrettyPrinting()
                        .create();
        FileWriter fileWriter = new FileWriter("dataCD.json");
        try {
            for (int page = 1; page < 10; page++) {
                CoindeskCrawler main = new CoindeskCrawler();
                main.crawlPage(page);
                String json = gson.toJson(articles);
                fileWriter.write(json);
            }
            System.out.println("Da in ra file json");
        } catch (JsonIOException e) {
            e.printStackTrace();
        } finally {
            fileWriter.close();
        }
    }
}
