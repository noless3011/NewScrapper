package searchengine;

import java.util.Comparator;
import java.util.List;

import model.Article;
import model.Facebook;
import model.Tweet;

public class Arrange {
	private covertToObject converter = new covertToObject(); 
	
	public static enum SortOptionArticle {
        TIME, RELEVANT
    }

    public static enum SortOptionTweet {
        TIME, RELEVANT, VIEW, LIKE, COMMENT
    }

    public static enum SortOptionFacebook {
        TIME, RELEVANT, REACTION, SHARE, COMMENT
    }
    
    public List <Tweet> arrangeTweet(List <Tweet> tweets, SortOptionTweet option, boolean direction){
    	 Comparator<Tweet> comparator = null;
         switch (option) {
             case TIME:
                 comparator = Comparator.comparingLong(tweet -> DateRange.formatterTimeToEpochSecond(tweet.getPublishedAt()));
                 break;
             case LIKE:
                 comparator = Comparator.comparingLong(tweet -> converter.toInteger(tweet.getNumber_of_liked()));
                 break;
             case VIEW:
                 comparator = Comparator.comparingLong(tweet -> converter.toInteger(tweet.getNumber_of_view()));
                 break;
             case COMMENT:
                 comparator = Comparator.comparingLong(tweet -> converter.toInteger(tweet.getNumber_of_comment()));
                 break;
             case RELEVANT:
                 break;
         }

         if (comparator != null) {
             tweets.sort(direction ? comparator : comparator.reversed());
         }
         
         return tweets;
    }
    
    public List <Article> arrangeArticle(List <Article> articles, SortOptionArticle option, boolean direction){
    	Comparator<Article> comparator = null;
    	switch(option) {
    		case TIME:
    			comparator = Comparator.comparingLong(tweet -> DateRange.formatterTimeToEpochSecond(tweet.getPublishedAt()));
    			break;
    		case RELEVANT:
    			break;
    	}
    	if (comparator != null) {
    		articles.sort(direction ? comparator : comparator.reversed());
        }
    	return articles;
    }
    
    public List <Facebook> arrangeFacebook(List <Facebook> facebooks, SortOptionFacebook option, boolean direction){
    	Comparator<Facebook> comparator = null;
        switch (option) {
            case TIME:
                comparator = Comparator.comparingLong(facebook -> DateRange.formatterTimeToEpochSecond(facebook.getPublishedAt()));
                break;
            case REACTION:
                comparator = Comparator.comparingLong(facebook -> converter.toInteger(facebook.getNumber_of_reaction()));
                break;
            case SHARE:
                comparator = Comparator.comparingLong(facebook -> converter.toInteger(facebook.getNumber_of_share()));
                break;
            case COMMENT:
                comparator = Comparator.comparingLong(facebook -> converter.toInteger(facebook.getNumber_of_comment()));
                break;
            case RELEVANT:
                break;
        }

        if (comparator != null) {
    		facebooks.sort(direction ? comparator : comparator.reversed());
        }
		return facebooks;
    	
    }
}
