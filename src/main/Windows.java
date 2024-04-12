package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.MyException;

import java.io.IOException;

import controller.MainController;


public class Windows extends Application{
	
	@Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/FXML/Main.fxml"));
            Parent root = loader.load();

            // Create the Scene
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/View/CSS/test.css").toExternalForm());

            MainController controller = loader.getController();
            // Set the Scene to the Stage
            primaryStage.setScene(scene);
            primaryStage.setTitle("JavaFX Application");

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();  
        }
    }
	public static void main(String[] args) {
		launch(args);

	}

}
