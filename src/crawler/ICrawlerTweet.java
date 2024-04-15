package crawler;

import java.util.List;

import model.Tweet;
import model.Content;

public abstract class ICrawlerTweet {
	//This function will be called prediodically
		public abstract void CrawlArticleList();
		
		//Need to check if the Article already exist to not have to store a duplicate using the id of the article
		public abstract void SaveToJson(List<Tweet> list);
		public abstract List<Tweet> GetArticlesFromJson();
}
