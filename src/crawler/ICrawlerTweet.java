package crawler;

import java.util.List;

import model.Article;

public interface ICrawlerTweet {
	//Hàm này sẽ lấy về 1 số lượng bài viết nhất định
		public abstract void crawlArticleList(int number);
		//Need to check if the Article already exist to not have to store a duplicate using the id of the article
		public abstract void saveToJson(List<Article> list);
		public abstract List<Article> getArticlesFromJson();
}
