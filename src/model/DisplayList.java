package model;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
//A singleton class of article list to represent all articles that is displaying on the screen
public class DisplayList {
	private static boolean dynamicUpdateToggle = true;
	private static ObservableList<Article> articleList = FXCollections.observableArrayList();
	private static ObservableList<Tweet> tweetList = FXCollections.observableArrayList();
	private static ObservableList<Facebook> postList = FXCollections.observableArrayList();
	private static ObservableList<Object> searchResultList = FXCollections.observableArrayList();
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
		searchResultList.addListener((ListChangeListener.Change<? extends Object> change) -> {
			refreshView(change);
		});
	}
	
	private void refreshView(ListChangeListener.Change<? extends Object> change) {
		if(dynamicUpdateToggle) {
			while(change.next()) {
				if(change.wasAdded() || change.wasRemoved()) {
					// TODO: implement dynamic reload in the future
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
	public static ObservableList<Object> getSearchResultList() {
		return searchResultList;
	}

	public static void toggleDynamicUpdate(boolean state) {
		dynamicUpdateToggle = state;
	}
}
