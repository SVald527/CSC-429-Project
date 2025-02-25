package model;
import java.util.Properties;
import java.util.Vector;
import javafx.scene.Scene;
import exception.InvalidPrimaryKeyException;
import event.Event;
import database.*;
import impresario.IView;
import userinterface.View;
import userinterface.ViewFactory;

public class PatronCollection  extends EntityBase implements IView {
    private static final String myTableName = "Patron";
    private Vector<Patron> patronList;

    public PatronCollection() {
        super(myTableName);
        patronList = new Vector<>(); // Initialize the collection
    }

    public void findPatronsOlderThan(String date) throws Exception {
        String query = "SELECT * FROM " + myTableName + " WHERE dateOfBirth < '" + date + "'";
        processQuery(query);
    }

    public void findPatronsYoungerThan(String date) throws Exception {
        String query = "SELECT * FROM " + myTableName + " WHERE dateOfBirth > '" + date + "'";
        processQuery(query);
    }

    public void findPatronsAtZipCode(String zip) throws Exception {
        String query = "SELECT * FROM " + myTableName + " WHERE zip = '" + zip + "'";
        processQuery(query);
    }

    public void findPatronsWithNameLike(String name) throws Exception {
        String query = "SELECT * FROM " + myTableName + " WHERE name LIKE '%" + name + "%'";
        processQuery(query);
    }

    private void processQuery(String query) throws Exception {
        Vector allDataRetrieved = getSelectQueryResult(query);

        if (allDataRetrieved != null) {
            patronList = new Vector<>();

            for (int cnt = 0; cnt < allDataRetrieved.size(); cnt++) {
                Properties nextPatronData = (Properties) allDataRetrieved.elementAt(cnt);
                Patron patron = new Patron(nextPatronData);
                if (patron != null) {
                    addPatron(patron);
                }
            }
        } else {
            throw new InvalidPrimaryKeyException("No books found for query: " + query);
        }
    }

    private void addPatron(Patron p) {
        int index = findIndexToAdd(p);
        patronList.insertElementAt(p,index);
    }

    private int findIndexToAdd(Patron p) {
        int low=0;
        int high = patronList.size()-1;
        int middle;

        while (low <=high) {
            middle = (low+high)/2;
            Patron midPatron = patronList.elementAt(middle);

            int result = Patron.compare(p,midPatron);

            if (result ==0) {
                return middle;
            } else if (result<0) {
                high=middle-1;
            } else {
                low=middle+1;
            }
        }
        return low;
    }

    public Object getState(String key) {
        if (key.equals("Patrons"))
            return patronList;

        else if (key.equals("PatronList"))
            return this;
        return null;
    }

    public void stateChangeRequest(String key, Object value) {
        myRegistry.updateSubscribers(key, this);
    }

    public Patron retrieve(String patronId) {
        Patron retValue = null;

        for (int cnt = 0; cnt < patronList.size(); cnt++) {
            Patron nextPatron = patronList.elementAt(cnt);
            String nextPatronId = (String)nextPatron.getState("patronId");

            if (nextPatronId.equals(patronId) == true) {
                retValue = nextPatron;
                return retValue;
            }
        }
        return retValue;
    }

    public void updateState(String key, Object value) {
        stateChangeRequest(key, value);
    }

    protected void createAndShowView() {
        Scene localScene = myViews.get("PatronCollectionView");

        if (localScene == null) {
            View newView = ViewFactory.createView("BookCollectionView", this);
            localScene = new Scene(newView);
            myViews.put("BookCollectionView", localScene);
        }
        swapToView(localScene);

    }

    public void displayCollection() {
        for (int i = 0; i < patronList.size(); i++) {
            System.out.println(patronList.elementAt(i).toString());
        }
    }

    protected void initializeSchema(String tableName) {
        if (mySchema == null) {
            mySchema = getSchemaInfo(tableName);
        }
    }
}