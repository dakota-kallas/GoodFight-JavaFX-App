package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;

/**
 * https://thegoodfight.club/about-us/
 * Account types:
 * 				Volunteer: 3
 * 				Donor: 2
 * 				Admin: 1
 * --module-path "C:\Users\Dakota\Documents\CS341\openjfx-17.0.0.1_windows-x64_bin-sdk\javafx-sdk-17.0.0.1\lib" --add-modules javafx.controls,javafx.fxml
 */
public class Main extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("LogIn.fxml"));
        primaryStage.setTitle("Log in!");
        primaryStage.setScene(new Scene(root, 600, 400));
		Image icon = new Image(getClass().getResourceAsStream("goodfightlogo.png"));
		primaryStage.getIcons().add(icon);
        primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
