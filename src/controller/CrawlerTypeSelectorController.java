package controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import crawler.Blockchain101Crawler;
import crawler.CNBCCrawler;
import crawler.FacebookCrawler;
import crawler.ICrawler;
import crawler.TwitterCrawler;
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
import model.Article;

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
		crawlerMap.put("Facebook crawler", this::addFacebookCrawler);
		crawlerMap.put("Blockchain101 crawler", this::addBlockchain101Crawler);
		crawlerMap.put("Twitter crawler", this::addTwitterCrawler);
		//crawlerMap.put(" crawler", this::addCrawler);
		
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
	
	private void addBlockchain101Crawler(ActionEvent event) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXML/CrawlerCard.fxml"));
		
		try {
			AnchorPane root = loader.load();
			CrawlerCardController cardController = loader.getController();
			
			cardController.setManager(managerController);
			String indexString = Integer.toString(CrawlerCardController.counter);
			cardController.setLabel("Crawler " + indexString);
			Blockchain101Crawler crawler = new Blockchain101Crawler();
			cardController.setCrawler(crawler);
			crawlerVBox.getChildren().add(0, root);
			closeMenu();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void addTwitterCrawler(ActionEvent event) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXML/CrawlerCard.fxml"));
		
		try {
			AnchorPane root = loader.load();
			CrawlerCardController cardController = loader.getController();
			
			cardController.setManager(managerController);
			String indexString = Integer.toString(CrawlerCardController.counter);
			cardController.setLabel("Crawler " + indexString);
			TwitterCrawler crawler = new TwitterCrawler();
			cardController.setCrawler(crawler);
			crawlerVBox.getChildren().add(0, root);
			closeMenu();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void addFacebookCrawler(ActionEvent event) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXML/CrawlerCard.fxml"));
		
		try {
			AnchorPane root = loader.load();
			CrawlerCardController cardController = loader.getController();
			
			cardController.setManager(managerController);
			String indexString = Integer.toString(CrawlerCardController.counter);
			cardController.setLabel("Crawler " + indexString);
			FacebookCrawler facebookCrawler = new FacebookCrawler();
			cardController.setCrawler(facebookCrawler);
			crawlerVBox.getChildren().add(0, root);
			closeMenu();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void addCNBCCrawler(ActionEvent event){
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXML/CrawlerCard.fxml"));
				
		try {
			AnchorPane root = loader.load();
			CrawlerCardController cardController = loader.getController();
			
			cardController.setManager(managerController);
			String indexString = Integer.toString(CrawlerCardController.counter);
			cardController.setLabel("Crawler " + indexString);
			ICrawler<Article> cnbcCrawler = new CNBCCrawler() ;
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
