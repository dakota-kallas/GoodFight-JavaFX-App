package application;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ViewEventsController implements Initializable{
	
	@FXML private Button button_logout;
	@FXML private Button button_home;
	@FXML private Button button_view_events;
	@FXML private Button button_register;

	@FXML private ListView listview_events;
	
	@FXML private Label label_name;
	@FXML private Label label_account_type;

	private String firstName = "",lastName = "", email = "", accountType = "";

	/**
	 * Method that runs listening for Action Events.
	 *
	 * @param location
	 * @param resources
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// Configure the ListView to display all available events
		Connection connection = null;
		PreparedStatement psGetEvents = null;
		ResultSet resultSet = null;

		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/npdb", "root", "admin");
			psGetEvents = connection.prepareStatement("SELECT * FROM event");
			resultSet = psGetEvents.executeQuery();

			while(resultSet.next()) {
				String eventName = resultSet.getString("Name");
				int eventID = resultSet.getInt("EventId");
				int spotsAvailable = resultSet.getInt("SpotsAvailable");
				String Date = resultSet.getDate("DtStart").toString();
				String event = "[" + eventID + "] " + eventName + " Spots:" + spotsAvailable + " Date:" + Date ;
				listview_events.getItems().add(event);
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

		// Assigned the action that is caused by the "Logout" button being clicked.
		button_logout.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				DBUtils.changeScene(event, "LogIn.fxml", "Log in!", null, null, null,null);
			}
		});

		// Assigned the action that is caused by the "Home" button being clicked.
		button_home.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(accountType.equals("Volunteer") || accountType.equals("Donor")) {
					DBUtils.changeScene(event, "VolunteerMainPage.fxml", "Home", email, firstName, lastName, accountType);
				} else if (accountType.equals("Admin")){
					DBUtils.changeScene(event, "AdminMainPage.fxml", "Home", email, firstName, lastName, accountType);
				}
			}
		}));

		// Assigned the action that is caused by the "Create Event" button being clicked.
		button_view_events.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Already Here :)");
			}
		}));

		button_register.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String selectedEvent = listview_events.getSelectionModel().getSelectedItem().toString();
				int eventId = Integer.valueOf(selectedEvent.substring(1, 5));
				String date = selectedEvent.substring(selectedEvent.length() - 10);

				DBUtils.registerForEvent(event, eventId, date, email, firstName, lastName, accountType);
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
	}
}
