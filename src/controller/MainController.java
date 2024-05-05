package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import controller.SearchPopupController.Field;
import controller.SearchPopupController.SearchOption;
import crawler.Blockchain101Crawler;
import crawler.CNBCCrawler;
import crawler.FacebookCrawler;
import crawler.TwitterCrawler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import model.Article;
import model.DisplayList;
import model.Facebook;
import model.Tweet;

public class MainController{
	
	//Enum to determine which tab is selected
	public enum TabType{
		ARTICLE, FACEBOOK, TWITTER, SETTING, CRAWLERMANAGER
	}
	
	private TabType tabState = TabType.ARTICLE;
	
	// Button of title bar (3)
	@FXML
	private Button exitButton;
	
	@FXML
	private Button restoreButton;
	
	@FXML
	private Button minimizeButton;
	
	//Button of switch page bar (5)
	@FXML
	private Button articleButton;
	
	@FXML
	private Button facebookButton;
	
	@FXML
	private Button twitterButton;
	
	@FXML
	private Button settingButton;
	
	@FXML
	private Button crawlerManagerButton;
	
	//Feature button (3)
	@FXML
	private Button reloadButton;
	
	@FXML
	private Button backButton;
	
	@FXML
	private Button advanceSearchButton;
	
	//VBOX
	@FXML
	private VBox contentVBox;
	
	@FXML
	private ScrollPane scrollPane;
	
	public void articleTabPress(ActionEvent event) {
		tabState = TabType.ARTICLE;
		reload();
	}
	
	public void facebookTabPress(ActionEvent event) {
		tabState = TabType.FACEBOOK;
		reload();
	}
	
	public void twitterTabPress(ActionEvent event) {
		tabState = TabType.TWITTER;
		reload();
	}
	
	public void settingTabPress(ActionEvent event) {
		tabState = TabType.SETTING;
		reload();
	}
	
	public void crawlerManagerTabPress(ActionEvent event) {
		tabState = TabType.CRAWLERMANAGER;
		System.out.print("pressed");
		reload();
	}
	
	public void reloadPress(ActionEvent event) {
		reload();
	}
	
	public void reload() {
		contentVBox.getChildren().clear();
		switch(tabState) {
		case ARTICLE:{
			DisplayList.toggleDynamicUpdate(false);
	    	CNBCCrawler cnbcCrawler = new CNBCCrawler();
	    	Blockchain101Crawler blockchain101Crawler = new Blockchain101Crawler();
	    	DisplayList.getArticleList().setAll(cnbcCrawler.getListFromJson());
	    	DisplayList.getArticleList().addAll(blockchain101Crawler.getListFromJson());
	    	DisplayList.toggleDynamicUpdate(true);
			int count = 0;
			for(Article article : DisplayList.getArticleList()) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXML/NewsCard.fxml"));
    			AnchorPane newsCardPane;
				try {
					newsCardPane = loader.load();
					NewsCardController controller = loader.getController();
	    			controller.setImage("");
	    			controller.setArticle(article);
	    			contentVBox.getChildren().add(newsCardPane);
	    			count++;
	        		if(count == 10) break;
				} catch (IOException e) {
					e.printStackTrace();
				}
        		
			}
			break;
		}
			
		case CRAWLERMANAGER:{
			try {
	            // Load the FXML file for the new window
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXML/CrawlerManager.fxml"));
	            AnchorPane root = loader.load();
	            CrawlerManagerController controller = loader.getController();
	            controller.setWidth(scrollPane.getWidth());
	            contentVBox.getChildren().add(root);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
			break;
		}
		case FACEBOOK:
			DisplayList.toggleDynamicUpdate(false);
			FacebookCrawler facebookCrawler = new FacebookCrawler();
	    	DisplayList.getPostList().setAll(facebookCrawler.getListFromJson());
	    	DisplayList.toggleDynamicUpdate(true);
			int count = 0;
			for(Facebook post : DisplayList.getPostList()) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXML/FacebookPost.fxml"));
    			VBox postVBox;
				try {
					postVBox = loader.load();
					PostController controller = loader.getController();
	    			controller.setData(post);
	    			contentVBox.getChildren().add(postVBox);
	    			count++;
	        		if(count == 10) break;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;
		case SETTING:
			break;
		case TWITTER:
			break;
		default:
			break;
		}
	}
	
	public void setTabState(TabType tabType) {
		tabState = tabType;
	}

}
