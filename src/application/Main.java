package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("mainMenu.fxml"));
			Parent root = (Parent) loader.load();
			Scene scene = new Scene(root, 1366, 768);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setResizable(false); // changes gui to non-resizable.

			primaryStage.setOnCloseRequest(event -> { // allows for alert to pop up upon closing window.
				event.consume();
				closeWindow(primaryStage);

			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/** Pop up confirmation to close the window */
	public void closeWindow(Stage stage) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Close Game"); // setting text for exit message
		alert.setHeaderText("You're about to leave!");
		alert.setContentText("Are you sure you want to leave");

		if (alert.showAndWait().get() == ButtonType.OK) {
			stage.close();
		}

	}

	public static void main(String[] args) {
		launch(args);
	}
}
