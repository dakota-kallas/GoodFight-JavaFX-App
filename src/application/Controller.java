package application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class Controller implements Initializable {

    @FXML private Button button_login;
    @FXML private Button button_sign_up;
    @FXML private Button button_user_manual;

    @FXML private TextField tf_email;
    @FXML private PasswordField pf_password;

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
                File file=new File("src/application/manual.pdf");
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.open(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
