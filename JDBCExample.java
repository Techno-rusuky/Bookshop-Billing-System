import java.sql.*;

public class JDBCExample {
    private static final String URL = "jdbc:mysql://localhost:3306/grocery_billing";  // Database URL
    private static final String USER = "root";  // Update with your database username
    private static final String PASSWORD = "Ruk@0003";  // Update with your database password
    private Connection conn;

    // Establish a connection to the database
    public void connect() {
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connected!");
            createTable();  // Ensure table is created after connecting
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create the billing table if it doesn't already exist
    public void createTable() {
        String createTableQuery = """
            CREATE TABLE IF NOT EXISTS billing (
                id INT AUTO_INCREMENT PRIMARY KEY,
                item_name VARCHAR(100) NOT NULL,
                quantity INT NOT NULL,
                price_per_item DOUBLE NOT NULL,
                discount DOUBLE NOT NULL DEFAULT 0.0,
                total DOUBLE NOT NULL
            );
        """;

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableQuery);
            System.out.println("Billing table is ready (created if not existed).");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Insert a new record into the billing table
    public void addItemToDatabase(String itemName, int quantity, double pricePerItem, double discount, double total) {
        String query = "INSERT INTO billing (item_name, quantity, price_per_item, discount, total) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, itemName);
            ps.setInt(2, quantity);
            ps.setDouble(3, pricePerItem);
            ps.setDouble(4, discount);
            ps.setDouble(5, total);
            ps.executeUpdate();
            System.out.println("Item added to database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Close the connection
    public void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
