package application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ViewProfileController implements Initializable{
	
	@FXML private Button button_logout;
	@FXML private Button button_home;
	@FXML private Button button_home_a;
	@FXML private Button button_create_event;
	@FXML private Button button_view_events;
	@FXML private Button button_view_events_a;
	@FXML private Button button_profile;

	@FXML private Button button_save;

	@FXML private TextField tf_edit_first_name;
	@FXML private TextField tf_edit_last_name;
	@FXML private TextField tf_edit_email;

	@FXML private Label label_display_name;
	@FXML private Label label_display_email_type;
	
	@FXML private Label label_name;
	@FXML private Label label_account_type;

	@FXML private VBox nav_admin;
	@FXML private VBox nav_volunteer;

	private String firstName = "",lastName = "", email = "", accountType = "";

	/**
	 * Method that runs listening for Action Events.
	 *
	 * @param location
	 * @param resources
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Assigned the action that is caused by the "Logout" button being clicked.
		button_logout.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "LogIn.fxml", "Log in!", null, null, null,null);
			}
		});

		// Assigned the action that is caused by the "Profile" icon being clicked.
		button_profile.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "ViewProfile.fxml", "My Profile", email, firstName, lastName, accountType);
			}
		}));


		// Assigned the action that is caused by the "Home" button being clicked an admin.
		button_home_a.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "AdminMainPage.fxml", "Home", email, firstName, lastName, accountType);
			}
		}));

		// Assigned the action that is caused by the "Home" button being clicked.
		button_home.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "VolunteerMainPage.fxml", "Home", email, firstName, lastName, accountType);
			}
		}));

		// Assigned the action that is caused by the "Create Event" button being clicked.
		button_create_event.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "CreateEvent.fxml", "Create an Event", email, firstName, lastName, accountType);
			}
		}));

		// Assigned the action that is caused by the "View Events" button being clicked by an admin.
		button_view_events_a.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "ViewEvents.fxml", "View Available Events", email, firstName, lastName, accountType);
			}
		}));

		// Assigned the action that is caused by the "View Events" button being clicked.
		button_view_events.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "ViewEvents.fxml", "View Available Events", email, firstName, lastName, accountType);
			}
		}));

		// Update the user information
		button_save.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.updateUser(event, email, tf_edit_first_name.getText(), tf_edit_last_name.getText(), accountType);
			}
		}));

	}
	
	public void setUserInformation(String firstName, String lastName, String email, String accountType) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.accountType = accountType;

		label_name.setText(firstName + " " + lastName);
		label_account_type.setText(accountType);

		label_display_name.setText(firstName + " " + lastName);
		label_display_email_type.setText(email + " - " + accountType);

		tf_edit_email.setText(email);
		tf_edit_first_name.setText(firstName);
		tf_edit_last_name.setText(lastName);

		// Configure the sidebar navigation
		if (accountType.equals("Admin")) {
			nav_admin.setVisible(true);
			nav_admin.setManaged(true);

			nav_volunteer.setVisible(false);
			nav_volunteer.setManaged(false);
		} else {
			nav_volunteer.setVisible(true);
			nav_volunteer.setManaged(true);

			nav_admin.setVisible(false);
			nav_admin.setManaged(false);
		}
	}
}
