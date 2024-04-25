package crawler;

import java.util.List;

import adapter.ProgressCallback;
import model.Article;
import model.Tweet;

public interface ICrawlerTweet {
	//Hàm này sẽ thu thập 1 lượng tweet mà người dùng nhập vào trong UI, parameter callback dùng để biết đc tiến độ của crawler hiện tại
	public abstract void crawlTweetList(int amount, ProgressCallback callback);
	//Need to check if the tweet already exist to not have to store a duplicate using the id of the article
	public abstract void saveToJson(List<Article> list);
	public abstract List<Tweet> getTweetFromJson();
}
