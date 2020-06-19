package dataBaseLogin;

import DataManipulation.Librarian;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller for the Login Screen
 * @author Adrian Mora
 */
public class MountieLibController implements Initializable {
 
    //declare all the FXML controls that you will use in the code
    @FXML private TextField textUserName;
    @FXML private PasswordField textPassword;
    @FXML private Button loginButton;
    
    //Manages connection to data base
    private Connection connection; 
    private static final String URL = 
                    "jdbc:mysql://localhost:3306/mountie_lib_db"; //?zeroDateTimeBehavior=convertToNull";
    private static String userDB= "pxndroid";
    private static String passDB = "MOZA0589";
    
    //Prepare statements for login screen.
    private PreparedStatement preparedStatment;
    private ResultSet resultSet;
    
    /**
     * Function to be run in case the login button is pressed. It will get the
     * user input from the login window, and do a query in the database to verify
     * that the user is registered in the database. If the user is not registered, 
     * the program will display a warning saying so. If the user is found, a message
     * will confirm and show a picture of the user found. Also, it displays an error
     * window if connection to db failed. Once user is found and authenticated, it 
     * will close the login screen and open the main screen of operations.
     * @param event The event that triggered this function.
     * @throws IOException if file cannot be opened.
     */
    @FXML private void loginButtonPressed(ActionEvent event) throws IOException{

        //Get user input
        String username = textUserName.getText();
        String password = textPassword.getText();
        
        //Create a Librarian object
        Librarian librarian1 = new Librarian(username, password);
        
        //Query statement and place holder
        String sql = "SELECT * FROM librarian WHERE adminName = ? and adminPassword = ?";
        
        //try to connect to data base, authenticate the user in data basr, or 
        //display any error of the data base or users not found.
        try 
        {  
            //get the connection to database
            connection = DriverManager.getConnection(URL, userDB, passDB); 
          
            //prepatre statements
            preparedStatment = connection.prepareStatement(sql);
            
            //set parameters
            preparedStatment.setString(1, librarian1.getAdminName());
            preparedStatment.setString(2, librarian1.getAdminPassword());
            
            //execute query
            resultSet = preparedStatment.executeQuery();
            
            //display result set if it matched
            if(resultSet.next())
            {   
                //Show an alert window if user found.
                displayAlert(AlertType.INFORMATION,"Loading" , "Welcome " , 
                        librarian1.getAdminName());
               
                preparedStatment.close();
                resultSet.close(); 

                //Display second Window after authentication.
                Stage stage2 = new Stage();
                Parent root2 = FXMLLoader.load(getClass().getResource("/mainWindow/MainWindow.fxml"));
                Scene scene2 = new Scene(root2);
                stage2.setScene(scene2);
                stage2.initModality(Modality.APPLICATION_MODAL);
                stage2.showAndWait();
            }
            else{
                displayAlert(AlertType.WARNING,"Login Error" , 
                  "User and/or password not found.", "");
            }
        }//end of Try
        catch(Exception e){
            displayAlert(AlertType.ERROR,"Error" , "Data base could not be loaded", "");
        }//end of catch
    }//end of loginButtonPressed

    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
           
    } 
    
    /**
     * Display an Alert depending on whether the database connection was successful,
     * the user was found, or the user is not found. As well customize the window
     * depending on what condition was met. Then go back to loginButtonPressed function
     * Displays and alert window if the connection to the data base failed.
     * @param type Type of alert to be displayed.
     * @param title title of the alerts window.
     * @param messageHeader Message to be displayed on the alert window.
     * @param errorCode to display the correct error message.
     * @param username extra information to show in the alert window.
     */
   private void displayAlert(AlertType type, String title, String messageHeader, String username) 
   {
        Alert alert = new Alert(type);
        
        //If database is not available 
        if(alert.getAlertType() == AlertType.ERROR)
        {
            alert.setTitle(title);
            alert.setHeaderText(messageHeader);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image("images/db.png"));
            alert.setGraphic(new ImageView(("images/db.png")));
            alert.showAndWait();
        }
        // if user is not found
        if(alert.getAlertType() == AlertType.WARNING)
        {
            alert.setTitle(title);
            alert.setHeaderText(messageHeader);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image("images/errorLogin.png"));
            alert.setGraphic(new ImageView(("images/errorLogin.png")));
            alert.showAndWait();
        }
        //User found and authenticated.
        else
        {
            alert.setTitle(title);
            alert.setHeaderText(messageHeader);
            
            //Stage customizes icon in the title bar
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image("images/titleIcon.png"));
            
            //Close login screen
            stage = (Stage) loginButton.getScene().getWindow();
            stage.close();
            
            //Set icon next to headerText
            alert.setGraphic(new ImageView(("images/" + username.toLowerCase() + ".jpg")));
            alert.showAndWait();
        }
   }//end of displayAlert
}//end of MountieLibController
