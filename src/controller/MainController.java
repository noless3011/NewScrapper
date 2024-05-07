package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.lucene.queryparser.classic.ParseException;

import controller.SearchPopupController.Field;
import controller.SearchPopupController.SearchOption;
import crawler.Blockchain101Crawler;
import crawler.CNBCCrawler;
import crawler.FacebookCrawler;
import crawler.TwitterCrawler;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Article;
import model.DisplayList;
import model.Facebook;
import model.Tweet;
import searchengine.DefaultSearch;

public class MainController{
	
	//Enum to determine which tab is selected
	public enum TabType{
		ARTICLE, FACEBOOK, TWITTER, SETTING, CRAWLERMANAGER, SEARCHRESULT, ARTICLEVIEW
	}
	
	private HashMap<TabType, List<Parent>> tabContents = new HashMap<>();
	private HashMap<TabType, Button> buttonTypeHashMap = new HashMap<>();
	private Stack<TabType> undoStack = new Stack<TabType>();
	private Stack<TabType> redoStack = new Stack<TabType>();
	
	private TabType currentTabState = TabType.ARTICLE;
	
	private double xOffset;
	private double yOffset;
	
	
	//Main stage
	private Stage primaryStage;
	
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
	private AnchorPane root;
	
	@FXML
	private Button reloadButton;
	
	@FXML
	private Button backButton;
	
	@FXML
	private Button forwardButton;
	
	@FXML
	private Button advanceSearchButton;
	
	@FXML
	private TextField searchTextField;
	
	//Containers
	@FXML
	private AnchorPane titleBarAnchor;
	
	@FXML
	private VBox contentVBox;
	
	@FXML
	private ScrollPane scrollPane;

	@FXML
	private AnchorPane contentAnchor;
	
	//Miscellaneous
	
	@FXML
	private ImageView reloadImageView;
	
	private RotateTransition rotateLoadTransition;
	
	@FXML
	private Pagination pagination;
	
	//Popup
	private Popup advanceSearchPopup = new Popup();
	
