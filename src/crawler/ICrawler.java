package crawler;

import java.util.List;

import model.Content;
import model.Article;

public abstract class ICrawler {
	//This function will be called prediodically
		public abstract void crawlArticleList();
		//Need to check if the Article already exist to not have to store a duplicate using the id of the article
		public abstract void saveToJson(List<Article> list);
		public abstract List<Article> getArticlesFromJson();
}