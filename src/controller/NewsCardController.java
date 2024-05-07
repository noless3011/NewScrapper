package controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import model.Article;


public class NewsCardController {
	
	private Article article;
	
	@FXML
	private Label titleText;
	
	@FXML
	private Label infoText;
	
	@FXML
	private ImageView newsImage;
	
	@FXML
	public void initialize() { 
		Rectangle clip = new Rectangle(newsImage.getFitWidth(), newsImage.getFitHeight());
		clip.setArcWidth(35);
		clip.setArcHeight(35);
		newsImage.setClip(clip);
		
	}
	
	public void setArticle(Article article) {
		this.article = article;
		setTitle();
	}
	
	public void setTitle() {
		titleText.setText(article.getTitle());
	}
	
	public void setInfo() {
		//infoText.setText(article.getContent().toString().substring(0, 100) + "...");
	}
	
	public void setImage(String url) {
		if(url.isEmpty()) {
			url = "https://www.musafir.com/SFImage/Images/650x200-rahhalah-2.jpg";
		}
		try {
			Image image = new Image(new URI(url).toString());
			newsImage.setImage(image);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		
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