package controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

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
