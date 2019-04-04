package GameBoard;

import Multiplayer.MultiplayerHandler;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Board extends Application {
	public Square[][] box = new Square[10][15];

	BorderPane border;

	Button restart;
	
	/**
	 * Connection to multi-player handler
	 */
	MultiplayerHandler connection;
	
	/**
	 * Variable to keep track of whether the server is connected or not
	 */
	Boolean connected;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		
		//Connect to multi-player or set flag indicating no connection
		try {
			connection = new MultiplayerHandler("lcoalhost", 4444, box);
			connected = true;
		}catch(Exception E) {
			System.out.println("Unable to connect to server");
			connected = false;
		}

		border = new BorderPane();
		GridPane game = new GridPane();
		Scene scene = new Scene(border, 1500, 1000);

		restart = new Button("Restart Game");
		restart.setPadding(new Insets(10, 10, 10, 10));
		restart.setOnAction(e -> restart());

		int row, column;

		for (row = 0; row < 10; row++) {
			for (column = 0; column < 15; column++) {
				if(column == 7)
				{
					game.add(box[row][column] = new Square(row, column, column), column, row);
				}
				else
				{
					game.add(box[row][column] = new Square(row, column, column), column, row);
				}
			}
		}
		border.setRight(restart);
		border.setCenter(game);

		stage.setTitle("Temp");
		stage.setScene(scene);
		stage.show();
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			//Override on close process to add leaving server to list of commands
			@Override
			public void handle(WindowEvent event) {
				if(connected) connection.leaveServer();

			}

		});
		
	}

	private void restart() {
		if(connected) connection.sendRestart();
		int row, column;

		for (row = 0; row < 10; row++) {
			for (column = 0; column < 15; column++) {
				box[row][column].setState();
			}
		}
	}

	public class Square extends StackPane {
		private int xloc, yloc;

		// Used to create and update the board
		public Square(int xloc, int yloc, int column) {
			this.xloc = xloc;
			this.yloc = yloc;

			
			Image grassImg = new Image(getClass().getResource("grassTile.jpg").toExternalForm());
			ImageView grass = new ImageView(grassImg);
			
			
			Image pathImg = new Image(getClass().getResource("pathTile.jpg").toExternalForm());
			ImageView path = new ImageView(pathImg);
			
			// Background color is white, boarders are black
			this.setStyle("-fx-border-color: black;");
			this.setPrefSize(100, 100);
			this.setOnMouseClicked(e -> mouseEvent());

			grass.setFitWidth(99);
			grass.setPreserveRatio(true);
			
			path.setFitWidth(99);
			path.setPreserveRatio(true);
			
			if(column != 7)
			{
				this.getChildren().add(grass);
			}
			else
			{
				this.getChildren().add(path);
			}
		}

		// Gets mouse event
		public void mouseEvent() {
			if(connected) connection.createX(xloc, yloc);
		}

		public void setState() {

		}

		public int getX() {
			return xloc;
		}

		public int getY() {
			return yloc;
		}
	}
}
