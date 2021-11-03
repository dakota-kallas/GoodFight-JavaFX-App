package application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.sql.*;
import java.util.ResourceBundle;

public class ViewEventsController implements Initializable{

	@FXML private Button button_logout;
	@FXML private Button button_home;
	@FXML private Button button_home_a;
	@FXML private Button button_view_events;
	@FXML private Button button_view_events_a;
	@FXML private Button button_donate_a;
	@FXML private Button button_profile;
	@FXML private Button button_create_event;

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
	@FXML private Button button_close_event;
	@FXML private Button button_event_register;

	@FXML private ListView listview_events;

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

		// Configure the ListView to display all available events
		Connection connection = null;
		PreparedStatement psGetEvents = null;
		ResultSet resultSet = null;
		listview_events.setStyle("-fx-font-family: \"Arial Rounded MT\"; -fx-font-size: 12px;");

		try {
			// Query the database to get all events on or after today's date
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/npdb", "root", "admin");
			psGetEvents = connection.prepareStatement("SELECT * FROM event WHERE DtStart >= ?");
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
				String event = "[" + eventID + "] " + eventName + "  Spots: " + spotsAvailable  + "  Location: " + eventLocation  + "  Date: " + dbDate;

				listview_events.getItems().add(event);
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
				if(accountType.equals("Volunteer")) {
					DBUtils.changeScene(event, "VolunteerMainPage.fxml", "Home", email, firstName, lastName, accountType);
				} else if (accountType.equals("Donor")){
					DBUtils.changeScene(event, "VolunteerMainPage.fxml", "Home", email, firstName, lastName, accountType);
				}
			}
		}));

		// Assigned the action that is caused by the "Home" button being clicked by an admin.
		button_home_a.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "AdminMainPage.fxml", "Home", email, firstName, lastName, accountType);
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

		// Assigned the action that is caused by the "View Events" button being clicked by an admin.
		button_view_events_a.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "ViewEvents.fxml", "View Available Events", email, firstName, lastName, accountType);
			}
		}));

		// Assigned the action that is caused by the "Donate" button being clicked by an admin.
		button_donate_a.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "DonatePage.fxml", "Donate", email, firstName, lastName, accountType);
			}
		}));

		// Assigned the action that is caused by the "View More" button being clicked.
		button_view_more.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// Check if an event is selected
				if (listview_events.getSelectionModel().isEmpty()) {
					// If an event is not selected, show an error
					System.out.println("No event selected.");
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Please select an event.");
					alert.show();
				} else {
					String selectedEvent = listview_events.getSelectionModel().getSelectedItem().toString();
					int eventId = Integer.valueOf(selectedEvent.substring(1, 5));

					// Query the database for more information on the event
					Connection connection = null;
					PreparedStatement psGetEvent = null;
					ResultSet resultSet = null;
					try {
						connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/npdb", "root", "admin");
						psGetEvent = connection.prepareStatement("SELECT Name, Location, DtStart, DtEnd, SpotsAvailable, sum(Amount) AS TotalDonations FROM event NATURAL LEFT JOIN donated_to NATURAL LEFT JOIN donation WHERE EventId = ?");
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
						String dateEnd = resultSet.getDate("DtEnd").toString();
						int donations = resultSet.getInt("TotalDonations");

						// Set the information for the popup
						label_event_id.setText("[" + eventId + "]");
						label_event_name.setText(eventName);
						label_event_location.setText(eventLocation);
						label_event_start_dt.setText(dateStart);
						label_event_end_dt.setText(dateEnd);
						label_event_spots_available.setText(spotsAvailable + "");
						label_event_donations.setText("$" + donations);

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
				}
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
				if (listview_events.getSelectionModel().isEmpty()) {
					// If an event is not selected, show an error
					System.out.println("No event selected.");
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Please select an event.");
					alert.show();
				} else {
					// If an event is selected, register for it
					String selectedEvent = listview_events.getSelectionModel().getSelectedItem().toString();
					int eventId = Integer.valueOf(selectedEvent.substring(1, 5));
					String date = selectedEvent.substring(selectedEvent.length() - 10);

					DBUtils.registerForEvent(event, eventId, date, email, firstName, lastName, accountType);
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

		pane_event_view.setVisible(false);
		pane_event_view.setManaged(false);

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
