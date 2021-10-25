package application;

import java.io.IOException;
import java.sql.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class DBUtils {
	/**
	 * Method used to change between scenes on the application
	 *
	 * @param event:       The event that causes the scene change
	 * @param fxmlFile:    The FXML file that we want to load
	 * @param title:       Title of the scene we are switching to
	 * @param email:       the user's unique email address
	 * @param accountType: a string version of the account type (Admin, Donor, Volunteer)
	 */
	public static void changeScene(ActionEvent event, String fxmlFile, String title, String email, String firstName, String lastName, String accountType) {
		Parent root = null;

		// Check to see if the user information was passed
		if (email != null && accountType != null) {
			try {
				FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
				root = loader.load();
				// Display the scene that is trying to be displayed
				if (fxmlFile.equals("CreateEvent.fxml")) {
					CreateEventController createEventController = loader.getController();
					createEventController.setUserInformation(firstName, lastName, email, accountType);
				} else if (fxmlFile.equals("AdminMainPage.fxml")) {
					AdminLoggedInController loggedInController = loader.getController();
					loggedInController.setUserInformation(firstName, lastName, email, accountType);
				} else if (fxmlFile.equals("VolunteerMainPage.fxml")){
					VolunteerLoggedInController loggedInController = loader.getController();
					loggedInController.setUserInformation(firstName, lastName, email, accountType);
				} else if (fxmlFile.equals("ViewEvents.fxml")) {
					ViewEventsController viewEventsController = loader.getController();
					viewEventsController.setUserInformation(firstName, lastName, email, accountType);
				} else if (fxmlFile.equals("ViewProfile.fxml")) {
					ViewProfileController viewProfileController = loader.getController();
					viewProfileController.setUserInformation(firstName, lastName, email, accountType);
				} else {
					System.out.println("Uh oh...");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				root = FXMLLoader.load(DBUtils.class.getResource(fxmlFile));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setTitle(title);
		stage.setScene(new Scene(root, 800, 600));
		stage.show();
	}

	/**
	 * Method used to register a user.
	 *
	 * @param event:       The event that causes the scene change
	 * @param email:       The unique email of the user
	 * @param password:    The password the user wants to use
	 * @param firstName:   The user's first name
	 * @param accountType: The account type the user wishes to have
	 */
	public static void signUpUser(ActionEvent event, String email, String password, String firstName, String lastName, String accountType) {
		Connection connection = null;
		PreparedStatement psInsert = null;
		PreparedStatement psCheckUserExists = null;
		ResultSet resultSet = null;

		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/npdb", "root", "admin");
			psCheckUserExists = connection.prepareStatement("SELECT * FROM user WHERE email = ?");
			psCheckUserExists.setString(1, email);
			resultSet = psCheckUserExists.executeQuery();

			if (resultSet.isBeforeFirst()) {
				System.out.println("User already exists!");
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setContentText("This email is already in use.");
				alert.show();
			} else {
				psInsert = connection.prepareStatement("INSERT INTO user (Email, Password, Type, FirstName, LastName) VALUES (?, ?, ?, ?, ?)");
				psInsert.setString(1, email);
				psInsert.setString(2, password);
				psInsert.setString(3, accountType);
				psInsert.setString(4, firstName);
				psInsert.setString(5, lastName);
				psInsert.executeUpdate();

				changeScene(event, "LogIn.fxml", "Log in!", null, null, null, null);
				Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
				alert.setContentText("Registration complete.");
				alert.show();

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
			if (psCheckUserExists != null) {
				try {
					psCheckUserExists.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (psInsert != null) {
				try {
					psInsert.close();
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
	 * Method used to create an event.
	 *
	 * @param event:       		The event that causes the scene change
	 * @param eventName:   		The name of the event the user is creating
	 * @param date:		   		The date of the event the user is creating
	 * @param spotsAvailable:   The number of spots available at the event the user is creating
	 * @param email:       		The unique email of the user
	 * @param firstName:   		The user's first name
	 * @param accountType: 		The account type of the user
	 */
	public static void createEvent(ActionEvent event, String eventName, String date, String location, int spotsAvailable, int startTime, int endTime, String email, String firstName, String lastName, String accountType) {
		Connection connection = null;
		PreparedStatement psInsert = null;

		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/npdb", "root", "admin");

			psInsert = connection.prepareStatement("INSERT INTO event (SpotsAvailable, DtStart, DtEnd, Name, Location) VALUES (?, ?, ?, ?, ?)");
			psInsert.setString(1, spotsAvailable + "");
			psInsert.setString(2, date + " " + startTime + ":00:00");
			psInsert.setString(3, date + " " + endTime + ":00:00");
			psInsert.setString(4, eventName);
			psInsert.setString(5, location);
			psInsert.executeUpdate();

			changeScene(event, "AdminMainPage.fxml", "Home", email, firstName, lastName, accountType);
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setContentText("Event created.");
			alert.show();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (psInsert != null) {
				try {
					psInsert.close();
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

	public static void registerForEvent(ActionEvent event, int eventId, String date, String email, String firstName, String lastName, String accountType) {
		Connection connection = null;
		PreparedStatement psInsert = null;
		PreparedStatement psCheckUserRegistered = null;
		ResultSet resultSet = null;

		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/npdb", "root", "admin");
			psCheckUserRegistered = connection.prepareStatement("SELECT * FROM attended WHERE EventId = ? AND Email = ?");
			psCheckUserRegistered.setInt(1, eventId);
			psCheckUserRegistered.setString(2, email);
			resultSet = psCheckUserRegistered.executeQuery();

			if (resultSet.isBeforeFirst()) {
				System.out.println("User is already registered for that event.");
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setContentText("You cannot register for the same event twice.");
				alert.show();
			} else {
				psInsert = connection.prepareStatement("INSERT INTO attended (EventId, Email, TimeAttended) VALUES (?, ?, ?)");
				psInsert.setInt(1, eventId);
				psInsert.setString(2, email);
				psInsert.setString(3, date + " 08:00:00");
				psInsert.executeUpdate();

				if(accountType.equals("Volunteer") || accountType.equals("Donor")) {
					changeScene(event, "VolunteerMainPage.fxml", "Home", email, firstName, lastName, accountType);
				} else if (accountType.equals("Admin")){
					changeScene(event, "AdminMainPage.fxml", "Home", email, firstName, lastName, accountType);
				}
				Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
				alert.setContentText("Event registration complete.");
				alert.show();

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
			if (psCheckUserRegistered != null) {
				try {
					psCheckUserRegistered.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (psInsert != null) {
				try {
					psInsert.close();
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

	public static void cancelRegistration(ActionEvent event, int eventId, String date, String email, String firstName, String lastName, String accountType) {
		Connection connection = null;
		PreparedStatement psCancelRegistration = null;

		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/npdb", "root", "admin");
			psCancelRegistration = connection.prepareStatement("DELETE FROM attended WHERE Email = ? AND EventId = ?");
			psCancelRegistration.setString(1, email);
			psCancelRegistration.setInt(2, eventId);
			psCancelRegistration.executeUpdate();

			// Refresh the main page
			if(accountType.equals("Volunteer")) {
				changeScene(event, "VolunteerMainPage.fxml", "Home", email, firstName, lastName, accountType);
			} else if (accountType.equals("Admin")){
				changeScene(event, "AdminMainPage.fxml", "Home", email, firstName, lastName, accountType);
			} else if (accountType.equals("Donor")) {
				changeScene(event, "VolunteerMainPage.fxml", "Home", email, firstName, lastName, accountType);
			}
			// Confirm to the user that they have cancelled their registration
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setContentText("Event registration cancelled.");
			alert.show();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (psCancelRegistration != null) {
				try {
					psCancelRegistration.close();
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
	 * Method used to log in a user.
	 *
	 * @param event: the event that caused the method to run.
	 * @param email: the email entered by the user.
	 * @param password: the password entered by the user.
	 */
	public static void logInUser(ActionEvent event, String email, String password) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/npdb", "root", "admin");
			preparedStatement = connection.prepareStatement("SELECT Password, Type, FirstName, LastName FROM user WHERE Email = ?");
			preparedStatement.setString(1, email);
			resultSet = preparedStatement.executeQuery();

			if (!resultSet.isBeforeFirst()) {
				System.out.println("User not found in the database!");
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setContentText("Provided credentials are incorrect!");
				alert.show();
			} else {
				while (resultSet.next()) {
					String retrievedPassword = resultSet.getString("Password");
					String retrievedAccountType = resultSet.getString("Type");
					String retrievedFirstName = resultSet.getString("FirstName");
					String retrievedLastName = resultSet.getString("LastName");
					if (retrievedPassword.equals(password)) {
						// Load the correct landing page
						if (retrievedAccountType.equals("Volunteer") || retrievedAccountType.equals("Donor")) {
							changeScene(event, "VolunteerMainPage.fxml", "Home", email, retrievedFirstName, retrievedLastName, retrievedAccountType);
						} else if (retrievedAccountType.equals("Admin")){
							changeScene(event, "AdminMainPage.fxml", "Home", email, retrievedFirstName, retrievedLastName, retrievedAccountType);
						} else {
							System.out.println("Uh oh...");
						}
					} else {
						System.out.println("Passwords did not match!");
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setContentText("The provided credentials are incorrect!");
						alert.show();
					}
				}
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
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
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

	public static void updateUser(ActionEvent event, String email, String firstName, String lastName, String accountType) {
		Connection connection = null;
		PreparedStatement psUpdate = null;

		try {
			// Update the user information in the database
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/npdb", "root", "admin");
			psUpdate = connection.prepareStatement("UPDATE user SET FirstName = ?, LastName = ? WHERE Email = ?");
			psUpdate.setString(1, firstName);
			psUpdate.setString(2, lastName);
			psUpdate.setString(3, email);
			psUpdate.executeUpdate();

			// Refresh the profile page
			changeScene(event, "ViewProfile.fxml", "My Profile", email, firstName, lastName, accountType);

			// Confirm to the user that they have cancelled their registration
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setContentText("User information updated.");
			alert.show();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (psUpdate != null) {
				try {
					psUpdate.close();
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