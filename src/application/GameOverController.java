package application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GameOverController {
	private static int finalScore = 0;
	private static long finalTime = 0;
	private static String finalUsername = "Anonymous";
	private static Boolean finalPractiseMode = false;
	
	private static ArrayList<String> finalCorrectWords = new ArrayList<String>();
	private static ArrayList<String> finalIncorrectWords = new ArrayList<String>();
	
	@FXML private  Label scoreLabel;
	@FXML private ListView<String> scoreBoard;
	@FXML private Button backToSummary;
	@FXML private Button backToMainMenu;
	@FXML private Button goToLeaderboard;
	@FXML private Button clearLeaderboard;
	@FXML private Button playAgainButton;
	@FXML public VBox mainMenuLeaderBoard;
	@FXML public VBox summaryLeaderBoard;
	@FXML public ListView<String> correctWords;
	@FXML public ListView<String> incorrectWords;
	
	/** Tab asking to clear leaderboard confirmation appears */
	@FXML
	public void clearLeaderboard(ActionEvent event) throws IOException, InterruptedException {
		Alert alert = new Alert(AlertType.CONFIRMATION); //opens alert when quit button is pressed.
		alert.setTitle("Clear LeaderBoard"); //setting text for exit message
		alert.setHeaderText("You're about to clear the Leaderboard!");
		alert.setContentText("Are you sure you want to clear?");
		
		if(alert.showAndWait().get() == ButtonType.OK) { //closes window if confirmation is selected on alert window.
			Process process = new ProcessBuilder("bash", "-c","> src/application/leaderboard/leaderBoard.txt").start();
			scoreBoard.getItems().clear();
		}		
	}
	
	/**Collects information for the game to load results */
	public void setGameData(int score, long time, String username, ArrayList<String> correctWords, ArrayList<String> incorrectWords, Boolean isPracticeModule) {
		finalTime = time;
		finalUsername = username;
		finalCorrectWords = correctWords;
		finalIncorrectWords = incorrectWords;
		finalPractiseMode = isPracticeModule;
		
		if(time < 40) {
			finalScore = 200 * score;
		}else if(time <50){
			finalScore = 150 * score;
		}else {
			finalScore = 100*score;
		}
		
	}
	
	/** From the leaderboard scene, this changes the scene back to the game summary */
	@FXML
	private void backToSummary(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("GameOver.fxml"));
		Parent root = loader.load();

		GameOverController gameOver = loader.getController();
		
		if(finalPractiseMode) {
			gameOver.playAgainButton.setText("Play Game");
			gameOver.goToLeaderboard.setVisible(false);
			gameOver.goToLeaderboard.setDisable(true);
			gameOver.scoreLabel.setText("You got " + finalCorrectWords.size() + "/5 words Correct. How about playing a game now?");
		}else {
		gameOver.scoreLabel.setText("You got a total of " + String.valueOf(finalScore) + " points :) and your time taken is " + finalTime + "s");
		}
		
		for (String word :finalCorrectWords) {
			gameOver.correctWords.getItems().add(word);
			}
		
		for (String word :finalIncorrectWords) {
			gameOver.incorrectWords.getItems().add(word);
			}
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root, 1366, 768);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setScene(scene);
		stage.show();

	}
	
	/** Changes the scene to the game summary scene*/
	@FXML
	public void reviewScene(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("GameOver.fxml"));
		Parent root = loader.load();
	

		GameOverController gameOver = loader.getController();
		
		if(finalPractiseMode) {
			gameOver.playAgainButton.setText("Play Game");
			gameOver.goToLeaderboard.setVisible(false);
			gameOver.goToLeaderboard.setDisable(true);
			gameOver.scoreLabel.setText("You got " + finalCorrectWords.size() + "/5 words Correct. How about playing a game now?");
		}else {
		gameOver.scoreLabel.setText("You got a total of " + String.valueOf(finalScore) + " points :) and your time taken is " + finalTime + "s");
		}
		
		String userScoreAndName = finalScore + " | " + finalUsername;
		leaderBoard(userScoreAndName);
		
		
		for (String word :finalCorrectWords) {
			gameOver.correctWords.getItems().add(word);
			}
		
		for (String word :finalIncorrectWords) {
			gameOver.incorrectWords.getItems().add(word);
			}
		
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root, 1366, 768);
		scene.getStylesheets().add(getClass().getResource("gameOver.css").toExternalForm());
		stage.setScene(scene);
		stage.show();

		Process process = new ProcessBuilder("bash", "src/speechSpeed/speechRate.sh",
				"Ka Pai", "1", "0").start();
	}
	

	/** Appends the users score to the leaderboard */
	public void leaderBoard(String userScoreAndName) {
			try {
				//passing word into appendLeaderBoard
				Process process = new ProcessBuilder("bash", "src/application/leaderboard/appendLeaderBoard", userScoreAndName).start();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	/** Changes the scene to the leaderboard*/
	@FXML
	public void goToLeaderBoard(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("leaderBoard.fxml"));
		Parent root = loader.load();
		
		GameOverController gameOver = loader.getController();
		updateScoreBoard(gameOver);
		
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root, 1366, 768);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setScene(scene);
		stage.show();
	}
	
	/**Prints out the scores and names for the score board*/
	public void updateScoreBoard(GameOverController gameOver) {
		try {
			BufferedReader br = new BufferedReader(new FileReader("src/application/leaderboard/leaderBoard.txt"));
			String st;
			while ((st = br.readLine()) != null) {
				gameOver.scoreBoard.getItems().add(st);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Changes the scene to the main menu scene */
	public void backToMainMenu(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("mainMenu.fxml"));
		Parent root = loader.load();
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root, 1366, 768);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setScene(scene);
		stage.show();

	}
	
	/** Changes the scene to the new game scene (Main Game functionality) */
	public void playAgain(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("mainMenu.fxml"));
		Parent root = loader.load();

		MainMenuController playAgain = loader.getController();
		playAgain.topicSelect(event);

		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root, 1366, 768);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setScene(scene);
		stage.show();
	}
}
