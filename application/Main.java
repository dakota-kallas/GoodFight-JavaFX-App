/**
 * Main.java
 * 
 * JavaFX Bookkeeping Software
 * 
 * This is the main class used to run the Bookkeeping software. A descriptions of this software
 * can be found in the project description writeup.
 * 
 */

package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception{
		// Load the Log in page to start the program and set the window size.
        Parent root = FXMLLoader.load(getClass().getResource("LogIn.fxml"));
        primaryStage.setTitle("Log in!");
		Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		// Gather the icon resource for the page and load it onto the window
		Image icon = new Image(getClass().getResourceAsStream("goodfightlogo.png"));
		primaryStage.getIcons().add(icon);
        primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
