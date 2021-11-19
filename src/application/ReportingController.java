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
		cb_user_attributes.setItems(FXCollections.observableArrayList("FirstName", "LastName", "Email", "Type"));
		cb_user_attributes.setTooltip(new Tooltip("Select the search attribute"));

		TableColumn<User, String> firstNameCol = new TableColumn<>("First Name");
		firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
		TableColumn<User, String> lastNameCol = new TableColumn<>("Last Name");
		lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
		TableColumn<User, String> emailCol = new TableColumn<>("Email");
		emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
		TableColumn<User, String> aTypeCol = new TableColumn<>("Type");
		aTypeCol.setCellValueFactory(new PropertyValueFactory<>("accType"));
		TableColumn<User, String> totalDonationsCol = new TableColumn<>("Donations");
		totalDonationsCol.setMinWidth(80);
		totalDonationsCol.setCellValueFactory(new PropertyValueFactory<>("donations"));
		TableColumn<User, String> totalHoursCol = new TableColumn<>("Volunteer Hours");
		totalHoursCol.setMinWidth(120);
		totalHoursCol.setCellValueFactory(new PropertyValueFactory<>("hours"));

		tableview_results.getColumns().addAll(firstNameCol, lastNameCol, emailCol, aTypeCol, totalDonationsCol, totalHoursCol);

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
					TableColumn<User, String> totalDonationsCol = new TableColumn<>("Donations");
					totalDonationsCol.setMinWidth(80);
					totalDonationsCol.setCellValueFactory(new PropertyValueFactory<>("donations"));
					TableColumn<User, String> totalHoursCol = new TableColumn<>("Volunteer Hours");
					totalHoursCol.setMinWidth(120);
					totalHoursCol.setCellValueFactory(new PropertyValueFactory<>("hours"));

					tableview_results.getColumns().addAll(firstNameCol, lastNameCol, emailCol, aTypeCol, totalDonationsCol, totalHoursCol);
					cb_user_attributes.setItems(FXCollections.observableArrayList("FirstName", "LastName", "Email", "Type"));
				} else if(table.equals("event")) {
					TableColumn<User, String> eventIdCol = new TableColumn<>("Event ID");
					eventIdCol.setCellValueFactory(new PropertyValueFactory<>("eventId"));
					TableColumn<User, String> nameCol = new TableColumn<>("Name");
					nameCol.setMinWidth(110);
					nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
					TableColumn<User, String> spotsAvailableCol = new TableColumn<>("Spots");
					spotsAvailableCol.setCellValueFactory(new PropertyValueFactory<>("spotsAvailable"));
					TableColumn<User, String> dtStartCol = new TableColumn<>("Starts On");
					dtStartCol.setCellValueFactory(new PropertyValueFactory<>("dtStart"));
					TableColumn<User, String> dtEndCol = new TableColumn<>("Ends On");
					dtEndCol.setCellValueFactory(new PropertyValueFactory<>("dtEnd"));
					TableColumn<User, String> locationCol = new TableColumn<>("Location");
					locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));

					tableview_results.getColumns().addAll(eventIdCol, nameCol, spotsAvailableCol, dtStartCol, dtEndCol, locationCol);
					cb_user_attributes.setItems(FXCollections.observableArrayList("EventId", "SpotsAvailable", "DtStart", "DtEnd", "Name", "Location"));
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
							psQuery = connection.prepareStatement("SELECT FirstName, LastName, Email, Type, TotalHours, TotalDonations FROM user NATURAL LEFT JOIN (SELECT Email, sum(HoursAttended) AS TotalHours FROM attended GROUP BY Email) AS HoursQuery NATURAL LEFT JOIN (SELECT Email, sum(Amount) AS TotalDonations FROM user NATURAL LEFT JOIN donated_by NATURAL LEFT JOIN donation GROUP BY Email) AS DonationQuery GROUP BY Email");
						} else {
							psQuery = connection.prepareStatement("SELECT FirstName, LastName, Email, Type, TotalHours, TotalDonations FROM user NATURAL LEFT JOIN (SELECT Email, sum(HoursAttended) AS TotalHours FROM attended GROUP BY Email) AS HoursQuery NATURAL LEFT JOIN (SELECT Email, sum(Amount) AS TotalDonations FROM user NATURAL LEFT JOIN donated_by NATURAL LEFT JOIN donation GROUP BY Email) AS DonationQuery WHERE " + attribute + " LIKE ? GROUP BY Email");
							psQuery.setString(1, "%" + searchValue + "%");
						}
					} else if(table.equals("event")) {
						if(attribute.equals("") || searchValue.equals("")) {
							psQuery = connection.prepareStatement("SELECT EventId, SpotsAvailable, DtStart, DtEnd, Name, Location FROM " + table);
						} else {
							psQuery = connection.prepareStatement("SELECT EventId, SpotsAvailable, DtStart, DtEnd, Name, Location FROM " + table + " WHERE " + attribute + " LIKE ?");
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
					System.out.println(psQuery.toString());
					resultSet = psQuery.executeQuery();

					while(resultSet.next()) {
						if(table.equals("user")) {
							String curFirstName = resultSet.getString("FirstName");
							String curLastName = resultSet.getString("LastName");
							String curEmail = resultSet.getString("Email");
							String curType = resultSet.getString("Type");
							String curDonations = "$" + resultSet.getInt("TotalDonations");
							String curHours = "" + resultSet.getInt("TotalHours");

							tableview_results.getItems().add(new User(curFirstName, curLastName, curEmail, curType, curDonations, curHours));
						} else if(table.equals("event")) {
							String curEventId = resultSet.getInt("EventId") + "";
							String curSpots = resultSet.getInt("SpotsAvailable") + "";
							String curStart = resultSet.getDate("DtStart").toString();
							String curEnd = resultSet.getDate("DtEnd").toString();
							String curName = resultSet.getString("Name");
							String curLocation = resultSet.getString("Location");

							tableview_results.getItems().add(new Event(curEventId, curSpots, curStart, curEnd, curName, curLocation));
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
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
	}

	protected static class User {

		private final SimpleStringProperty firstName;
		private final SimpleStringProperty lastName;
		private final SimpleStringProperty email;
		private final SimpleStringProperty accType;
		private final SimpleStringProperty donations;
		private final SimpleStringProperty hours;

		private User(String fName, String lName, String email, String aType, String amount, String totalHours) {
			this.firstName = new SimpleStringProperty(fName);
			this.lastName = new SimpleStringProperty(lName);
			this.email = new SimpleStringProperty(email);
			this.accType = new SimpleStringProperty(aType);
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

	protected static class Event {

		private final SimpleStringProperty eventId;
		private final SimpleStringProperty spotsAvailable;
		private final SimpleStringProperty dtStart;
		private final SimpleStringProperty dtEnd;
		private final SimpleStringProperty name;
		private final SimpleStringProperty location;

		private Event(String eId, String spots, String start, String end, String n, String loc) {
			this.eventId = new SimpleStringProperty(eId);
			this.spotsAvailable = new SimpleStringProperty(spots);
			this.dtStart = new SimpleStringProperty(start);
			this.dtEnd = new SimpleStringProperty(end);
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

		public String getDtStart() {
			return dtStart.get();
		}

		public void setDtStart(String start) {
			dtStart.set(start);
		}
		public String getDtEnd() {
			return dtEnd.get();
		}

		public void setDtEnd(String end) {
			dtEnd.set(end);
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
