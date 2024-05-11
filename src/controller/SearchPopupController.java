package controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.queryparser.classic.ParseException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.DisplayList;
import searchengine.AdvanceSearch;
import searchengine.AdvanceSearch.SortOptionArticle;
import searchengine.DateRange;
import searchengine.Index;



public class SearchPopupController {
	private static boolean indexed = false;
	private final LocalDateTime MINTIME = LocalDateTime.of(2015, 1, 1, 0, 0);
	public enum SearchOption{
		FACEBOOK, ARTICLES, TWITTER
	}
	
	public enum Field{
		TITLE, AUTHOR, CONTENT
	}
	private SearchOption option;
	
	@FXML
	private VBox vBox;
	
	@FXML
	private Button searchButton;
	
	@FXML
	private DatePicker startDatePicker;
	
	@FXML
	private DatePicker endDatePicker;
	
	@FXML
	private Button indexButton;
	
	
	private Map<Field, SearchCardController> controllers = new HashMap<>();
	
	public void setSearchOption(SearchOption option) {
		this.option = option;
	}
	
	public void addSearchField(Field field) {
		
		try {
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXML/SearchCard.fxml"));
			AnchorPane root = loader.load();
			SearchCardController newSearchCardController = loader.getController();
			controllers.put(field ,newSearchCardController);
			newSearchCardController.setName(field.name());
			
			vBox.getChildren().add(0, root);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void indexPress(ActionEvent event) {
		try {
			Index.indexAll();
			Index.indexArticle();
			Index.indexFacebook();
			Index.indexTweet();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void searchPress(ActionEvent event) {
		if(!indexed) {
			try {
				Index.indexAll();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			indexed = true;
		}
		switch(option) {
		case ARTICLES:{
			String searchTokenTitle = controllers.get(Field.TITLE).getSearchToken();
			String searchTokenAuthor = controllers.get(Field.AUTHOR).getSearchToken();
			String searchTokenContent = controllers.get(Field.CONTENT).getSearchToken();
			LocalDateTime start;
			LocalDateTime end;
			LocalDate ldStart = startDatePicker.getValue();
			LocalDate ldEnd = endDatePicker.getValue();
			if(ldStart == null && ldEnd == null) {
				start = MINTIME;
				end = LocalDateTime.now();
			}else if(ldStart == null && ldEnd != null) {
				start = MINTIME;
				end = ldEnd.atStartOfDay();
			}else if(ldStart != null && ldEnd == null) {
				start = ldStart.atStartOfDay();
				end = LocalDateTime.now();
			}else {
				start = ldStart.atStartOfDay();
				end = ldEnd.atStartOfDay();
			}
			DateRange dateRange = new DateRange(start, end);
			try {
				
				DisplayList.getSearchResultList().setAll(AdvanceSearch.searchAdvanceArticle(
						searchTokenTitle,searchTokenAuthor, 
						dateRange , searchTokenContent,
						SortOptionArticle.RELEVANT, false)); 
				MainControllerSingleton.getMainController().showSearchResult(MainControllerSingleton.getMainController().searchResultToParents(DisplayList.getSearchResultList()));
				MainControllerSingleton.getMainController().reloadView();
				
			} catch (ParseException | IOException e) {
				e.printStackTrace();
			}
		}
		case FACEBOOK:{
			
		}
		case TWITTER:{
			
		}
		}
	}
}
