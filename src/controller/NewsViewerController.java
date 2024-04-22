package controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import model.Article;
import model.Content;

public class NewsViewerController {
	@FXML
	private Label titleLabel;
	
	@FXML
	private Label authorLabel;
	
	@FXML
	private Label dateLabel;
	
	@FXML
	private VBox paragraphsVBox;
	
	void setContent(Content content) {

		for(int i = 0; i < content.getParagraphList().size(); i++ ) {
			Label paragraphLabel = new Label();
			paragraphLabel.getStyleClass().add("paragraph-label");
			paragraphLabel.setWrapText(true);
			paragraphLabel.setText(content.getParagraphList().get(i));
			paragraphsVBox.getChildren().addAll(paragraphLabel);
		}
		
	}
	
	void setTitle(String title) {
		titleLabel.setWrapText(true);
		titleLabel.setText(title);
	}
	void setAuthor(String author) {
		authorLabel.setText(author);
	}
	
	void setDate(LocalDateTime datetime) {
		dateLabel.setText(datetime.toString());
	}
	
	void setArticle(Article article) {
		setTitle(article.getTitle());
		setAuthor(article.getAuthor());
		setDate(article.getPublishedAt());
		setContent(article.getContent());
	}
}
