package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import crawler.CNBCCrawler;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

public class CrawlerTypeSelectorController{
	private Map<String, EventHandler<ActionEvent>> crawlerMap = new HashMap<>();
	private CrawlerManagerController managerController;
	private VBox crawlerVBox;
	
	@FXML
	private VBox crawlerTypeVBox;
	public void createButtons(CrawlerManagerController controller, VBox crawlerVBox){
		managerController = controller;
		this.crawlerVBox = crawlerVBox;
		
		crawlerMap.put("CNBC crawler", this::addCNBCCrawler);
		
		
		for(String crawler : crawlerMap.keySet()) {
			Button button = new Button();
			Scene scene = crawlerTypeVBox.getScene();
			
			button.getStyleClass().add("button");
			button.setText(crawler);
			button.setPrefHeight(25);
			button.setPrefWidth(250);
			button.setAlignment(Pos.CENTER);
			button.setOnAction(crawlerMap.get(crawler));	
			crawlerTypeVBox.getChildren().add(button);
			
		}
		
	}
	
	private void addCNBCCrawler(ActionEvent event){
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXML/CrawlerCard.fxml"));
				
		try {
			AnchorPane root = loader.load();
			CrawlerCardController cardController = loader.getController();
			
			cardController.setManager(managerController);
			String indexString = Integer.toString(cardController.counter);
			cardController.setLabel("Crawler " + indexString);
			CNBCCrawler cnbcCrawler = new CNBCCrawler();
			cardController.setCrawler(cnbcCrawler);
			crawlerVBox.getChildren().add(0, root);
			closeMenu();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void closeMenu() {
		Popup menu = (Popup)crawlerTypeVBox.getScene().getWindow();
		menu.hide();
	}
}
