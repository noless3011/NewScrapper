package controller;

import java.time.LocalDateTime;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import model.Article;
import model.Content;

public class NewsViewerController {
	
    @FXML
    private ImageView image;
	
	@FXML
	private Label titleLabel;
	
	@FXML
	private Label tagLabel;
	
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
		setTag("    Tag: " + article.getTags());
		setTitle(article.getTitle());
		setAuthor("  Author: " + article.getAuthor());
		setDate(article.getPublishedAt());
		
		// Lấy ra content của bài viết
		Content content = article.getContent();
    	setContent(content);  
    	
    	// Tạo luồng load ảnh
        Thread thread = new Thread(() -> {
            String[] parts = content.toString().split("\\{url=");
        	String imgURL = "";
        	imgURL = parts[1].split(", description=")[0];
        	
            if(!imgURL.isEmpty()) {
            	setImg(imgURL);
            }
        });
        thread.start();
	}
	
	void setImg(String imgUrl) {
		if (!imgUrl.isEmpty()) {
			image.setImage(new Image(imgUrl));	
		}
	}
	
	void setTag(String tags) {
		tagLabel.setText(tags);
	}
}
