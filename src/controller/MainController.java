package controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainController{
	@FXML
	private ScrollPane newsScroll;
	
	@FXML
	private AnchorPane root;
	
    @FXML
    private TextField searchBar;
    
    @FXML
    private AnchorPane newsAnchor;
    
    @FXML
    private VBox newsVBox;
    
    

	public void refresh(ActionEvent event) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/FXML/NewsCard.fxml"));
		try {
			Pane cardRoot = fxmlLoader.load();
			NewsCardController cardController = fxmlLoader.getController();
			cardController.setTitle("abc");
			newsVBox.getChildren().add(cardRoot);
;		} catch (IOException e) {
			e.printStackTrace();
		}
				
	}
	
    
    

}
