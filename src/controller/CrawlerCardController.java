package controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CrawlerCardController {
	private int id;
	
	public static int counter = 0;
	
	
	private CrawlerManagerController managerController;
	
	@FXML
	private Button runButton;
	
	@FXML
	private TextField articleNumberField;
	
	@FXML
	private Button deleteButton;
	
	@FXML
	private Label crawlerLabel;
	
	public CrawlerCardController() {
		this.id = ++counter;
	}
	
	public int getID(){
		return id;
	}
	public void deleteCrawlerPress(ActionEvent event) throws IOException{
		managerController.deleteCrawler(id);
	}
	
	public void runCrawlerPress(ActionEvent event) throws IOException{
		
	}
	
	public void setLabel(String s) {
		crawlerLabel.setText(s);
	}
	public void setManager(CrawlerManagerController managerController) {
		this.managerController = managerController;
	}
}
