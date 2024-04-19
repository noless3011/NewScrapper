package controller;

import java.io.IOException;
import java.util.List;

import crawler.CNBCCrawler;
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
import model.Article;

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
    
    

	public void openCrawlersManager(ActionEvent event) throws IOException {
		try {
            // Load the FXML file for the new window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXML/CrawlerManager.fxml"));
            Parent root = loader.load();

            // Create a new stage
            Stage newStage = new Stage();

            // Set the scene of the new stage with the loaded FXML
            newStage.setScene(new Scene(root));

            // Show the new stage
            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
    
    

}
