//package crawler;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Locale;
//import java.time.Duration;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.Reader;
//import java.lang.reflect.Type;
//
//import org.openqa.selenium.By;
//import org.openqa.selenium.JavascriptExecutor;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.chrome.ChromeOptions;
//import org.openqa.selenium.support.ui.WebDriverWait;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.reflect.TypeToken;
//
//import adapter.LocalDateTimeAdapter;
//import adapter.ProgressCallback;
//import model.Content;
//import model.Image;
//import model.Article;
//
//public class CoindeskCrawler implements ICrawler<Article> {
//    private static List<Article> articles = new ArrayList<Article>();
//    private WebDriver mainDriver;
//    private JavascriptExecutor mainJsExecutor;
//    private WebDriverWait mainWaiter;
//    private ChromeOptions mainOptions;
//    private WebDriver articleDriver;
//    private JavascriptExecutor articleJsExecutor;
//    private WebDriverWait articleWaiter;
//    private ChromeOptions articleOptions;
//
//    public CoindeskCrawler() {
//    }
//
//    public void setUpArticleDriver() {
//        articleOptions = new ChromeOptions();
//        articleOptions.addArguments("--headless");
//        articleDriver = new ChromeDriver(articleOptions);
//        try {
//            articleWaiter = new WebDriverWait(articleDriver, Duration.ofSeconds(10));
//            articleJsExecutor = (JavascriptExecutor) articleDriver;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void updateArticleDriver(String url) {
//        try {
//            articleDriver.get(url);
//            articleWaiter = new WebDriverWait(articleDriver, Duration.ofSeconds(10));
//            articleJsExecutor = (JavascriptExecutor) articleDriver;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void setUpMainDriver(String url) {
//        mainOptions = new ChromeOptions();
//        mainOptions.addArguments("--headless");
//        mainDriver = new ChromeDriver(mainOptions);
//        try {
//            mainDriver.get(url);
//            mainWaiter = new WebDriverWait(mainDriver, Duration.ofSeconds(10));
//            mainJsExecutor = (JavascriptExecutor) mainDriver;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void crawlList(int amount, ProgressCallback callback) {
//        setUpMainDriver("https://www.coindesk.com/search?s=blockchain");
//        setUpArticleDriver();
//        int i = 0;
//        List<WebElement> elements = mainDriver.findElements(By.cssSelector("div.Box-sc-1hpkeeg-0.fyqwAv"));
//
//        while (elements.size() < amount) {
//            i = i + 1;
//            elements = mainDriver.findElements(By.cssSelector("div.Box-sc-1hpkeeg-0.fyqwAv"));
//        }
//
//        int index = 0;
//        for (WebElement element : elements) {
//            try {
//                List<WebElement> geng = element.findElements(By.cssSelector("a.Box-sc-1hpkeeg-0.hmLHIZ"));
//                WebElement drx = geng.get(1);
//                String url = drx.getAttribute("href");
//                System.out.println(url);
//
//                WebElement dtitle = drx.findElement(By.tagName("h6"));
//                String title = dtitle.getText();
//
//                List<WebElement> dauthors = element.findElements(By.cssSelector("a.author"));
//                WebElement dauthor;
//                if (dauthors.size() > 0) {
//                    dauthor = dauthors.get(0);
//                } else {
//                    dauthor = element.findElement(By.cssSelector("div.typography__StyledTypography-sc-owin6q-0.gzrhkO"));
//                }
//                String author = dauthor.getText();
//
//                WebElement ddate = element.findElement(By.cssSelector("h6.typography__StyledTypography-sc-owin6q-0.jjSgIS"));
//                String dateString = ddate.getText();
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
//                LocalDate date = LocalDate.parse(dateString, formatter);
//                LocalDateTime dateTime = date.atStartOfDay();
//
//                Content content = extractContentFromUrl(url);
//       
//					Article article = new Article(title, author, content, dateTime, url);
//					articles.add(article);
//					//Update index and page
//					index++;
////					callback.updateProgress(index);
//					if (index == amount) break;
//            } catch (org.openqa.selenium.NoSuchElementException e ) {
//                e.printStackTrace();
//                continue;
//            }
//        }
//        mainDriver.close();
//        articleDriver.close();
//        saveToJson(articles);
//        articles.clear();
//    }
//
//    @Override
//    public void saveToJson(List<Article> list) {
//        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).setPrettyPrinting().create();
//        try (FileWriter writer = new FileWriter("dataCD.json")) {
//            gson.toJson(articles, writer);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println("Đã lưu thành công vào file json");
//    }
//
//    @Override
//    public List<Article> getListFromJson() {
//        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).setPrettyPrinting().create();
//        try (Reader reader = new FileReader("dataCD.json")) {
//            Type listType = new TypeToken<List<Article>>() {}.getType();
//            return gson.fromJson(reader, listType);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return new ArrayList<Article>();
//        }
//    }
//
//    private Content extractContentFromUrl(String url) {
//        updateArticleDriver(url);
//        Content content = new Content();
//        try {
//        	
//        	// Nếu có ảnh ở đầu bài viết
//        	try {
//        		String imgUrl = articleDriver.findElement(By.xpath("//*[@id='article-header']/div/header/div[2]/div/div/div[1]/div[6]/div/figure/picture/img")).getAttribute("src");
//        		Image image = new Image(imgUrl);
//        		content.AddElement(image);
//        	} catch (org.openqa.selenium.NoSuchElementException e) {
//        		e.printStackTrace();
//        	}
//        	
//        	// Truy cập đến body bài viết
//        	WebElement articleBody = articleDriver.findElement(By.xpath("//div[@data-submodule-name='composer-content']"));
//        	List <WebElement> allchildElement = articleBody.findElements(By.xpath("./*"));
//        	for (WebElement element: allchildElement) {
//        		if (checkElementClass(element, "common-textstyles__StyledWrapper-sc-18pd49k-0 eSbCkN")) {
//        			content.AddElement(element.getText() + "\n");
//        		}
//        		if(checkElementClass(element, "imagestyles__StyledWrapper-sc-18f23w7-0 kRaHHW")) {
//        			String imageUrl = element.findElement(By.xpath(".//div[@class='at-img']/figure/picture/img")).getAttribute("src");
//        			Image image = new Image(imageUrl);
//        			content.AddElement(image);
//        		}
//        	}
//        } catch (org.openqa.selenium.NoSuchElementException e) {
//            e.printStackTrace();
//            System.out.println("Some Element not found");
//            return new Content();
//        }
//        return content;
//    }
//    private static boolean checkElementClass(WebElement element, String className) {
//        String classes = element.getAttribute("class");
//        return classes.equals(className);
//    }
//    public static void main(String[] args) {
//        CoindeskCrawler test = new CoindeskCrawler();
//        test.crawlList(10, null);
//    }
//   
//}
