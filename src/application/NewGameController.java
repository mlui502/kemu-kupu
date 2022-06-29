package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.scene.control.ProgressBar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class NewGameController {
	// ---------------------------------Values for Game Module code functionality---------------------------------
	ArrayList<String> wordList = new ArrayList<String>();

	String currentWord;
	String finalUserName = "Anonymous";
	ArrayList<String> correctWords = new ArrayList<String>();
	ArrayList<String> incorrectWords = new ArrayList<String>();

	Boolean practiseModule = false;
	Boolean hasFaulted = false;

	int currentWordNum = 0;
	int gamePoints;
	long startTime;
	double progressRatio;
	private int oldCaretPosition;
	
	private Timeline countDown;

	static String firstTime = "0";

	// ------------------------------------Main Game Module Scene Fields------------------------------------------
	@FXML
	private Label scoreCount;
	@FXML
	private Label secondLetter;
	@FXML
	private Label wordNumText;
	@FXML
	private Label feedbackText;
	@FXML
	private Label numOfLettersLabel;
	@FXML
	private Label speedLabel;
	@FXML
	private Button startButton;
	@FXML
	private Button playAgainButton;
	@FXML
	private Button skipButton;
	@FXML
	private Slider voiceSpeedSlider;
	@FXML
	private TextField userSpelling;
	@FXML
	private ProgressBar timeProgressBar;
	@FXML
	private Label multiplyLabel;

	// -----------------------------------Macron Fields------------------------------------------
	@FXML
	private Button aMacron;
	@FXML
	private Button eMacron;
	@FXML
	private Button iMacron;
	@FXML
	private Button oMacron;
	@FXML
	private Button uMacron;

	// ----------------------------------- Game Initialization ------------------------------------------
	
	/**
	 This method is used to initialize the game, making use of both java and javafx elements
	 @param event, event is called upon button being pressed
	 @return void
	 **/
	@FXML
	void startGame(ActionEvent event) throws IOException {
		gamePoints = 0;
		if (!practiseModule) {
			timeProgressBar.setOpacity(1);
			multiplyLabel.setOpacity(1);
			startCountDown();
		}
		startTime = System.currentTimeMillis();
		callWord(event);
		
		userSpelling.focusedProperty().addListener((observable, oldValue, newValue) -> {
		    if (!newValue) {
		        oldCaretPosition = userSpelling.getCaretPosition();
		    }
		});
	}

	/**
	 This method is used to set the word list to the current 5 or less words.
	 @param input, This is a stringarray of the current words to set to the word list
	 @param practiseModule, This is a boolean to determine whether the game is set to practise or standard.
	 @return void
	 **/
	public void setWordList(ArrayList<String> input, boolean practiseModule) {
		this.wordList = input;
		this.practiseModule = practiseModule;
	}

	/**
	 This method sets the players username to be updated onto the leaderboard.
	 @param username, this is the players username they put in the textfield in topic select.
	 @return void
	 **/
	public void setUsername(String username) {
		if (!username.equals("")) {
			finalUserName = username;
		}

	}

	/**
	 This method provides a pause or delay in the code
	 @param period, duration in seconds.
	 @return void
	 **/
	private void delay(double period) throws IOException {
		try {
			Thread.sleep((long) period * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 This method is used to disable buttons with vocal features, to prevent clutter of voices. Also re-enables the buttons
	 once voices are finished speaking.
	 @param isDisabled. determines whether gui nodes are disabled
	 @return void
	 **/
	private void GUIToggle(Boolean isDisabled) {
		new Thread(() -> {
			skipButton.setDisable(isDisabled);
			playAgainButton.setDisable(isDisabled);

			if (isDisabled) {
				userSpelling.setOnAction(null);
			} else {
				userSpelling.setOnAction(event -> {
					try {
						markWord(event);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			}
		}).start();
	}
	// -----------------------------------Main Game Functionality-----------------------------------------

	/**
	 This method changes the current word to a new word for the user to spell. Switches to game
	 over scene if it was the last word
	 @param event, called upon pressing enter of the textfield to check word
	 @return void
	 **/
	private void callWord(ActionEvent event) throws IOException {

		// Takes to Game Over Scene if last question reached
		if (currentWordNum == wordList.size()) {
			GUIToggle(true);
			delay(1.5);
			gameOver(event);
			return;
		}

		hasFaulted = false;
		currentWord = wordList.get(currentWordNum);
		numOfLettersLabel.setText(showNumOfLetters());

		// Updating visable Nodes on New Game Scene
		wordNumText.setText("Word " + (currentWordNum + 1) + " of " + wordList.size());
		secondLetter.setText("");
		speakWord(1, firstTime);
		firstTime = "1";
	}

	/**
	 This is the functionality to mark the word the user has input. In all cases the textfield is reset to nothing. In 
	 the case the word is correct, it provides the user with a new word and increments the score and counter and plays a 
	 correct sounds. When the user faults the word, the same word is provided and an extra chance with an encouraging message.
	 If the word wrong, it provides the user with a new word in the next attempt.
	 @param event, event called upon pressing enter on the textfield.
	 @return void
	 **/
	@FXML
	void markWord(ActionEvent event) throws IOException {
		feedbackText.setOpacity(1);

		if (userSpelling.getText().equalsIgnoreCase(currentWord)) {
			// User gets spelling correct
			userSpelling.setText("");
			correctWords.add(currentWord);
			gamePoints++;
			scoreCount.setText("Words Correct: " + gamePoints + "/5");
			feedbackText.setText("Last word was correct. Well Done!");
			playSound("correct.wav");
			currentWordNum++;

		} else if (!hasFaulted) {
			// User gets spelling incorrect (first time)
			userSpelling.setText("");
			feedbackText.setText("Last word was incorrect. Please try again.");
			lettersReveal();
			playSound("incorrect.wav");
			speakWord(1, "1");
			hasFaulted = true;
			return;

		} else {
			// User gets spelling incorrect (second time)
			userSpelling.setText("");

			if (practiseModule) {
				feedbackText.setText("Last word was " + currentWord);
			} else {
				feedbackText.setText("Last word was incorrect.");
			}
			incorrectWords.add(currentWord);
			currentWordNum++;
			playSound("incorrect.wav");

		}

		callWord(event);
	}

	/**
	 This method is used to play a sound mainly to indicate if words are correct or incorrect or when a game is finished,
	 utilising processBuilder to play the sound.
	 @param fileName, This is the file name that is to be played
	 @return void
	 **/
	private void playSound(String fileName) throws IOException {
		new Thread(() -> {
			try {
				Process process = new ProcessBuilder("bash", "src/bashScripts/playSound.sh", fileName).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	/**
	 This method is used to reveal the second letter in a real game and a third of the letters randomly selected 
	 if in a practise module game, This changes the numOfLettersLabel to underscores and letters randomly
	 @param void
	 @return void
	 **/
	@FXML
	private void lettersReveal() {
		if (practiseModule) {
			double numToReveal = Math.ceil(currentWord.replaceAll("\s", "").length() / 3.0);
			String[] word = currentWord.split("");

			Random rand = new Random();
			String[] wordDisplay = showNumOfLetters().split("");

			for (int i = 0; i < numToReveal; i++) {
				int index = rand.nextInt(currentWord.length());
				wordDisplay[index] = word[index];
			}

			String display = "";
			for (String i : wordDisplay) {
				display = display + i;
			}

			numOfLettersLabel.setText(display);
		} else {
			// When user gets spelling incorrect first time
			String currentUnderScore = numOfLettersLabel.getText();
			numOfLettersLabel.setText("_" + currentWord.charAt(1) + currentUnderScore.substring(2));
		}
	}

	/**
	 This method is used to play back the current word vocally, also using whatever the slider speed is set to
	 @param event, event is called upon speak again button is pressed.
	 @return void
	 **/
	@FXML
	void repeatWord(ActionEvent event) throws IOException {
		speakWord(voiceSpeedSlider.getValue(), "0");
	}

	/**
	 This method is used to vocalize the current word using the maori voice pack
	 @param speed, This is the speed of the voice, 1 being default, 2 being 0.5x and 0.5 being 2x
	 @param pause, puts current word through text to speech, and determines if a pause should be used.
	 @return void
	 **/
	private void speakWord(double speed, String pause) throws IOException {

		// Bash speaks spelling word on new thread so there's no delay in GUI update
		new Thread(() -> {
			try {

				GUIToggle(true);

				Process process = new ProcessBuilder("bash", "src/bashScripts/speechRate.sh",
						wordList.get(currentWordNum), String.valueOf(1 / speed), pause).start();

				BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
				BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

				int exitStatus = process.waitFor();

				// If process did not run, check for output of 0 which indicates an error
				// occurred
				if (exitStatus == 0) {
					String line;
					while ((line = stdout.readLine()) != null) {
						System.out.println(line);
					}
				} else {
					String line;
					while ((line = stderr.readLine()) != null) {
						System.err.println(line);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Re-enabling buttons to allow game to continue
			GUIToggle(false);
		}).start();

	}

	/**
	 This method marks the current word as incorrect and proceeds to the next word once button is pressed
	 @param event, skip word button is pressed to change to next word.
	 @return void
	 **/
	@FXML
	private void skipWord(ActionEvent event) throws IOException {
		// Updating GUI nodes
		userSpelling.setText("");
		playSound("incorrect.wav");

		if (practiseModule) {
			feedbackText.setText("Last word was " + currentWord);
		} else {
			feedbackText.setText("That was a tough one! Try this word instead");
		}
		incorrectWords.add(currentWord);
		currentWordNum++;
		if (currentWordNum == wordList.size()) {
			GUIToggle(true);
		}

		// Moving to next word
		callWord(event);
	}

	/**
	 This method utilizes the javafx buttons to insert macrons of the maori vowels into the text field
	 @param event, upon one of the button being pressed, the respective macron is place in the text field
	 @return void
	 **/
	@FXML
	private void addMacron(ActionEvent event) {
//https://stackoverflow.com/questions/36054363/is-there-more-than-one-caret-in-javafx-textfield
	    String formerText = userSpelling.getText();
	    String additionalText = ((Button)event.getSource()).getText();
	    userSpelling.setText(formerText.substring(0, oldCaretPosition)
	                        + additionalText
	                        + formerText.substring(oldCaretPosition));

		userSpelling.requestFocus();
		userSpelling.positionCaret(oldCaretPosition + 1);
	}

	/**
	 This method is used to show number of letters in the form of underscores("_") of the current word
	 @param void
	 @return current word replaced with underscores, twoLines;
	 **/
	@FXML
	private String showNumOfLetters() {
		// Splits lengthy words into 2 strings
		String underscoreString = currentWord.replaceAll("[^ ]", "_");
		String twoLines = "";
		String[] splitStrings = underscoreString.split("\\s+");

		for (int i = 0; i < splitStrings.length; i++) {
			twoLines = (twoLines + splitStrings[i] + " ");
			if ((splitStrings.length > 6) && (i == splitStrings.length / 2)) {
				twoLines = twoLines + "\n";
			}
		}
		return twoLines;
	}

	/**
	 This method is used to start the score multiplier system once gameStart is called
	 @param void
	 @return void
	 **/
	private void startCountDown() {

		progressRatio = 0;

		if (countDown != null) {
			countDown.stop();
		}

		countDown = new Timeline(new KeyFrame(Duration.seconds(1), (ActionEvent event) -> {
			progressRatio++;
			timeProgressBar.setProgress(1 - progressRatio / 50);
			if (progressRatio == 40) {
				multiplyLabel.setText("1.5x");
			}
			if (progressRatio == 50) {
				multiplyLabel.setText("1.0x");
				countDown.stop();
			}
		}));

		countDown.setCycleCount(Timeline.INDEFINITE);
		countDown.play();
	}

	/**
	 This method is used to set up elements for gameover scene, ie endTime, and plays sound on end scene.
	 @param event, event is called upon last word being checked
	 @return void
	 **/
	private void gameOver(ActionEvent event) throws IOException {
		long endTime = (long) ((System.currentTimeMillis() - startTime) / 1000.0);
		GameOverController gameOver = new GameOverController();
		gameOver.setGameData(gamePoints, endTime, finalUserName, correctWords, incorrectWords, practiseModule);
		gameOver.reviewScene(event);
		playSound("endGame.wav");
	}
}
