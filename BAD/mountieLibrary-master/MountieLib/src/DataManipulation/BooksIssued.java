package DataManipulation;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * 
 * @author Adrian Mora
 */
public class BooksIssued {
    //constants
    final int DUE_DATE = 3;
    
    private int transID;
    private long isbn;
    private int cardID;
    private String issueDate;
    private String dueDate;
    private String returnedDate;
    
    /**
     * Default constructor with no-arguments.
     */
    public BooksIssued(){        
    }
    
    /**
     * Constructor with 1 parameter that sets the students ID.
     * @param cardID the students id number.
     */
    public BooksIssued(int cardID){  
        setCardID(cardID);
    }
    
    /**
     * Constructor that initialized all arguments to be used for transactions.
     * @param isbn the books isbn number.
     * @param id the students id number.
     * @throws IOException In case bad data is entered.
     * @throws SQLException If a problem connecting to the database.
     */
    public BooksIssued( long isbn, int id) throws IOException, SQLException
    {
        setTransID(transRamdonGenerator()); //sets a ramdon transaction #
        setIsbn(isbn);
        setCardID(id);
        setIssueDate(); //sets the current date.
        setDueDate();   //sets the due date.
    }

    /**
     * Sets the transaction id number.
     * @param transID transaction id number.
     */
    public void setTransID(int transID) {
        this.transID = transID;
    }

    /**
     * Sets the isbn number.
     * @param isbn books isbn number.
     */
    public void setIsbn(long isbn) {
        this.isbn = isbn;
    }

    /**
     * sets the student id.
     * @param cardID students id number.
     */
    public void setCardID(int cardID) {
        this.cardID = cardID;
    }

