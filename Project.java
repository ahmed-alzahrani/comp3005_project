import java.util.*;
import java.sql.*;

public class Project {
  public static void main(String[] args) throws SQLException {

    // initialize connection to DB, these values will change based on your PSQL setup
    String db_url = "jdbc:postgresql://localhost/project";
    Properties db_props = new Properties();
    db_props.setProperty("user", "ahmed");
    db_props.setProperty("password", "newPass19!"); // substitute when testing
    db_props.setProperty("ssl", "false");
    Connection db_connection = DriverManager.getConnection(db_url, db_props);
    Statement statement = db_connection.createStatement();


    // bool for control flow
    Scanner scan = new Scanner(System.in);
    boolean running = true;

    while(running) {
      System.out.println("Log in as a customer, or an owner? Enter `done` to logout");

      String login = scan.nextLine().toLowerCase();


      // switch which UI the user sees (customer vs. owner)
      switch(login) {
        case "customer":
          customerFunctions(statement);
          break;
        case "owner":
          ownerFunctions(statement);
          break;
        case "done":
          running = false;
          break;
        default:
          System.out.println("Invalid input");
      }
    }
  }

  static void customerFunctions(Statement statement) throws SQLException {
    Scanner scan = new Scanner(System.in);
    // set up arrays to track our cart and quantities
    ArrayList<Hashtable> cart = new ArrayList<Hashtable>();
    ArrayList<Integer> cart_quantities = new ArrayList<Integer>();

    // another flag for control flow
    boolean customer_running = true;
    while (customer_running) {
      ArrayList<Hashtable> current_search = new ArrayList<Hashtable>();
      // print user options
      System.out.println("Welcome to the book store!");
      System.out.println("Your cart currently has: " + cart.size() + " items.");
      System.out.println("Pick an attribute to search books by from the following menu by inputting the correct number:");
      System.out.println("1. All Books");
      System.out.println("2. Books by Title");
      System.out.println("3. Books by Author");
      System.out.println("4. Books by ISBN");
      System.out.println("5. Books by Genre");
      System.out.println("6. Checkout cart");
      System.out.println("7. Back to main menu");

      String input = scan.nextLine();

      // switch on their input to populate current search properly
      switch(input) {
        case "1":
          current_search = books_query(statement);
          break;
        case "2":
          System.out.println("Please enter title to search by:");
          String title = scan.nextLine();
          current_search = books_query(statement, "title", title);
          break;
        case "3":
          System.out.println("Please enter author to search by:");
          String author = scan.nextLine();
          current_search = books_query(statement, "author", author);
          break;
        case "4":
          System.out.println("Please enter ISBN to search by:");
          String isbn = scan.nextLine();
          current_search = books_query(statement, "ISBN", isbn);
          break;
        case "5":
          System.out.println("Please enter Genre to search by:");
          String genre = scan.nextLine();
          current_search = books_query(statement, "Genre", genre);
          break;
        case "6":
          checkoutOrder(statement, cart, cart_quantities);
          cart.clear();
          cart_quantities.clear();
          break;
        case "7":
          customer_running = false;
          break;
        default:
          System.out.println("Invalid input");
          break;
      }
      // if there is anything in the current search
      if (!(current_search.isEmpty())) {
        // we kick off the add to cart flow
        ArrayList<Hashtable> add_to_cart = add_to_cart_flow(current_search);
        // if there is something to add to cart
        if (!(add_to_cart.isEmpty())) {
          // see if its a book already in our cart or not
          boolean modified = false;
          for (int i = 0; i < cart.size(); i++) {
            if (cart.get(i).containsValue(add_to_cart.get(0).get("isbn"))) {
              // if its already in our cart, update the quantity
              int value = cart_quantities.get(i);
              cart_quantities.set(i, value + 1);
              modified = true;
            }
          }
          // else add it to cart
          if (!(modified)){
            cart.add(add_to_cart.get(0));
            cart_quantities.add(1);
          }
        }
      }
    }
  }

