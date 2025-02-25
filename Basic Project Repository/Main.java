import java.util.Scanner;
import model.Book;
import model.BookCollection;
import model.Patron;
import model.PatronCollection;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws Exception {
        forBook();
        //forPatron();
        //getBooksMatchingTitle();
        //getBooksPublishedBeforeYear();
        //getPatronsYoungerThanDate();
        //getPatronsLivingAtZip();

    }

    public static void forBook() {
        Properties insertProp = new Properties();
        Scanner bookScanner = new Scanner(System.in);
        System.out.println("\nMethod to insert a new book called.\nEnter Book Title: ");
        String bookTitle = bookScanner.nextLine();
        System.out.println("Enter Book Author: ");
        String bookAuthor = bookScanner.nextLine();
        System.out.println("Enter the book's publish year: ");
        String bookPubYear = bookScanner.nextLine();
        insertProp.setProperty("bookTitle", bookTitle);
        insertProp.setProperty("author", bookAuthor);
        insertProp.setProperty("pubYear", bookPubYear);
        insertProp.setProperty("status", "Active");
        Book insertBook = new Book(insertProp);
        insertBook.save();
        System.out.println("Book successfully added to the database.");
    }

    public static void forPatron() {
        Properties insertProp = new Properties();
        Scanner patronScanner = new Scanner(System.in);
        System.out.println("\nMethod to enter a new patron called.\nEnter Patron Full Name: ");
        String patronName = patronScanner.nextLine();
        System.out.println("Enter Patron Address: ");
        String patronAddress = patronScanner.nextLine();
        System.out.println("Enter the Patron City: ");
        String patronCity = patronScanner.nextLine();
        System.out.println("Enter the Patron State Code: ");
        String patronStateCode = patronScanner.nextLine();
        System.out.println("Enter the Patron Zip Code: ");
        String patronZip = patronScanner.nextLine();
        System.out.println("Enter the Patron Email: ");
        String patronEmail = patronScanner.nextLine();
        System.out.println("Enter the Patron DOB in YYYY-MM-DD format: ");
        String patronDOB = patronScanner.nextLine();
        insertProp.setProperty("name", patronName);
        insertProp.setProperty("address", patronAddress);
        insertProp.setProperty("city", patronCity);
        insertProp.setProperty("stateCode", patronStateCode);
        insertProp.setProperty("zip", patronZip);
        insertProp.setProperty("email", patronEmail);
        insertProp.setProperty("dateOfBirth", patronDOB);
        insertProp.setProperty("status", "Active");
        Patron insertPatron = new Patron(insertProp);
        insertPatron.save();
        System.out.println("Patron successfully added to the database.");
    }

    public static void getBooksMatchingTitle() throws Exception {
        Scanner bookTScanner = new Scanner(System.in);
        System.out.println("\nMethod called to find all book data matching a given title.\nEnter title of book to search: ");
        String bookTitle = bookTScanner.nextLine();
        BookCollection findBook = new BookCollection();
        findBook.findBooksWithTitleLike(bookTitle);
        System.out.println("Books matching title '" + bookTitle + "' found: ");
        findBook.displayCollection();
    }

    public static void getBooksPublishedBeforeYear() throws Exception {
        Scanner bookDScanner = new Scanner(System.in);
        System.out.println("\nMethod called to print all books published before a given year.\nEnter the publishing year: ");
        String bookDate = bookDScanner.nextLine();
        BookCollection findBook = new BookCollection();
        findBook.findBooksOlderThanDate(bookDate);
        System.out.println("Books published before " + bookDate + " found: ");
        findBook.displayCollection();
    }

    public static void getPatronsYoungerThanDate() throws Exception {
        Scanner patronDScanner = new Scanner(System.in);
        System.out.println("\nMethod called to print all patrons younger than a given date.\nEnter date in YYYY-MM-DD format: ");
        String patronDate = patronDScanner.nextLine();
        PatronCollection findPatron = new PatronCollection();
        findPatron.findPatronsYoungerThan(patronDate);
        System.out.println("Patrons younger than " + patronDate + " found: ");
        findPatron.displayCollection();
    }

    public static void getPatronsLivingAtZip() throws Exception {
        Scanner patronZScanner = new Scanner(System.in);
        System.out.println("\nMethod called to print all data of patrons living in a given zip code.\nEnter zip code: ");
        String patronZip = patronZScanner.nextLine();
        PatronCollection findPatron = new PatronCollection();
        findPatron.findPatronsAtZipCode(patronZip);
        System.out.println("Patrons that live in " + patronZip + " found: ");
        findPatron.displayCollection();
    }
}