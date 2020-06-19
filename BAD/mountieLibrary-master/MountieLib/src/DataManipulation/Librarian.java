package DataManipulation;

/**
 * This class handles the librarians that login into the application.
 * @author Adrian Mora
 */
public class Librarian {

    private String adminName;
    private String adminPassword;

    /**
     * Default constructor with no-arguments.
     */
    public Librarian(){}
    
    /**
     * Constructor with arguments. 
     * @param adminName The librarian user name.
     * @param adminPassword The librarian password.
     */
    public Librarian(String adminName, String adminPassword) {
        this.adminName = adminName;
        this.adminPassword = adminPassword;
    }

    /**
     * Gets the librarian user name.
     * @return a <code>String</code> specifying the librarian user name.
     */
    public String getAdminName() {
        return adminName;
    }

    /**
     * Gets the librarian password.
     * @return a <code>String</code> specifying the librarian password.
     */
    public String getAdminPassword() {
        return adminPassword;
    }

    /**
     * Sets the librarian user name.
     * @param adminName the librarian user name. 
     */
    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    /**
     * Gets the librarian password.
     * @param adminPassword the librarian password.
     */
    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }
}
