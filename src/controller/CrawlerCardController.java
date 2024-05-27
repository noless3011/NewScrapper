package controller;

import java.io.IOException;

import datamanager.crawler.ICrawler;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class CrawlerCardController {
	private int id;
	
	private Thread thread;
	
	public static int counter = 0;
	
	
	private CrawlerManagerController managerController;
	private ICrawler crawler;
	
	@FXML
	private Button runButton;
	
	@FXML
	private TextField articleNumberField;
	
	@FXML
	private Button deleteButton;
	
	@FXML
	private Label crawlerLabel;
	
	@FXML
	private ProgressBar progressBar;
	
	@FXML
	private AnchorPane root;
	
	public CrawlerCardController() {
		this.id = ++counter;
	}
	
	public void setWidth(AnchorPane warper) {
		root.prefWidthProperty().bind(warper.widthProperty());
	}
	
	public int getID(){
		return id;
	}
	public void deleteCrawlerPress(ActionEvent event) throws IOException{
		managerController.deleteCrawler(id);
	}
	
	public void runCrawlerPress(ActionEvent event) throws IOException{
		runCrawler();
	}
	
	void runCrawler() {
		int amount;
		try {
			amount = Integer.parseInt(articleNumberField.getText()); 
		} catch (NumberFormatException e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning");
	        alert.setContentText("Please enter a number");
	        alert.showAndWait();
	        return;
		}
		
		Task<Void> task = new Task<Void>() {
			
			@Override
			protected Void call() throws Exception {
				crawler.crawlList(amount, (progress) ->{
					updateProgress(progress, amount);
				});
				return null;
			}
			
			
		};
		progressBar.progressProperty().bind(task.progressProperty());
		thread = new Thread(task);
		thread.start();
	}
	
	public void setCrawler(ICrawler crawler ) {
		this.crawler = crawler;
		
	}

	public void setLabel(String s) {
		crawlerLabel.setText(s);
	}
	public void setManager(CrawlerManagerController managerController) {
		this.managerController = managerController;
	}
	
	public void stopThread() {
		if(thread != null) {
			thread.interrupt();
			crawler.saveToJson();
			
		}	
	}
}
