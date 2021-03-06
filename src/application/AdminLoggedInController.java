/**
 * AdminLoggedInController.java
 *
 * JavaFX Bookkeeping Software
 *
 * This is the controller class for when the home page is loaded for an admin account.
 *
 */

package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

public class AdminLoggedInController implements Initializable{
	// Declare all JavaFX interactive controls
	@FXML private Button button_logout;
	@FXML private Button button_home;
	@FXML private Button button_create_event;
	@FXML private Button button_view_events;
	@FXML private Button button_profile;
	@FXML private Button button_donate;
	@FXML private Button button_reporting;

	@FXML private TableView tableview_results;
	@FXML private CheckBox checkbox_cancelled;
	@FXML private Label label_cancelled;
	@FXML private Button button_cancel;
	
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
		label_cancelled.setVisible(false);

		// Configure Columns for the TableView
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

		// Listener to update GUI and Table when the "Show cancelled?" checkbox is changed
		checkbox_cancelled.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue ov,Boolean old_val, Boolean new_val) {
				tableview_results.getItems().clear();

				// Set up the connection to the SQL server
				Connection connection = null;
				PreparedStatement psGetEvents = null;
				ResultSet resultSet = null;
				int active = 0;
				label_cancelled.setVisible(true);

				// Check if the "Show cancelled?" box is not checked
				if(!new_val) {
					active = 1;
					label_cancelled.setVisible(false);
				}

				try {
					// Connect to the SQL server and generate the query
					connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/npdb", "root", "admin");
					psGetEvents = connection.prepareStatement("SELECT Name, Location, DtStart, DtEnd, EventId, SpotsAvailable, Email FROM event NATURAL JOIN attended WHERE email = ? AND DtStart >= ? AND Active = ?");
					psGetEvents.setString(1, email);
					psGetEvents.setString(2, LocalDate.now().toString());
					psGetEvents.setInt(3, active);
					resultSet = psGetEvents.executeQuery();

					// Iterate through the events in the query results and add them to the TableView
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
			}
		});

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

		// Assigned the action that is caused by the "Reporting" button being clicked.
		button_reporting.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "Reporting.fxml", "Reporting", email, firstName, lastName, accountType);
			}
		}));

		// Assigned the action that is caused by the "View Events" button being clicked.
		button_cancel.setOnAction((new EventHandler<ActionEvent>() {
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

					DBUtils.cancelRegistration(event, eventId, date, email, firstName, lastName, accountType);
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

		// Configure the TableView to display all the user's events
		Connection connection = null;
		PreparedStatement psGetEvents = null;
		ResultSet resultSet = null;

		try {
			// Connect to the SQL server
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/npdb", "root", "admin");
			psGetEvents = connection.prepareStatement("SELECT Name, Location, DtStart, DtEnd, EventId, SpotsAvailable, Email FROM event NATURAL JOIN attended WHERE email = ? AND DtStart >= ? AND Active = 1");
			psGetEvents.setString(1, email);
			psGetEvents.setString(2, LocalDate.now().toString());
			resultSet = psGetEvents.executeQuery();

			// Iterate through the events in the query results and add them to the TableView
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
			// Close the SQL statements and connection
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


