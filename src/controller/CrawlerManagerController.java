package controller;

import java.awt.event.FocusEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;

import crawler.CNBCCrawler;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Article;

public class CrawlerManagerController {
	@FXML
	Button addButton;
	
	
	@FXML
	VBox crawlerVBox;
	
	Popup popup;
	
	public void runAllPress(ActionEvent event) throws IOException{
		for(Node node : crawlerVBox.getChildren()) {
			CrawlerCardController controller = (CrawlerCardController)node.getUserData();
			controller.runCrawler();
		}
	}
	
	public void addCrawler(ActionEvent event) throws IOException{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXML/CrawlerTypeSelector.fxml"));
		
		try {
			if (popup == null) {
				popup = new Popup();
				
				AnchorPane root = loader.load();
				CrawlerTypeSelectorController typeController = loader.getController();
				
				typeController.createButtons(this, crawlerVBox);
				popup.getContent().add(root);
				
				
				
				addButton.getScene().setOnMouseClicked(e -> {
		            if (popup == null) return;
					if (!popup.isShowing()) {
						
						return;
					}
		            Bounds boundsInScreen = root.localToScreen(root.getBoundsInLocal());
		            if (popup.isShowing() && !boundsInScreen.contains(e.getX(), e.getY())) {
		            	popup.hide();
		            }
		        });
				
				popup.setX(addButton.localToScreen(addButton.boundsInLocalProperty().get()).getMinX());
				popup.setY(addButton.localToScreen(addButton.boundsInLocalProperty().get()).getMaxY());
                popup.show(addButton.getScene().getWindow());
                popup = null;
            } else {
                popup.hide();
                popup = null;
                return;
            }
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void deleteCrawler(int id) {
		int nodeIndex = 0;
		
		for(Node node : crawlerVBox.getChildren()) {
			if(node instanceof AnchorPane) {
				Object cardControllerObject = node.getUserData();
				CrawlerCardController cardController = (CrawlerCardController)cardControllerObject;
				if(id == cardController.getID()) {
					break;
				}
				nodeIndex++;
			}
		}
		crawlerVBox.getChildren().remove(nodeIndex);
	}
}
