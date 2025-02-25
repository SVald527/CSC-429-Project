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

public class BookCollection  extends EntityBase implements IView {
    private static final String myTableName = "Book";
    private Vector<Book> bookList;

    public BookCollection() {
        super(myTableName);
        bookList = new Vector<>();
    }

    public void findBooksOlderThanDate(String year) throws Exception {
        String query = "SELECT * FROM " + myTableName + " WHERE (pubYear < '" + year + "') ORDER BY bookTitle" ;
        processQuery(query);
    }

    public void findBooksNewerThanDate(String year) throws Exception {
        String query = "SELECT * FROM " + myTableName + " WHERE (pubYear >= '" + year + "') ORDER BY bookTitle";
        processQuery(query);
    }

    public void findBooksWithTitleLike(String title) throws Exception {
        String query = "SELECT * FROM " + myTableName + " WHERE bookTitle LIKE '%" + title + "%'";
        processQuery(query);
    }

    public void findBooksWithAuthorLike(String author) throws Exception {
        String query = "SELECT * FROM " + myTableName + " WHERE author LIKE '%" + author + "%'";
        processQuery(query);
    }

    private void processQuery(String query) throws Exception {
        Vector allDataRetrieved = getSelectQueryResult(query);

        if (allDataRetrieved != null) {
            bookList = new Vector<>();

            for (int cnt = 0; cnt < allDataRetrieved.size(); cnt++) {
                Properties nextBookData = (Properties) allDataRetrieved.elementAt(cnt);
                Book book = new Book(nextBookData);
                if (book != null) {
                    addBook(book);
                }
            }
        } else {
            throw new InvalidPrimaryKeyException("No books found for query: " + query);
        }
    }

    private void addBook(Book b)
    {
        int index = findIndexToAdd(b);
        bookList.insertElementAt(b,index);
    }

    private int findIndexToAdd(Book b) {
        int low=0;
        int high = bookList.size()-1;
        int middle;

        while (low <=high) {
            middle = (low+high)/2;
            Book midBook = bookList.elementAt(middle);

            int result = Book.compare(b,midBook);

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
        if (key.equals("Books"))
            return bookList;
        else if (key.equals("BookList"))
            return this;
        return null;
    }

    public void stateChangeRequest(String key, Object value) {
        myRegistry.updateSubscribers(key, this);
    }

    public Book retrieve(String bookId) {
        Book retValue = null;

        for (int cnt = 0; cnt < bookList.size(); cnt++) {
            Book nextBook = bookList.elementAt(cnt);
            String nextBookId = (String)nextBook.getState("BookId");

            if (nextBookId.equals(bookId) == true) {
                retValue = nextBook;
                return retValue; // we should say 'break;' here
            }
        }
        return retValue;
    }

    public void updateState(String key, Object value) {
        stateChangeRequest(key, value);
    }

    protected void createAndShowView() {
        Scene localScene = myViews.get("BookCollectionView");

        if (localScene == null) {
            View newView = ViewFactory.createView("BookCollectionView", this);
            localScene = new Scene(newView);
            myViews.put("BookCollectionView", localScene);
        }
        swapToView(localScene);
    }

    public void displayCollection() {
        for (int i = 0; i < bookList.size(); i++) {
            System.out.println(bookList.elementAt(i).toString());
        }
    }

    protected void initializeSchema(String tableName) {
        if (mySchema == null) {
            mySchema = getSchemaInfo(tableName);
        }
    }
}