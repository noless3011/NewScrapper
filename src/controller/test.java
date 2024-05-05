package controller;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import model.Article;
import model.DisplayList;
import model.Content;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class test {

	public static void main(String[] args) {
		Content content = new Content("abc");
		LocalDateTime date = LocalDateTime.now();
		Article article = new Article("", "", content, date, "");
		List<Article> articles = new ArrayList<>();
		articles.add(article);
		articles.add(article);
		articles.add(article);
		articles.add(article);
		DisplayList.getArticleList().addAll(articles);
		System.out.print("end");
	}

}
