package application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateEventController implements Initializable{
	
	@FXML private Button button_logout;
	@FXML private Button button_home;
	@FXML private Button button_create_event;
	@FXML private Button button_submit_event;
	
	@FXML private Label label_name;
	@FXML private Label label_account_type;

	@FXML private TextField tf_event_name;
	@FXML private TextField tf_location;

	@FXML private DatePicker dp_select_date;

	@FXML private Spinner spinner_spots;
	@FXML private Spinner spinner_start_time;
	@FXML private Spinner spinner_end_time;

	private String firstName = "",lastName = "", email = "", accountType = "";

	/**
	 * Method that runs listening for Action Events.
	 *
	 * @param location
	 * @param resources
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// Configure the Spinner
		SpinnerValueFactory<Integer> configureValues = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,500,10);
		this.spinner_spots.setValueFactory(configureValues);
		configureValues = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,24,8);
		this.spinner_start_time.setValueFactory(configureValues);
		configureValues = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,24,13);
		this.spinner_end_time.setValueFactory(configureValues);

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

		// Assigned the action that is caused by the "Create Event" button being clicked.
		button_create_event.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Already Here :)");
			}
		}));

		// Assigned the action that is caused by the "Submit Event" button being clicked.
		button_submit_event.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				String eventName = tf_event_name.getText();

				if (dp_select_date.getValue() == null || eventName.trim().equals("") || tf_location.getText().trim().isEmpty()) {
					System.out.println("Please fill in all information.");
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Please fill in all information fields.");
					alert.show();
					return;
				}
				String date = dp_select_date.getValue().toString();
				String location = tf_location.getText();

				DBUtils.createEvent(event, eventName, date, location, (int) spinner_spots.getValue(), (int) spinner_start_time.getValue(), (int) spinner_end_time.getValue(), email, firstName, lastName, accountType);
				System.out.println("You've created an event! :)");
			}
		});
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
