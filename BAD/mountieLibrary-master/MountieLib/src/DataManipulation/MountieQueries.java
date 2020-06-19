package DataManipulation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * This class handles most of the queries with the data base.
 * @author Adrian Mora
 */
public class MountieQueries{
    private String sql;
    private String searchString;
    private static Connection connection; 
    private static final String URL = 
                    "jdbc:mysql://localhost:3306/mountie_lib_db";
    private static String userDB= "pxndroid";
    private static String passDB = "MOZA0589";
    private PreparedStatement preparedStatment;
    private ResultSet resultSet;
     
    /**
     * Default Constructor with no-arguments.
     */
    public MountieQueries() {
    } 
    
    /**
     * Constructor with one argument; used mostly for SELECT * queries.
     * @param sql The SQL statement to be executed.
     */
    public MountieQueries(String sql) {
        try{
            setSQL(sql);
            connection = DriverManager.getConnection(URL, userDB, passDB); 
            preparedStatment = connection.prepareStatement(sql);
        }
        catch (SQLException sqlException) {
         sqlException.printStackTrace();
         displayAlert(Alert.AlertType.ERROR,"Error" , 
                "Data base could not be loaded", searchString);
         System.exit(1);
      } 
    }//end of constructor with 1 parameters   
    
    /**
     * Constructor with SQL argument and a search argument; used to find specific
     * records.
     * @param sql The SQL statement to be executed.
     * @param searchString The specific record to be search for.
     */
    public MountieQueries(String sql, String searchString) {
        try{
            setSQL(sql);
            setSearchString(searchString);
            
            //get the connection to database
            connection = DriverManager.getConnection(URL, userDB, passDB); 
          
            //prepatre statements
            preparedStatment = connection.prepareStatement(sql);
            
            //set parameters
            preparedStatment.setString(1, searchString);
            
            //execute query
            resultSet = preparedStatment.executeQuery();
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
            displayAlert(Alert.AlertType.ERROR,"Error" , 
                "Data base could not be loaded", searchString);
            System.exit(1);
        } 
    }//end of constructor with 2 parameters    
    
    /**
     * Constructor with arguments; used to search a record that matched 2 parameter.
     * @param sql The SQL statement to be executed.
     * @param search1 The first specific record attribute to be search for.
     * @param search2 The second specific record attribute to be search for.
     */
    public MountieQueries(String sql, String search1, String search2) {
        try{
            setSQL(sql);
            
            //get the connection to database
            connection = DriverManager.getConnection(URL, userDB, passDB); 
          
            //prepatre statements
            preparedStatment = connection.prepareStatement(sql);
            
            //set parameters
            preparedStatment.setString(1, search1);
            preparedStatment.setString(2, search2);
            
            //execute query
            resultSet = preparedStatment.executeQuery();
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
            displayAlert(Alert.AlertType.ERROR,"Error" , 
                "Data base could not be loaded", searchString);
            System.exit(1);
        } 
    }//end of constructor with 3 parameters
    
    /**
     * Sets the SQL statement to be executed.
     * @param sql the SQL statement.
     */
    public void setSQL(String sql){
        this.sql = sql;
    }
    
    /**
     * Sets the search argument.
     * @param setSearchString  the search argument for the SQL statement.
     */
    public void setSearchString(String setSearchString){
        this.searchString = setSearchString;
    }
    
    /**
     * Sets the Result set.
     * @throws SQLException If the connection to the data base failed. 
     */
    public void setResultSet() throws SQLException{
        try{
            resultSet = preparedStatment.executeQuery();
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
            displayAlert(Alert.AlertType.ERROR,"Error" , 
                "Data base could not be loaded", searchString);
            System.exit(1);
        } 
    }

    /**
     * Gets the SQL statement.
     * @return a <code>String</code> specifying the SQL statement.
     */
    public String getSQL() {
        return sql;
    }

    /**
     * Gets the search String argument.
     * @return a <code>String</code> specifying the search string.
     */
    public String getSearchString() {
        return searchString;
    }

    /**
     * Gets the Result set.
     * @return a <code>ResulSet</code> specifying the result of the query.
     */
    public ResultSet getResultSet() {
        return resultSet;
    }
    
    /**
     * Gets the preparedStament
     * @return a <code>PreparedStatement</code> specifying the SQL set.
     */
    public PreparedStatement getPreparedStatment(){
        return preparedStatment;
    }
    
    /**
     * Add an entry to the data base with the specified arguments.
     * @param fName_isbn the students first name or books isbn of new entry.
     * @param lName_title the students last name or books title of the book.
     * @param address_pubYear the students address or the books publication year.
     * @param phone_copies the students phone number or books number of copies.
     * @param email_author the students email or books author of the book.
     * @return an <code>integer</code> specifying if the query executed successfully.
     */
    public int addEntry(String fName_isbn, String lName_title, String address_pubYear,
            String phone_copies, String email_author)
    {
        int success = 0; //0 means unsucesfully
        
        try {
            // set parameters
            preparedStatment.setString(1, fName_isbn);
            preparedStatment.setString(2, lName_title);
            preparedStatment.setString(3, address_pubYear);
            preparedStatment.setString(4, phone_copies);  
            preparedStatment.setString(5, email_author);  

            return preparedStatment.executeUpdate();       
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return success; 
        }
    }
    
