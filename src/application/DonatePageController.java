/**
 * DonatePageController.java
 *
 * JavaFX Bookkeeping Software
 *
 * This is the controller class for when the donate page is loaded.
 *
 */

package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class DonatePageController implements Initializable{
	// Declare all JavaFX interactive controls
	@FXML private Button button_logout;
	@FXML private Button button_home;
	@FXML private Button button_create_event;
	@FXML private Button button_view_events;
	@FXML private Button button_profile;
	@FXML private Button button_donate;
	@FXML private Button button_reporting;

	@FXML private TableView tableview_results;
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
	@FXML private Label label_org;
	@FXML private Label label_org_total;
	
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
		// Configure the TableView
		tableview_results.setEditable(true);
		tableview_results.getColumns().clear();

		// Configure Columns for the TableView
		TableColumn<ReportingController.User, String> eventIdCol = new TableColumn<>("Event ID");
		eventIdCol.setCellValueFactory(new PropertyValueFactory<>("eventId"));
		TableColumn<ReportingController.User, String> nameCol = new TableColumn<>("Name");
		nameCol.setMinWidth(110);
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		TableColumn<ReportingController.User, String> donationsCol = new TableColumn<>("Donations");
		donationsCol.setCellValueFactory(new PropertyValueFactory<>("donations"));
		TableColumn<ReportingController.User, String> dateCol = new TableColumn<>("Date");
		dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
		TableColumn<ReportingController.User, String> startTimeCol = new TableColumn<>("Starts At");
		startTimeCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
		TableColumn<ReportingController.User, String> endTimeCol = new TableColumn<>("Ends At");
		endTimeCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));
		TableColumn<ReportingController.User, String> locationCol = new TableColumn<>("Location");
		locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));

		tableview_results.getColumns().addAll(eventIdCol, nameCol, donationsCol, dateCol, startTimeCol, endTimeCol, locationCol);

		// Configure the TableView to display all the user's events
		Connection connection = null;
		PreparedStatement psGetEvents = null;
		ResultSet resultSet = null;

		try {
			// Connect to the SQL server and generate the query
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/npdb", "root", "admin");
			psGetEvents = connection.prepareStatement("SELECT Name, Location, DtStart, DtEnd, event.EventId, Donations FROM event NATURAL LEFT JOIN (Select EventId, sum(Amount) AS Donations FROM donated_to NATURAL JOIN donation GROUP BY EventId) AS TotDonations WHERE DtStart >= ? AND Active = 1 GROUP BY event.EventId");
			psGetEvents.setString(1, LocalDate.now().toString());
			resultSet = psGetEvents.executeQuery();

			// Iterate through the events in the query results and add them to the TableView
			while(resultSet.next()) {
				String eventName = resultSet.getString("Name");
				String eventLocation = resultSet.getString("Location");
				String eventStTime = resultSet.getTime("DtStart").toString();
				String eventEdTime = resultSet.getTime("DtEnd").toString();
				int eventID = resultSet.getInt("EventId");
				String donations = "$" + resultSet.getInt("Donations");
				String Date = resultSet.getDate("DtStart").toString();

				tableview_results.getItems().add(new Event(eventID + "", donations + "", Date, eventStTime, eventEdTime, eventName, eventLocation));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// Close all the SQL statements and connection
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
				if(accountType.equals("Volunteer") || accountType.equals("Donor")) {
					DBUtils.changeScene(event, "VolunteerMainPage.fxml", "Home", email, firstName, lastName, accountType);
				} else if (accountType.equals("Admin")){
					DBUtils.changeScene(event, "AdminMainPage.fxml", "Home", email, firstName, lastName, accountType);
				}
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

		// Assigned the action that is caused by the "Reporting" button being clicked.
		button_reporting.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "Reporting.fxml", "Reporting", email, firstName, lastName, accountType);
			}
		}));

		// Assigned the action that is caused by the "Submit Donation" button being clicked.
		button_submit_donation.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// Get the amount and type of donation from the user
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
					if (tableview_results.getSelectionModel().isEmpty()) {
						// If an event is not selected, show an error
						System.out.println("No event selected.");
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setContentText("Please select an event.");
						alert.show();
					} else {
						// If an event is selected, donate to said event
						Event currentEvent =  (Event) tableview_results.getSelectionModel().getSelectedItem();
						int eventId = Integer.valueOf(currentEvent.getEventId());

						DBUtils.processRestrictedDonation(event, eventId, amount, email, firstName, lastName, accountType);
					}
				}
			}
		}));
	}

	/**
	 * Method used set a user's information on the current page.
	 *
	 * @param firstName: the user's first name
	 * @param lastName: the user's last name
	 * @param email: the user's unique email
	 * @param accountType: the user's account type
	 */
	public void setUserInformation(String firstName, String lastName, String email, String accountType) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.accountType = accountType;

		label_name.setText(firstName + " " + lastName);
		label_account_type.setText(accountType);

		// Configure the sidebar navigation
		if (accountType.equals("Donor")) {
			button_create_event.setVisible(false);
			button_create_event.setManaged(false);

			button_reporting.setVisible(false);
			button_reporting.setManaged(false);

			label_org.setVisible(false);
			label_org_total.setVisible(false);
		} else if (accountType.equals("Volunteer")) {
			button_create_event.setVisible(false);
			button_create_event.setManaged(false);

			button_reporting.setVisible(false);
			button_reporting.setManaged(false);

			button_donate.setVisible(false);
			button_donate.setManaged(false);

			label_org.setVisible(false);
			label_org_total.setVisible(false);
		}

		// Find the total donation amount of the user
		Connection connection = null;
		PreparedStatement psGetDonations = null;
		ResultSet resultSet = null;

		try {
			// Query the database to get all events on or after today's date
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/npdb", "root", "admin");

			psGetDonations = connection.prepareStatement("SELECT sum(Amount) AS total_amount FROM donation NATURAL JOIN donated_by WHERE Email = ?");
			psGetDonations.setString(1, email);
			resultSet = psGetDonations.executeQuery();
			if(resultSet.isBeforeFirst()) {
				resultSet.next();
				label_contribution.setText("$" + resultSet.getInt("total_amount"));
			}

			// Get the organization total
			psGetDonations = connection.prepareStatement("SELECT sum(Amount) AS total_amount FROM donation");
			resultSet = psGetDonations.executeQuery();
			if(resultSet.isBeforeFirst()) {
				resultSet.next();
				label_org_total.setText("$" + resultSet.getInt("total_amount"));
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

	/**
	 * An inner class that is used to create a datatype for Events.
	 */
	protected static class Event {

		private final SimpleStringProperty eventId;
		private final SimpleStringProperty donations;
		private final SimpleStringProperty date;
		private final SimpleStringProperty startTime;
		private final SimpleStringProperty endTime;
		private final SimpleStringProperty name;
		private final SimpleStringProperty location;

		private Event(String eId, String donation, String dt, String start, String end, String n, String loc) {
			this.eventId = new SimpleStringProperty(eId);
			this.donations = new SimpleStringProperty(donation);
			this.date = new SimpleStringProperty(dt);
			this.startTime = new SimpleStringProperty(start);
			this.endTime = new SimpleStringProperty(end);
			this.name = new SimpleStringProperty(n);
			this.location = new SimpleStringProperty(loc);
		}

		public String getEventId() {
			return eventId.get();
		}

		public void setEventId(String n) {
			eventId.set(n);
		}

		public String getDonations() {
			return donations.get();
		}

		public void setDonations(String donation) {
			donations.set(donation);
		}

		public String getDate() {
			return date.get();
		}

		public void setDate(String dt) {
			date.set(dt);
		}

		public String getStartTime() {
			return startTime.get();
		}

		public void setStartTime(String start) {
			startTime.set(start);
		}

		public String getEndTime() {
			return endTime.get();
		}

		public void setEndTime(String end) {
			endTime.set(end);
		}

		public String getName() {
			return name.get();
		}

		public void setName(String n) {
			name.set(n);
		}

		public String getLocation() {
			return location.get();
		}

		public void setLocation(String loc) {
			location.set(loc);
		}
	}
}
