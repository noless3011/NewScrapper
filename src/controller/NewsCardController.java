package controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class NewsCardController {
	
	@FXML
	private TextArea titleText;
	
	@FXML
	private ImageView newsImage;
	
	@FXML
	public void initialize() { 
		Rectangle clip = new Rectangle(newsImage.getFitWidth(), newsImage.getFitHeight());
		clip.setArcWidth(35);
		clip.setArcHeight(35);
		newsImage.setClip(clip);
		
	}
	
	public void setTitle(String title) {
		titleText.setText(title);
	}
	
	public void setImage(String url) {
		if(url.isEmpty()) {
			url = "https://upload.wikimedia.org/wikipedia/commons/b/bc/Flabellina-300x200.jpg";
		}
		try {
			Image image = new Image(new URI(url).toString());
			newsImage.setImage(image);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}