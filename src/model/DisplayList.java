package model;

import controller.MainControllerSingleton;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
//A singleton class of article list to represent all articles that is displaying on the screen
public class DisplayList {
	private static boolean dynamicUpdateToggle = true;
	private static ObservableList<Article> articleList = FXCollections.observableArrayList();
	private static ObservableList<Tweet> tweetList = FXCollections.observableArrayList();
	private static ObservableList<Facebook> postList = FXCollections.observableArrayList();
	static {
		new DisplayList();
	}
	private DisplayList() {
		
		articleList.addListener((ListChangeListener.Change<? extends Article> change) -> {
			refreshView(change);
		});
		tweetList.addListener((ListChangeListener.Change<? extends Article> change) -> {
			refreshView(change);
		});
		postList.addListener((ListChangeListener.Change<? extends Article> change) -> {
			refreshView(change);
		});
	}
	
	private void refreshView(ListChangeListener.Change<? extends Article> change) {
		if(dynamicUpdateToggle) {
			while(change.next()) {
				if(change.wasAdded() || change.wasRemoved()) {
					MainControllerSingleton.getMainController().refreshView();
				}
			}
		}
	}
	
	public static ObservableList<Article> getArticleList(){
		return articleList;
	}
	public static ObservableList<Tweet> getTweetList(){
		return tweetList;
	}
	public static ObservableList<Facebook> getPostList(){
		return postList;
	}
	public static void toggleDynamicUpdate(boolean state) {
		dynamicUpdateToggle = state;
	}
}
