/**
 * DBUtils.java
 * 
 * JavaFX Bookkeeping Software
 * 
 * This class handles the interaction with the database. It also process changes
 * in scenes between pages of the software. This information is often specific to
 * the type of user that is logged in, and this class handles that information.
 * 
 */

package application;

import java.io.IOException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.Base64;

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
				} else if (fxmlFile.equals("DonatePage.fxml")){
					DonatePageController donateController = loader.getController();
					donateController.setUserInformation(firstName, lastName, email, accountType);
				} else if (fxmlFile.equals("Reporting.fxml")){
					ReportingController reportingController = loader.getController();
					reportingController.setUserInformation(firstName, lastName, email, accountType);
				} else {
					System.out.println("[ERROR] Page not loaded.");
				}
			// Catch any exception that is thrown and print its stack trace
			} catch (IOException e) {
				e.printStackTrace();
			}
		// Navigate to the login page. We do not need to process user information here.
		} else {
			try {
				root = FXMLLoader.load(DBUtils.class.getResource(fxmlFile));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Configure the page to be displayed
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setTitle(title);
		Scene scene = new Scene(root, 800, 600);
		scene.getStylesheets().add("application/application.css");
		stage.setScene(scene);
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
		// Set up variables that will be used to query the database.
		Connection connection = null;
		PreparedStatement psInsert = null;
		PreparedStatement psCheckUserExists = null;
		ResultSet resultSet = null;

		/*
		 *   Password hashing through PBKDF2
		 * 	 - one of the most widely adopted government standardized password hashing algorithms
		 */

		// Change password string into a char array
		char[] charArrPass = password.toCharArray();

		// Create a cryptographically secure pseudo-random number generator (CSPRING) and use it to make a
		// 4 byte long random byte array to use as salt for password encryption.
		SecureRandom secRan = new SecureRandom();
		byte[] salt = new byte[8];
		secRan.nextBytes(salt);

		// Create a key factory using SHA-512 algorithm with HMAC
		SecretKeyFactory pbkdf2KeyFactory = null;
		try {
			pbkdf2KeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		// Create keySpec and pass it the password char array, salt byte array, # of iterations, and ending password length
		PBEKeySpec keySpec = new PBEKeySpec(charArrPass,	// Input character array of password
				salt, 		// CSPRNG, unique
				10000, 		// Iteration count (c) 10,000 (150k recommended, too CPU intense)
				256     ); 	// 256 bits output hashed password

		// Computes hashed password using PBKDF2HMACSHA512 algorithm and provided PBE specs.
		byte[] hashedPassArr = null;
		try {
			hashedPassArr = pbkdf2KeyFactory.generateSecret(keySpec).getEncoded() ;
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}

		try {
			// Connect to the database and run the query to gather all current users.
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/npdb", "root", "admin");
			psCheckUserExists = connection.prepareStatement("SELECT * FROM user WHERE email = ?");
			psCheckUserExists.setString(1, email);
			resultSet = psCheckUserExists.executeQuery();

			// If the email is already in use, throw an error for the user.
			if (resultSet.isBeforeFirst()) {
				System.out.println("User already exists!");
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setContentText("This email is already in use.");
				alert.show();
			// If no error is found, insert the user information into the database.
			} else {
				psInsert = connection.prepareStatement("INSERT INTO user (Email, Password, Type, FirstName, LastName, Salt) VALUES (?, ?, ?, ?, ?, ?)");
				psInsert.setString(1, email);
				psInsert.setString(2, Base64.getEncoder().encodeToString(hashedPassArr)); //convert byte array password to string for storage
				psInsert.setString(3, accountType);
				psInsert.setString(4, firstName);
				psInsert.setString(5, lastName);
				psInsert.setString(6, Base64.getEncoder().encodeToString(salt)); //convert byte array salt to string for storage
				psInsert.executeUpdate();

				// Confirm with the user that their account has been created.
				changeScene(event, "LogIn.fxml", "Log in!", null, null, null, null);
				Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
				alert.setContentText("Registration complete.");
				alert.show();

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
	public static void createEvent(ActionEvent event, String eventName, String description, String date, String location, int spotsAvailable, int startTime, int endTime, String email, String firstName, String lastName, String accountType) {
		// Set up variables to connect and query the database.
		Connection connection = null;
		PreparedStatement psInsert = null;

		try {
			// Connect to the database
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/npdb", "root", "admin");
			// Create a query to insert an event into the database and execute it.
			if(description == null) {
				psInsert = connection.prepareStatement("INSERT INTO event (SpotsAvailable, DtStart, DtEnd, Name, Location) VALUES (?, ?, ?, ?, ?)");
			} else {
				psInsert = connection.prepareStatement("INSERT INTO event (SpotsAvailable, DtStart, DtEnd, Name, Location, Description) VALUES (?, ?, ?, ?, ?, ?)");
				psInsert.setString(6, description);
			}
			psInsert.setString(1, spotsAvailable + "");
			psInsert.setString(2, date + " " + startTime + ":00:00");
			psInsert.setString(3, date + " " + endTime + ":00:00");
			psInsert.setString(4, eventName);
			psInsert.setString(5, location);
			psInsert.executeUpdate();

			// Change back to the admin main page and confirm with the user that the event was created.
			changeScene(event, "AdminMainPage.fxml", "Home", email, firstName, lastName, accountType);
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setContentText("Event created.");
			alert.show();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// Close all statements that have been used to query and connect to the database.
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
	 * Method used to process a restricted donation.
	 *
	 * @param event:       		The event that causes the method to run
	 * @param eventId:			The event that the user wishes to donate to
	 * @param amount:   		The amount the user is donating
	 * @param email:       		The unique email of the user
	 * @param firstName:   		The user's first name
	 * @param lastName:   		The user's last name
	 * @param accountType: 		The account type of the user
	 */
	public static void processRestrictedDonation(ActionEvent event, int eventId, int amount, String email, String firstName, String lastName, String accountType) {
		// Set up variables to connect and query the database.
		Connection connection = null;
		PreparedStatement psInsert = null;
		PreparedStatement getDonation = null;
		ResultSet resultSet = null;

		try {
			// Connect to the database and create a new donation.
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/npdb", "root", "admin");
			psInsert = connection.prepareStatement("INSERT INTO donation (Amount) VALUES (?)");
			psInsert.setInt(1, amount);
			psInsert.executeUpdate();

			// Get the donation ID that was generated
			getDonation = connection.prepareStatement("SELECT LAST_INSERT_ID() AS last_id FROM donation");
			resultSet = getDonation.executeQuery();
			resultSet.next();
			int DonationId = resultSet.getInt("last_id");

			// Insert the relationship between the donation and the user.
			psInsert = connection.prepareStatement("INSERT INTO donated_by (DonationId, Email) VALUES (?, ?)");
			psInsert.setInt(1, DonationId);
			psInsert.setString(2, email);
			psInsert.executeUpdate();

			// Insert the relationship between the donation and the event.
			psInsert = connection.prepareStatement("INSERT INTO donated_to (DonationId, EventId) VALUES (?, ?)");
			psInsert.setInt(1, DonationId);
			psInsert.setInt(2, eventId);
			psInsert.executeUpdate();

			// Once they have been registered, navigate back to their respective home page.
			if(accountType.equals("Volunteer") || accountType.equals("Donor")) {
				changeScene(event, "VolunteerMainPage.fxml", "Home", email, firstName, lastName, accountType);
			} else if (accountType.equals("Admin")){
				changeScene(event, "AdminMainPage.fxml", "Home", email, firstName, lastName, accountType);
			}

			// Display confirmation that the event has been created for the user.
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setContentText("Your donation has been processed.");
			alert.show();


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
			if (psInsert != null) {
				try {
					psInsert.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (getDonation != null) {
				try {
					getDonation.close();
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
	 * Method used to process an unrestricted donation.
	 *
	 * @param event:       		The event that causes the method to run
	 * @param amount:   		The amount the user is donating
	 * @param email:       		The unique email of the user
	 * @param firstName:   		The user's first name
	 * @param lastName:   		The user's last name
	 * @param accountType: 		The account type of the user
	 */
	public static void processUnrestrictedDonation(ActionEvent event, int amount, String email, String firstName, String lastName, String accountType) {
		// Set up variables to connect and query the database.
		Connection connection = null;
		PreparedStatement psInsert = null;
		PreparedStatement getDonation = null;
		ResultSet resultSet = null;

		try {
			// Connect to the database and create a new donation.
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/npdb", "root", "admin");
			psInsert = connection.prepareStatement("INSERT INTO donation (Amount) VALUES (?)");
			psInsert.setInt(1, amount);
			psInsert.executeUpdate();

			// Get the donation ID that was generated
			getDonation = connection.prepareStatement("SELECT LAST_INSERT_ID() AS last_id FROM donation");
			resultSet = getDonation.executeQuery();
			resultSet.next();
			int DonationId = resultSet.getInt("last_id");

			// Insert the relationship between the donation and the user.
			psInsert = connection.prepareStatement("INSERT INTO donated_by (DonationId, Email) VALUES (?, ?)");
			psInsert.setInt(1, DonationId);
			psInsert.setString(2, email);
			psInsert.executeUpdate();

			// Once they have been registered, navigate back to their respective home page.
			if(accountType.equals("Volunteer") || accountType.equals("Donor")) {
				changeScene(event, "VolunteerMainPage.fxml", "Home", email, firstName, lastName, accountType);
			} else if (accountType.equals("Admin")){
				changeScene(event, "AdminMainPage.fxml", "Home", email, firstName, lastName, accountType);
			}

			// Display confirmation that the event has been created for the user.
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setContentText("Your donation has been processed.");
			alert.show();


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
			if (psInsert != null) {
				try {
					psInsert.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (getDonation != null) {
				try {
					getDonation.close();
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
	 * Method used to cancel an event.
	 *
	 * @param event:       		The event that causes the method to run
	 * @param eventId:   		The unique id of the event
	 */
	public static void cancelEvent(ActionEvent event, int eventId) {
		// Set up variables to connect and query the database.
		Connection connection = null;
		PreparedStatement psCancelAttended = null;
		PreparedStatement psCancelDonatedTo = null;
		PreparedStatement psCancelEvent = null;
		try {
			// Connect to the database.
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/npdb", "root", "admin");

			// Cancel the event
			psCancelEvent = connection.prepareStatement("UPDATE event SET Active = 0 WHERE EventId = ?");
			psCancelEvent.setInt(1, eventId);
			psCancelEvent.executeUpdate();

			// Confirm to the user that the event has been cancelled.
			System.out.println("Event successfully cancelled.");
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setContentText("The event has been successfully cancelled.");
			alert.show();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// Close all statements that have been used to query and connect to the database.
			if (psCancelAttended != null) {
				try {
					psCancelAttended.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (psCancelDonatedTo != null) {
				try {
					psCancelDonatedTo.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (psCancelEvent != null) {
				try {
					psCancelEvent.close();
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
	 * Method used to register for an event.
	 *
	 * @param event:       		The event that causes the method to run
	 * @param eventId:   		The unique id of the event the user is registering for
	 * @param date:		   		The date of the event the user is registering for
	 * @param email:       		The unique email of the user
	 * @param firstName:   		The user's first name
	 * @param lastName:   		The user's last name
	 * @param accountType: 		The account type of the user
	 */
	public static void registerForEvent(ActionEvent event, int eventId, String date, String email, String firstName, String lastName, String accountType) {
		// Set up variables to connect and query the database.
		Connection connection = null;
		PreparedStatement psInsert = null;
		PreparedStatement psCheckUserRegistered = null;
		PreparedStatement psCheckSpots = null;
		ResultSet resultSet = null;

		try {
			// Connect to the database and run the query to see if the user is already registered for the event.
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/npdb", "root", "admin");
			psCheckUserRegistered = connection.prepareStatement("SELECT * FROM attended WHERE EventId = ? AND Email = ?");
			psCheckUserRegistered.setInt(1, eventId);
			psCheckUserRegistered.setString(2, email);
			resultSet = psCheckUserRegistered.executeQuery();

			// If the user is already registered, display an error.
			if (resultSet.isBeforeFirst()) {
				System.out.println("User is already registered for that event.");
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setContentText("You cannot register for the same event twice.");
				alert.show();
				return;
			}

			// Check how many spots are available in the event
			psCheckSpots = connection.prepareStatement("SELECT SpotsAvailable, DtStart, DtEnd FROM event WHERE EventId = ?");
			psCheckSpots.setInt(1, eventId);
			resultSet = psCheckSpots.executeQuery();
			resultSet.next();
			int spotsAvailable = resultSet.getInt("SpotsAvailable");

			if(spotsAvailable <= 0) {
				System.out.println("This event is full.");
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setContentText("This event is at its maximum number of volunteers.");
				alert.show();
				return;
			}

			// Check if the user is already registered for an event at the same time as this event
			Date startDate = resultSet.getDate("DtStart");
			Time startTime = resultSet.getTime("DtStart");
			int numStartTime = Integer.valueOf(startTime.toString().substring(0,2));
			Date endDate = resultSet.getDate("DtEnd");
			Time endTime = resultSet.getTime("DtEnd");
			int numEndTime = Integer.valueOf(endTime.toString().substring(0,2));
			psCheckUserRegistered = connection.prepareStatement("SELECT EventId, DtStart, DtEnd, Active FROM attended NATURAL JOIN event WHERE Email = ?");
			psCheckUserRegistered.setString(1, email);
			resultSet = psCheckUserRegistered.executeQuery();

			while(resultSet.next()) {
				// Check if the dates are the same
				if(resultSet.getDate("DtStart").equals(startDate) && resultSet.getInt("Active") == 1) {
					int curStartTime = Integer.valueOf(resultSet.getTime("DtStart").toString().substring(0,2));
					int curEndTime = Integer.valueOf(resultSet.getTime("DtEnd").toString().substring(0,2));

					// Check if the event starts while another event the user is attending is going on
					if(numStartTime > curStartTime && numStartTime < curEndTime) {
						System.out.println("This event is is at the same time as another the user is registered for.");
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setContentText("This event conflicts with another event you are registered for.");
						alert.show();
						return;
					}
					// Check if the event ends while another event the user is attending is going on
					else if (numEndTime < curEndTime && numEndTime > curStartTime) {
						System.out.println("This event is is at the same time as another the user is registered for.");
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setContentText("This event conflicts with another event you are registered for.");
						alert.show();
						return;
					}
					// Check if the event starts before and ends after another event the user is attending
					else if (numEndTime > curEndTime && numStartTime < curStartTime) {
						System.out.println("This event is is at the same time as another the user is registered for.");
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setContentText("This event conflicts with another event you are registered for.");
						alert.show();
						return;
					}
					// Check if they start or end at the same time
					else if (curEndTime == numEndTime || curStartTime == numStartTime) {
						System.out.println("This event is is at the same time as another the user is registered for.");
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setContentText("This event conflicts with another event you are registered for.");
						alert.show();
						return;
					}
				}
			}

			int hoursAttended = numEndTime - numStartTime;
			// If no errors occur, insert the user and the event they wish to attend into the 'attended' table.
			psInsert = connection.prepareStatement("INSERT INTO attended (EventId, Email, HoursAttended) VALUES (?, ?, ?)");
			psInsert.setInt(1, eventId);
			psInsert.setString(2, email);
			psInsert.setInt(3, hoursAttended);
			psInsert.executeUpdate();

			// Update the amount of available spots for the event.
			psInsert = connection.prepareStatement("UPDATE event SET SpotsAvailable = (SpotsAvailable - 1) WHERE EventId = ?");
			psInsert.setInt(1, eventId);
			psInsert.executeUpdate();

			// Once they have been registered, navigate back to their respective home page.
			if (accountType.equals("Volunteer") || accountType.equals("Donor")) {
				changeScene(event, "VolunteerMainPage.fxml", "Home", email, firstName, lastName, accountType);
			} else if (accountType.equals("Admin")) {
				changeScene(event, "AdminMainPage.fxml", "Home", email, firstName, lastName, accountType);
			}

			// Display confirmation that the event has been created for the user.
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setContentText("Event registration complete.");
			alert.show();

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

	/**
	 * Method used to cancel their registration for an event.
	 *
	 * @param event:       		The event that causes the method to run
	 * @param eventId:   		The unique id of the event the user is cancelling their registration for
	 * @param date:		   		The date of the event the user is cancelling their registration for
	 * @param email:       		The unique email of the user
	 * @param firstName:   		The user's first name
	 * @param lastName:   		The user's last name
	 * @param accountType: 		The account type of the user
	 */
	public static void cancelRegistration(ActionEvent event, int eventId, String date, String email, String firstName, String lastName, String accountType) {
		// Set up variables to connect and query the database.
		Connection connection = null;
		PreparedStatement psCancelRegistration = null;

		try {
			// Connect to the database and run the query to delete the user registration for the event from the database.
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/npdb", "root", "admin");
			psCancelRegistration = connection.prepareStatement("DELETE FROM attended WHERE Email = ? AND EventId = ?");
			psCancelRegistration.setString(1, email);
			psCancelRegistration.setInt(2, eventId);
			psCancelRegistration.executeUpdate();

			// Update the amount of spots available for the event.
			psCancelRegistration = connection.prepareStatement("UPDATE event SET SpotsAvailable = (SpotsAvailable + 1) WHERE EventId = ?");
			psCancelRegistration.setInt(1, eventId);
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
			// Close all statements that have been used to query and connect to the database.
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
		// Set up variables to connect and query the database.
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		// Convert string password to char array to prepare for encrypting
		char[] charArrInputPass = password.toCharArray();

		try {
			// Connect to the database and run the query to gather the user information from the database
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/npdb", "root", "admin");
			preparedStatement = connection.prepareStatement("SELECT Password, Type, Active, FirstName, LastName, Salt FROM user WHERE Email = ?");
			preparedStatement.setString(1, email);
			resultSet = preparedStatement.executeQuery();

			// If the user is not found, display an error for the user.
			if (!resultSet.isBeforeFirst()) {
				System.out.println("User not found in the database!");
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setContentText("Provided credentials are incorrect!");
				alert.show();
			} else {
				// If the user is found, load their information.
				while (resultSet.next()) {
					String retrievedPassword = resultSet.getString("Password");
					String retrievedAccountType = resultSet.getString("Type");
					String retrievedFirstName = resultSet.getString("FirstName");
					String retrievedLastName = resultSet.getString("LastName");
					String retrievedSalt = resultSet.getString("Salt");
					int retrievedActive = resultSet.getInt("Active");
					if(retrievedActive == 0) {
						// If the user is set to inactive, warn user.
						System.out.println("User is inactive.");
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setContentText("This account has been set to inactive. Please contact an administrator.");
						alert.show();
						return;
					}

					//create key factory
					SecretKeyFactory pbkdf2KeyFactory = null;
					try {
						pbkdf2KeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					}

					//create keySpec and pass it the password char array, salt converted from string to byte array, # of iterations, and ending password length
					PBEKeySpec keySpec = new PBEKeySpec(charArrInputPass,	// Input character array of password
							Base64.getDecoder().decode(retrievedSalt), 		// CSPRNG, unique
							10000, 		// Iteration count (c) 10,000 (150k recommended, too CPU intense)
							256     ); 	// 256 bits output hashed password

					//Computes hashed password using salt from database.
					byte[] hashedInputPass = null;
					try {
						hashedInputPass = pbkdf2KeyFactory.generateSecret(keySpec).getEncoded() ;
					} catch (InvalidKeySpecException e) {
						e.printStackTrace();
					}

					//compare the string converted hashed password from the database to the newly string converted hashed password
					if (retrievedPassword.equals(Base64.getEncoder().encodeToString(hashedInputPass))) {
						// Load the correct landing page
						if (retrievedAccountType.equals("Volunteer") || retrievedAccountType.equals("Donor")) {
							changeScene(event, "VolunteerMainPage.fxml", "Home", email, retrievedFirstName, retrievedLastName, retrievedAccountType);
						} else if (retrievedAccountType.equals("Admin")){
							changeScene(event, "AdminMainPage.fxml", "Home", email, retrievedFirstName, retrievedLastName, retrievedAccountType);
						} else {
							System.out.println("Uh oh...");
						}
					} else {
						// If the password was incorrect, display an error to the user.
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
			// Close all statements that have been used to query and connect to the database.
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

	/**
	 * Method used to update user information.
	 *
	 * @param event: the event that caused the method to run.
	 * @param email: the email entered by the user.
	 * @param firstName: the user's updated first name
	 * @param lastName: the user's updated last name
	 * @param accountType: the user's account type.
	 */
	public static void updateUser(ActionEvent event, String email, String firstName, String lastName, String accountType) {
		// Set up variables to connect and query the database.
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
			// Close all statements that have been used to query and connect to the database.
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

	/**
	 * Method to update a user's active status.
	 *
	 * @param event: the event that caused the method to run.
	 * @param email: the email entered by the user.
	 */
	public static void setUserActiveStatus(ActionEvent event, String email, int status) {
		// Set up variables to connect and query the database.
		Connection connection = null;
		PreparedStatement psUpdate = null;

		try {
			// Update the user information in the database
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/npdb", "root", "admin");
			psUpdate = connection.prepareStatement("UPDATE user SET Active = ? WHERE Email = ?");
			psUpdate.setInt(1, status);
			psUpdate.setString(2, email);
			psUpdate.executeUpdate();
			String statusMsg = "true";
			if(status == 0) {
				statusMsg = "false";
			}
			// Confirm to the user that they have cancelled their registration
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setContentText("User active status has been set to " + statusMsg + ".");
			alert.show();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// Close all statements that have been used to query and connect to the database.
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