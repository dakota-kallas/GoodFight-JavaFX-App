/**
 * Controller.java
 *
 * JavaFX Bookkeeping Software
 *
 * This is the controller class for when the login page is loaded.
 *
 */

package application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    // Declare all JavaFX interactive controls
    @FXML private Button button_login;
    @FXML private Button button_sign_up;
    @FXML private Button button_user_manual;

    @FXML private TextField tf_email;
    @FXML private PasswordField pf_password;

    /**
     * Method that runs listening for Action Events.
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Assigned the action that is caused by the "Login" button being clicked.
        button_login.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.logInUser(event, tf_email.getText(), pf_password.getText());
            }
        });

        // Assigned the action that is caused by the "Sign Up!" button being clicked.
        button_sign_up.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.changeScene(event, "Register.fxml", "Register", null, null, null, null);
            }
        });

        // Assigned the action that is caused by the "Sign Up!" button being clicked.
        button_user_manual.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.changeScene(event, "Help.fxml", "Need help?", null, null, null, null);
            }
        });
    }
}