  // add to cart flow
  static ArrayList<Hashtable> add_to_cart_flow(ArrayList<Hashtable> current_search) {
    ArrayList<Hashtable> add_to_cart = new ArrayList<Hashtable>();
    Scanner scan = new Scanner(System.in);
    System.out.println("Displaying Books:");
    // show the title of the current books
    display_titles(current_search);

    boolean valid_selection = false;
    // find which book the user wants to view more closely
    while (!(valid_selection)) {
      System.out.println("Enter a number to view more details about a book, or add to cart");
      String book_selection = scan.nextLine();

      // check that input is numeric
      if (isNumeric(book_selection)) {
        // convert to integer
        int int_book_selection = Integer.parseInt(book_selection);
        // check its valid, that it matches a book in the current search
        if (int_book_selection > current_search.size()) {
          System.out.println("Invalid input, please enter a number between 1 and " + current_search.size());
        } else {
          // print more details for the user
          print_book(current_search.get(int_book_selection - 1));
          // offer to add to cart
          System.out.println("Would you like to add to cart? y/n");
          String add_to_cart_input = scan.nextLine();
          // if the user wants to add to cart, we add it it to our  temp add to cart array and return
          if (add_to_cart_input.matches("y")) {
            add_to_cart.add(current_search.get(int_book_selection - 1));
            return add_to_cart;
          }
          // else return it empty
          valid_selection = true;
          return add_to_cart;
        }
      } else {
        System.out.println("Invalid input, please enter a number between 1 and " + current_search.size());
      }
    }
    return add_to_cart;
  }


  // a method to perform a query for all books, serialize the contents to Hashtables and return
  static ArrayList<Hashtable> books_query(Statement statement) throws SQLException {
    ArrayList<Hashtable> current_search = new ArrayList<Hashtable>();
    ResultSet results = statement.executeQuery("SELECT * FROM book");
    while (results.next()) {
      Hashtable book = serializeBook(results);
      current_search.add(book);
    }
    return current_search;
  }

  // same as above method, but instead of all books we query based on some attribute
  static ArrayList<Hashtable> books_query(Statement statement, String attribute, String value) throws SQLException {
    ArrayList<Hashtable> current_search = new ArrayList<Hashtable>();
    ResultSet results = statement.executeQuery("SELECT * FROM book WHERE " + attribute + " = '" + value + "'");
    while (results.next()) {
      Hashtable book = serializeBook(results);
      current_search.add(book);
    }
    return current_search;
  }


  // owner flow begins
  static void ownerFunctions(Statement statement) throws SQLException {
    Scanner scan = new Scanner(System.in);
    boolean owner_running = true;
    // offer them owner functions
    System.out.println("Welcome to the Owner view!");
    System.out.println("Select an operation / report to perform that operation or view a report:");
    System.out.println("1. Add new publisher to contacts (create new publisher)");
    System.out.println("2. Add a new address to the database");
    System.out.println("3. Order a new book (create new book)");
    System.out.println("4. Order more of an existing book (adjust stock of existing books)");
    System.out.println("5. View sales by user report");
    System.out.println("6. View sales by book report");

    String input = scan.nextLine();

    // switch on input and call correct method
    switch(input) {
      case "1":
        create_new_publisher(statement);
        break;
      case "2":
        create_new_address(statement);
        break;
      case "3":
        create_new_book(statement);
        break;
      case "4":
        order_existing_book(statement);
        break;
      case "5":
        sales_by_user_report(statement);
        break;
      case "6":
        sales_by_book_report(statement);
        break;
      default:
        System.out.println("Invalid input");
        break;
    }
  }

  // takes the records from SQL and turns it into a Hashtable with relevant K/V pairs
  static Hashtable serializeBook(ResultSet results) throws SQLException {
    Hashtable book = new Hashtable();
    book.put("id", results.getInt("ID"));
    book.put("title", results.getString("TITLE"));
    book.put("author", results.getString("AUTHOR"));
    book.put("genre", results.getString("GENRE"));
    book.put("isbn", results.getString("ISBN"));
    book.put("publisher", results.getInt("PUBLISHER"));
    book.put("stock", results.getInt("STOCK"));
    book.put("price", results.getFloat("PRICE"));
    book.put("margin", results.getInt("MARGIN"));
    book.put("pages", results.getInt("PAGES"));
    return book;
  }

