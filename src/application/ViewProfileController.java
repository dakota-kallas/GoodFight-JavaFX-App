package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ViewProfileController implements Initializable{
	
	@FXML private Button button_logout;
	@FXML private Button button_home;
	@FXML private Button button_create_event;
	@FXML private Button button_view_events;
	@FXML private Button button_donate;
	@FXML private Button button_profile;
	@FXML private Button button_reporting;

	@FXML private TableView tableview_results;
	@FXML private Button button_save;

	@FXML private TextField tf_edit_first_name;
	@FXML private TextField tf_edit_last_name;
	@FXML private TextField tf_edit_email;

	@FXML private Label label_display_name;
	@FXML private Label label_display_email_type;
	
	@FXML private Label label_name;
	@FXML private Label label_account_type;

	@FXML private VBox navbar;

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
		tableview_results.setEditable(false);
		tableview_results.getColumns().clear();

		TableColumn<ReportingController.User, String> eventIdCol = new TableColumn<>("Event ID");
		eventIdCol.setCellValueFactory(new PropertyValueFactory<>("eventId"));
		TableColumn<ReportingController.User, String> nameCol = new TableColumn<>("Name");
		nameCol.setMinWidth(110);
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		TableColumn<ReportingController.User, String> spotsAvailableCol = new TableColumn<>("Spots");
		spotsAvailableCol.setCellValueFactory(new PropertyValueFactory<>("spotsAvailable"));
		TableColumn<ReportingController.User, String> dateCol = new TableColumn<>("Date");
		dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
		TableColumn<ReportingController.User, String> startTimeCol = new TableColumn<>("Starts At");
		startTimeCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
		TableColumn<ReportingController.User, String> endTimeCol = new TableColumn<>("Ends At");
		endTimeCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));
		TableColumn<ReportingController.User, String> locationCol = new TableColumn<>("Location");
		locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));

		tableview_results.getColumns().addAll(eventIdCol, nameCol, spotsAvailableCol, dateCol, startTimeCol, endTimeCol, locationCol);

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

		// Assigned the action that is caused by the "Donate" button being clicked by an admin.
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

		// Update the user information
		button_save.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.updateUser(event, email, tf_edit_first_name.getText(), tf_edit_last_name.getText(), accountType);
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

		label_display_name.setText(firstName + " " + lastName);
		label_display_email_type.setText(email + " - " + accountType);

		tf_edit_email.setText(email);
		tf_edit_first_name.setText(firstName);
		tf_edit_last_name.setText(lastName);

		// Configure the sidebar navigation
		if (accountType.equals("Donor")) {
			button_create_event.setVisible(false);
			button_create_event.setManaged(false);

			button_reporting.setVisible(false);
			button_reporting.setManaged(false);
		} else if (accountType.equals("Volunteer")) {
			button_create_event.setVisible(false);
			button_create_event.setManaged(false);

			button_reporting.setVisible(false);
			button_reporting.setManaged(false);

			button_donate.setVisible(false);
			button_donate.setManaged(false);
		}

		// Configure the ListView to display all the user's events
		Connection connection = null;
		PreparedStatement psGetEvents = null;
		ResultSet resultSet = null;

		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/npdb", "root", "admin");
			psGetEvents = connection.prepareStatement("SELECT Name, Location, DtStart, DtEnd, EventId, SpotsAvailable, Email FROM event NATURAL JOIN attended WHERE email = ? AND DtStart < ?");
			psGetEvents.setString(1, email);
			psGetEvents.setString(2, LocalDate.now().toString());
			resultSet = psGetEvents.executeQuery();

			while(resultSet.next()) {
				String eventName = resultSet.getString("Name");
				String eventLocation = resultSet.getString("Location");
				String eventStTime = resultSet.getTime("DtStart").toString();
				String eventEdTime = resultSet.getTime("DtEnd").toString();
				int eventID = resultSet.getInt("EventId");
				int spotsAvailable = resultSet.getInt("SpotsAvailable");
				String Date = resultSet.getDate("DtStart").toString();

				tableview_results.getItems().add(new Event(eventID + "", spotsAvailable + "", Date, eventStTime, eventEdTime, eventName, eventLocation));
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

	/**
	 * An inner class that is used to create a datatype that is populated in the reporting table for Events.
	 */
	protected static class Event {

		private final SimpleStringProperty eventId;
		private final SimpleStringProperty spotsAvailable;
		private final SimpleStringProperty date;
		private final SimpleStringProperty startTime;
		private final SimpleStringProperty endTime;
		private final SimpleStringProperty name;
		private final SimpleStringProperty location;

		private Event(String eId, String spots, String dt, String start, String end, String n, String loc) {
			this.eventId = new SimpleStringProperty(eId);
			this.spotsAvailable = new SimpleStringProperty(spots);
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

		public String getSpotsAvailable() {
			return spotsAvailable.get();
		}

		public void setSpotsAvailable(String spots) {
			spotsAvailable.set(spots);
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
