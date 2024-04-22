package controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void openArticle(MouseEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXML/NewsViewer.fxml"));
			Parent root = loader.load();
			NewsViewerController controller = loader.getController();
			controller.setArticle(article);
			
	        // Create a new stage
	        Stage newStage = new Stage();
	        // Set the scene of the new stage with the loaded FXML
	        newStage.setScene(new Scene(root));

	        // Show the new stage
	        newStage.show();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
	}
}