  // just loops through a hashtable and prints everything
  static void display_books(ArrayList<Hashtable> books) {

    for (int i = 0; i < books.size(); i++) {
      Hashtable book = books.get(i);
      Set <String> keys = book.keySet();
      for (String key: keys) {
        System.out.println(key + ": " + book.get(key));
      }
      System.out.println();
      System.out.println();
    }
  }

  static void checkoutOrder(Statement statement, ArrayList<Hashtable> cart, ArrayList<Integer> cart_quantities) throws SQLException {
    // to randomize order numbers
    Random rand = new Random();
    int random = rand.nextInt();
    // total for the order
    float total = 0;
    // loop through our cart
    for (int i = 0; i < cart.size(); i++) {
      // how many quantities of each book is in the cart
      System.out.println("You have " + cart_quantities.get(i) + " copies of " + cart.get(i).get("title") + " in your cart.");
      // execute the query to find the books record and get its price
      ResultSet book = statement.executeQuery("SELECT * FROM book WHERE title = '" + cart.get(i).get("title") + "'");
      while (book.next()) {
        // update the total based on the price and quantity
        total += (book.getFloat("PRICE") * cart_quantities.get(i));
      }
      // update the stock of the books were buying
      statement.executeUpdate("UPDATE book SET stock = stock - " + cart_quantities.get(i) + " WHERE title = '" + cart.get(i).get("title") + "'");
    }

    // create the user order record
    statement.executeUpdate("INSERT INTO user_order(total, billing_address, shipping_address, order_number, shipping_status, book_user) VALUES (" + total + ", 4, 4, " + random + ", 'Not shipped', 2)");

    // get the order we just created
    ResultSet new_order = statement.executeQuery("select * from user_order where id=(SELECT max(id) from user_order)");
    int new_order_id = 0;
    while (new_order.next()) {
      new_order_id = new_order.getInt("ID");
    }

    // use that order record's ID to create the book_order records and track the books in each order in those rows
    for (int i = 0; i < cart.size(); i++) {
      statement.executeUpdate("INSERT INTO book_order(book, user_order) values(" + cart.get(i).get("id") + ", " + new_order_id + ")");
    }

    // show the total to the user
    System.out.println("Congrats! Order # " + random + " for a total of $" + total + "!");
  }


  static void display_titles(ArrayList<Hashtable> books) {

    for (int i = 0; i < books.size(); i++) {
      Hashtable book = books.get(i);
      System.out.println(i + 1 + ". " + book.get("title"));
    }
  }

  // takes in user input and creates a new publisher based on that input
  static void create_new_publisher(Statement statement) {
    Scanner scan = new Scanner(System.in);
    System.out.println("Adding a new publisher to the list.");

    System.out.println("Enter the publisher's name");
    String name = scan.nextLine();

    System.out.println("Enter the publisher's email");
    String email = scan.nextLine();

    System.out.println("Enter the publisher's phone number (10 digits max)");
    String phone = scan.nextLine();

    System.out.println("Enter the ID of the publisher's address");
    String address = scan.nextLine();

    System.out.println("Enter the bank account # of the publisher");
    String bank_account = scan.nextLine();

    try {
      statement.executeUpdate("INSERT INTO publisher (name, email, phone, address, bank_account) values('" + name + "', '" + email + "', '" + phone + "', " + address + ", '" + bank_account + "')");
    }
    catch (Exception err) {
      System.out.println("Error adding new publisher!");
      err.printStackTrace();
    }
  }

