package mainWindow;

import DataManipulation.Books;
import DataManipulation.BooksIssued;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import DataManipulation.Borrower;
import DataManipulation.MountieQueries;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;

/**
 * Controller for MainWindow of operations after login screen.
 * @author Adrian Mora
 */
public class MainWindowController implements Initializable {
    
    //Constant Variables
    final int CARD_ID_SIZE = 10;
    final int ISBN_SIZE = 12;
    //Search borrower consts
    final String CARDID = "Card ID";
    final String BORROWER_FN = "F. Name";
    final String BORROWER_LN = "L. Name";
    final String BORROWER_ADDRESS = "Address";
    final String BORROWER_PHONE = "Phone";
    final String BORROWER_EMAIL = "Email";
    final int F_NAME_SIZE = 100;
    final int L_NAME_SIZE = 100;
    //Search book consts
    final String ISBN = "ISBN";
    final String BOOK_TITLE = "Title";
    final String PUBLICATION_YEAR = "Pub. Year";
    final String NUMBER_OF_COPIES = "Copies Avl.";
    final String AUTHOR = "Author";
    final int TITLE_COLUMN_SIZE = 400;
    final int ISBN_COLUMN_SIZE = 100;
    final int NUMBER_OF_COPIES_SIZE = 125;
    final int PUB_SIZE = 75;
            
    //FXML objects in the interface MountieLib.fxml
    @FXML private TextField searchTextField;
    @FXML private TableView resultsTableView;
    @FXML private TableColumn studentIDCol; 
    @FXML private TableColumn studentFirstNameCol;
    @FXML private TableColumn studentLastNameCol;
    @FXML private TableColumn studentPhoneCol;
    @FXML private TableColumn studentAddressCol;
    @FXML private TableColumn studentEmailCol;
    @FXML private MenuItem logoutMenuItem; 
    @FXML private MenuBar menuBar;
    @FXML private RadioButton borrowersRadioButton;
    @FXML private RadioButton booksRadioButton;
    @FXML private TextField newStudFN;
    @FXML private TextField newStudLN;
    @FXML private TextField newStudAddress;
    @FXML private TextField newStudPhone;
    @FXML private TextField newStudEmail;
    @FXML private TextField newBookISBN;
    @FXML private TextField newBookTitle;
    @FXML private TextField newBookYear;
    @FXML private TextField newBookCopies;
    @FXML private TextField newBookAuthor;
    @FXML private TextField fNameUpdateTF;
    @FXML private TextField lNameUpdateTF;
    @FXML private TextField iDUpdateTF;
    @FXML private TextField addressUpdateTF;
    @FXML private TextField phoneUpdateTF;        
    @FXML private TextField emailUpdateTF;
    @FXML private TextField isbnUpdateTF;
    @FXML private TextField titleUpdateTF;
    @FXML private TextField yearUpdateTF;       
    @FXML private TextField copiesUpdateTF;
    @FXML private TextField authorUpdateTF;
    @FXML private TextField fNameCheckOutTF;
    @FXML private TextField lNameCheckOutTF;
    @FXML private TextField iDCheckOutTF;
    @FXML private TextField addressCheckOutTF;
    @FXML private TextField phoneCheckOutTF;
    @FXML private TextField emailCheckOutTF;
    @FXML private TextField isbnCheckOutTF;
    @FXML private TextField titleCheckOutTF;
    @FXML private TextField yearCheckOutTF;
    @FXML private TextField authorCheckOutTF;
    @FXML private TextField copiesCheckOutTF;
    @FXML private ComboBox checkOutCBox;
    @FXML private TextField transCheckOutTF;
  
    //Manages connection
    private ResultSet resultSet;
    
   /**
    * Function to run if a student ID, or an ISBN is entered in the search box.
    * It will get the data from the search box, and if a match is found in the data base,
    * it will display the information to the mainWindow tableView. If not data matched the
    * query, the program will display an alert saying so.
    * @param event The event that trigger this function.
    * @throws IOException If bad data was entered.
    * @throws SQLException  If connection to data base failed.
    */
    @FXML public void searchButtonPressed(ActionEvent event) throws IOException, SQLException
    {
        //Get student Id from search box and declare sql query statement.
        String searchString = searchTextField.getText();

        //If entered value is a student ID do the following.
        if(searchString.length() == CARD_ID_SIZE)
            processCardID(searchString);
        else if(searchString.length() == ISBN_SIZE) //if is an ISBN do the following.
            processISBN(searchString);
        else{                                       //else display no found.
            displayAlert(Alert.AlertType.ERROR, "Not found", 
                "\ncard ID/ISBN invalid or not found", "1");
        } 
        searchTextField.clear();
    }//end of searchIDButtonPressed
    
