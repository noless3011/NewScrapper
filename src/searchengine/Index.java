package searchengine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import crawler.Blockchain101Crawler;
import crawler.CNBCCrawler;
import crawler.CoindeskCrawler;
import crawler.CryptonewsCrawler;
import crawler.FacebookCrawler;
import crawler.TwitterCrawler;
import model.Article;
import model.Facebook;
import model.Tweet;

public class Index {
    private static final String ARTICLE_INDEX_DIR = "articles_index";
    private static final String ALL_INDEX_DIR = "all_index";
    private static final String TWEET_INDEX_DIR = "tweet_index";
    private static final String FACEBOOK_INDEX_DIR = "facebook_index";
    private StandardAnalyzer analyzer = new StandardAnalyzer();
    private TwitterCrawler twittercrawler = new TwitterCrawler();
    private FacebookCrawler facebookcrawler = new FacebookCrawler();
    private CNBCCrawler cnbccrawler = new CNBCCrawler();
    private Blockchain101Crawler blockchain101crawler = new Blockchain101Crawler();
    private CoindeskCrawler coindeskcrawler = new CoindeskCrawler();
    private CryptonewsCrawler cryptonewscrawler = new CryptonewsCrawler();
    private AddDocument addDocument = new AddDocument();
    
    public void indexTweet() throws IOException {
        // Khởi tạo index để chuẩn bị
        Directory dir = FSDirectory.open(Path.of(TWEET_INDEX_DIR));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(dir, config);

        // Thêm các trường vào index
        List<Tweet> tweets = twittercrawler.getListFromJson();
        for (Tweet tweet : tweets) {
        	addDocument.tweet(writer, tweet);
        }
        writer.close();
    }

    public void indexArticle() throws IOException {
        // Khởi tạo index để chuẩn bị
        Directory dir = FSDirectory.open(Path.of(ARTICLE_INDEX_DIR));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(dir, config);

        // Thêm các trường vào index
        List<Article> articles = cnbccrawler.getListFromJson();
        articles.addAll(blockchain101crawler.getListFromJson());
        articles.addAll(coindeskcrawler.getListFromJson());
        articles.addAll(cryptonewscrawler.getListFromJson());
        for (Article article : articles) {
        	addDocument.article(writer, article);
        }
        writer.close();
    }

    public void indexFacebook() throws IOException {
        // Khởi tạo index để chuẩn bị
        Directory dir = FSDirectory.open(Path.of(FACEBOOK_INDEX_DIR));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(dir, config);

        // Thêm các trường vào index
        List<Facebook> facebooks = facebookcrawler.getListFromJson();
        for (Facebook facebook : facebooks) {
        	addDocument.facebook(writer, facebook);
        }
        writer.close();
    }

    public void indexAll() throws IOException {
        Directory dir = FSDirectory.open(Path.of(ALL_INDEX_DIR));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(dir, config);

        List<Object> objectList = new ArrayList<>();
        objectList.addAll(twittercrawler.getListFromJson());
        objectList.addAll(cnbccrawler.getListFromJson());
        objectList.addAll(blockchain101crawler.getListFromJson());
        objectList.addAll(facebookcrawler.getListFromJson());
        objectList.addAll(cryptonewscrawler.getListFromJson());
        for (Object object : objectList) {
            if (object instanceof Tweet) {
            	addDocument.tweet(writer, (Tweet) object);
            } else if (object instanceof Facebook) {
            	addDocument.facebook(writer, (Facebook) object);
            } else {
				addDocument.article(writer, (Article) object);
            }
        }
        writer.close();
    }
}