  // takes in user input and creates a new address based on that input
  static void create_new_address(Statement statement) {
    Scanner scan = new Scanner(System.in);
    System.out.println("Adding a new address to the list.");

    System.out.println("Enter the street address");
    String street = scan.nextLine();

    System.out.println("Enter the city");
    String city = scan.nextLine();

    System.out.println("Enter the postal code");
    String postal = scan.nextLine();

    try {
      statement.executeUpdate("INSERT INTO address (street, city, postal) values('" + street + "', '" + city + "', '" + postal + "')");
    }
    catch (Exception err) {
      System.out.println("Error adding new publisher!");
      err.printStackTrace();
    }
  }

  // takes in user input and creates a new record based on that input
  static void create_new_book(Statement statement) {
    Scanner scan = new Scanner(System.in);
    System.out.println("Ordering a brand new book");

    System.out.println("Enter the title of the book you would like to order");
    String title = scan.nextLine();

    System.out.println("Enter the author of the new book");
    String author = scan.nextLine();

    System.out.println("Enter the genre of the new book");
    String genre = scan.nextLine();

    System.out.println("Enter the ISBN of this new book");
    String isbn = scan.nextLine();

    System.out.println("Enter the ID of the publisher who publishes this book");
    String publisher = scan.nextLine();

    System.out.println("Enter the number of this book you would like to order");
    String stock = scan.nextLine();

    System.out.println("Enter the price you are charging for the new book");
    String price = scan.nextLine();

    System.out.println("What are the margins the publisher charges?");
    String margin = scan.nextLine();

    System.out.println("How many pages in the new book?");
    String pages = scan.nextLine();

    try {
      statement.executeUpdate("INSERT INTO book (title, author, genre, isbn, publisher, stock, price, margin, pages) values('" + title + "', '" + author + "', '" + genre + "', '" + isbn + "', " + publisher + ", " + stock + ", " + price + ", " + margin + ", " + pages + ")");;
    }
    catch (Exception err) {
      System.out.println("Error adding new book!");
      err.printStackTrace();
    }
  }

  static void order_existing_book(Statement statement) throws SQLException {
    Scanner scan = new Scanner(System.in);
    ArrayList<Hashtable> existing_books = books_query(statement);

    display_books(existing_books);

    System.out.println("What is the title of the book you want to order more of?");
    String title = scan.nextLine();

    System.out.println("What is the number of copies you would like to order?");
    String amount = scan.nextLine();

    try {
      statement.executeUpdate("UPDATE book SET stock = stock + " + amount + " WHERE title = '" + title + "'");
    }
    catch (Exception err) {
      System.out.println("Error adding new book!");
      err.printStackTrace();
    }
  }

  static void sales_by_user_report(Statement statement) throws SQLException {
    Scanner scan = new Scanner(System.in);

    System.out.println("Enter the ID of the user you would like to see an order list for.");
    String user = scan.nextLine();

    ResultSet results = statement.executeQuery("SELECT * FROM user_order where book_user = " + user );
    float user_total = 0;
    while (results.next()) {
      user_total += results.getFloat("total");
      System.out.println("This user has placed order # " + results.getString("order_number") + " which was for a total of " + results.getFloat("total"));
    }
    System.out.println("This users order history totals " + user_total);
  }

  static void sales_by_book_report(Statement statement) throws SQLException {
    Scanner scan = new Scanner(System.in);

    System.out.println("Enter the ID of hte book you would like to see an order list for.");
    String book = scan.nextLine();

    ResultSet results = statement.executeQuery("SELECT * from user_order WHERE ID in (select user_order from book_order where book = " + book + ")");
    float book_total = 0;

    while (results.next()) {
      book_total += results.getFloat("total");
      System.out.println("This book was a part of order # " + results.getString("order_number") + " which was for a total of " + results.getFloat("total"));
    }
    System.out.println("This books order history totals " + book_total);
  }

  // little util func to check if a string is numeric
  public static boolean isNumeric(String str) {
  try {
    Double.parseDouble(str);
    return true;
  } catch(NumberFormatException e){
    return false;
  }
  }

  // loops through a single book and prints out the K/V pairs
  public static void print_book(Hashtable book) {
    Set <String> keys = book.keySet();
    for (String key: keys) {
      System.out.println(key + ": " + book.get(key));
    }
  }
}
