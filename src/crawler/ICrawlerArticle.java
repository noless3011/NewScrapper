package crawler;

import java.util.List;

import adapter.ProgressCallback;
import model.Content;
import model.Article;

public abstract interface ICrawlerArticle{
	//Hàm này sẽ thu thập 1 lượng article mà người dùng nhập vào tron UI, parameter callback dùng để biết đc tiến độ của crawler hiện tại
	public abstract void crawlArticleList(int amount, ProgressCallback callback);
	//Need to check if the Article already exist to not have to store a duplicate using the id of the article
	public abstract void saveToJson(List<Article> list);
	public abstract List<Article> getArticlesFromJson();
}