    /**
     * Handles logout request, and return the user to the login screen.
     * @param event The event that trigger this function.
     * @throws IOException If the fxml was not found.
     */
    @FXML public void logoutUser(ActionEvent event) throws IOException
    {
        //Close current window, and go back to login screen
        Stage stage = (Stage) menuBar.getScene().getWindow();
        stage.close();
        
        Stage stage2 = new Stage();
        Parent root2 = FXMLLoader.load(getClass().getResource("/dataBaseLogin/MountieLib.fxml"));
        Scene scene2 = new Scene(root2);
        stage2.setScene(scene2);
        stage2.getIcons().add(new Image("images/titleIcon.png"));
        stage2.initModality(Modality.NONE);
        stage2.show();
    }
        
    //Initializes the controller class.
    @Override public void initialize(URL url, ResourceBundle rb) {
        logoutMenuItem.setGraphic(new ImageView(new Image("images/switchUser.png")));
    }    
    
    /**
     * Process the search of a student using the students id. If a student is found,
     * display result in the tableView;otherwise, display a warning message.
     * @param searchString the student id to search for.
     * @throws SQLException if connection to data base failed.
     */
    private void processCardID(String searchString) throws SQLException
    {
        Borrower student = new Borrower(Integer.valueOf(searchString));
        int success = student.searchBorrowers(student.getCardID());

        //display result set if it matched
        if(success == 1){
            //List of class Borrowers to store objects to be display in the tableView
            ObservableList<Borrower> data;

            //Create objects of class Borrower with all parameters adquired 
            //from the data base and stored as Strings, and saved the objects
            //in the data observableArrayList.
            data = FXCollections.observableArrayList(student);

            //Set columns for this search
            studentIDCol.setText(CARDID);
            studentFirstNameCol.setText(BORROWER_FN);
            studentLastNameCol.setText(BORROWER_LN);
            studentAddressCol.setText(BORROWER_ADDRESS);
            studentPhoneCol.setText(BORROWER_PHONE);
            studentEmailCol.setText(BORROWER_EMAIL);
            studentAddressCol.setVisible(true);
            studentEmailCol.setVisible(true);
            studentFirstNameCol.setPrefWidth(F_NAME_SIZE);
            studentLastNameCol.setPrefWidth(L_NAME_SIZE);

            //Populate the tableView and columns with data obtained from 
            //objects of classBorrowers store in the observableArrayList.
            studentIDCol.setCellValueFactory(
                    new PropertyValueFactory<Borrower, String>("cardID"));
            studentFirstNameCol.setCellValueFactory(
                    new PropertyValueFactory<Borrower, String>("firstName"));
            studentLastNameCol.setCellValueFactory(
                    new PropertyValueFactory<Borrower, String>("lastName"));
            studentAddressCol.setCellValueFactory(
                    new PropertyValueFactory<Borrower, String>("address"));
            studentPhoneCol.setCellValueFactory(
                    new PropertyValueFactory<Borrower, String>("phoneNum"));
            studentEmailCol.setCellValueFactory(
                    new PropertyValueFactory<Borrower, String>("email"));
            resultsTableView.setItems(data);
        }//end of if
        //Display a warning alert if student ID is not found in data base
        else{
            displayAlert(Alert.AlertType.WARNING,"Not on record" , 
              ("\nStudent ID: " + searchString + " not found"), "2");
        }
    }//end of processingCardID
    
    /**
     * Process the search for a book using the isbn number. If a book is found,
     *  display the result in the tableView;otherwise, display a warning message.
     * @param searchString the isbn number to search for.
     * @throws SQLException if the connection to the data base failed.
     */
    private void processISBN(String searchString) throws SQLException
    {
        Books book = new Books(Long.valueOf(searchString));
        int success = book.searchBook(book.getIsbn());
        
        //display result set if it matched
        if(success == 1){  
            //List of class Books to store objects to be display in the tableView
            ObservableList<Books> data;

            data = FXCollections.observableArrayList(book);

            //Hide unnecessary columns for this search result
            studentIDCol.setText(ISBN);
            studentFirstNameCol.setText(BOOK_TITLE);
            studentLastNameCol.setText(PUBLICATION_YEAR);
            studentPhoneCol.setText(NUMBER_OF_COPIES);
            studentAddressCol.setText(NUMBER_OF_COPIES);
            studentEmailCol.setVisible(false);
            studentFirstNameCol.setPrefWidth(TITLE_COLUMN_SIZE);
            studentIDCol.setPrefWidth(ISBN_COLUMN_SIZE);
            studentPhoneCol.setPrefWidth(NUMBER_OF_COPIES_SIZE);
            studentAddressCol.setPrefWidth(ISBN_COLUMN_SIZE);

            //Populate the tableView and columns with data obtained from 
            //objects of classBorrowers store in the observableArrayList.
            studentIDCol.setCellValueFactory(
                    new PropertyValueFactory<Books, String>("isbn"));
            studentFirstNameCol.setCellValueFactory(
                    new PropertyValueFactory<Books, String>("bookTitle"));
            studentLastNameCol.setCellValueFactory(
                    new PropertyValueFactory<Books, String>("publicationDate"));
            studentPhoneCol.setCellValueFactory(
                    new PropertyValueFactory<Books, String>("numberOfCopies"));
            studentAddressCol.setCellValueFactory(
                    new PropertyValueFactory<Books, String>("author"));
            resultsTableView.setItems(data);
        }//end of if   
        else{ //Display a warning alert if student ID is not found in data base
            displayAlert(Alert.AlertType.WARNING,"Not on record" , 
              ("\nBook isbn: " + searchString + " not found"), "3");
        }
    }//end of processingISBN
    
