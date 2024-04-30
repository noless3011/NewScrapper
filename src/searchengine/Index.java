package searchengine;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import crawler.Blockchain101Crawler;
import crawler.CNBCCrawler;
import crawler.CoindeskCrawler;
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
	    private static StandardAnalyzer analyzer = new StandardAnalyzer();
	    private static TwitterCrawler twittercrawler = new TwitterCrawler();
	    private static FacebookCrawler facebookcrawler = new FacebookCrawler();
	    private static CNBCCrawler cnbccrawler = new CNBCCrawler();
	    private static Blockchain101Crawler blockchain101crawler = new Blockchain101Crawler();
	    private static CoindeskCrawler coindeskcrawler = new CoindeskCrawler();

	    public static void indexTweet() throws IOException {
			// Khởi tạo index để chuẩn bị
	    		Directory dir = FSDirectory.open(Path.of(TWEET_INDEX_DIR));
				IndexWriterConfig config = new IndexWriterConfig(analyzer);
			    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
				IndexWriter writer = new IndexWriter(dir, config);
				
				// Thêm các trường vào index
				List <Tweet> tweets = twittercrawler.getListFromJson();
				for (Tweet tweet : tweets) {
					AddDocument.tweet(writer,tweet);
				}
				writer.close();
		}
		
		public static void indexArticle() throws IOException {
			// Khởi tạo index để chuẩn bị
				Directory dir = FSDirectory.open(Path.of(ARTICLE_INDEX_DIR));
				IndexWriterConfig config = new IndexWriterConfig(analyzer);
			    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
				IndexWriter writer = new IndexWriter(dir, config);
				
				// Thêm các trường vào index
				List <Article> articles = cnbccrawler.getListFromJson();
				articles.addAll(blockchain101crawler.getListFromJson());
//				articles.addAll(coindeskcrawler.getListFromJson());
				for (Article article : articles) {
					
					AddDocument.article(writer, article);
				}
				writer.close();
		}
		
		public static void indexFacebook() throws IOException {
			// Khởi tạo index để chuẩn bị
				Directory dir = FSDirectory.open(Path.of(FACEBOOK_INDEX_DIR));
				IndexWriterConfig config = new IndexWriterConfig(analyzer);
			    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
				IndexWriter writer = new IndexWriter(dir, config);

				// Thêm các trường vào index
				List <Facebook> facebooks = facebookcrawler.getListFromJson();
				for (Facebook facebook : facebooks) {
					AddDocument.facebook(writer, facebook);
				}
				writer.close();
		}
		public static void indexAll() throws IOException {
			Directory dir = FSDirectory.open(Path.of(ALL_INDEX_DIR));
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
		    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
			IndexWriter writer = new IndexWriter(dir, config);
			
			List<Object> objectList = new ArrayList<>();
			objectList.addAll(twittercrawler.getListFromJson());
			objectList.addAll(cnbccrawler.getListFromJson());
			objectList.addAll(blockchain101crawler.getListFromJson());
			objectList.addAll(facebookcrawler.getListFromJson());
			for (Object object: objectList) {
				if (object instanceof Tweet) {
					AddDocument.tweet(writer, (Tweet) object);
				} else if (object instanceof Facebook) {
					AddDocument.facebook(writer, (Facebook) object);
				} else  {
					AddDocument.article(writer, (Article) object);
				}
			}
			writer.close();
		}
}
