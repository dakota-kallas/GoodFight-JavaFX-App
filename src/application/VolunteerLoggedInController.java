package application;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.control.ListView;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class VolunteerLoggedInController implements Initializable{
	
	@FXML private Button button_logout;
	@FXML private Button button_home;
	@FXML private Button button_profile;
	@FXML private Button button_view_events;

	@FXML private ListView listview_my_events;
	@FXML private Button button_cancel;
	
	@FXML private Label label_name;
	@FXML private Label label_account_type;

	private String firstName = "",lastName = "", email = "", accountType = "";
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Configure the style of the events list
		listview_my_events.setStyle("-fx-font-family: \"Arial Rounded MT\"; -fx-font-size: 12px;");

		// Assigned the action that is caused by the "Logout" button being clicked.
		button_logout.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "LogIn.fxml", "Log in!", null, null,null, null);
			}
		});

		// Assign the action to navigate the profile page once the "Profile" button is clicked.
		button_profile.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "ViewProfile.fxml", "My Profile", email, firstName, lastName, accountType);
			}
		});

		// Assigned the action that is caused by the "Home" button being clicked.
		button_home.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Already Here :)");
			}
		}));

		// Assigned the action that is caused by the "View Events" button being clicked.
		button_view_events.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "ViewEvents.fxml", "View Available Events", email, firstName, lastName, accountType);
			}
		}));

		// Assigned the action that is caused by the "View Events" button being clicked.
		button_cancel.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// Check if an event is selected
				if (listview_my_events.getSelectionModel().isEmpty()) {
					// If an event is not selected, show an error
					System.out.println("No event selected.");
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Please select an event.");
					alert.show();
				} else {
					String selectedEvent = listview_my_events.getSelectionModel().getSelectedItem().toString();
					int eventId = Integer.valueOf(selectedEvent.substring(1, 5));
					String date = selectedEvent.substring(selectedEvent.length() - 10);

					DBUtils.cancelRegistration(event, eventId, date, email, firstName, lastName, accountType);
				}
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

		// Configure the ListView to display all the user's events
		Connection connection = null;
		PreparedStatement psGetEvents = null;
		ResultSet resultSet = null;

		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/npdb", "root", "admin");
			psGetEvents = connection.prepareStatement("SELECT Name, Location, DtStart, DtEnd, EventId, SpotsAvailable, Email FROM event NATURAL JOIN attended WHERE email = ?");
			psGetEvents.setString(1, email);
			resultSet = psGetEvents.executeQuery();

			while(resultSet.next()) {
				String eventName = resultSet.getString("Name");
				String eventLocation = resultSet.getString("Location");
				String eventStTime = resultSet.getTime("DtStart").toString();
				String eventEdTime = resultSet.getTime("DtEnd").toString();
				int eventID = resultSet.getInt("EventId");
				int spotsAvailable = resultSet.getInt("SpotsAvailable");
				String Date = resultSet.getDate("DtStart").toString();
				String event = "[" + eventID + "] " + eventName + "  Spots: " + spotsAvailable  + "  Location: " + eventLocation  + "  Date: " + Date;

				listview_my_events.getItems().add(event);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (psGetEvents != null) {
				try {
					psGetEvents.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
