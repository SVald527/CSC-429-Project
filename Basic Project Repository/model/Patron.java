package model;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import javax.swing.JFrame;
import exception.InvalidPrimaryKeyException;
import database.*;
import impresario.IView;

public class Patron extends EntityBase implements IView {
    private static final String myTableName = "Patron";
    protected Properties dependencies;

    private String updateStatusMessage = "";

    public Patron(String patronId)
            throws InvalidPrimaryKeyException {
        super(myTableName);

        setDependencies();
        String query = "SELECT * FROM " + myTableName + " WHERE (patronId = " + patronId + ")";
        Vector<Properties> allDataRetrieved = getSelectQueryResult(query);

        if (allDataRetrieved != null) {
            int size = allDataRetrieved.size();

            if (size != 1) {
                throw new InvalidPrimaryKeyException("Multiple patrons matching id : " + patronId + " found.");
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
            throw new InvalidPrimaryKeyException("No patron matching id : " + patronId + " found.");
        }
    }

    public Patron(Properties props) {
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

    public static int compare(Patron a, Patron b) {
        String aNum = (String)a.getState("patronID");
        String bNum = (String)b.getState("patronID");
        return aNum.compareTo(bNum);
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

    public void save() // save()
    {
        updateStateInDatabase();
    }

    private void updateStateInDatabase() {
        try {
            if (persistentState.getProperty("patronId") != null) {
                Properties whereClause = new Properties();
                whereClause.setProperty("patronId",
                        persistentState.getProperty("patronId"));

                updatePersistentState(mySchema, persistentState, whereClause);
                updateStatusMessage = "Patron data for account : " + persistentState.getProperty("patronId") + " updated successfully in database!";
            } else {
                Integer patronId =
                        insertAutoIncrementalPersistentState(mySchema, persistentState);

                persistentState.setProperty("patronId", "" + patronId.intValue());
                updateStatusMessage = "Patron data for new patron: " +  persistentState.getProperty("patronId") + "installed successfully in database!";
            }
        }

        catch (SQLException ex) {
            updateStatusMessage = "Error in installing account data in database!";
        }
    }

    public Vector<String> getEntryListView() {
        Vector<String> v = new Vector<String>();
        v.addElement(persistentState.getProperty("patronId"));
        v.addElement(persistentState.getProperty("name"));
        v.addElement(persistentState.getProperty("address"));
        v.addElement(persistentState.getProperty("city"));
        v.addElement(persistentState.getProperty("stateCode"));
        v.addElement(persistentState.getProperty("zip"));
        v.addElement(persistentState.getProperty("email"));
        v.addElement(persistentState.getProperty("dateOfBirth"));
        v.addElement(persistentState.getProperty("status"));

        return v;
    }

    public String toString() {
        return "Name: " + persistentState.getProperty("name") + "   Address: " + persistentState.getProperty("address") +
                "   City: " + persistentState.getProperty("city") + "  State: " + persistentState.getProperty("stateCode") +
                " Zip Code: " + persistentState.getProperty("zip") + " Email: " + persistentState.getProperty("email") +
                " DOB: " + persistentState.getProperty("dateOfBirth") + " Status: " + persistentState.getProperty("status");
    }

    protected void initializeSchema(String tableName) {
        if (mySchema == null) {
            mySchema = getSchemaInfo(tableName);
        }
    }
}