	public void setupController(Stage stage) {
		primaryStage = stage;
		List<Parent> empty = new ArrayList<>();
		undoStack.push(currentTabState);
		tabContents.put(TabType.ARTICLE, empty);
		tabContents.put(TabType.FACEBOOK, empty);
		tabContents.put(TabType.TWITTER, empty);
		tabContents.put(TabType.CRAWLERMANAGER, empty);
		tabContents.put(TabType.SETTING, empty);
		tabContents.put(TabType.SEARCHRESULT, empty);
		tabContents.put(TabType.ARTICLEVIEW, empty);
		contentAnchor.prefWidthProperty().bind(scrollPane.widthProperty().subtract(15));
		
		
		buttonTypeHashMap.put(TabType.ARTICLE, articleButton);
		buttonTypeHashMap.put(TabType.TWITTER, twitterButton);
		buttonTypeHashMap.put(TabType.FACEBOOK, facebookButton);
		buttonTypeHashMap.put(TabType.CRAWLERMANAGER, crawlerManagerButton);
		buttonTypeHashMap.put(TabType.SETTING, settingButton);
		
		
		rotateLoadTransition = new RotateTransition(Duration.seconds(0.5), reloadImageView);
		rotateLoadTransition.setByAngle(-360); // Rotate 360 degrees
        rotateLoadTransition.setCycleCount(Animation.INDEFINITE);
        rotateLoadTransition.setInterpolator(Interpolator.LINEAR);
		titleBarAnchor.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = primaryStage.getX() - event.getScreenX();
                yOffset = primaryStage.getY() - event.getScreenY();
            }
        });
		titleBarAnchor.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                primaryStage.setX(event.getScreenX() + xOffset);
                primaryStage.setY(event.getScreenY() + yOffset);
            }
        });
		
		searchTextField.setOnKeyPressed( event -> {
			  if( event.getCode() == KeyCode.ENTER ) {
			    try {
					showSearchResult(searchResultToParents(DefaultSearch.searchDefaultAll(searchTextField.getText())));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			  }
			} );
		
	}
	
	public void advanceSearchPress(ActionEvent event) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXML/SearchPopup.fxml"));
		
		try {
			
			
			if(advanceSearchPopup.isShowing()) {
				advanceSearchPopup.hide();
				advanceSearchPopup.getContent().clear();
				return;
			}
			
			AnchorPane root = loader.load();
			SearchPopupController advanceSearchController = loader.getController();
			List<Field> fields = new ArrayList<>();
			if(currentTabState == TabType.ARTICLE) {
				fields.add(Field.TITLE);
				fields.add(Field.AUTHOR);
				fields.add(Field.CONTENT);
				advanceSearchController.setSearchOption(SearchOption.ARTICLES);
			}
			if(currentTabState == TabType.FACEBOOK || currentTabState == TabType.TWITTER) {
				fields.add(Field.TITLE);
				fields.add(Field.AUTHOR);
				if(currentTabState == TabType.FACEBOOK) {
					advanceSearchController.setSearchOption(SearchOption.FACEBOOK);
				}
				if(currentTabState == TabType.TWITTER) {
					advanceSearchController.setSearchOption(SearchOption.TWITTER);
				}
				
			}
			for(Field field : fields) {
				advanceSearchController.addSearchField(field);
			}
			
			
			advanceSearchPopup.getContent().add(root);
			
			advanceSearchPopup.setAutoHide(true);
			
			advanceSearchPopup.setX(primaryStage.getX() + primaryStage.getWidth()/2 - 100);
			advanceSearchPopup.setY(primaryStage.getY() + 70);
			advanceSearchPopup.show(advanceSearchButton.getScene().getWindow());
      
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<Parent> searchResultToParents(List<Object> searchResult) {
		int progressCount = 0;
		List<Parent> elements = new ArrayList<>();
		for(Object result : searchResult) {
			if(result instanceof Facebook) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXML/facebookpost.fxml"));
				VBox postVBox;
				try {
					postVBox = loader.load();
					PostController controller = loader.getController();
					controller.setData((Facebook)result);
					elements.add(postVBox);
					System.out.print("added a facebook");
				} catch (IOException e) {
					e.printStackTrace();
				}
				continue;
			}
			if(result instanceof Article) {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXML/NewsCard.fxml"));
				Parent newsCardPane;
				try {
					newsCardPane = loader.load();
					NewsCardController controller = loader.getController();
					controller.setImage("");
					controller.setArticle((Article) result);
					elements.add(newsCardPane);
				} catch (IOException e) {
					e.printStackTrace();
				}
				continue;
			}
		}
		return elements;
	}
	
	public void showSearchResult(List<Parent> searchResult) {
		undoStack.push(TabType.SEARCHRESULT);
		tabContents.replace(TabType.SEARCHRESULT, searchResult);
		reloadView();
	}
	
	public void closePress(ActionEvent event) {
		Platform.exit();
	}
	
	public void minimizePress(ActionEvent event) {
		primaryStage.setIconified(true);
	}
	
	public void restorePress(ActionEvent event) {
		primaryStage.setMaximized(!primaryStage.isMaximized());

	}
	
	public void forwardPress(ActionEvent event) {
		if(redoStack.size() <= 1) return;
		undoStack.push(redoStack.pop());
		contentVBox.getChildren().setAll(tabContents.get(undoStack.peek()));
	}
	
	public void backPress(ActionEvent event) {
		if(undoStack.size() <= 1) return;
		redoStack.push(undoStack.pop());
		contentVBox.getChildren().setAll(tabContents.get(undoStack.peek()));
		currentTabState = undoStack.peek();
	}
	
	
	public void articleTabPress(ActionEvent event) {
		checkToggle(TabType.ARTICLE);
		
	}
	
	public void facebookTabPress(ActionEvent event) {
		checkToggle(TabType.FACEBOOK);
	}
	
	public void twitterTabPress(ActionEvent event) {
		checkToggle(TabType.TWITTER);
	}
	
	public void settingTabPress(ActionEvent event) {
		checkToggle(TabType.SETTING);
	}
	
	public void crawlerManagerTabPress(ActionEvent event) {
		checkToggle(TabType.CRAWLERMANAGER);
	}
	
	public void openArticleView() {
		checkToggle(TabType.ARTICLEVIEW);
	}
	
	public void loadArticle(List<Parent> articleRootList) {
		tabContents.replace(TabType.ARTICLEVIEW, articleRootList);
		System.out.println(undoStack);
	}
	
	private void checkToggle(TabType currentTabType) {
		if(currentTabState == currentTabType) return;
		redoStack.clear();
		EnumSet<TabType> clickable = EnumSet.of(TabType.ARTICLE, TabType.TWITTER, TabType.FACEBOOK, TabType.CRAWLERMANAGER, TabType.SETTING);
		EnumSet<TabType> nonClickable = EnumSet.of(TabType.ARTICLEVIEW, TabType.SEARCHRESULT);
		if(clickable.contains(currentTabType) && clickable.contains(currentTabState)) {
			buttonTypeHashMap.get(currentTabState).getStyleClass().set(0, "side-bar-button");
			buttonTypeHashMap.get(currentTabType).getStyleClass().set(0, "side-bar-button-selected");
			currentTabState = currentTabType;
			undoStack.push(currentTabType);
		}else {
			currentTabState = currentTabType;
			undoStack.push(currentTabType);
		}
		
		reloadView();
	}
	
	public void reloadPress(ActionEvent event) {
		reload();
	}
	
	public void reloadView() {
		contentVBox.getChildren().setAll(tabContents.get(undoStack.peek()));
		if(contentVBox.getChildren().isEmpty()) {
			reload();
			return;
		}
	}
	
	public void reload() {
		playLoadAnimation();
		contentVBox.getChildren().clear();
		Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
            	
        		switch(currentTabState) {
        		case ARTICLE:{
        			DisplayList.toggleDynamicUpdate(false);
        	    	CNBCCrawler cnbcCrawler = new CNBCCrawler();
        	    	Blockchain101Crawler blockchain101Crawler = new Blockchain101Crawler();
        	    	DisplayList.getArticleList().setAll(cnbcCrawler.getListFromJson());
        	    	DisplayList.getArticleList().addAll(blockchain101Crawler.getListFromJson());
        	    	DisplayList.toggleDynamicUpdate(true);
        			int count = 0;
        			List<Parent> newscardPanes = new ArrayList<>();
        			for(Article article : DisplayList.getArticleList()) {
        				FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXML/NewsCard.fxml"));
            			Parent newsCardPane;
        				try {
        					newsCardPane = loader.load();
        					NewsCardController controller = loader.getController();
        	    			controller.setImage("");
        	    			controller.setArticle(article);
        	    			newscardPanes.add(newsCardPane);
        	    			count++;
        	        		if(count == 10) break;
        				} catch (IOException e) {
        					e.printStackTrace();
        				}
                		
        			}
        			tabContents.replace(TabType.ARTICLE, newscardPanes);
        			break;
        		}
        			
        		case CRAWLERMANAGER:{
        			try {
        	            // Load the FXML file for the new window
        	            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXML/CrawlerManager.fxml"));
        	            Parent root = loader.load();
        	            CrawlerManagerController controller = loader.getController();
        	            controller.setWidth(scrollPane);
        	            List<Parent> roots = new ArrayList<>();
        	            roots.add(root);
        	            tabContents.replace(TabType.CRAWLERMANAGER, roots);
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
        			List<Parent> posts = new ArrayList<>();
        			for(Facebook post : DisplayList.getPostList()) {
        				FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXML/FacebookPost.fxml"));
            			Parent postVBox;
        				try {
        					postVBox = loader.load();
        					PostController controller = loader.getController();
        	    			controller.setData(post);
        	    			posts.add(postVBox);
        	    			System.out.println(count);
        	    			count++;

        	        		if(count == 10) break;
        				} catch (IOException e) {
        					e.printStackTrace();
        				}
        			}
        			tabContents.replace(TabType.FACEBOOK, posts);
        			break;
        		case SETTING:
        			break;
        		case TWITTER:
        			break;
        		default:
        			break;
        		}
				return null;
        		
            }
        };
        Thread thread = new Thread(task);
        thread.start();
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
            	Platform.runLater(()->{
            		stopLoadAnimation();
            		contentVBox.getChildren().setAll(tabContents.get(undoStack.peek()));
            	});
            	
            }
        });
        
	}
	
	public void playLoadAnimation() {
        rotateLoadTransition.play();
	}
	
	public void stopLoadAnimation() {
		rotateLoadTransition.stop();
		reloadImageView.setRotate(0);
	}
	
	
	
	public void setTabState(TabType tabType) {
		currentTabState = tabType;
	}

}