    /**
     * Process the action when the View button is pressed. Shows all students or books.
     * @param event The event that triggered this function.
     * @throws IOException If a file cannot be loaded.
     * @throws SQLException If the connection to the data base failed.
     */
    @FXML public void viewButtonPressed(ActionEvent event) throws IOException, SQLException
    {
        if(borrowersRadioButton.isSelected()){
            String sql = "SELECT * FROM borrower ORDER BY borrower_LN ASC, borrower_FN ASC";
            MountieQueries query = new MountieQueries(sql);
            query.setResultSet();
            resultSet = query.getResultSet();

            //List of class Borrowers to store objects to be display in the tableView
            ObservableList<Borrower> borrowersList = FXCollections.observableArrayList();
                
            //Set columns for this search
            studentIDCol.setText(CARDID);
            studentFirstNameCol.setText(BORROWER_FN);
            studentLastNameCol.setText(BORROWER_LN);
            studentAddressCol.setText(BORROWER_ADDRESS);
            studentPhoneCol.setText(BORROWER_PHONE);
            studentEmailCol.setText(BORROWER_EMAIL);
            studentAddressCol.setVisible(true);
            studentEmailCol.setVisible(true);
            studentFirstNameCol.setPrefWidth(F_NAME_SIZE);
            studentLastNameCol.setPrefWidth(L_NAME_SIZE);

            //display result set if it matched
            while(resultSet.next())
            {   //Create objects of class Borrower with all parameters adquired 
                //from the data base and stored as Strings, and saved the objects
                //in the data observableArrayList.
                borrowersList.add( (new Borrower(resultSet.getInt("cardID"),
                        resultSet.getString("borrower_FN"),
                        resultSet.getString("borrower_LN"),
                        resultSet.getString("borrower_Address"),
                        resultSet.getString("borrower_Phone"),
                        resultSet.getString("borrower_Email")
                        )));

                //Populate the tableView and columns with data obtained from 
                //objects of classBorrowers store in the observableArrayList.
                studentIDCol.setCellValueFactory(
                        new PropertyValueFactory<Borrower, String>("cardID"));
                studentFirstNameCol.setCellValueFactory(
                        new PropertyValueFactory<Borrower, String>("firstName"));
                studentLastNameCol.setCellValueFactory(
                        new PropertyValueFactory<Borrower, String>("lastName"));
                studentAddressCol.setCellValueFactory(
                        new PropertyValueFactory<Borrower, String>("address"));
                studentPhoneCol.setCellValueFactory(
                        new PropertyValueFactory<Borrower, String>("phoneNum"));
                studentEmailCol.setCellValueFactory(
                        new PropertyValueFactory<Borrower, String>("email"));
            }//end of while

            resultsTableView.setItems(borrowersList);
            query.getPreparedStatment().close();
            resultSet.close(); 
            query.getResultSet().close();
        }//end of borrowers if
        if(booksRadioButton.isSelected()){
            String sql = "SELECT * FROM book ORDER BY book_title ASC";
            MountieQueries query = new MountieQueries(sql);
            query.setResultSet();
            resultSet = query.getResultSet();
           
            //List of class Borrowers to store objects to be display in the tableView
            ObservableList<Books> booksList = FXCollections.observableArrayList();

            //Hide unnecessary columns for this search result
            studentFirstNameCol.setText(BOOK_TITLE);
            studentLastNameCol.setText(PUBLICATION_YEAR);
            studentIDCol.setText(ISBN);
            studentPhoneCol.setText(NUMBER_OF_COPIES);
            studentAddressCol.setText(AUTHOR);
            studentEmailCol.setVisible(false);
            
            studentFirstNameCol.setPrefWidth(TITLE_COLUMN_SIZE);
            studentLastNameCol.setPrefWidth(PUB_SIZE);
            studentIDCol.setPrefWidth(ISBN_COLUMN_SIZE);
            studentPhoneCol.setPrefWidth(PUB_SIZE);
                
            while(resultSet.next()){     //display result set if it matched
                booksList.add(new Books(resultSet.getLong("isbn"),
                        resultSet.getString("book_title"),
                        resultSet.getString("date_of_publication"),
                        resultSet.getInt("number_of_copies"),
                        resultSet.getString("author")));

                //Populate the tableView and columns with data obtained from 
                //objects of classBorrowers store in the observableArrayList.
                studentIDCol.setCellValueFactory(
                        new PropertyValueFactory<Books, String>("isbn"));
                studentFirstNameCol.setCellValueFactory(
                        new PropertyValueFactory<Books, String>("bookTitle"));
                studentLastNameCol.setCellValueFactory(
                        new PropertyValueFactory<Books, String>("publicationDate"));
                studentPhoneCol.setCellValueFactory(
                        new PropertyValueFactory<Books, String>("numberOfCopies"));
                studentAddressCol.setCellValueFactory(
                    new PropertyValueFactory<Books, String>("author"));
            }//end of while
            
            resultsTableView.setItems(booksList);
            query.getPreparedStatment().close();
            resultSet.close(); 
            query.getResultSet().close();
        }//end of books if
        
        final Tooltip tooltip = new Tooltip();
        tooltip.setText( "Double click to populate \n" + "update & checkout tabs.\n" );
        resultsTableView.setTooltip(tooltip);
    }//end of viewBUtton
    
