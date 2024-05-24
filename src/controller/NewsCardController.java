package controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import model.Article;


public class NewsCardController {
	
	private Article article;
	
	@FXML
	private Label titleText;
	
	@FXML
	private Label authorLabel;
	
	@FXML
	private Label sourceUrl;
	
	@FXML
	private Label contentText;
	
	@FXML
	private Label publishedTime;
	
	
	public void setArticle(Article article) {
		this.article = article;
		setDetails();
		
	}
	
	public void setDetails() {
		sourceUrl.setText(article.getSourceUrl());
		titleText.setText(article.getTitle());
		contentText.setText(article.getContent().toString());
		publishedTime.setText("Published at: " + article.getPublishedAt().toString());
		authorLabel.setText("Author: " + article.getAuthor());
	}
		
	
	public void openArticle(MouseEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXML/NewsViewer.fxml"));
			Parent root = loader.load();
			NewsViewerController controller = loader.getController();
			controller.setArticle(article);
			List<Parent> articleElementList = new ArrayList<>();
			articleElementList.add(root);
	        MainControllerSingleton.getMainController().loadArticle(articleElementList);
	        MainControllerSingleton.getMainController().openArticleView();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
	}
}