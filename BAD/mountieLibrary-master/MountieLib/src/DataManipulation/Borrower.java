package DataManipulation;

import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * This class handles the actions, and the information of borrowers (students) 
 * @author Adrian Mora
 */
public class Borrower {
    final int ZERO = 0;
    private SimpleIntegerProperty cardID;
    private SimpleStringProperty firstName;
    private SimpleStringProperty lastName;
    private SimpleStringProperty address;
    private SimpleStringProperty phoneNum;
    private SimpleStringProperty email;
    private SimpleIntegerProperty booksBorrow;
    
    /**
     * Constructor with no-arguments.
     */
    public Borrower(){}
    
    /**
     * Constructor with one parameter; used for searches.
     * @param cardID the student id number.
     */
    public Borrower(int cardID){
        setCardID(cardID);
    }
    
    /**
     * Constructor with arguments. It is used to set up new books on the database.
     * @param firstName the students first name.
     * @param lastName the students last name.
     * @param address the students address.
     * @param phoneNum the students phone number.
     * @param email the students email.
     */
    public Borrower(String firstName, String lastName,
            String address, String phoneNum, String email)
    {
        setFirstName(firstName);
        setLastName(lastName);
        setAddress(address);
        setPhoneNum(phoneNum);
        setEmail(email);
    }
    
    /**
     * Constructor with arguments. It is used to display students from data base.
     * Not to setup new ones.
     * @param cardID the students id number.
     * @param firstName the students first name.
     * @param lastName the students last name.
     * @param address the students address.
     * @param phoneNum the students phone number.
     * @param email the students email.
     */
    public Borrower(int cardID ,String firstName, String lastName,
            String address, String phoneNum, String email)
    {
        setCardID(cardID);
        setFirstName(firstName);
        setLastName(lastName);
        setAddress(address);
        setPhoneNum(phoneNum);
        setEmail(email);
    }

    /**
     * Sets the student id number.
     * @param cardID the students id number
     */
    public void setCardID(int cardID) {
        this.cardID = new SimpleIntegerProperty(cardID);
    }
    
    /**
     * Sets the students first name
     * @param firstName the students first name.
     */
    public void setFirstName(String firstName) {
        this.firstName = new SimpleStringProperty(firstName);
    }
     
    /**
     * Sets the students last name.
     * @param lastName the students last name.
     */
    public void setLastName(String lastName) {
        this.lastName = new SimpleStringProperty(lastName);
    }
    
    /**
     * Sets the students address.
     * @param address the students address.
     */
    public void setAddress(String address) {
        this.address = new SimpleStringProperty(address);
    }
    
    /**
     * Sets the students phone number.
     * @param phoneNum the students phone number
     */
    public void setPhoneNum(String phoneNum) {
        this.phoneNum = new SimpleStringProperty(phoneNum);
    }
    
    /**
     * Sets the students email. 
     * @param email the students email.
     */
    public void setEmail(String email){
        this.email = new SimpleStringProperty(email);
    }
    
    /**
     * Gets the students id number.
     * @return an <code>integer</code> specifying the students number id.
     */
    public int getCardID() {
        return cardID.get();
    }
     
    /**
     * Gets the students first name.
     * @return a <code>String</code> specifying the students first name.
     */
    public String getFirstName() {
        return firstName.get();
    }
    
    /**
     * Gets the students last name.
     * @return a <code>String</code> specifying the students last name.
     */
    public String getLastName() {
        return lastName.get();
    }

    /**
     * Gets the students address.
     * @return a <code>String</code> specifying the students address.
     */
    public String getAddress() {
        return address.get();
    }

    /**
     * Gets the students phone number.
     * @return a <code>String</code> specifying the students phone number.
     */
    public String getPhoneNum() {
        return phoneNum.get();
    }
    
    /**
     * Gets the students email.
     * @return a <code>String</code> specifying the students email.
     */
    public String getEmail(){
        return email.get();
    }
    
    /**
     * This searches for a student in the data base using the students id number.
     * @param cardID the students id number.
     * @return an <code>integer</code> specifying if the query was successful.
     * @throws SQLException If connection to the data base failed.
     */
    public int searchBorrowers(int cardID) throws SQLException
    {
        int success = 0; //0 == unsuccessfully
        
        String sql = "SELECT * FROM borrower WHERE cardID = ?";
        MountieQueries query = new MountieQueries(sql,String.valueOf(cardID));
        query.setResultSet();
        ResultSet resultSet = query.getResultSet();
        
        if(resultSet.next()){
            success = 1;
            setFirstName(resultSet.getString("borrower_FN"));
            setLastName(resultSet.getString("borrower_LN"));
            setAddress(resultSet.getString("borrower_Address"));
            setPhoneNum(resultSet.getString("borrower_Phone"));
            setEmail(resultSet.getString("borrower_Email"));
            
            query.getPreparedStatment().close();
            return success; 
        }
        else
            return success;    
    }
}//end of borrowers.