    /**
     * Process new student record. It makes sure fields are not empty. Also, it 
     * displays an alert window whether the operation succeed or failed. 
     * @param event The event that triggered this function.
     * @throws IOException if a file cannot be loaded.
     * @throws SQLException If the connection to the data base failed.
     */
    @FXML public void newStudentButton(ActionEvent event) throws IOException, SQLException
    {
        if(!newStudFN.getText().isEmpty() && !newStudLN.getText().isEmpty() && 
            !newStudAddress.getText().isEmpty() && !newStudPhone.getText().isEmpty() &&
            !newStudEmail.getText().isEmpty())
        {
            String sql = "INSERT INTO borrower (borrower_FN, borrower_LN, borrower_Address"
                + ", borrower_Phone, borrower_Email) VALUES (?, ?, ?, ?, ?)";
            MountieQueries query = new MountieQueries(sql);
            Borrower student = new Borrower(newStudFN.getText(), newStudLN.getText(), 
                newStudAddress.getText(), newStudPhone.getText(), newStudEmail.getText());
            int success = query.addEntry(student.getFirstName(), student.getLastName(),
                student.getAddress(), student.getPhoneNum(), student.getEmail());

            if(success == 1){ //Get new recorded info and display it in an alert window.
                sql = "SELECT * FROM borrower order by cardID desc limit 1";
                query = new MountieQueries(sql);
                query.setResultSet();
                resultSet = query.getResultSet();

                if(resultSet.next()){
                    displayAlert(Alert.AlertType.INFORMATION,"Student added successfully" , 
                        ("Assigned Student ID: " + resultSet.getInt("cardID") + 
                         "\nName: " + resultSet.getString("borrower_FN") + " " +
                         resultSet.getString("borrower_LN") + "\nAddres: " +
                         resultSet.getString("borrower_Address") + "\nPhone: " + 
                         resultSet.getString("borrower_Phone") + "\nEmail: " +
                         resultSet.getString("borrower_Email")), "4");

                    newStudFN.clear();
                    newStudLN.clear();
                    newStudAddress.clear();
                    newStudPhone.clear();
                    newStudEmail.clear();
                }
                query.getPreparedStatment().close();
                resultSet.close(); 
                query.getResultSet().close();
                resultsTableView.getItems().clear(); //clear table data to show new one.
            }
            else{
                    displayAlert(Alert.AlertType.WARNING,"Error" , 
                        ("Error adding student"), "2");
            }
        }// end of not empty textFields if
        else{
            displayAlert(Alert.AlertType.WARNING,"Error" , 
                        ("Complete all data fields!"), "6");
        }
    }
    
    /**
     * Process new book record. It makes sure fields are not empty. Also, it 
     * displays an alert window whether the operation succeed or failed. 
     * @param event The event that triggered this function.
     * @throws IOException IOException if a file cannot be loaded.
     * @throws SQLException SQLException If the connection to the data base failed.
     */
    @FXML public void newBookButton(ActionEvent event) throws IOException, SQLException
    {
        if(!newBookISBN.getText().isEmpty() && !newBookTitle.getText().isEmpty() && 
            !newBookYear.getText().isEmpty() && !newBookCopies.getText().isEmpty() &&
            !newBookAuthor.getText().isEmpty())
        {
            String sql = "INSERT INTO book (isbn, book_title, date_of_publication, " +
                "number_of_copies, author) VALUES (?, ?, ?, ?, ?)";
            MountieQueries query = new MountieQueries(sql);
            Books book = new Books(Long.valueOf(newBookISBN.getText()), newBookTitle.getText(), 
                    newBookYear.getText(), Integer.valueOf(newBookCopies.getText()),
                    newBookAuthor.getText());
            int success = query.addEntry(String.valueOf(book.getIsbn()), book.getBookTitle(), 
                    book.getPublicationDate(), String.valueOf(book.getNumberOfCopies()),
                    book.getAuthor());

            if(success == 1){ 
                displayAlert(Alert.AlertType.INFORMATION,"Book added successfully" , 
                    ("Book ISBN: " + newBookISBN.getText() + 
                     "\nTitle: " + newBookTitle.getText() + 
                     "\nPublication Year: " + newBookYear.getText() + 
                     "\nCopies: " + newBookCopies.getText() + 
                     "\nAuthor: " + newBookAuthor.getText()), "5");

                newBookISBN.clear();
                newBookTitle.clear();
                newBookYear.clear();
                newBookCopies.clear();
                newBookAuthor.clear();

                query.getPreparedStatment().close();
                resultsTableView.getItems().clear(); //clear table data to show new one.
            }
            else{
                    displayAlert(Alert.AlertType.WARNING,"Error" , 
                        ("Error adding book"), "3");
            }
        }//end of not empty textfields if
        else{
            displayAlert(Alert.AlertType.WARNING,"Error" , 
                        ("Complete all data fields!"), "6");
        }
    }//end of newBookButton
    
