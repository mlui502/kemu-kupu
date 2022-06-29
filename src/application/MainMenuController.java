package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class MainMenuController {

	@FXML
	AnchorPane mainPane;
	@FXML
	Button gamesModuleBtn;
	@FXML
	Button leaderBoardBtn;
	@FXML
	Button quitBtn;
	@FXML
	VBox mainMenuScene;
	@FXML
	VBox topicSelectScene;
	@FXML
	ComboBox<String> topicSelector;
	@FXML
	Label selectTopicError;
	@FXML
	TextField usernameText;

	boolean practiseModule = false;

	/** Gives a drop down for the topics the user can play */

	@FXML
	public void topicSelect(ActionEvent event) {
		// This takes the user to select a Topic Scene
		if (((Button) event.getSource()).getText().equals("Practise Module")) {
			practiseModule = true;
		}

		mainMenuScene.setVisible(false);
		mainMenuScene.setDisable(true);

		topicSelectScene.setVisible(true);
		topicSelectScene.setDisable(false);

		usernameText.setOpacity(1);
		usernameText.setDisable(false);

		usernameText.setDisable(practiseModule);
		usernameText.setVisible(!practiseModule);

		topicSelector.getItems().addAll("Babies", "Colours", "Compass Points", "Days of the Week – Maori Original",
				"Days of the Week – English Derivative", "Engineering", "Feelings", "Months – Maori Original",
				"Months – English Derivative", "Software", "University Life", "Weather", "Work");

	}

	/** Pop up window for confirmation when quit has been selected*/

	public void quitBtnSelected(ActionEvent event) {
		Alert alert = new Alert(AlertType.CONFIRMATION); // opens alert when quit button is pressed.
		alert.setTitle("Close Game"); // setting text for exit message
		alert.setHeaderText("You're about to leave!");
		alert.setContentText("Are you sure you want to leave");

		if (alert.showAndWait().get() == ButtonType.OK) { // closes window if confirmation is selected on alert window.
			Stage stage = (Stage) mainPane.getScene().getWindow();
			stage.close();
		}

	}

	
	/** Changes the scene to the leaderboard scene as well as updates the scoreboard*/

	@FXML
	public void goToLeaderboard(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("leaderBoard.fxml"));
		Parent root = loader.load();

		GameOverController gameOver = loader.getController();
		gameOver.mainMenuLeaderBoard.setVisible(true);
		gameOver.mainMenuLeaderBoard.setDisable(false);
		gameOver.summaryLeaderBoard.setVisible(false);
		gameOver.summaryLeaderBoard.setDisable(true);

		gameOver.updateScoreBoard(gameOver);

		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root, 1366, 768);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setScene(scene);
		stage.show();
	}

	/** Starts a new game in the Main Game functionality, and determines if it is a practise module*/

	public void StartNewGame(ActionEvent event) throws IOException {
		if (topicSelector.getValue() == null) {
			selectTopicError.setVisible(true);
			return;
		}

		FXMLLoader loader = new FXMLLoader(getClass().getResource("NewGameScene.fxml"));
		Parent root = loader.load();

		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root, 1366, 768);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setScene(scene);
		stage.show();

		// Prompts user to Select a topic for the Quiz and sets wordList
		NewGameController NewGameController = loader.getController();
		NewGameController.setWordList(getWords(event), practiseModule);
		NewGameController.startGame(event);
		NewGameController.setUsername(usernameText.getText());
	}

	/** Loads the user's chosen word list for the Main game module*/

	public ArrayList<String> getWords(ActionEvent event) {
		String topic = topicSelector.getValue();
		String fileName = null;

		switch (topic) {
		case "Practise Module":
			fileName = "allWords";
			break;
		case "Babies":
			fileName = "babies";
			break;
		case "Colours":
			fileName = "colours";
			break;
		case "Compass Points":
			fileName = "compass";
			break;
		case "Days of the Week – Maori Original":
			fileName = "daysOfWeek1";
			break;
		case "Days of the Week – English Derivative":
			fileName = "daysOfWeek2";
			break;
		case "Engineering":
			fileName = "engineering";
			break;
		case "Feelings":
			fileName = "feelings";
			break;
		case "Months – Maori Original":
			fileName = "monthsOfYear1";
			break;
		case "Months – English Derivative":
			fileName = "monthsOfYear2";
			break;
		case "Software":
			fileName = "software";
			break;
		case "University Life":
			fileName = "uniLife";
			break;
		case "Weather":
			fileName = "weather";
			break;
		case "Work":
			fileName = "work";
			break;
		}

		ArrayList<String> allWords = readTopicFile(fileName);

		Collections.shuffle(allWords);

		ArrayList<String> quizWords;

		if (allWords.size() == 0) {
			return null;
		}

		if (allWords.size() >= 5) { // Replace 5 with variable, eventually
			quizWords = new ArrayList<String>(allWords.subList(0, 5));
		} else { // sets output to allWords if there are less than 5 word in the text file.
			quizWords = allWords;
		}

		return quizWords;

	}


	/** Reads the chosen word list and places it into an array for the Main game*/	
	public ArrayList<String> readTopicFile(String fileName){

		File topicFile = new File("src/application/words/" + fileName);
		ArrayList<String> allWords = new ArrayList<String>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(topicFile));
			String st;
			while ((st = br.readLine()) != null) {
				allWords.add(st);
			}

			br.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return allWords;
	}

}