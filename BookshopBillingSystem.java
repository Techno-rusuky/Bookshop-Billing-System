import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class BookshopBillingSystem {
    public static void main(String[] args) {
       
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("Bookshop Billing System");
        frame.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

       
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(240, 248, 255)); 

        
        JLabel headingLabel = new JLabel("BOOKSHOP BILLING SYSTEM", SwingConstants.CENTER);
        headingLabel.setBounds(20, 10, 640, 40);
        headingLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headingLabel.setForeground(new Color(0, 102, 204)); 
        headingLabel.setBorder(new LineBorder(Color.DARK_GRAY, 2));
        panel.add(headingLabel);

        
        JLabel itemNameLabel = new JLabel("Item Name:");
        itemNameLabel.setBounds(20, 80, 100, 25);
        itemNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(itemNameLabel);

        JComboBox<String> itemNameCombo = new JComboBox<>(new String[]{"Book", "Pen", "Notebook", "Eraser","pencil"});
        itemNameCombo.setBounds(130, 80, 200, 30);
        itemNameCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(itemNameCombo);

        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setBounds(20, 130, 100, 25);
        quantityLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(quantityLabel);

        JTextField quantityField = new JTextField();
        quantityField.setBounds(130, 130, 200, 30);
        quantityField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(quantityField);

        JLabel priceLabel = new JLabel("Price Per Item:");
        priceLabel.setBounds(20, 180, 120, 25);
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(priceLabel);

        JTextField priceField = new JTextField();
        priceField.setBounds(130, 180, 200, 30);
        priceField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(priceField);

        // Buttons with Colors
        JButton addItemButton = new JButton("Add Item");
        addItemButton.setBounds(20, 240, 100, 35);
        addItemButton.setFont(new Font("Arial", Font.BOLD, 12));
        addItemButton.setBackground(new Color(102, 255, 102)); // Green
        addItemButton.setBorder(new LineBorder(Color.BLACK, 1));
        panel.add(addItemButton);

        JButton deleteItemButton = new JButton("Delete Item");
        deleteItemButton.setBounds(140, 240, 100, 35);
        deleteItemButton.setFont(new Font("Arial", Font.BOLD, 12));
        deleteItemButton.setBackground(new Color(255, 102, 102)); // Red
        deleteItemButton.setBorder(new LineBorder(Color.BLACK, 1));
        panel.add(deleteItemButton);

        JButton resetButton = new JButton("Reset");
        resetButton.setBounds(260, 240, 100, 35);
        resetButton.setFont(new Font("Arial", Font.BOLD, 12));
        resetButton.setBackground(new Color(255, 255, 153)); // Yellow
        resetButton.setBorder(new LineBorder(Color.BLACK, 1));
        panel.add(resetButton);

       
        String[] columnNames = {"Item Name", "Quantity", "Price Per Item", "Total"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.setBorder(new LineBorder(Color.GRAY, 1));

        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setBounds(20, 290, 640, 120);
        tableScrollPane.setBorder(new LineBorder(Color.DARK_GRAY, 2));
        panel.add(tableScrollPane);

     
        JLabel totalLabel = new JLabel("Total: Rs. 0.00", SwingConstants.RIGHT);
        totalLabel.setBounds(400, 420, 250, 25);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setForeground(Color.RED);
        totalLabel.setBorder(new LineBorder(Color.BLACK, 1));
        panel.add(totalLabel);

        addItemButton.addActionListener(e -> {
            String itemName = (String) itemNameCombo.getSelectedItem();
            String quantityText = quantityField.getText();
            String priceText = priceField.getText();

            if (!quantityText.isEmpty() && !priceText.isEmpty()) {
                try {
                    int quantity = Integer.parseInt(quantityText);
                    double price = Double.parseDouble(priceText);
                    double total = quantity * price;


                    tableModel.addRow(new Object[]{itemName, quantity, price, total});
                    updateTotal(tableModel, totalLabel);

                    
                    saveToDatabase(itemName, quantity, price, total);

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid quantity or price format", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please enter valid data", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteItemButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.removeRow(selectedRow);
                updateTotal(tableModel, totalLabel);
            } else {
                JOptionPane.showMessageDialog(frame, "Please select an item to delete", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        resetButton.addActionListener(e -> {
            tableModel.setRowCount(0);
            totalLabel.setText("Total: Rs. 0.00");
        });

        
        frame.add(panel);
        frame.setVisible(true);
    }

    private static void updateTotal(DefaultTableModel tableModel, JLabel totalLabel) {
        double total = 0.0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            total += (double) tableModel.getValueAt(i, 3);
        }
        totalLabel.setText("Total: Rs. " + String.format("%.2f", total));
    }

    
    private static void saveToDatabase(String itemName, int quantity, double price, double total) {
        String url = "jdbc:mysql://localhost:3306/bookshop";  
        String username = "root";  
        String password = "Ruk@0003";  

        String query = "INSERT INTO sales1 (item_name, quantity, price, total) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, itemName);
            stmt.setInt(2, quantity);
            stmt.setDouble(3, price);
            stmt.setDouble(4, total);

            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error saving to database", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}