     /**
      * Clears Form for New Student Record when button is pressed.
      * @param event The even that triggered this function.
      */
    @FXML public void clearNewStudentForm(ActionEvent event) {
        newStudFN.clear();
        newStudLN.clear();
        newStudAddress.clear();
        newStudPhone.clear();
        newStudEmail.clear();
    }
    
    /**
     * Clears New Book Form when button is pressed.
     * @param event The even that triggered this function.
     */
    @FXML public void clearNewBookForm(ActionEvent event) {
        newBookISBN.clear();
        newBookTitle.clear();
        newBookYear.clear();
        newBookCopies.clear();
        newBookAuthor.clear();
    }
    
    /**
     * Clears Update Form when button is pressed.
     * @param event The even that triggered this function.
     */
    @FXML public void clearUpdateFormButton(ActionEvent event) {
        clearUpdateForm();
    }
    
    /**
     * Clears Update Form.
     */
    public void clearUpdateForm(){
        isbnUpdateTF.clear();
        titleUpdateTF.clear();
        yearUpdateTF.clear();
        copiesUpdateTF.clear();
        authorUpdateTF.clear();
        fNameUpdateTF.clear();
        lNameUpdateTF.clear();
        iDUpdateTF.clear();
        addressUpdateTF.clear();
        phoneUpdateTF.clear();
        emailUpdateTF.clear();
    }
    
    /**
     * Clears TableView records.
     * @param event The even that triggered this function.
     * @throws IOException In case a file cannot be loaded.
     */
    @FXML public void clearTableView(MouseEvent  event) throws IOException{
        resultsTableView.getItems().clear(); //clear table data to show new one.
    }
    
    /**
     * Populate update tab and check up tab with the information double clicked by the
     * mouse for faster processing of books and students.
     * @param event The even that triggered this function.
     * @throws IOException In case a file cannot be loaded.
     * @throws SQLException If connection to the data base failed.
     */
    @FXML public void mouseClickTableView(MouseEvent  event) throws IOException, SQLException
    {
        if(event.getClickCount() == 2){ 
            clearUpdateForm();
            checkOutCBox.getItems().clear();
            
            if(borrowersRadioButton.isSelected()){
                Borrower populateObj = 
                    (Borrower) resultsTableView.getSelectionModel().getSelectedItem();
                BooksIssued currentBooks = 
                    new BooksIssued(populateObj.getCardID());
                Books bookTitle = new Books();
                
                //Populate Checkbox if student has books check out
                List<Long> checkOutResults = new ArrayList<Long>();
                checkOutResults = currentBooks.getCurrentBooks(); //isbn

                if(checkOutResults.isEmpty()){}//no current check out books
                else{   //fill combobox with check out books
                    String title;
                    for(int i=0; i < checkOutResults.size(); i++){
                        bookTitle.searchBook(checkOutResults.get(i));
                        title = bookTitle.getBookTitle();
                        checkOutCBox.getItems().add(checkOutResults.get(i) + " - " + title);
                    } 
                }
                fNameUpdateTF.setText(populateObj.getFirstName());
                lNameUpdateTF.setText(populateObj.getLastName());
                iDUpdateTF.setText(String.valueOf(populateObj.getCardID()));
                addressUpdateTF.setText(populateObj.getAddress());
                phoneUpdateTF.setText(populateObj.getPhoneNum());
                emailUpdateTF.setText(populateObj.getEmail()); 
                
                fNameCheckOutTF.setText(populateObj.getFirstName());
                lNameCheckOutTF.setText(populateObj.getLastName());
                iDCheckOutTF.setText(String.valueOf(populateObj.getCardID()));
                addressCheckOutTF.setText(populateObj.getAddress());
                phoneCheckOutTF.setText(populateObj.getPhoneNum());
                emailCheckOutTF.setText(populateObj.getEmail()); 
            }
            if(booksRadioButton.isSelected()){
                Books populateObj = 
                    (Books)resultsTableView.getSelectionModel().getSelectedItem();
                isbnUpdateTF.setText(Long.toString(populateObj.getIsbn()));
                titleUpdateTF.setText(populateObj.getBookTitle());
                yearUpdateTF.setText(populateObj.getPublicationDate());
                copiesUpdateTF.setText(Integer.toString(populateObj.getNumberOfCopies()));
                authorUpdateTF.setText(populateObj.getAuthor());
                
                isbnCheckOutTF.setText(Long.toString(populateObj.getIsbn()));
                titleCheckOutTF.setText(populateObj.getBookTitle());
                yearCheckOutTF.setText(populateObj.getPublicationDate());
                copiesCheckOutTF.setText(Integer.toString(populateObj.getNumberOfCopies()));
                authorCheckOutTF.setText(populateObj.getAuthor());
            }
        }//end of if clicks == 2
    }//end of mouseClick
    
