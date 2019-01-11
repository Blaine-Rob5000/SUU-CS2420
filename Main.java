

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * 																							 *
 * 		CS1400 fall 2018 final project														 *
 * 		coded by: Robin G. Blaine															 *
 *																							 *
 *		Citatations: Dr. Cecily Heiner, https://docs.oracle.com, http://stackoverflow.com	 *
 *																							 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package application;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;


public class Main extends Application {
	// constants
	final int CELL_SIZE = 60;
	final int BLACK = 1;
	final int RED = 2;

	int selectedButton;
	int activePlayer;
	int numBlackCheckers;
	int numRedCheckers;

	Square[] square = new Square[18];
	Checker[] checker = new Checker[12];

	@Override
	public void start(Stage primaryStage) {
		try {

			//variables
			activePlayer = BLACK;		// whose turn is it?
			numBlackCheckers = 6;
			numRedCheckers = 6;
			selectedButton=0;
			GridPane board = new GridPane();

			// add column and row constraints
			for(int i=0; i<8; i++) {
				if(i<6) {
					board.getColumnConstraints().add(new ColumnConstraints(CELL_SIZE));
				} // end if
				board.getRowConstraints().add(new RowConstraints(CELL_SIZE));
			} // end for

			// create rotating images for corners
			Image battleImage = new Image("Battle2.png");
			HBox[] battlePane = new HBox[4];
			ImageView[] battleIV = new ImageView[4];
			RotateTransition[] rt = new RotateTransition[4];

			for(int i=0; i<4; i++) {
				battlePane[i] = new HBox();
				battlePane[i].setPrefWidth(CELL_SIZE);
				battlePane[i].setPrefHeight(CELL_SIZE);
				battlePane[i].setAlignment(Pos.CENTER);
				battleIV[i] = new ImageView();
				rt[i] = new RotateTransition(Duration.millis(1500), battleIV[i]);
				rt[i].setByAngle(360);
				rt[i].setCycleCount(Animation.INDEFINITE);
				rt[i].setInterpolator(Interpolator.LINEAR);
				rt[i].play();
				battleIV[i].setImage(battleImage);
				battlePane[i].getChildren().add(battleIV[i]);
			} // end for

			board.add(battlePane[0], 0, 0);
			board.add(battlePane[1], 5, 0);
			board.add(battlePane[2], 0, 7);
			board.add(battlePane[3], 5, 7);

			// add labels
			Label gameLabel = new Label(" * * * BATTLE  CHECKERS * * * ");
			gameLabel.setPrefWidth(CELL_SIZE * 4);
			gameLabel.setPrefHeight(CELL_SIZE);
			gameLabel.setFont(new Font("Times New Roman", 16));
			gameLabel.setTextAlignment(TextAlignment.CENTER);
			gameLabel.setContentDisplay(ContentDisplay.CENTER);
			board.add(gameLabel, 1, 0, 4, 1);
			
			Label messageLabel = new Label("Black's move...");
			gameLabel.setPrefWidth(CELL_SIZE * 4);
			messageLabel.setPrefHeight(CELL_SIZE);
			messageLabel.setFont(new Font("Times New Roman", 12));
			messageLabel.setTextAlignment(TextAlignment.CENTER);
			messageLabel.setContentDisplay(ContentDisplay.CENTER);
			board.add(messageLabel,  1, 7, 4, 1);

			Button[] button  = new Button[18];

			for(int i=0; i<18; i++) {
				button[i] = new Button();
				button[i].setPrefWidth(CELL_SIZE*.9);
				button[i].setPrefHeight(CELL_SIZE*.9);
				button[i].setContentDisplay(ContentDisplay.CENTER);

				int clickedButton = i;
				selectedButton = -1;
				button[i].setOnAction(new EventHandler<ActionEvent>() {
					@Override public void handle(ActionEvent e) {
						messageLabel.setText("Button " + clickedButton + " clicked.");

						/*
						 * selectedButton is the button that was in focus prior to this button click
						 * clickedButton is the button click that created this event
						 * 
						 * if clickedButton is the current selectedButton, do nothing
						 * else if selectedButton contains a checker (that is not dead) belonging to activePlayer
						 *	and clickedButton is a valid move for that checker (if not, message = "invalid move")
						 *	and clickedButton does not contain a checker belonging to activePlayer (if not, message = "invalid move")
						 *		if clickedButton is empty, move checker from selectedButton to clickedButton
						 *		else there must be an enemy checker in that square; fight it (message = "xxx fights xxx!")
						 *			both sides roll 1d6 (kings get +1 to roll); higher roll wins, ties go to activePlayer ("xxx wins!)
						 *			if win, kill enemy checker and move that square, -1 to number of checkers left for other player
						 *			else kill this checker, -1 to number of checkers for activePlayer
						 *	if clickedButton is in enemy back row, promote checker to King
						 *
						 *reset button graphics based on checker positions (dead checkers do not display)
						 *
						 *check for win/loss - if either players number of checkers == 0, that player loses and other player wins
						 *	display victory message, game over
						 *
						 *toggle activePlayer, update messageLabel ("xxx's move")
						 *
						 */

						boolean madeValidMove = false;
						if(selectedButton==-1) {
							selectedButton=clickedButton;
						}
						
						int selectedChecker=square[selectedButton].getChecker();
						String selectedColor = "";
						if(selectedChecker!=-1) {
							if(checker[selectedChecker].getPlayer()==1) {
								selectedColor = "Black";
							} else {
								selectedColor = "Red";
							} // end if/else
						} // end if

						int targetChecker=square[clickedButton].getChecker();
						String targetColor = "";
						if(targetChecker!=-1) {
							if(checker[targetChecker].getPlayer()==1) {
								targetColor = "Black";
							} else {
								targetColor = "Red";
							} // end if/else
						} // end if/else

						
						// DEBUG * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
						if(selectedChecker!=-1) {
							System.out.println("\nSelected Checker = " + checker[selectedChecker].toString());
						} else {
							System.out.println("Selected Checker = none, Selected Square = " + selectedButton);
						}
						
						if(targetChecker!=-1) {
							System.out.println("Target Checker = " + checker[targetChecker].toString());
						} else {
							System.out.println("Target Checker = none, Target Square = " + clickedButton);
						} // end if/else
						System.out.println("Active player = " + activePlayer);
						// END DEBUG * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

						
						if((clickedButton != selectedButton)&&(selectedChecker != -1)) {
							if((checker[selectedChecker].getPlayer()==activePlayer)&&(!checker[selectedChecker].isDead())) {
								if(targetChecker!=-1) {
									if((checker[selectedChecker].isValidMove(clickedButton))&&(checker[targetChecker].getPlayer()!=activePlayer)) {
										// checker fight
										int selectedRoll = (int)(Math.random()*6)+1;
										if(checker[selectedChecker].isKing()) {
											selectedRoll+=1;
										} // end if
										System.out.println(selectedColor + " rolls " + selectedRoll);	// DEBUG * * * * * *

										int targetRoll = (int)(Math.random()*6)+1;
										if(checker[targetChecker].isKing()) {
											targetRoll+=1;
										} // end if
										System.out.println(targetColor + " rolls " + targetRoll);		// DEBUG * * * * * *

										if(targetRoll>selectedRoll) {
											/*
											 * kill selectedChecker
											 * messageLabel.setText(selectedColor + " fights " + targetColor + " and " + targetColor + " wins!");
											 */
											
											if(checker[selectedChecker].getPlayer()==1 ) {
												numBlackCheckers-=1;
											} else {
												numRedCheckers-=1;
											}
											checker[selectedChecker].setDead(true);
											square[checker[selectedChecker].getLocation()].setChecker(-1);
											

											System.out.println(targetColor + " wins!");									// DEBUG * * * * * *
										} else {
											/*
											 * kill targetChecker
											 * decrease numBlackCheckers or numRedCheckers (as appropriate) ... if now equals 0, other player wins, game over
											 * move selectedChecker to targetSquare
											 * madeValidMove = true;
											 * messageLabel.setText(selectedColor + " fights " + targetColor + " and " + targetColor + " wins the fight!");
											 */
											
											if(checker[targetChecker].getPlayer()==1 ) {
												numBlackCheckers-=1;
											} else {
												numRedCheckers-=1;
											}
											checker[targetChecker].setDead(true);
											checker[selectedChecker].setLocation(clickedButton);
											if((square[clickedButton].getX()==5)&&(checker[selectedChecker].getPlayer()==1)) {
												checker[selectedChecker].promoteToKing();
											} else if((square[clickedButton].getX()==0)&&(checker[selectedChecker].getPlayer()==2)) {
												checker[selectedChecker].promoteToKing();
											} // end if/else
											square[selectedButton].setChecker(-1);
											square[clickedButton].setChecker(selectedChecker);

											System.out.println(selectedColor + " wins!");									// DEBUG * * * * * *
										} //end if/else
									} else {
										selectedButton = -1;
										messageLabel.setText("Invalid move...");	    					
									}
								} else {
									/*
									 * move selectedChecker to clickedButton square
									 */

									System.out.println(selectedColor + " moves from " + selectedButton + " to " + clickedButton);	// DEBUG * * * * * *

									checker[selectedChecker].setLocation(clickedButton);
									if((square[clickedButton].getX()==5)&&(checker[selectedChecker].getPlayer()==1)) {
										checker[selectedChecker].promoteToKing();
									} else if((square[clickedButton].getX()==0)&&(checker[selectedChecker].getPlayer()==2)) {
										checker[selectedChecker].promoteToKing();
									} // end if/else
									square[selectedButton].setChecker(-1);
									square[clickedButton].setChecker(selectedChecker);
									madeValidMove = true;

								} // end if/else
							} // end if/else
						} else {
							if(square[clickedButton].getChecker()==-1) {
								selectedButton = -1;
							}
						} // end if/else
						
						
						/* * * * * * * * * * * * * * * * * * *
						 * update button graphics
						 * selectedButton = clickedButton;
						 * change activePlayer
						 * update messageLabel
						 * * * * * * * * * * * * * * * * * * */
						
						if(madeValidMove) {
							toggleActivePlayer();
							selectedButton = -1;
							for(int i=0; i<18; i++) {
								if(square[i].getChecker()==-1) {
									button[i].setGraphic(null);
								} else {
									button[i].setGraphic(new ImageView(checker[square[i].getChecker()].getPicture()));
								} // end if/else
							} // end for

							selectedButton = -1;

							messageLabel.setText(messageLabel.getText() + "\n" + selectedColor + "'s move...");
						}

					} // end Override handle(ActionEvent e)
				}); // end button.setOnAction

				square[i] = new Square(i);

				board.add(button[i], square[i].getX(), square[i].getY());
			} // end for

			for(int i=0; i<6; i++) {
				// black checkers: 0-5, on squares 0, 3, 6, 9, 12, 15
				checker[i] = new Checker(i, 1, i*3);
				button[i*3].setGraphic(new ImageView(checker[i].picture));
				square[i*3].setChecker(checker[i].getCheckerNumber());
				// red checkers: 6-11, on squares 2, 5, 8, 11, 14, 17
				checker[i+6] = new Checker(i+6, 2, i*3+2);
				button[i*3+2].setGraphic(new ImageView(checker[i+6].picture));
				square[i*3+2].setChecker(checker[i+6].getCheckerNumber());
			} // end for

			Scene scene = new Scene(board,CELL_SIZE*6,CELL_SIZE*8);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();

			boolean gameOver = false;
			while(!gameOver) {


				gameOver = true;
			} // end while

		} catch(Exception e) {
			e.printStackTrace();
		} // end catch/try
	} // end method: start

