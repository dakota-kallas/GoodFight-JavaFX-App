/**
 * ViewEventsController.java
 *
 * JavaFX Bookkeeping Software
 *
 * This is the controller class for when the view events page is loaded.
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.sql.*;
import java.util.ResourceBundle;

public class ViewEventsController implements Initializable{
	// Declare all JavaFX interactive controls
	@FXML private Button button_logout;
	@FXML private Button button_home;
	@FXML private Button button_view_events;
	@FXML private Button button_donate;
	@FXML private Button button_profile;
	@FXML private Button button_create_event;
	@FXML private Button button_reporting;

	@FXML private Button button_view_more;

	// Event Details Pane
	@FXML private Pane pane_event_view;
	@FXML private Label label_event_id;
	@FXML private Label label_event_name;
	@FXML private Label label_event_location;
	@FXML private Label label_event_start_dt;
	@FXML private Label label_event_end_dt;
	@FXML private Label label_event_spots_available;
	@FXML private Label label_event_donations;
	@FXML private Label label_description;
	@FXML private Button button_close_event;
	@FXML private Button button_event_register;
	@FXML private Button button_cancel_event;

	@FXML private TableView tableview_results;

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
		tableview_results.setEditable(true);
		tableview_results.getColumns().clear();

		// Configure Columns for the TableView
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

		tableview_results.getColumns().addAll(nameCol, spotsAvailableCol, dateCol, startTimeCol, endTimeCol, locationCol);

		// Configure the ListView to display all available events
		Connection connection = null;
		PreparedStatement psGetEvents = null;
		ResultSet resultSet = null;

		try {
			// Query the database to get all events on or after today's date
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/npdb", "root", "admin");
			psGetEvents = connection.prepareStatement("SELECT * FROM event WHERE DtStart >= ? AND Active = 1");
			psGetEvents.setString(1, LocalDate.now().toString());
			resultSet = psGetEvents.executeQuery();

			// While there are still events in the result set, add them to the displayed list
			while(resultSet.next()) {
				String eventName = resultSet.getString("Name");
				String eventLocation = resultSet.getString("Location");
				String eventStTime = resultSet.getTime("DtStart").toString();
				String eventEdTime = resultSet.getTime("DtEnd").toString();
				int eventID = resultSet.getInt("EventId");
				int spotsAvailable = resultSet.getInt("SpotsAvailable");
				String dbDate = resultSet.getDate("DtStart").toString();

				tableview_results.getItems().add(new Event(eventID + "", spotsAvailable + "", dbDate, eventStTime, eventEdTime, eventName, eventLocation));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// Close all statements that have been used to query and connect to the database.
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

		// Assign the action to navigate the profile page once the "Profile" button is clicked.
		button_profile.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "ViewProfile.fxml", "My Profile", email, firstName, lastName, accountType);
			}
		});

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

		// Assigned the action that is caused by the "View More" button being clicked.
		button_view_more.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// Check if an event is selected
				if (tableview_results.getSelectionModel().isEmpty()) {
					// If an event is not selected, show an error
					System.out.println("No event selected.");
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Please select an event.");
					alert.show();
				} else {
					Event currentEvent =  (Event) tableview_results.getSelectionModel().getSelectedItem();
					int eventId = Integer.valueOf(currentEvent.getEventId());

					// Query the database for more information on the event
					Connection connection = null;
					PreparedStatement psGetEvent = null;
					ResultSet resultSet = null;
					try {
						// Connect to the SQL server and generate the query
						connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/npdb", "root", "admin");
						psGetEvent = connection.prepareStatement("SELECT Name, Location, DtStart, DtEnd, SpotsAvailable, Description, sum(Amount) AS TotalDonations FROM event NATURAL LEFT JOIN donated_to NATURAL LEFT JOIN donation WHERE EventId = ?");
						psGetEvent.setString(1, eventId + "");
						resultSet = psGetEvent.executeQuery();

						// Collect the attribute data from the event
						resultSet.next();
						String eventName = resultSet.getString("Name");
						String eventLocation = resultSet.getString("Location");
						String eventStTime = resultSet.getTime("DtStart").toString();
						String eventEdTime = resultSet.getTime("DtEnd").toString();
						int spotsAvailable = resultSet.getInt("SpotsAvailable");
						String dateStart = resultSet.getDate("DtStart").toString();
						int startTime = Integer.valueOf(resultSet.getTime("DtStart").toString().substring(0,2));
						String dateEnd = resultSet.getDate("DtEnd").toString();
						int endTime = Integer.valueOf(resultSet.getTime("DtEnd").toString().substring(0,2));
						int donations = resultSet.getInt("TotalDonations");
						String description = resultSet.getString("Description");

						// Determine if the time is in AM or PM
						String startAMPM = "AM";
						String endAMPM = "AM";
						if(startTime > 12) {
							startAMPM = "PM";
							startTime = startTime % 12;
						}
						if(endTime > 12) {
							endAMPM = "PM";
							endTime = endTime % 12;
						}

						// Set the information for the popup
						label_event_id.setText("[" + eventId + "]");
						label_event_name.setText(eventName);
						label_event_location.setText(eventLocation);
						label_event_start_dt.setText(dateStart + " @ " + startTime + startAMPM);
						label_event_end_dt.setText(dateEnd + " @ " + endTime + endAMPM);
						label_event_spots_available.setText(spotsAvailable + "");
						label_event_donations.setText("$" + donations);
						if(description != null) {
							label_description.setText(description);
						}

					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						// Close all statements that have been used to query and connect to the database.
						if (resultSet != null) {
							try {
								resultSet.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						if (psGetEvent != null) {
							try {
								psGetEvent.close();
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

					// Make the popup visible
					pane_event_view.setVisible(true);
					pane_event_view.setManaged(true);
					if(!accountType.equals("Admin")) {
						button_cancel_event.setVisible(false);
						button_cancel_event.setManaged(false);
					}
					else {
						button_cancel_event.setVisible(true);
						button_cancel_event.setManaged(true);
					}
				}
			}
		}));

		// Assigned the action that is caused by the "Donate" button being clicked by an admin.
		button_cancel_event.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				int eventId = Integer.valueOf(label_event_id.getText().substring(1, 5));
				DBUtils.cancelEvent(event, eventId);
				DBUtils.changeScene(event, "ViewEvents.fxml", "View Events", email, firstName, lastName, accountType);
			}
		}));

		// Set the action for when the user clicks the "Register" button
		button_close_event.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// Make the popup invisible
				pane_event_view.setVisible(false);
				pane_event_view.setManaged(false);
			}
		}));

		// Set the action for when the user clicks the "Register" button
		button_event_register.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// Check if an event is selected
				if (tableview_results.getSelectionModel().isEmpty()) {
					// If an event is not selected, show an error
					System.out.println("No event selected.");
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Please select an event.");
					alert.show();
				} else {
					// If an event is selected, register for it
					Event currentEvent =  (Event) tableview_results.getSelectionModel().getSelectedItem();
					int eventId = Integer.valueOf(currentEvent.getEventId());
					String date = currentEvent.getDate();

					DBUtils.registerForEvent(event, eventId, date, email, firstName, lastName, accountType);
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
		} else if (accountType.equals("Volunteer")) {
			button_create_event.setVisible(false);
			button_create_event.setManaged(false);

			button_reporting.setVisible(false);
			button_reporting.setManaged(false);

			button_donate.setVisible(false);
			button_donate.setManaged(false);
		}

		pane_event_view.setVisible(false);
		pane_event_view.setManaged(false);
	}

	/**
	 * An inner class that is used to create a datatype for Events.
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