    /**
     * Process the books check-out by a student, and then it fills the combobox with them.
     * @param event The even that triggered this function.
     * @throws IOException In case a file cannot be loaded.
     * @throws SQLException If connection to the data base failed.
     */
    @FXML public void mouseClickComboBox(MouseEvent  event) throws IOException, SQLException
    {
        String isbnExtractor = 
            String.valueOf(checkOutCBox.getSelectionModel().getSelectedItem());
        String isbn = null;

        if(!checkOutCBox.getSelectionModel().isEmpty()){
            isbn = isbnExtractor.substring(0, isbnExtractor.indexOf(" ")); 
            BooksIssued transaction = new BooksIssued();
            Books book = new Books(Long.valueOf(isbn));
            int success = transaction.searchTransactions(Long.valueOf(isbn),
                Integer.valueOf(iDCheckOutTF.getText()));
            int success2 = book.searchBook(Long.valueOf(isbn));

            if(success == 1 && success2 == 1){
                transCheckOutTF.setText(String.valueOf(transaction.getTransID()));
                isbnCheckOutTF.setText(isbn);
                titleCheckOutTF.setText(book.getBookTitle());
                yearCheckOutTF.setText(book.getPublicationDate());
                authorCheckOutTF.setText(book.getAuthor());
                copiesCheckOutTF.setText(String.valueOf(book.getNumberOfCopies()));
            }
            else{
                displayAlert(Alert.AlertType.WARNING,"Error" , 
                    ("Error finding transaction."), "6");
            }
        }
    }
    
    /**
     * Updates student or book record on the data base, when button pressed.
     * @param event The even that triggered this function.
     * @throws IOException IOException In case a file cannot be loaded.
     * @throws SQLException The even that triggered this function.
     */
    @FXML public void saveUpdateButtonPressed(ActionEvent event) 
            throws IOException, SQLException
    {       
        if(!iDUpdateTF.getText().isEmpty() && !fNameUpdateTF.getText().isEmpty() && 
            !lNameUpdateTF.getText().isEmpty() && !addressUpdateTF.getText().isEmpty() &&
            !phoneUpdateTF.getText().isEmpty() && !emailUpdateTF.getText().isEmpty()
            && borrowersRadioButton.isSelected())
        {   
            String sql = "UPDATE borrower SET borrower_FN = ?, borrower_LN = ?, " +
                "borrower_Address = ?, borrower_Phone = ?, borrower_Email = ? WHERE"
                    + " cardID = ?";
            MountieQueries query = new MountieQueries(sql);
            int success = query.updateStudent( fNameUpdateTF.getText(), 
                    lNameUpdateTF.getText(), addressUpdateTF.getText(), 
                    phoneUpdateTF.getText(), emailUpdateTF.getText(), 
                    Integer.valueOf(iDUpdateTF.getText()));

            if(success == 1){
                displayAlert(Alert.AlertType.INFORMATION,"Record Updated" , 
                    ("Student updated successfully "), "4");

                processCardID(iDUpdateTF.getText());
                query.getPreparedStatment().close();
            }
            else{
                displayAlert(Alert.AlertType.WARNING,"Error" , 
                   ("Error adding student"), "2");
            }
        }//end of boorrowersRadioButton.isSelected()
        if(!isbnUpdateTF.getText().isEmpty() && !titleUpdateTF.getText().isEmpty() && 
            !yearUpdateTF.getText().isEmpty() && !copiesUpdateTF.getText().isEmpty() &&
            !authorUpdateTF.getText().isEmpty() && booksRadioButton.isSelected())
        {
            String sql = "UPDATE book SET book_title = ?, date_of_publication = ?, " +
                "number_of_copies = ?, author = ? WHERE isbn = ?";
            MountieQueries query = new MountieQueries(sql);
            int success = query.updateBook(titleUpdateTF.getText(), yearUpdateTF.getText(), 
                    Integer.valueOf(copiesUpdateTF.getText()), authorUpdateTF.getText(),
                    Long.valueOf(isbnUpdateTF.getText()));

            if(success == 1){
                displayAlert(Alert.AlertType.INFORMATION,"Record Updated" , 
                        ("Book updated successfully "), "5");

                processISBN(isbnUpdateTF.getText());
                query.getPreparedStatment().close();
            }
            else{
                displayAlert(Alert.AlertType.WARNING,"Error" , 
                   ("Error adding student"), "3");
            }
        }//end of booksRadioButton.isSelected()
        resultsTableView.getItems().clear(); //clear table data to show new one.
        clearUpdateForm();
        clearCheckOutForm();
    }//end of saveUpdateButtonPressed
    
