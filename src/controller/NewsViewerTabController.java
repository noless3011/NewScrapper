package controller;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import model.Article;

public class NewsViewerTabController {
	@FXML
	private TabPane tabPane;
	
	private List<Tab> tabList;
	
	void addTab(Article article) {
		Tab tab = new Tab(article.getTitle());
		FXMLLoader newsViewerLoader = new FXMLLoader(getClass().getResource("/View/FXML/NewsViewer.fxml"));
		NewsViewerController controller = newsViewerLoader.getController();
		AnchorPane newsViewerRoot;
		try {
			newsViewerRoot = newsViewerLoader.load();
			controller.setArticle(article);
			tab.setContent(newsViewerRoot);
			tabPane.getTabs().add(0, tab);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