    /**
     * Sets the current date as the issue date for new transactions.
     */
    public void setIssueDate() {
        this.issueDate = 
            ZonedDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
    
    /**
     * Sets the issue date for queries, but not for new transactions.
     * @param date the issue date.
     */
    public void setIssueDate(String date) {
        this.issueDate = date;
    }

    /**
     * Set the due date 3 days after current day, for new transactions.
     */
    public void setDueDate() {
        this.dueDate = 
            ZonedDateTime.now().plusDays(DUE_DATE).format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
    
    /**
     * Sets the due date for queries, but not for new transactions.
     * @param date due date. the due date for the book.
     */
    public void setDueDate(String date) {
        this.dueDate = date;
    }

    /**
     * Sets the returned day when book is returned.
     */
    public void setReturnDate() {
        this.returnedDate = 
                ZonedDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
    
    /**
     * Set returned day for queries,but not for check in.
     * @param returnedDate the date the book was returned.
     */
    public void setReturnDate(String returnedDate) {
        this.returnedDate = returnedDate;
    }
    
    /**
     * Gets transaction id number
     * @return an <code>integer</code> specifying the transaction number.
     */
    public int getTransID() {
        return transID;
    }

    /**
     * Gets the isbn number.
     * @return a <code>long</code> specifying the books isbn number.
     */
    public long getIsbn() {
        return isbn;
    }

    /**
     * Gets the student id number.
     * @return an <code>integer</code> specifying the student id number.
     */
    public int getCardID() {
        return cardID;
    }

    /**
     * Gets the issue date of the book.
     * @return a <code>String</code> specifying the issue date of the book.
     */
    public String getIssueDate() {
        return issueDate;
    }

    /**
     * Gets the dude date of the book.
     * @return a <code>String</code> specifying the due date of the book.
     */
    public String getDueDate() {
        return dueDate;
    }

    /**
     * Gets the day the book was returned.
     * @return a <code>String</code> specifying the date the book was returned.
     */
    public String getReturnDate() {
        return returnedDate;
    }
    
    /**
     * Generates a random transaction number, making sure is not repeated by doing
     * a query for current transaction numbers stored in the database.
     * @return an <code>integer</code> specifying the transaction number generated. 
     * @throws IOException In case of invalid data entered.
     * @throws SQLException In case database is not available.
     */
    public int transRamdonGenerator() throws IOException, SQLException{
        Random rand = new Random(); //Get a trans. # between 100,000,000-999,999,999
        int transactionID = rand.nextInt(899999999) + 100000000; 
        
        String sql = "SELECT * FROM transactions WHERE transactionID = ?";
        MountieQueries query = new MountieQueries(sql,String.valueOf(transactionID));
        query.setResultSet();
        ResultSet resultSet = query.getResultSet();
        
        if(resultSet.next()) // if transaction id is found try to make a new one.
        {
            //If transaction ID is not unique generate a new one.
            while(resultSet.getInt("transactionID") == transactionID){
                transactionID = transRamdonGenerator();
                query = new MountieQueries(sql,String.valueOf(transactionID));
                query.setResultSet();
                resultSet = query.getResultSet();
        }
            query.getPreparedStatment().close();
            resultSet.close();
        }
        return transactionID;
    }//end of transRamdonGenerator
    
    /**
     * Add the check-out transaction to the database.
     * @return an <code>integer</code> specifying if the check out was successfully done.
     * @throws SQLException If problems accessing the database.
     */
    public int processCheckOutTransaction() throws SQLException
    {
        int success = 0; //0 == unsuccessfully
        
        String sql = "INSERT INTO transactions (transactionID, isbn, cardID, date_Issue," +
            " date_due) VALUES (?, ?, ?, ?, ?)";
        MountieQueries query = new MountieQueries(sql);
        success = query.addCheckOut(getTransID(), getIsbn(), getCardID(), 
            getIssueDate(), getDueDate());
        
        query.getPreparedStatment().close();
         
        return success;
    }
    
    /**
     * Add the check-in transaction to the database. 
     * @return an <code>integer</code> specifying if the transaction was successfully done. 
     * @throws SQLException If problems accessing the database.
     */
    public int processCheckInTransaction() throws SQLException
    {
        int success = 0; //0 == unsuccessfully
        setReturnDate();
        
        String sql = "UPDATE transactions SET date_Return = ? Where transactionID = ?";
        MountieQueries query = new MountieQueries(sql);
        success = query.addCheckIn(getTransID(), getReturnDate());
        
        query.getPreparedStatment().close();
         
        return success;
    }
    
    /**
     * Gets all current books check-out by a student.
     * @return a <code>List</code> of long with the books check-out by a student.
     * @throws SQLException If problems accessing the database.
     */
    public List<Long> getCurrentBooks() throws SQLException
    {
        List<Long> booksISBN = new ArrayList<Long>();
        String sql = "SELECT * FROM transactions WHERE cardID = ?";
        MountieQueries query = 
            new MountieQueries(sql,String.valueOf(getCardID()));
        query.setResultSet();
        ResultSet resultSet = query.getResultSet();
        
        while(resultSet.next())
        {   //Make sure returned books don't appear in the combobox.
            String nulli = resultSet.getString("date_Return");
            if(nulli == null){
                booksISBN.add(resultSet.getLong("isbn"));
            }
        }

        query.getPreparedStatment().close();
        resultSet.close();
        
        return booksISBN; //Return the list of books.
    }//end of getCurrentBooks
    
    /**
     * Gets a current check-out selected book by the students id number.
     * @param isbn books isb number
     * @param cardID students card id number
     * @return an <code>integer</code> speficying if the query was successfull.
     * @throws SQLException if there was a problem accessing the database.
     */
    public int searchTransactions(long isbn, int cardID) throws SQLException{
        int success = 0; //0 == unsuccessfully

        String sql = "SELECT * FROM transactions WHERE isbn = ? AND cardID = ?";
        MountieQueries query = new MountieQueries(sql, String.valueOf(isbn),
                String.valueOf(cardID));
        ResultSet resultSet = query.getResultSet();

        if(resultSet.next()){
            success = 1;
            setTransID(resultSet.getInt("transactionID"));
            setIssueDate(resultSet.getString("date_Issue"));
            setDueDate(resultSet.getString("date_due"));
            setReturnDate(resultSet.getString("date_Return"));
            
            query.getPreparedStatment().close();
            return success; 
        }
        else
            return success;    
    }//end of searchTransactions
}//end of BooksIssued Class
