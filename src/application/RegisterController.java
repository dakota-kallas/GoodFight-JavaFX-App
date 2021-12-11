/**
 * RegisterController.java
 *
 * JavaFX Bookkeeping Software
 *
 * This is the controller class for when the registration page is loaded.
 *
 */

package application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {
    // Declare all JavaFX interactive controls
    @FXML private Button button_register;
    @FXML private Button button_log_in;

    @FXML private TextField tf_firstname;
    @FXML private TextField tf_lastname;
    @FXML private TextField tf_email;
    @FXML private PasswordField pf_password;
    @FXML private PasswordField pf_confirm_password;

    @FXML private RadioButton rb_volunteer;
    @FXML private RadioButton rb_donor;

    /**
     * Method that runs listening for Action Events.
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configure the account type radio buttons
        ToggleGroup accountToggle = new ToggleGroup();
        rb_volunteer.setToggleGroup(accountToggle);
        rb_donor.setToggleGroup((accountToggle));

        rb_volunteer.setSelected(true);

        // Assigned the action that is caused by the "Register" button being clicked.
        button_register.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Ge the type of account from the user
                String toggleType = ((RadioButton) accountToggle.getSelectedToggle()).getText();

                // Check for data validation on if all fields were completed
                if (!tf_email.getText().trim().isEmpty() && !pf_password.getText().trim().isEmpty() && !pf_confirm_password.getText().trim().isEmpty() && !tf_firstname.getText().trim().isEmpty()) {
                    // Check for data validation if the passwords match
                    if(pf_confirm_password.getText().equals(pf_password.getText())) {
                        DBUtils.signUpUser(event, tf_email.getText(), pf_password.getText(), tf_firstname.getText(), tf_lastname.getText(), toggleType);
                    } else {
                        System.out.println("Passwords do not match.");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Passwords do not match!");
                        alert.show();
                    }
                } else {
                    System.out.println("Please fill in all information");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Please fill in all information to sign up!");
                    alert.show();
                }
            }
        });

        // Assigned the action that is caused by the "Log in!" button being clicked.
        button_log_in.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.changeScene(event, "LogIn.fxml", "Log in!", null, null, null, null);
            }
        });
    }
}
