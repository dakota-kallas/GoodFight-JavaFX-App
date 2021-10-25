package application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML
    private Button button_register;
    @FXML
    private Button button_log_in;

    @FXML
    private TextField tf_firstname;
    @FXML
    private TextField tf_lastname;
    @FXML
    private TextField tf_email;
    @FXML
    private PasswordField pf_password;

    @FXML
    private RadioButton rb_volunteer;
    @FXML
    private RadioButton rb_donor;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ToggleGroup accountToggle = new ToggleGroup();
        rb_volunteer.setToggleGroup(accountToggle);
        rb_donor.setToggleGroup((accountToggle));

        rb_volunteer.setSelected(true);

        // Assigned the action that is caused by the "Register" button being clicked.
        button_register.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String toggleType = ((RadioButton) accountToggle.getSelectedToggle()).getText();

                if (!tf_email.getText().trim().isEmpty() && !pf_password.getText().trim().isEmpty() && !tf_firstname.getText().trim().isEmpty()) {
                    DBUtils.signUpUser(event, tf_email.getText(), pf_password.getText(), tf_firstname.getText(), tf_lastname.getText(), toggleType);
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
