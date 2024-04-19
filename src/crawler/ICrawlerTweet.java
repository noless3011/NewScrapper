package crawler;

import java.util.List;

import model.Article;
import model.Tweet;

public interface ICrawlerTweet {
	//Hàm này sẽ trả về 1 số float trong khoảng từ 0-1, cho biết đã crawl được bao nhiêu phần trăm
	public float progress();
	//Hàm này sẽ thu thập 1 lượng article mà người dùng nhập vào tron UI
	public abstract void crawlTweetList(int amount);
	//Need to check if the tweet already exist to not have to store a duplicate using the id of the article
	public abstract void saveToJson(List<Article> list);
	public abstract List<Tweet> getTweetFromJson();
}
