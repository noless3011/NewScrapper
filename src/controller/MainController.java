package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.Semaphore;

import org.apache.lucene.queryparser.classic.ParseException;

import controller.MainController.TabType;
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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Callback;
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
	// Hash Map này dùng để chứa các nội dung được hiển thị trong phần nội dung để tiết kiệm bộ nhớ
	private HashMap<TabType, List<Parent>> tabContents = new HashMap<>();
	// Map dùng để xác định loại tab của nút bấm chuyển tab
	private HashMap<TabType, Button> buttonTypeHashMap = new HashMap<>();
	// Stack dùng để implement nút back, forward
	private Stack<TabType> undoStack = new Stack<TabType>();
	private Stack<TabType> redoStack = new Stack<TabType>();
	// Tab đang được hiển thị hiện tai
	private TabType currentTabState = TabType.ARTICLE;
	
	
	//Vị trí của cửa sổ trên màn hình
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
	
	
	private VBox contentVBox = new VBox();
	
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
	
	private int newsPerPage = 13;
	
	//Popup
	private Popup advanceSearchPopup = new Popup();
	
	// Hàm này để setup phần chia trang
	public void setupPagination() {
		
		pagination.setPageFactory(new Callback<Integer, Node>() {
			
			@Override
			public Node call(Integer index) {
				//set element bên trong pagiantion bằng vbox chứa content
				reload(index);
				return contentVBox;
			}
		});
	}
	
	public void setupController(Stage stage) {
		primaryStage = stage;
		List<Parent> empty = new ArrayList<>();
		undoStack.push(currentTabState);
		
		//setup bảng để ánh xạ từ element tới loại content
		tabContents.put(TabType.ARTICLE, empty);
		tabContents.put(TabType.FACEBOOK, empty);
		tabContents.put(TabType.TWITTER, empty);
		tabContents.put(TabType.CRAWLERMANAGER, empty);
		tabContents.put(TabType.SETTING, empty);
		tabContents.put(TabType.SEARCHRESULT, empty);
		tabContents.put(TabType.ARTICLEVIEW, empty);
		//cố định Anchor pane chứa phần nội dung với cửa sổ
		contentAnchor.prefWidthProperty().bind(scrollPane.widthProperty().subtract(15));
		
		//Setup bảng ánh xạ từ loại nút tới nút
		buttonTypeHashMap.put(TabType.ARTICLE, articleButton);
		buttonTypeHashMap.put(TabType.TWITTER, twitterButton);
		buttonTypeHashMap.put(TabType.FACEBOOK, facebookButton);
		buttonTypeHashMap.put(TabType.CRAWLERMANAGER, crawlerManagerButton);
		buttonTypeHashMap.put(TabType.SETTING, settingButton);
		
		//setup animation loading
		rotateLoadTransition = new RotateTransition(Duration.seconds(0.5), reloadImageView);
		rotateLoadTransition.setByAngle(-360); // Rotate 360 degrees
        rotateLoadTransition.setCycleCount(Animation.INDEFINITE);
        rotateLoadTransition.setInterpolator(Interpolator.LINEAR);
        
        
        //Do ứng dụng dùng cửa sổ undecorated và custom title bar nên ta phải tự làm hệ thống di chuyển cửa sổ 
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
		//setup event bấm enter của search field
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
		// Load file fxml
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXML/SearchPopup.fxml"));
		
		try {
			
			// Ẩn popup advancde search nếu nó đang hiện
			if(advanceSearchPopup.isShowing()) {
				advanceSearchPopup.hide();
				advanceSearchPopup.getContent().clear();
				return;
			}
			
			AnchorPane root = loader.load();
			SearchPopupController advanceSearchController = loader.getController();
			advanceSearchPopup = new Popup();
			List<Field> fields = new ArrayList<>();
			
			//Kiểm tra xem đang ở tab nào và hiện phần tìm kiếm tương ứng
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
			// Add các field tìm kiếm vào popup
			for(Field field : fields) {
				advanceSearchController.addSearchField(field);
			}
			
			
			advanceSearchPopup.getContent().add(root);
			
			advanceSearchPopup.setAutoHide(true);
			//setup kích thước của popup tìm kiếm
			advanceSearchPopup.setX(primaryStage.getX() + primaryStage.getWidth()/2 - 100);
			advanceSearchPopup.setY(primaryStage.getY() + 70);
			advanceSearchPopup.show(advanceSearchButton.getScene().getWindow());
      
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Hàm này để chuyển từ list kết quả tìm kiếm sang javafx elements
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
	// Hàm này chạy để kiểm tra xem có bấm lại vào tab cũ không (VD: đang ở tab Article mà lại bấm vào Article tiếp)
	private void checkToggle(TabType currentTabType) {
		//Kiểm tra
		if(currentTabState == currentTabType) return;
		// Nếu không bấm lại tab cũ thì sẽ chuyển qua tab được truyền vào bên trên
		redoStack.clear();
		EnumSet<TabType> clickable = EnumSet.of(TabType.ARTICLE, TabType.TWITTER, TabType.FACEBOOK, TabType.CRAWLERMANAGER, TabType.SETTING);
		// Do kiểu tab còn 2 kiểu không thể vào qua các nút chuyển tab là Article View và Search Result nên ta phải kiểm tra xem 
		// Nếu là kiểu tab có nút để chuyển thì highlight nút đó lên
		if(clickable.contains(currentTabType) && clickable.contains(currentTabState)) {
			buttonTypeHashMap.get(currentTabState).getStyleClass().set(0, "side-bar-button");
			buttonTypeHashMap.get(currentTabType).getStyleClass().set(0, "side-bar-button-selected");
		}
		currentTabState = currentTabType;
		undoStack.push(currentTabType);
		
		reloadView();
	}
	
	public void reloadPress(ActionEvent event) {
		reload(pagination.getCurrentPageIndex());
	}
	// Đây là hàm đùng để reload view và hiển thị search result
	public void reloadView() {
		contentVBox.getChildren().setAll(tabContents.get(undoStack.peek()));
		if(contentVBox.getChildren().isEmpty()) {
			reload(pagination.getCurrentPageIndex());
			return;
		}
	}
	// Task dùng để reload view 
	public class LoadViewTask extends Task<Void>{
		private int startIndex, endIndex;
		 private final Semaphore semaphore = new Semaphore(1);
		
		public LoadViewTask(int start, int end) {
			startIndex = start;
			endIndex = end;
			this.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
	            @Override
	            public void handle(WorkerStateEvent event) {
	            	Platform.runLater(()->{
	            		stopLoadAnimation();
	            		
	            	});
            		contentVBox.getChildren().setAll(tabContents.get(undoStack.peek()));
	            }
	        });
		}
		@Override
		protected Void call() throws Exception{
			try {
				 semaphore.acquire();
				 switch(currentTabState) {
		    		case ARTICLE:{
		    			List<Parent> newscardPanes = new ArrayList<>();
		    			for(int i = startIndex; i < endIndex; i++) {
		    				FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXML/NewsCard.fxml"));
		        			Parent newsCardPane;
		    				try {
		    					newsCardPane = loader.load();
		    					NewsCardController controller = loader.getController();
		    	    			controller.setArticle(DisplayList.getArticleList().get(i));
		    	    			newscardPanes.add(newsCardPane);
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
		    			int count = 0;
		    			List<Parent> posts = new ArrayList<>();
		    			for(int i = startIndex; i < endIndex; i++) {
		    				FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXML/facebookpost.fxml"));
		        			Parent postVBox;
		    				try {
		    					postVBox = loader.load();
		    					PostController controller = loader.getController();
		    	    			controller.setData(DisplayList.getPostList().get(i));
		    	    			posts.add(postVBox);
		    	    			
		    				} catch (IOException e) {
		    					e.printStackTrace();
		    				}
		    			}
		    			tabContents.replace(TabType.FACEBOOK, posts);
		    			break;
		    		case SETTING:
		    			break;
		    		case TWITTER:
		    			int counttwitter = 0;
		    			List<Parent> twitter_posts = new ArrayList<>();
		    			for(int i = startIndex; i < endIndex; i++) {
		    				FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXML/TwitterPost.fxml"));
		        			Parent postVBox;
		    				try {
		    					postVBox = loader.load();
		    					PostController controller = loader.getController();
		    	    			controller.setData(DisplayList.getTweetList().get(i));
		    	    			twitter_posts.add(postVBox);
		    	    			System.out.println(counttwitter);
		    	    			counttwitter++;

		    	        		if(counttwitter == 10) break;
		    				} catch (IOException e) {
		    					e.printStackTrace();
		    				}
		    			}
		    			tabContents.replace(TabType.TWITTER, twitter_posts);
		    			break;
		    		default:
		    			break;
		    		}
					return null;
			}catch (InterruptedException e) {
	            e.printStackTrace();
	        } finally {
	            semaphore.release(); // Release the permit
	            
	        }
			return null;
			
		}
	}
	
	// Task này để load các list từ file json lên các Display list (Display list là list các article, post, tweet để hiển thị lên màn hình)
	public class LoadListTask extends Task<Void> {
		// Semaphore dùng để đảm bảo chỉ có 1 task load chạy tại 1 thời điểm
		private final Semaphore semaphore = new Semaphore(1);
		public LoadListTask(LoadViewTask loadViewTask) {
	        this.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent arg0) {
					Thread loadViewThread = new Thread(loadViewTask);
					loadViewThread.start();
				}
			});
		}
        @Override
        protected Void call() throws Exception {
        	try {
        		semaphore.acquire();
        		switch(currentTabState) {
        		case ARTICLE:{
        			DisplayList.toggleDynamicUpdate(false);
        	    	CNBCCrawler cnbcCrawler = new CNBCCrawler();
        	    	Blockchain101Crawler blockchain101Crawler = new Blockchain101Crawler();
        	    	DisplayList.getArticleList().setAll(cnbcCrawler.getListFromJson());
        	    	DisplayList.getArticleList().addAll(blockchain101Crawler.getListFromJson());
        	    	DisplayList.toggleDynamicUpdate(true);
        			break;
        		}
        			
        		case CRAWLERMANAGER:{
        			break;
        		}
        		case FACEBOOK:
        			DisplayList.toggleDynamicUpdate(false);
        			FacebookCrawler facebookCrawler = new FacebookCrawler();
        	    	DisplayList.getPostList().setAll(facebookCrawler.getListFromJson());
        	    	DisplayList.toggleDynamicUpdate(true);
        			break;
        		case SETTING:
        			break;
        		case TWITTER:
        			DisplayList.toggleDynamicUpdate(false);
        			TwitterCrawler twitterCrawler = new TwitterCrawler();
        	    	DisplayList.getTweetList().setAll(twitterCrawler.getListFromJson());
        	    	DisplayList.toggleDynamicUpdate(true);
        			break;
        		default:
        			break;
        		}
	        }catch (InterruptedException e) {
	            e.printStackTrace();
	        } finally {
	            semaphore.release(); // Release the permit
	            
	        }
			return null;
        }
    };
	
	
	public void reload(int index) {
		playLoadAnimation();
		contentVBox.getChildren().clear();
		
        LoadViewTask loadViewTask = new LoadViewTask(index * newsPerPage, index * newsPerPage + newsPerPage);
        LoadListTask loadListTask = new LoadListTask(loadViewTask);

        Thread thread = new Thread(loadListTask);
        thread.start();
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