	public class Square {
		// class variables
		int x;
		int y;
		int button;
		boolean isSelected = false;
		int checker = -1;

		public Square(int n) {
			super();
			this.button = n;			
			this.y = n/3+1;
			this.x = (n%3)*2+(this.y%2);
		} // end constructor

		public int getChecker() {
			return checker;
		} // end getChecker

		public void setChecker(int checker) {
			this.checker = checker;
		} // end setChecker

		public int getX() {
			return x;
		} // end getX

		public int getY() {
			return y;
		} // end getY

		public boolean isSelected() {
			return isSelected;
		} // end getSelected

		public void setSelected(boolean selected) {
			this.isSelected = selected;
		} // end setSelected

		public int getButton() {
			return button;
		} // end getButton

		public boolean isAdjacent(int targetN) {
			int targetY = targetN/3+1;
			int targetX = (targetN%3)*2+(targetY%2);
			if( ((targetN-3)%6==0) && ((targetX-this.x)==1) ) {
				return true;
			} else if( ((targetN-2)%6==0) && ((targetX-this.x)==-1) ) {
				return true;
			} else if(Math.abs(targetX-this.x)==1) {
				return true;
			}
			return false;
		} // end isAdjacent


	} // end class: GameSquare

	public class Checker {
		// variables
		int checkerNumber;
		int player;
		int location;
		int x;
		int y;
		boolean isKing = false;
		boolean isDead = false;
		Image picture = null;