    /**
     *  Deletes a record form the data base.
     * @param event The even that triggered this function.
     * @throws IOException IOException In case a file cannot be loaded.
     * @throws SQLException The even that triggered this function
     */
    @FXML public void deleteUpdateButtonPressed(ActionEvent event) 
            throws IOException, SQLException
    {
            String student = "Are you sure about deleting"
                    + " student \nid: " + iDUpdateTF.getText() + " ?";
            String book = "Are you sure about deleting"
                    + " book \nisbn: " + isbnUpdateTF.getText() + " ?";
            //Delete Student.
            if(!iDUpdateTF.getText().isEmpty() && !fNameUpdateTF.getText().isEmpty() && 
                !lNameUpdateTF.getText().isEmpty() && !addressUpdateTF.getText().isEmpty() &&
                !phoneUpdateTF.getText().isEmpty() && !emailUpdateTF.getText().isEmpty()
                && borrowersRadioButton.isSelected() && optionsAlert(student) == 1)
            {
                String sql = "DELETE FROM borrower WHERE cardID = ?";
                MountieQueries query = new MountieQueries(sql);
                int success = query.deleteStudent(Integer.valueOf(iDUpdateTF.getText()));

                if(success == 1){
                    displayAlert(Alert.AlertType.INFORMATION,"Record Deleted" , 
                        ("Student deleted successfully "), "2");
                    query.getPreparedStatment().close();
                    resultsTableView.getItems().clear(); //clear table data to show new one.
                    clearUpdateForm();
                }
                else{
                    displayAlert(Alert.AlertType.ERROR,"Error" , 
                       ("Error deleting student"), "1");
                }
            }//end of delete student
            //Delete Book
            if(!isbnUpdateTF.getText().isEmpty() && !titleUpdateTF.getText().isEmpty() && 
                !yearUpdateTF.getText().isEmpty() && !copiesUpdateTF.getText().isEmpty() &&
                !authorUpdateTF.getText().isEmpty() && booksRadioButton.isSelected() && 
                optionsAlert(book) == 1)
            {
                String sql = "DELETE FROM book WHERE isbn = ?";
                MountieQueries query = new MountieQueries(sql);
                int success = query.deleteBook(Long.valueOf(isbnUpdateTF.getText()));

                if(success == 1){
                    displayAlert(Alert.AlertType.INFORMATION,"Record Deleted" , 
                            ("Book deleted successfully "), "3");
                    query.getPreparedStatment().close();
                    resultsTableView.getItems().clear(); //clear table data to show new one.
                    clearUpdateForm();
                }
                else{
                    displayAlert(Alert.AlertType.ERROR,"Error" , 
                       ("Error deleting book"), "1");
                }
            }//end of delete book
            else if(borrowersRadioButton.isSelected() || booksRadioButton.isSelected()){}
            else{
                displayAlert(Alert.AlertType.WARNING,"Empty Fields" , 
                            ("Double click a record to populate fields first! "), "6");
            }
    }//end of deleteUpdateButtonPressed
    
    /**
     * Process the transaction when a student check-out a book.
     * @param event The even that triggered this function.
     * @throws IOException IOException In case a file cannot be loaded.
     * @throws SQLException The even that triggered this function
     */
    @FXML public void processCheckOutButtonPressed(ActionEvent event) 
            throws IOException, SQLException
    {
        BooksIssued transaction = new BooksIssued(Long.valueOf(isbnCheckOutTF.getText()),
                Integer.valueOf(iDCheckOutTF.getText()));
        int success = transaction.processCheckOutTransaction();
        
        if(success == 1){
            displayAlert(Alert.AlertType.INFORMATION,"Receipt" , 
                ("Transaction ID: " + transaction.getTransID() + "\n\nStudent ID: " + 
                transaction.getCardID() + "\nIssue Date: " + transaction.getIssueDate() +
                "\nDue Date: " + transaction.getDueDate()), "1");
            
            Books book = new Books(Long.valueOf(isbnCheckOutTF.getText()),
                Integer.valueOf(copiesCheckOutTF.getText()));
            book.decreasedCopies();
            clearCheckOutForm();
            resultsTableView.getItems().clear(); //clear table data to show new one.
        }
        else{
            displayAlert(Alert.AlertType.WARNING,"Error" , 
                ("Error adding student"), "6");
        }
    }//end of processCheckOutButtonPressed
    
