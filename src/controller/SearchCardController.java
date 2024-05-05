package controller;


import controller.SearchPopupController.Field;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class SearchCardController {
	@FXML
	private Label name;
	
	@FXML
	private TextField searchField;
	
	public void setName(String name) {
		this.name.setText(name);
	}
	
	public String getSearchToken() {
		return searchField.getText();
	}
}
