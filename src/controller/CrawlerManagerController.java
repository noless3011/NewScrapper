package controller;

import java.io.IOException;
import java.util.List;

import crawler.CNBCCrawler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import model.Article;

public class CrawlerManagerController {
	@FXML
	Button addButton;
	
	
	@FXML
	VBox crawlerVBox;
	
	public void addCrawler(ActionEvent event) throws IOException{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXML/CrawlerCard.fxml"));
		
		try {
			AnchorPane root = loader.load();
			CrawlerCardController cardController = loader.getController();
			
			cardController.setManager(this);
			String indexString = Integer.toString(cardController.counter);
			cardController.setLabel("Crawler " + indexString);
			CNBCCrawler cnbcCrawler = new CNBCCrawler();
			cardController.setCrawler(cnbcCrawler);
			crawlerVBox.getChildren().add(0, root);
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