    /**
     * Process the transaction when a book is returned.
     * @param event The even that triggered this function.
     * @throws IOException IOException In case a file cannot be loaded.
     * @throws SQLException The even that triggered this function
     */
    @FXML public void processCheckInButtonPressed(ActionEvent event) 
            throws IOException, SQLException
    {
        BooksIssued transaction = new BooksIssued();
        transaction.setTransID(Integer.valueOf(transCheckOutTF.getText()));
        int success = transaction.processCheckInTransaction();
        int succes2 = transaction.searchTransactions(Long.valueOf(isbnCheckOutTF.getText()),
                Integer.valueOf(iDCheckOutTF.getText()));
        
        if(success == 1 && succes2 ==1){
            displayAlert(Alert.AlertType.INFORMATION,"Receipt" , 
                ("Transaction ID: " + transaction.getTransID() + "\n\nStudent ID: " + 
                iDCheckOutTF.getText() + "\nIssue Date: " + transaction.getIssueDate() +
                "\nDue Date: " + transaction.getDueDate() + "\nReturn date: " +
                transaction.getReturnDate()), "1");
            
            Books book = new Books(Long.valueOf(isbnCheckOutTF.getText()),
                Integer.valueOf(copiesCheckOutTF.getText()));
            book.increasedCopies();
            clearCheckOutForm();
        }
        else{
            displayAlert(Alert.AlertType.ERROR,"Error" , 
                ("Transaction Error. Try again!"), "6");
        }
    }//end of processCheckInButtonPressed
    
    /**
     * Clears the check-out form when button is pressed
     * @param event The even that triggered this function.
     * @throws IOException IOException In case a file cannot be loaded.
     * @throws SQLException The even that triggered this function
     */
    @FXML public void clearCheckoutFormButtonPressed(ActionEvent event) 
             throws IOException, SQLException{
         clearCheckOutForm();
     }//end of clearCheckoutFormButtonPressed
     
    /**
     * Clears the check-out form
     */
     public void clearCheckOutForm(){
        fNameCheckOutTF.clear();
        lNameCheckOutTF.clear();
        iDCheckOutTF.clear();
        addressCheckOutTF.clear();
        phoneCheckOutTF.clear();
        emailCheckOutTF.clear();
        checkOutCBox.getItems().clear();

        transCheckOutTF.clear();
        isbnCheckOutTF.clear();
        titleCheckOutTF.clear();
        yearCheckOutTF.clear();
        copiesCheckOutTF.clear();
        authorCheckOutTF.clear();
     }

    /**
     * Displays and alert window if the connection to the data base failed.
     * @param type Type of alert to be displayed.
     * @param title title of the alerts window.
     * @param messageHeader Message to be displayed on the alert window.
     * @param errorCode to display the correct error message.
     */
    private void displayAlert(Alert.AlertType type, String title, 
            String messageHeader, String errorCode) 
    {
        Alert alert = new Alert(type);

        switch(errorCode){
            case "1":       //general error.
                alert.setTitle(title);
                alert.setContentText(messageHeader);
                alert.setHeaderText(null);
                alert.showAndWait();
                break;
            case "2":       //error login, or student not found or deleted.
                alert.setTitle(title);
                alert.setContentText(messageHeader);
                alert.setHeaderText(null);
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image("images/errorLogin.png"));
                alert.setGraphic(new ImageView(("images/errorLogin.png")));
                alert.showAndWait();
                break;
            case "3":       //error book not found or removed.
                alert.setTitle(title);
                alert.setContentText(messageHeader);
                alert.setHeaderText(null);
                Stage stage2 = (Stage) alert.getDialogPane().getScene().getWindow();
                stage2.getIcons().add(new Image("images/bookRemoved.png"));
                alert.setGraphic(new ImageView(("images/bookRemoved.png")));
                alert.showAndWait();
                break;
            case "4":       //Student succesfully added.
                alert.setTitle(title);
                alert.setContentText(messageHeader);
                alert.setHeaderText(null);
                Stage stage3 = (Stage) alert.getDialogPane().getScene().getWindow();
                stage3.getIcons().add(new Image("images/studentAdded.png"));
                alert.setGraphic(new ImageView(("images/studentAdded.png")));
                alert.showAndWait();
                break;
            case "5":       //Book succesfully added.
                alert.setTitle(title);
                alert.setContentText(messageHeader);
                alert.setHeaderText(null);
                Stage stage4 = (Stage) alert.getDialogPane().getScene().getWindow();
                stage4.getIcons().add(new Image("images/bookAdded.png"));
                alert.setGraphic(new ImageView(("images/bookAdded.png")));
                alert.showAndWait();
                break;
            case "6":       //Emtpy fields or transaction error.
                alert.setTitle(title);
                alert.setContentText(messageHeader);
                alert.setHeaderText(null);
                Stage stage5 = (Stage) alert.getDialogPane().getScene().getWindow();
                stage5.getIcons().add(new Image("images/emptySpace.png"));
                alert.setGraphic(new ImageView(("images/emptySpace.png")));
                alert.showAndWait();
                break;
            default:
        }//end switch
   }//end of displayAlert  
    
    /**
     * Display an option Alert to verify user selection.
     * @param message The message to be displayed at the window.
     * @return an <code>integer</code> specifying if user wants to proceed or not.
     */
    public int optionsAlert(String message){
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Delete permanetly");
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        ButtonType button1 = new ButtonType("Yes");
        ButtonType button2 = new ButtonType("No");

        alert.getButtonTypes().setAll(button2, button1);
        Optional<ButtonType> result = alert.showAndWait();
       
        if (result.get() == button1)
            return 1; //1 == delete
        else
            return 0; //0 == don't delete
    }//end of optionsAlert
}//end of MainWindowController
