package crawler;

import model.Article;
import model.Facebook;
import model.Tweet;

import java.util.List;

public interface ICrawlerFacebook {
    //Hàm này sẽ thu thập 1 lượng article mà người dùng nhập vào tron UI
    public abstract void crawlFacebookList();
    //Need to check if the tweet already exist to not have to store a duplicate using the id of the article
    public abstract void saveToJson(List<Facebook> list);
    public abstract List<Facebook> getFacebookFromJson();
}
