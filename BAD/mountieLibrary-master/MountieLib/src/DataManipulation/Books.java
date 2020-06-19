                                package DataManipulation;

import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * This class handles all book related actions and information.
 * It initializes objects, and set up parameters and arguments. 
 * @author Adrian Mora
 */
public class Books {
    
    private SimpleLongProperty isbn;
    private SimpleStringProperty bookTitle;
    private SimpleStringProperty publicationYear;
    private SimpleIntegerProperty numberOfCopies;
    private SimpleStringProperty author;

    /**
     * Book constructor with no-arguments.
     */
    public Books(){}
    
    /**
     * Book constructor with one argument; used for searches.
     * @param isbn the books isbn number.
     */
    public Books(long isbn){
        setISBN(isbn);
    }
    
    /**
     * Book constructor with arguments; used for specific tasks as
     * increasing or decreasing book quantity.
     * @param isbn the books isbn number.
     * @param copies number of copies avaliable.
     */
    public Books(long isbn, int copies){
        setISBN(isbn);
        setNumberOfCopies(copies);
    }
 
    /**
     * Book constructor with arguments; used to initialized a new book to be added
     * to the data base.
     * @param isbn the books isbn number.
     * @param bookTitle the books title.
     * @param publicationYear publication year of the book.
     * @param numberOfCopies number of copies avaliable.
     * @param author the author of the book.
     */
    public Books(long isbn, String bookTitle, String publicationYear, 
            int numberOfCopies, String author)
    {
        setISBN(isbn);
        setBookTitle(bookTitle);
        setPublicationDate(publicationYear);
        setNumberOfCopies(numberOfCopies);
        setAuthor(author);
    }

    /**
     * Sets the Books ISBN
     * @param isbn the books isbn number.
     * @throws IllegalArgumentException in case a smaller or bigger ISBN is entered.
     */
    public void setISBN(long isbn) {
        if(isbn < 99999999999L || isbn > 1000000000000L)
            throw new IllegalArgumentException("ISBN mus bee 12 digits.");
        
        this.isbn = new SimpleLongProperty(isbn);
    }
    
    /**
     * Sets books title
     * @param bookTitle the books title
     */
    public void setBookTitle(String bookTitle){
        this.bookTitle = new SimpleStringProperty(bookTitle);
    }
    
    /**
     * Sets books publication year
     * @param publicationYear the publication year of the book.
     */
    public void setPublicationDate(String publicationYear){
        this.publicationYear = new SimpleStringProperty(publicationYear);
    }
    
    /**
     * Sets books avaliable copies.
     * @param numberOfCopies the number of copies avaliable.
     */
    public void setNumberOfCopies(int numberOfCopies){
        this.numberOfCopies = new SimpleIntegerProperty(numberOfCopies);
    }
    
    /**
     * Sets books author
     * @param author the author of the book.
     */
    public void setAuthor(String author){
        this.author = new SimpleStringProperty(author);
    }
    
    /**
     * Gets books ISBN
     * @return a <code>long</code> specifying the books ISBN.
     */
    public long getIsbn() {
        return isbn.get();
    }

    /**
     * Gets books title
     * @return a <code>String</code> specifying the books title.
     */
    public String getBookTitle() {
        return bookTitle.get();
    }

    /**
     * Gets book publication year.
     * @return a <code>String</code> specifying the year published.
     */
    public String getPublicationDate() {
        return publicationYear.get();
    }

    /**
     * Gets book avaliable copies
     * @return an <code>integer</code> specifying the number of copies avaliable.
     */
    public int getNumberOfCopies() {
        return numberOfCopies.get();
    }
    
    /**
     * Gets book Author.
     * @return a <code>String</code> specifying the books author.
     */
    public String getAuthor(){
        return author.get();
    }
    
    /**
     * Gets the isbn from mainWindowController to use it as a parameter in the
     * SQL statement which looks for the book with that isbn number. If a match is
     * found, it returns a 1 meaning is true, or a 0 if it did not found a match.
     * @param isbn the books isbn number.
     * @return an <code>integer</code> specifying if a book was found in the database.
     * @throws SQLException if there is an error communicating with the database.
     */
    public int searchBook(long isbn) throws SQLException
    {
        int success = 0; //0 == unsuccessfully
        
        String sql = "SELECT * FROM book WHERE isbn = ?";
        MountieQueries query = new MountieQueries(sql, String.valueOf(isbn));
        query.setResultSet();
        ResultSet resultSet = query.getResultSet();
        
        if(resultSet.next()){
            success = 1;
            setBookTitle(resultSet.getString("book_title"));
            setPublicationDate(resultSet.getString("date_of_publication"));
            setNumberOfCopies(resultSet.getInt("number_of_copies"));
            setAuthor(resultSet.getString("author"));
            
            query.getPreparedStatment().close();
            return success; 
        }
        else
            return success;    
    }
    
    /**
     * Decreases the number of avaliable books in the database.
     * @throws SQLException if there is an error communicating with the database.
     */
     public void decreasedCopies() throws SQLException{
        String sql = "UPDATE book SET number_of_copies = ? WHERE isbn = ?";
        MountieQueries query = new MountieQueries(sql);
        int success = query.updateBookCopies(getIsbn(), getNumberOfCopies()-1);
        query.getPreparedStatment().close();
        
        if(success == 0){
            displayAlert(Alert.AlertType.ERROR,"Error" , 
                ("Book avl. copies was not updated"), "6");
        }
    }
     
    /**
     * Increases the number of books avaliable in the database.
     * @throws SQLException if there is an error communicating with the database.
     */
    public void increasedCopies() throws SQLException{
        String sql = "UPDATE book SET number_of_copies = ? WHERE isbn = ?";
        MountieQueries query = new MountieQueries(sql);
        int success = query.updateBookCopies(getIsbn(), getNumberOfCopies()+1);
        query.getPreparedStatment().close();
        
        if(success == 0){
            displayAlert(Alert.AlertType.ERROR,"Error" , 
                ("Book avl. copies was not updated"), "6");
        }
    }

    /**
     * Displays and alert window if the there was an error updating books 
     * avaliable copies.
     * @param type type of alert to display
     * @param title the title of the alert window
     * @param messageHeader the message on the alert window
     * @param errorCode the error code to be displayed if multiple avaliable.
     */
   private void displayAlert(Alert.AlertType type, String title, 
            String messageHeader, String errorCode) 
    {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(messageHeader);
        alert.setHeaderText(null);
        Stage stage5 = (Stage) alert.getDialogPane().getScene().getWindow();
        stage5.getIcons().add(new Image("images/emptySpace.png"));
        alert.setGraphic(new ImageView(("images/emptySpace.png")));
        alert.showAndWait();
    }
}//end of class Books.
