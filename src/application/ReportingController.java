package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class ReportingController implements Initializable{
	
	@FXML private Button button_logout;
	@FXML private Button button_home;
	@FXML private Button button_create_event;
	@FXML private Button button_view_events;
	@FXML private Button button_profile;
	@FXML private Button button_donate;
	@FXML private Button button_reporting;

	@FXML private TableView tableview_results;
	@FXML private Button button_search;
	@FXML private ChoiceBox cb_reporting_type;
	@FXML private ChoiceBox cb_user_attributes;
	@FXML private TextField tf_search;
	@FXML private Button button_delete_selected;
	@FXML private Button button_set_inactive;
	@FXML private Button button_set_active;
	
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
		// Configure the style of the events list
		tableview_results.setEditable(true);
		tableview_results.getColumns().clear();
		// Configure the choice boxes
		String[] reportingTypes = new String[]{"User", "Event"};

		cb_reporting_type.setItems(FXCollections.observableArrayList("User", "Event"));
		cb_reporting_type.setValue("User"); // set the default value
		cb_reporting_type.setTooltip(new Tooltip("Select report type"));
		cb_user_attributes.setItems(FXCollections.observableArrayList("FirstName", "LastName", "Email", "Type", "Active"));
		cb_user_attributes.setTooltip(new Tooltip("Select the search attribute"));

		TableColumn<User, String> firstNameCol = new TableColumn<>("First Name");
		firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
		TableColumn<User, String> lastNameCol = new TableColumn<>("Last Name");
		lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
		TableColumn<User, String> emailCol = new TableColumn<>("Email");
		emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
		TableColumn<User, String> aTypeCol = new TableColumn<>("Type");
		aTypeCol.setCellValueFactory(new PropertyValueFactory<>("accType"));
		TableColumn<User, String> activeCol = new TableColumn<>("Active");
		activeCol.setCellValueFactory(new PropertyValueFactory<>("accActive"));
		TableColumn<User, String> totalDonationsCol = new TableColumn<>("Donations");
		totalDonationsCol.setMinWidth(80);
		totalDonationsCol.setCellValueFactory(new PropertyValueFactory<>("donations"));
		TableColumn<User, String> totalHoursCol = new TableColumn<>("Volunteer Hours");
		totalHoursCol.setMinWidth(120);
		totalHoursCol.setCellValueFactory(new PropertyValueFactory<>("hours"));

		tableview_results.getColumns().addAll(firstNameCol, lastNameCol, emailCol, aTypeCol, activeCol, totalDonationsCol, totalHoursCol);
		button_delete_selected.setVisible(false);
		button_delete_selected.setManaged(false);

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

		// Assigned the action that is caused by the "Donate" button being clicked.
		button_reporting.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBUtils.changeScene(event, "Reporting.fxml", "Reporting", email, firstName, lastName, accountType);
			}
		}));

		// Assigned the action that is caused by the "Delete Selected" button being clicked.
		button_delete_selected.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(tableview_results.getSelectionModel().isEmpty()) {
					System.out.println("No item selected.");
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Please select an event to delete.");
					alert.show();
					return;
				}
				Event currentEvent =  (Event) tableview_results.getSelectionModel().getSelectedItem();
				int eventId = Integer.valueOf(currentEvent.getEventId());
				DBUtils.cancelEvent(event, eventId);
				DBUtils.changeScene(event, "Reporting.fxml", "Reporting", email, firstName, lastName, accountType);
			}
		}));

		// Assigned the action that is caused by the "Set Inactive" button being clicked.
		button_set_inactive.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(tableview_results.getSelectionModel().isEmpty()) {
					System.out.println("No item selected.");
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Please select an user to update.");
					alert.show();
					return;
				}

				User currentUser =  (User) tableview_results.getSelectionModel().getSelectedItem();
				DBUtils.setUserActiveStatus(event, currentUser.getEmail(), 0);
				DBUtils.changeScene(event, "Reporting.fxml", "Reporting", email, firstName, lastName, accountType);
			}
		}));

		// Assigned the action that is caused by the "Set Active" button being clicked.
		button_set_active.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(tableview_results.getSelectionModel().isEmpty()) {
					System.out.println("No item selected.");
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Please select a user to update.");
					alert.show();
					return;
				}

				User currentUser =  (User) tableview_results.getSelectionModel().getSelectedItem();
				DBUtils.setUserActiveStatus(event, currentUser.getEmail(), 1);
				DBUtils.changeScene(event, "Reporting.fxml", "Reporting", email, firstName, lastName, accountType);
			}
		}));

		// A listener that looks for a change in table selected
		cb_reporting_type.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number value, Number new_value) {
				String table = reportingTypes[new_value.intValue()].toLowerCase();
				tableview_results.getItems().clear();
				tableview_results.getColumns().clear();

				if(table.equals("user")) {
					TableColumn<User, String> firstNameCol = new TableColumn<>("First Name");
					firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
					TableColumn<User, String> lastNameCol = new TableColumn<>("Last Name");
					lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
					TableColumn<User, String> emailCol = new TableColumn<>("Email");
					emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
					TableColumn<User, String> aTypeCol = new TableColumn<>("Type");
					aTypeCol.setCellValueFactory(new PropertyValueFactory<>("accType"));
					TableColumn<User, String> activeCol = new TableColumn<>("Active");
					activeCol.setCellValueFactory(new PropertyValueFactory<>("accActive"));
					TableColumn<User, String> totalDonationsCol = new TableColumn<>("Donations");
					totalDonationsCol.setMinWidth(80);
					totalDonationsCol.setCellValueFactory(new PropertyValueFactory<>("donations"));
					TableColumn<User, String> totalHoursCol = new TableColumn<>("Volunteer Hours");
					totalHoursCol.setMinWidth(120);
					totalHoursCol.setCellValueFactory(new PropertyValueFactory<>("hours"));

					tableview_results.getColumns().addAll(firstNameCol, lastNameCol, emailCol, aTypeCol, activeCol, totalDonationsCol, totalHoursCol);
					cb_user_attributes.setItems(FXCollections.observableArrayList("FirstName", "LastName", "Email", "Type", "Active"));
					button_delete_selected.setVisible(false);
					button_delete_selected.setManaged(false);
					button_set_inactive.setVisible(true);
					button_set_inactive.setManaged(true);
					button_set_active.setVisible(true);
					button_set_active.setManaged(true);
				} else if(table.equals("event")) {
					TableColumn<User, String> eventIdCol = new TableColumn<>("Event ID");
					eventIdCol.setCellValueFactory(new PropertyValueFactory<>("eventId"));
					TableColumn<User, String> nameCol = new TableColumn<>("Name");
					nameCol.setMinWidth(110);
					nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
					TableColumn<User, String> spotsAvailableCol = new TableColumn<>("Spots");
					spotsAvailableCol.setCellValueFactory(new PropertyValueFactory<>("spotsAvailable"));
					TableColumn<User, String> dateCol = new TableColumn<>("Date");
					dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
					TableColumn<User, String> startTimeCol = new TableColumn<>("Starts At");
					startTimeCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
					TableColumn<User, String> endTimeCol = new TableColumn<>("Ends At");
					endTimeCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));
					TableColumn<User, String> locationCol = new TableColumn<>("Location");
					locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
					TableColumn<User, String> activeCol = new TableColumn<>("Active");
					activeCol.setCellValueFactory(new PropertyValueFactory<>("active"));
					TableColumn<User, String> descriptionCol = new TableColumn<>("Description");
					descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));

					tableview_results.getColumns().addAll(eventIdCol, nameCol, spotsAvailableCol, dateCol, startTimeCol, endTimeCol, locationCol, activeCol, descriptionCol);
					cb_user_attributes.setItems(FXCollections.observableArrayList("EventId", "SpotsAvailable", "Date", "Name", "Location", "Description"));
					button_delete_selected.setVisible(true);
					button_delete_selected.setManaged(true);
					button_set_inactive.setVisible(false);
					button_set_inactive.setManaged(false);
					button_set_active.setVisible(false);
					button_set_active.setManaged(false);
				}
			}
		});

		// Assigned the action that is caused by the "Donate" button being clicked.
		button_search.setOnAction((new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// Clear the current items in the tableview
				tableview_results.getItems().clear();

				// Configure the ListView to display all the user's events
				Connection connection = null;
				PreparedStatement psQuery = null;
				ResultSet resultSet = null;

				String attribute = "";
				String searchValue = "";
				String table = "";
				// Check if a table to report on is selected
				if(!cb_reporting_type.getSelectionModel().isEmpty()) {
					table = cb_reporting_type.getValue().toString().toLowerCase();
				} else {
					System.out.println("No table selected.");
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Please select a reporting type.");
					alert.show();
					return;
				}


				// Check if the search bar is empty
				if(!tf_search.getText().trim().isEmpty()) {
					searchValue = tf_search.getText();
				}
				// Check if the attribute to search on is empty
				if(!cb_user_attributes.getSelectionModel().isEmpty()) {
					attribute = cb_user_attributes.getValue().toString();
				}

				try {
					connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/npdb", "root", "admin");
					if(table.equals("user")) {
						if(attribute.equals("") || searchValue.equals("")) {
							psQuery = connection.prepareStatement("SELECT FirstName, LastName, Email, Type, Active, TotalHours, TotalDonations FROM user NATURAL LEFT JOIN (SELECT Email, sum(HoursAttended) AS TotalHours FROM attended GROUP BY Email) AS HoursQuery NATURAL LEFT JOIN (SELECT Email, sum(Amount) AS TotalDonations FROM user NATURAL LEFT JOIN donated_by NATURAL LEFT JOIN donation GROUP BY Email) AS DonationQuery GROUP BY Email ORDER BY Active DESC, LastName, FirstName");
						} else {
							if(attribute.equals("Active")) {
								psQuery = connection.prepareStatement("SELECT FirstName, LastName, Email, Type, Active,  TotalHours, TotalDonations FROM user NATURAL LEFT JOIN (SELECT Email, sum(HoursAttended) AS TotalHours FROM attended GROUP BY Email) AS HoursQuery NATURAL LEFT JOIN (SELECT Email, sum(Amount) AS TotalDonations FROM user NATURAL LEFT JOIN donated_by NATURAL LEFT JOIN donation GROUP BY Email) AS DonationQuery WHERE " + attribute + " = ? GROUP BY Email");
								if(searchValue.equals("1") || searchValue.toLowerCase().equals("true")) {
									psQuery.setInt(1, 1);
								} else if(searchValue.equals("0") || searchValue.toLowerCase().equals("false")) {
									psQuery.setInt(1, 0);
								} else {
									System.out.println("Invalid search value.");
									Alert alert = new Alert(Alert.AlertType.ERROR);
									alert.setContentText("Please enter a valid search.");
									alert.show();
									return;
								}
							}
							else {
								psQuery = connection.prepareStatement("SELECT FirstName, LastName, Email, Type, Active,  TotalHours, TotalDonations FROM user NATURAL LEFT JOIN (SELECT Email, sum(HoursAttended) AS TotalHours FROM attended GROUP BY Email) AS HoursQuery NATURAL LEFT JOIN (SELECT Email, sum(Amount) AS TotalDonations FROM user NATURAL LEFT JOIN donated_by NATURAL LEFT JOIN donation GROUP BY Email) AS DonationQuery WHERE " + attribute + " LIKE ? GROUP BY Email");
								psQuery.setString(1, "%" + searchValue + "%");
							}
						}
					} else if(table.equals("event")) {
						if(attribute.equals("") || searchValue.equals("")) {
							psQuery = connection.prepareStatement("SELECT EventId, SpotsAvailable, DtStart, DtEnd, Name, Location, Description, Active FROM " + table + " ORDER BY Active DESC, DtStart DESC, Name");
						} else {
							psQuery = connection.prepareStatement("SELECT EventId, SpotsAvailable, DtStart, DtEnd, Name, Location, Description, Active FROM " + table + " WHERE " + attribute + " LIKE ?");
							if(attribute.equals("EventId") || attribute.equals("SpotsAvailable")) {
								try {
									psQuery.setInt(1, Integer.valueOf(searchValue));
								} catch(NumberFormatException e) {
									System.out.println("Invalid search type.");
									Alert alert = new Alert(Alert.AlertType.ERROR);
									alert.setContentText("Please enter a valid search.");
									alert.show();
									return;
								}
							}
							psQuery.setString(1, "%" + searchValue + "%");
						}
					}
					resultSet = psQuery.executeQuery();

					while(resultSet.next()) {
						if(table.equals("user")) {
							String curFirstName = resultSet.getString("FirstName");
							String curLastName = resultSet.getString("LastName");
							String curEmail = resultSet.getString("Email");
							String curType = resultSet.getString("Type");
							String curActive = "" + resultSet.getInt("Active");
							if(curActive.equals("1")) {
								curActive = "True";
							} else {
								curActive = "False";
							}
							String curDonations = "$" + resultSet.getInt("TotalDonations");
							String curHours = "" + resultSet.getInt("TotalHours");

							tableview_results.getItems().add(new User(curFirstName, curLastName, curEmail, curType, curActive, curDonations, curHours));
						} else if(table.equals("event")) {
							String curEventId = resultSet.getInt("EventId") + "";
							String curSpots = resultSet.getInt("SpotsAvailable") + "";
							String curDate = resultSet.getDate("DtStart").toString();
							String curStart = resultSet.getTime("DtStart").toString();
							String curEnd = resultSet.getTime("DtEnd").toString();
							String curName = resultSet.getString("Name");
							String curLocation = resultSet.getString("Location");
							String curDesc = resultSet.getString("Description");
							String curActive = "" + resultSet.getInt("Active");
							if(curDesc == null) {
								curDesc = "";
							}
							if(curActive.equals("1")) {
								curActive = "True";
							} else {
								curActive = "False";
							}

							tableview_results.getItems().add(new Event(curEventId, curSpots, curDate, curStart, curEnd, curName, curLocation, curDesc, curActive));
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
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
	}

	/**
	 * An inner class that is used to create a datatype that is populated in the reporting table for Users.
	 */
	protected static class User {

		private final SimpleStringProperty firstName;
		private final SimpleStringProperty lastName;
		private final SimpleStringProperty email;
		private final SimpleStringProperty accType;
		private final SimpleStringProperty accActive;
		private final SimpleStringProperty donations;
		private final SimpleStringProperty hours;

		private User(String fName, String lName, String email, String aType, String aActive, String amount, String totalHours) {
			this.firstName = new SimpleStringProperty(fName);
			this.lastName = new SimpleStringProperty(lName);
			this.email = new SimpleStringProperty(email);
			this.accType = new SimpleStringProperty(aType);
			this.accActive = new SimpleStringProperty(aActive);
			this.donations = new SimpleStringProperty(amount);
			this.hours = new SimpleStringProperty(totalHours);
		}

		public String getFirstName() {
			return firstName.get();
		}

		public void setFirstName(String fName) {
			firstName.set(fName);
		}

		public String getLastName() {
			return lastName.get();
		}

		public void setLastName(String fName) {
			lastName.set(fName);
		}

		public String getEmail() {
			return email.get();
		}

		public void setEmail(String fName) {
			email.set(fName);
		}

		public String getAccType() {
			return accType.get();
		}

		public void setAccType(String aType) {
			accType.set(aType);
		}

		public String getAccActive() {
			return accActive.get();
		}

		public void setAccActive(String active) {
			accActive.set(active);
		}

		public String getDonations() {
			return donations.get();
		}

		public void setDonations(String amount) {
			donations.set(amount);
		}

		public String getHours() {
			return hours.get();
		}

		public void setHours(String amount) {
			hours.set(amount);
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
		private final SimpleStringProperty description;
		private final SimpleStringProperty active;

		private Event(String eId, String spots, String dt, String start, String end, String n, String loc, String desc, String activity) {
			this.eventId = new SimpleStringProperty(eId);
			this.spotsAvailable = new SimpleStringProperty(spots);
			this.date = new SimpleStringProperty(dt);
			this.startTime = new SimpleStringProperty(start);
			this.endTime = new SimpleStringProperty(end);
			this.name = new SimpleStringProperty(n);
			this.location = new SimpleStringProperty(loc);
			this.description = new SimpleStringProperty(desc);
			this.active = new SimpleStringProperty(activity);
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

		public String getDescription() {
			return description.get();
		}

		public void setDescription(String desc) {
			description.set(desc);
		}

		public String getActive() {
			return active.get();
		}

		public void setActive(String activity) {
			active.set(activity);
		}
	}
}