		// images
		Image blackSword = new Image("BlackSword.png");
		Image redSword = new Image("RedSword.png");
		Image blackCrown = new Image("BlackCrown.png");
		Image redCrown = new Image("RedCrown.png");

		public Checker(int chkNum, int player, int square) {
			super();
			this.checkerNumber = chkNum;
			this.player = player;
			this.location = square;
			this.y = square/3+1;
			this.x = (square%3)*2+(this.y%2);
			setPicture();
		} // end constructor
		
		public int getCheckerNumber() {
			return checkerNumber;
		} // end getCheckerNumber

		public int getPlayer() {
			return player;
		} // end getPlayer

		public int getLocation() {
			return location;
		} // end getSquare

		public void setLocation(int newLocation) {
			System.out.println("Checker: " + toString() + ", moving to " + newLocation);	//DEBUG * * * * * * *
			this.location = newLocation;
		} // endSetSquare

		public boolean isKing() {
			return isKing;
		} // end isKing

		public void promoteToKing() {
			this.isKing = true;
			setPicture();
		} // end setKing

		public boolean isDead() {
			return isDead;
		} // end isDead

		public void setDead(boolean isDead) {
			this.isDead = isDead;
		} // end setDead

		public Image getPicture() {
			return picture;
		} // end getPicture

