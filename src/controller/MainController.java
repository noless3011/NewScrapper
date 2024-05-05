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
	// News tab elements
	@FXML
	private Tab newsTab;
	
	@FXML
	private ScrollPane newsScroll;
    
    @FXML
    private AnchorPane newsAnchor;
    
    @FXML
    private VBox newsVBox;
    
    // Tweet tab elements
    @FXML
	private Tab tweetsTab;
	
	@FXML
	private ScrollPane tweetsScroll;
    
    @FXML
    private AnchorPane tweetsAnchor;
    
    @FXML
    private VBox tweetsVBox;
    
    // Facebook post elements
    @FXML
	private Tab postsTab;
	
	@FXML
	private ScrollPane postsScroll;
    
    @FXML
    private AnchorPane postsAnchor;
    
    @FXML
    private VBox postsVBox;
    
    
    //root elements
    @FXML
	private AnchorPane root;
	
    @FXML
    private TextField searchBar;
    
    @FXML
    private Button advanceSearchButton;
    
    @FXML
    private Button refreshButton;
    
    @FXML
    private TabPane tabPane;
    
    private Popup advanceSearchPopup = new Popup();

    public void refresh(ActionEvent event) throws IOException{
    	DisplayList.toggleDynamicUpdate(false);
    	CNBCCrawler cnbcCrawler = new CNBCCrawler();
    	Blockchain101Crawler blockchain101Crawler = new Blockchain101Crawler();
    	DisplayList.getArticleList().setAll(cnbcCrawler.getListFromJson());
    	DisplayList.getArticleList().addAll(blockchain101Crawler.getListFromJson());
    	TwitterCrawler twitterCrawler = new TwitterCrawler();
    	DisplayList.getTweetList().setAll(twitterCrawler.getListFromJson());
    	FacebookCrawler facebookCrawler = new FacebookCrawler();
    	
    	DisplayList.getPostList().setAll(facebookCrawler.getListFromJson());
    	refreshView();
    	DisplayList.toggleDynamicUpdate(true);
    	
    }
    
    public void refreshView() {
    	newsVBox.getChildren().clear();
    	tweetsVBox.getChildren().clear();
    	postsVBox.getChildren().clear();
    	try {	
    		int count = 0;
    		for(Article article : DisplayList.getArticleList()) {
    			FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXML/NewsCard.fxml"));
    			AnchorPane newsCardPane = loader.load();
        		NewsCardController controller = loader.getController();
    			controller.setImage("");
    			controller.setArticle(article);
        		newsVBox.getChildren().add(newsCardPane);
        		count++;
        		if(count == 10) break;
    		}
    		count = 0;
    		for(Facebook post : DisplayList.getPostList()) {
    			FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXML/facebookpost.fxml"));
    			VBox newPost = loader.load();
        		PostController controller = loader.getController();
    			controller.setData(post);
    			System.out.println(post.getImgUrl());
        		postsVBox.getChildren().add(newPost);
        		count++;
        		if(count == 10) break;
    		}
    		
    		
    	}catch (Exception e) {
            e.printStackTrace();
        }
    }
    
	public void openCrawlersManager(ActionEvent event) throws IOException {
		try {
            // Load the FXML file for the new window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXML/CrawlerManager.fxml"));
            Parent root = loader.load();

            // Create a new stage
            Stage newStage = new Stage();

            // Set the scene of the new stage with the loaded FXML
            newStage.setScene(new Scene(root));

            // Show the new stage
            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
    public void openAdvanceSearch(ActionEvent event) throws IOException{
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXML/SearchPopup.fxml"));
		
		try {
			
			
			if(advanceSearchPopup.isShowing()) {
				advanceSearchPopup.hide();
				advanceSearchPopup.getContent().clear();
				return;
			}
			
			AnchorPane root = loader.load();
			SearchPopupController advanceSearchController = loader.getController();
			Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
			List<Field> fields = new ArrayList<>();
			if(selectedTab.equals(newsTab)) {
				fields.add(Field.TITLE);
				fields.add(Field.AUTHOR);
				fields.add(Field.CONTENT);
				advanceSearchController.setSearchOption(SearchOption.ARTICLES);
			}
			if(selectedTab.equals(postsTab) || selectedTab.equals(tweetsTab)) {
				fields.add(Field.TITLE);
				fields.add(Field.AUTHOR);
				if(selectedTab.equals(postsTab)) {
					advanceSearchController.setSearchOption(SearchOption.FACEBOOK);
				}
				if(selectedTab.equals(tweetsTab)) {
					advanceSearchController.setSearchOption(SearchOption.TWITTER);
				}
				
			}
			for(Field field : fields) {
				advanceSearchController.addSearchField(field);
			}
			
			
			advanceSearchPopup.getContent().add(root);
			
			advanceSearchPopup.setAutoHide(true);
			
			advanceSearchPopup.setX(advanceSearchButton.localToScreen(advanceSearchButton.boundsInLocalProperty().get()).getMinX());
			advanceSearchPopup.setY(advanceSearchButton.localToScreen(advanceSearchButton.boundsInLocalProperty().get()).getMaxY());
			advanceSearchPopup.show(advanceSearchButton.getScene().getWindow());
      
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    

}
