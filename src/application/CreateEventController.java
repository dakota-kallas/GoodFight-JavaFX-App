/**
 * CreateEventController.java
 *
 * JavaFX Bookkeeping Software
 *
 * This is the controller class for when the create event page is loaded.
 *
 */

package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class CreateEventController implements Initializable{
	// Declare all JavaFX interactive controls
	@FXML private Button button_logout;
	@FXML private Button button_home;
	@FXML private Button button_create_event;
	@FXML private Button button_view_events;
	@FXML private Button button_donate;
	@FXML private Button button_reporting;
	@FXML private Button button_profile;
	@FXML private Button button_submit_event;
	
	@FXML private Label label_name;
	@FXML private Label label_account_type;

	@FXML private TextField tf_event_name;
	@FXML private TextField tf_location;
	@FXML private TextArea ta_description;

	@FXML private DatePicker dp_select_date;

	@FXML private Spinner spinner_spots;
	@FXML private Spinner spinner_start_time;
	@FXML private Spinner spinner_end_time;
	@FXML private Spinner spinner_start_time_ampm;
	@FXML private Spinner spinner_end_time_ampm;

	private String firstName = "",lastName = "", email = "", accountType = "";

	/**
	 * Method that runs listening for Action Events.
	 *
	 * @param location
	 * @param resources
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// Configure the Spinners
		SpinnerValueFactory<Integer> configureValues = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,500,10);
		this.spinner_spots.setValueFactory(configureValues);
		configureValues = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,12,8);
		this.spinner_start_time.setValueFactory(configureValues);
		configureValues = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,12,5);
		this.spinner_end_time.setValueFactory(configureValues);

		// Configure the DatePicker
		dp_select_date.setDayCellFactory(picker -> new DateCell() {
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				LocalDate today = LocalDate.now();
				setDisable(empty || date.compareTo(today) < 0 );
			}
		});

		// Configure the lists used in the spinner selectors
		ObservableList<String> end_ampm = FXCollections.observableArrayList("AM", "PM");
		ObservableList<String> start_ampm = FXCollections.observableArrayList("AM", "PM");
		SpinnerValueFactory<String> startValues = new SpinnerValueFactory.ListSpinnerValueFactory<String>(start_ampm);
		this.spinner_start_time_ampm.setValueFactory(startValues);
		startValues.setValue("AM");
		spinner_start_time_ampm.setValueFactory(startValues);
		SpinnerValueFactory<String> endValues = new SpinnerValueFactory.ListSpinnerValueFactory<String>(end_ampm);
		this.spinner_end_time_ampm.setValueFactory(endValues);
		endValues.setValue("PM");
		spinner_end_time_ampm.setValueFactory(endValues);

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

		// Assigned the action that is caused by the "Submit Event" button being clicked.
		button_submit_event.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				String eventName = tf_event_name.getText();

				// Data Validation: Check to ensure all fields were filled in
				if (dp_select_date.getValue() == null || eventName.trim().equals("") || tf_location.getText().trim().isEmpty()) {
					System.out.println("Please fill in all information.");
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Please fill in all information fields.");
					alert.show();
					return;
				}
				int startTime;
				int endTime = 0;

				// Calculate the start time of the event (convert to 24-hour time)
				if(spinner_start_time_ampm.getValue().toString().equals("PM") && ((int) spinner_start_time.getValue() != 12)) {
					startTime = (int) spinner_start_time.getValue() + 12;
				} else {
					startTime = (int) spinner_start_time.getValue();
				}

				// Calculate the end time of the event (convert to 24-hour time)
				if(spinner_end_time_ampm.getValue().toString().equals("PM") && ((int) spinner_end_time.getValue() != 12)) {
					endTime = (int) spinner_end_time.getValue() + 12;
				} else {
					endTime = (int) spinner_end_time.getValue();
				}

				// Check to see if the times were entered correctly
				if (startTime >= endTime) {
					System.out.println("Time error (Make sure the event starts before it ends).");
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Please make sure the event starts before it ends.");
					alert.show();
					return;
				}

				String date = dp_select_date.getValue().toString();
				String location = tf_location.getText();
				// If the description field is null, do not pass it to the event creation
				if(ta_description.getText().trim().isEmpty()) {
					DBUtils.createEvent(event, eventName, null, date, location, (int) spinner_spots.getValue(), startTime, endTime, email, firstName, lastName, accountType);
				} else {
					DBUtils.createEvent(event, eventName, ta_description.getText(), date, location, (int) spinner_spots.getValue(), startTime, endTime, email, firstName, lastName, accountType);
				}

				System.out.println("You've created an event! :)");
			}
		});
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
	}
}
