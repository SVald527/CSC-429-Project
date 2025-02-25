package model;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import javax.swing.JFrame;
import exception.InvalidPrimaryKeyException;
import database.*;
import impresario.IView;
import userinterface.View;
import userinterface.ViewFactory;

public class Book extends EntityBase implements IView {
    private static final String myTableName = "Book";

    protected Properties dependencies;

    private String updateStatusMessage = "";

    public Book(String bookId)
            throws InvalidPrimaryKeyException
    {
        super(myTableName);

        setDependencies();
        String query = "SELECT * FROM " + myTableName + " WHERE (bookId = " + bookId + ")";

        Vector<Properties> allDataRetrieved = getSelectQueryResult(query);

        if (allDataRetrieved != null) {
            int size = allDataRetrieved.size();

            if (size != 1) {
                throw new InvalidPrimaryKeyException("Multiple books matching id : " + bookId + " found.");
            } else {
                Properties retrievedAccountData = allDataRetrieved.elementAt(0);
                persistentState = new Properties();

                Enumeration allKeys = retrievedAccountData.propertyNames();
                while (allKeys.hasMoreElements() == true) {
                    String nextKey = (String)allKeys.nextElement();
                    String nextValue = retrievedAccountData.getProperty(nextKey);

                    if (nextValue != null) {
                        persistentState.setProperty(nextKey, nextValue);
                    }
                }
            }
        } else {
            throw new InvalidPrimaryKeyException("No book matching id : " + bookId + " found.");
        }
    }

    public Book(Properties props) {
        super(myTableName);

        setDependencies();

        persistentState = new Properties();

        Enumeration allKeys = props.propertyNames();

        while (allKeys.hasMoreElements() == true) {
            String nextKey = (String)allKeys.nextElement();
            String nextValue = props.getProperty(nextKey);

            if (nextValue != null) {
                persistentState.setProperty(nextKey, nextValue);
            }
        }
    }

    private void setDependencies() {
        dependencies = new Properties();
        myRegistry.setDependencies(dependencies);
    }

    public Object getState(String key) {
        if (key.equals("UpdateStatusMessage") == true)
            return updateStatusMessage;
        return persistentState.getProperty(key);
    }

    public void stateChangeRequest(String key, Object value) {
        myRegistry.updateSubscribers(key, this);
    }

    public void updateState(String key, Object value) {
        stateChangeRequest(key, value);
    }

    public static int compare(Book a, Book b) {
        String aNum = (String)a.getState("bookId");
        String bNum = (String)b.getState("bookId");
        return aNum.compareTo(bNum);
    }

    public void save() // save()
    {
        updateStateInDatabase();
    }

    private void updateStateInDatabase() {
        try {
            if (persistentState.getProperty("bookId") != null) {
                Properties whereClause = new Properties();
                whereClause.setProperty("bookId",

                        persistentState.getProperty("bookId"));
                updatePersistentState(mySchema, persistentState, whereClause);
                updateStatusMessage = "Book data for account number : " + persistentState.getProperty("bookId") + " updated successfully in database!";
            } else {
                Integer bookId = insertAutoIncrementalPersistentState(mySchema, persistentState);
                persistentState.setProperty("bookId", "" + bookId.intValue());
                updateStatusMessage = "Book data for new book: " +  persistentState.getProperty("bookId") + "installed successfully in database!";
            }
        }

        catch (SQLException ex) {
            updateStatusMessage = "Error in installing account data in database!";
        }
    }

    public Vector<String> getEntryListView() {
        Vector<String> v = new Vector<String>();

        v.addElement(persistentState.getProperty("bookId"));
        v.addElement(persistentState.getProperty("bookTitle"));
        v.addElement(persistentState.getProperty("author"));
        v.addElement(persistentState.getProperty("pubYear"));
        v.addElement(persistentState.getProperty("status"));

        return v;
    }

    public String toString() {
        return "Title: " + persistentState.getProperty("bookTitle") + "   Author: " +
                persistentState.getProperty("author") + "   Year: " + persistentState.getProperty("pubYear") +
                "   State: " + persistentState.getProperty("status");
    }

    protected void initializeSchema(String tableName) {
        if (mySchema == null) {
            mySchema = getSchemaInfo(tableName);
        }
    }
}