    /**
     * Adds a check-out transaction to the data base.
     * @param transID the transaction id number.
     * @param isbn the books isbn number.
     * @param cardID the student id number.
     * @param dateIssue the date the book was check-out.
     * @param dateDue the date the book is due.
     * @return an <code>integer</code> specifying if the query executed successfully.
     */
    public int addCheckOut(int transID, long isbn, int cardID, String dateIssue,
            String dateDue)
    {
        int success = 0; //0 means unsucesfully
       
        try {
            // set parameters
            preparedStatment.setInt(1, transID);
            preparedStatment.setLong(2, isbn);
            preparedStatment.setInt(3, cardID);
            preparedStatment.setString(4, dateIssue);  
            preparedStatment.setString(5, dateDue);  

            return preparedStatment.executeUpdate();       
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return success; 
        }
    }
    
    /**
     * Process the books check-in process.
     * @param isbn the books isbn number.
     * @param returnDate the day the book was returned.
     * @return an <code>integer</code> specifying if the query executed successfully.
     */
     public int addCheckIn(long isbn, String returnDate)
    {
        int success = 0; //0 means unsucesfully
       
        try {
            // set parameters
            preparedStatment.setString(1, returnDate);
            preparedStatment.setLong(2, isbn);

            return preparedStatment.executeUpdate();       
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return success; 
        }
    }
    
     /**
      * Updates Students information in the data base.
      * @param fName Students first name.
      * @param lName Students last name.
      * @param address Students address.
      * @param phone Students phone number.
      * @param email Students email.
      * @param id Students id number.
      * @return an <code>integer</code> specifying if the query executed successfully.
      */
     public int updateStudent(String fName, String lName, String address, String phone,
            String email, int id)
    {
        int success = 0; //0 means unsucesfully
       
        try {// set parameters
            preparedStatment.setString(1, fName);
            preparedStatment.setString(2, lName);
            preparedStatment.setString(3, address);
            preparedStatment.setString(4, phone);  
            preparedStatment.setString(5, email);  
            preparedStatment.setInt(6, id);
            
            return preparedStatment.executeUpdate();       
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return success; 
        }
    }
     
     /**
      * Updates the book information in the data base.
      * @param title Books title.
      * @param year Books publication year.
      * @param copies Books number of copies.
      * @param author Books author.
      * @param isbn Books isbn number.
      * @return an <code>integer</code> specifying if the query executed successfully.
      */
    public int updateBook(String title, String year, int copies,
            String author, long isbn)
    {
        int success = 0; //0 means unsucesfully
        
        try {// set parameters
            preparedStatment.setString(1, title);
            preparedStatment.setString(2, year);
            preparedStatment.setInt(3, copies);
            preparedStatment.setString(4, author);  
            preparedStatment.setLong(5, isbn);

            return preparedStatment.executeUpdate();       
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return success; 
        }
    }
    
    /**
     * Update avaliable copies in the data base.
     * @param isbn Books isbn number.
     * @param copies Books number of copies.
     * @return an <code>integer</code> specifying if the query executed successfully.
     */
    public int updateBookCopies(long isbn, int copies)
    {
        int success = 0; //0 means unsucesfully
       
        try {
            // set parameters
            preparedStatment.setLong(1, copies); 
            preparedStatment.setLong(2, isbn);
            
            return preparedStatment.executeUpdate();       
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return success; 
        }
    }
    
    /**
     * Deletes a student in the data base.
     * @param cardID Students id number.
     * @return an <code>integer</code> specifying if the query executed successfully.
     */
    public int deleteStudent(int cardID)
    {
        int success = 0; //0 means unsucesfully
      
        try {
            preparedStatment.setInt(1, cardID);
            return preparedStatment.executeUpdate();       
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return success; 
        }
    }
     
    /**
     * Deletes a Book in the data base.
     * @param isbn Books isbn number.
     * @return an <code>integer</code> specifying if the query executed successfully.
     */
    public int deleteBook(long isbn)
    {
        int success = 0; //0 means unsucesfully
        
        try {
            preparedStatment.setLong(1, isbn);
            return preparedStatment.executeUpdate();       
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return success; 
        }
    }
    
    /**
     * Displays and alert window if the connection to the data base failed.
     * @param type Type of alert to be displayed.
     * @param title title of the alerts window.
     * @param messageHeader Message to be displayed on the alert window.
     * @param extraText extra text to display (optional)
     */
    private void displayAlert(Alert.AlertType type, String title, String messageHeader, 
            String extraText) 
    {
        Alert alert = new Alert(type);
        
        //If database is not available 
            alert.setTitle(title);
            alert.setHeaderText(messageHeader);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image("images/db.png"));
            alert.setGraphic(new ImageView(("images/db.png")));
            alert.showAndWait();
   }//end of displayAlert  
}
