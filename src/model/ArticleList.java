package model;

import java.util.List;

//A singleton class of article list to represent all articles that is displaying on the screen
public class ArticleList {
	private static List<Article> articleList;

	private ArticleList() {

	}

	public static List<Article> getList() {
		return articleList;
	}
}
