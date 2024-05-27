package controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

public class CrawlerManagerController {
	@FXML
	Button addButton;
	
	@FXML
	VBox crawlerVBox;
	
	@FXML
	AnchorPane root;
	
	@FXML
	AnchorPane crawlersAnchor;
	
	@FXML
	AnchorPane addButtonAnchor;
	
	Popup popup = new Popup();
	
	public void runAllPress(ActionEvent event) throws IOException{
		for(Node node : crawlerVBox.getChildren()) {
			CrawlerCardController controller = (CrawlerCardController)node.getUserData();
			controller.runCrawler();
		}
	}
	
	public void addCrawler(ActionEvent event) throws IOException{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXML/CrawlerTypeSelector.fxml"));
		
		try {
			AnchorPane root = loader.load();
			CrawlerTypeSelectorController typeController = loader.getController();
			
			typeController.createButtons(this, crawlerVBox);
			popup.getContent().add(root);
			
			popup.setAutoHide(true);
			
			popup.setX(addButton.localToScreen(addButton.boundsInLocalProperty().get()).getMinX());
			popup.setY(addButton.localToScreen(addButton.boundsInLocalProperty().get()).getMaxY());
            popup.show(addButton.getScene().getWindow());
      
			
			
			
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
					cardController.stopThread();
					
					break;
				}
				nodeIndex++;
			}
		}
		crawlerVBox.getChildren().remove(nodeIndex);
	}
	
	public void setWidth(ScrollPane scrollPane) {
		root.prefWidthProperty().bind(scrollPane.widthProperty().subtract(15));
		crawlersAnchor.prefWidthProperty().bind(root.widthProperty().subtract(25));
		addButtonAnchor.prefWidthProperty().bind(crawlersAnchor.widthProperty());
	}
	
	public double getWidth() {
		return root.getWidth();
	}
}
