package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

import adapter.ResizeHelper;
import controller.MainController;
import controller.MainControllerSingleton;


public class Windows extends Application{
	
	@Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXML/MainWindow.fxml"));
            Parent root = loader.load();

            // Create the Scene
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/View/CSS/MainWindow.css").toExternalForm());

            MainControllerSingleton.setController(loader.getController());
            MainControllerSingleton.getMainController().setupController(primaryStage);
            System.out.println("start main");
            MainControllerSingleton.getMainController().reload();
            // Set the Scene to the Stage
            primaryStage.setScene(scene);
            primaryStage.setTitle("JavaFX Application");
            primaryStage.initStyle(StageStyle.UNDECORATED);
            
            ResizeHelper resizeHelper = new ResizeHelper();
            resizeHelper.addResizeListener(primaryStage, 800, 500, 10000, 10000);
            primaryStage.setHeight(501);
            primaryStage.setWidth(801);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();  
        }
    }
	public static void main(String[] args) {
		launch(args);

	}

}
