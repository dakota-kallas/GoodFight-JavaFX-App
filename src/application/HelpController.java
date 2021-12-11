/**
 * HelpController.java
 *
 * JavaFX Bookkeeping Software
 *
 * This is the controller class for when the help page is loaded.
 *
 */

package application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class HelpController implements Initializable {
    // Declare all JavaFX interactive controls
    @FXML private Button button_log_in;

    /**
     * Method that runs listening for Action Events.
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Assigned the action that is caused by the "Log in!" button being clicked.
        button_log_in.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.changeScene(event, "LogIn.fxml", "Log in!", null, null, null, null);
            }
        });
    }
}