		public void setPicture() {
			if(this.player==1) {					// player 1 (black)
				if(this.isKing) {
					this.picture = blackCrown;		// is a king
				} else {
					this.picture = blackSword;		// is not a king
				} // end if/else
			} else {								// player 2 (red)
				if(this.isKing) {
					this.picture = redCrown;		// is a king
				} else {
					this.picture = redSword;		// is not a king
				} // end if/else
			} // end if/else
		} // end setPicture

		public boolean isValidMove(int targetN) {
			int targetY = targetN/3+1;
			int targetX = (targetN%3)*2+(targetY%2);
			boolean isAdjacent;
			boolean isValid;

			if( ((targetN-3)%6==0) && ((targetX-this.x)==1) ) {
				isAdjacent = true;
			} else if( ((targetN-2)%6==0) && ((targetX-this.x)==-1) ) {
				isAdjacent = true;
			} else if(Math.abs(targetX-this.x)==1) {
				isAdjacent = true;
			} else {
				return false;
			} // end if/else

			if(this.isKing) {
				return true;
			} else if((this.player==1)&&(targetX>this.x)) {
				return true;
			} else if((this.player==2)&&(targetX<this.x)) {
				return true;
			} // end if/else

			return false;
		} // end isValidMove

		public String toString()
		{
			return "Player = " + player + ", Square = " + location;
		}
	} // end class: Checker

	public void toggleActivePlayer() {
		if(activePlayer==1) {
			activePlayer=2;
			System.out.println("\ntoggleActivePlayer: Red's Turn");
		} else {
			activePlayer=1;
			System.out.println("\ntoggleActivePlayer: Black's Turn");
		}
	}

	public static void main(String[] args) {
		launch(args);
	} // end method: main
} // end class Main
