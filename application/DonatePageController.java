package application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class DonatePageController implements Initializable{
	
	@FXML private Button button_logout;
	@FXML private Button button_home;
	@FXML private Button button_create_event;
	@FXML private Button button_view_events;
	@FXML private Button button_profile;
	@FXML private Button button_donate;

	@FXML private ListView listview_events;
	@FXML private RadioButton rd_restricted;
	@FXML private RadioButton rd_unrestricted;
	@FXML private RadioButton rd_25;
	@FXML private RadioButton rd_50;
	@FXML private RadioButton rd_100;
	@FXML private RadioButton rd_250;
	@FXML private RadioButton rd_other;
	@FXML private TextField tf_other_amount;
	@FXML private Button button_submit_donation;
	@FXML private Label label_contribution;
	
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

		//Configure the radio buttons
		ToggleGroup donationTypeToggle = new ToggleGroup();
		rd_restricted.setToggleGroup(donationTypeToggle);
		rd_unrestricted.setToggleGroup((donationTypeToggle));
		rd_unrestricted.setSelected(true);

		ToggleGroup donationAmountToggle = new ToggleGroup();
		rd_25.setToggleGroup(donationAmountToggle);
		rd_50.setToggleGroup((donationAmountToggle));
		rd_100.setToggleGroup(donationAmountToggle);
		rd_250.setToggleGroup((donationAmountToggle));
		rd_other.setToggleGroup(donationAmountToggle);
		rd_other.setSelected(true);

		// Assigned the action that is caused by the "Logout" button being clicked.
		button_logout.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "LogIn.fxml", "Log in!", null, null, null,null);
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
				DBUtils.changeScene(event, "AdminMainPage.fxml", "Home", email, firstName, lastName, accountType);
			}
		}));

		// Assigned the action that is caused by the "Create Event" button being clicked.
		button_create_event.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "CreateEvent.fxml", "Create an Event", email, firstName, lastName, accountType);
			}
		}));

		// Assigned the action that is caused by the "View Events" button being clicked.
		button_view_events.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "ViewEvents.fxml", "View Available Events", email, firstName, lastName, accountType);
			}
		}));

		// Assigned the action that is caused by the "Donate" button being clicked.
		button_donate.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "DonatePage.fxml", "Donate", email, firstName, lastName, accountType);
			}
		}));

		// Assigned the action that is caused by the "Submit Donation" button being clicked.
		button_submit_donation.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				boolean valid = true;
				String toggleType = ((RadioButton) donationTypeToggle.getSelectedToggle()).getText();
				String donation = ((RadioButton) donationAmountToggle.getSelectedToggle()).getText();
				int amount = 0;
				// Check what donation amount was selected
				if(donation.equals("other")) {
					if(tf_other_amount.getText().trim().matches("[0-9]+")) {
						amount = Integer.valueOf(tf_other_amount.getText().trim());
					} else {
						valid = false;
						System.out.println("Invalid donation amount.");
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setContentText("Please enter a valid donation amount.");
						alert.show();
					}
				} else {
					amount = Integer.valueOf(donation);
				}
				// Process the donation depending on donation type
				if (toggleType.equals("General (Unrestricted)") && valid) {
					DBUtils.processUnrestrictedDonation(event, amount, email, firstName, lastName, accountType);
				} else {
					// Check if an event is selected
					if (listview_events.getSelectionModel().isEmpty()) {
						// If an event is not selected, show an error
						System.out.println("No event selected.");
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setContentText("Please select an event.");
						alert.show();
					} else {
						// If an event is selected, donate to said event
						String selectedEvent = listview_events.getSelectionModel().getSelectedItem().toString();
						int eventId = Integer.valueOf(selectedEvent.substring(1, 5));

						DBUtils.processRestrictedDonation(event, eventId, amount, email, firstName, lastName, accountType);
					}
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

		// Configure the ListView to display all the events
		Connection connection = null;
		PreparedStatement psGetEvents = null;
		PreparedStatement psGetDonations = null;
		ResultSet resultSet = null;

		try {
			// Query the database to get all events on or after today's date
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/npdb", "root", "admin");
			psGetEvents = connection.prepareStatement("SELECT * FROM event WHERE DtStart >= ?");
			psGetEvents.setString(1, LocalDate.now().toString());
			resultSet = psGetEvents.executeQuery();
			listview_events.setStyle("-fx-font-family: \"Arial Rounded MT\"; -fx-font-size: 12px;");

			// While there are still events in the result set, add them to the displayed list
			while(resultSet.next()) {
				String eventName = resultSet.getString("Name");
				String eventLocation = resultSet.getString("Location");
				String eventStTime = resultSet.getTime("DtStart").toString();
				String eventEdTime = resultSet.getTime("DtEnd").toString();
				int eventID = resultSet.getInt("EventId");
				int spotsAvailable = resultSet.getInt("SpotsAvailable");
				String dbDate = resultSet.getDate("DtStart").toString();
				String event = "[" + eventID + "] " + eventName + "  Spots: " + spotsAvailable  + "  Location: " + eventLocation  + "  Date: " + dbDate;

				listview_events.getItems().add(event);
			}

			psGetDonations = connection.prepareStatement("SELECT sum(Amount) AS total_amount FROM donation NATURAL JOIN donated_by WHERE Email = ?");
			psGetDonations.setString(1, email);
			resultSet = psGetDonations.executeQuery();
			if(resultSet.isBeforeFirst()) {
				resultSet.next();
				label_contribution.setText("$" + resultSet.getInt("total_amount"));
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
			if (psGetDonations != null) {
				try {
					psGetDonations.close();
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
