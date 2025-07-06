import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GroceryBillingWithDelete {

    private JFrame frame;  
    private JDBCExample dbConnection; 

    public GroceryBillingWithDelete() {
        dbConnection = new JDBCExample(); 
        dbConnection.connect(); 


        frame = new JFrame("Grocery Billing System with Delete Option");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setLayout(new BorderLayout());

       
        JLabel titleLabel = new JLabel("Grocery Billing System", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(34, 139, 34));
        frame.add(titleLabel, BorderLayout.NORTH);

        
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 15, 15));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel itemLabel = new JLabel("Item Name:");
        itemLabel.setFont(new Font("Arial", Font.PLAIN, 18));

        String[] items = {"Apple", "Banana", "Orange", "Grapes", "Mango"};
        JComboBox<String> itemComboBox = new JComboBox<>(items);

        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        JTextField quantityField = new JTextField();

        JLabel priceLabel = new JLabel("Price per Item:");
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        JTextField priceField = new JTextField();

        JLabel discountLabel = new JLabel("Discount (%) (Optional):");
        discountLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        JTextField discountField = new JTextField();

        JButton addButton = new JButton("Add Item");
        addButton.setFont(new Font("Arial", Font.BOLD, 16));
        addButton.setBackground(new Color(50, 205, 50));
        addButton.setForeground(Color.WHITE);

        inputPanel.add(itemLabel);
        inputPanel.add(itemComboBox); 
        inputPanel.add(quantityLabel);
        inputPanel.add(quantityField);
        inputPanel.add(priceLabel);
        inputPanel.add(priceField);
        inputPanel.add(discountLabel);
        inputPanel.add(discountField);
        inputPanel.add(new JLabel());
        inputPanel.add(addButton);

     
        String[] columns = {"Item Name", "Quantity", "Price per Item", "Discount (%)", "Total"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable billTable = new JTable(tableModel);
        billTable.setFont(new Font("Arial", Font.PLAIN, 16));
        billTable.setRowHeight(25);
        JScrollPane tableScrollPane = new JScrollPane(billTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Bill Details"));

        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JLabel totalLabel = new JLabel("Total: Rs0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton deleteButton = new JButton("Delete Item");
        deleteButton.setFont(new Font("Arial", Font.BOLD, 16));
        deleteButton.setBackground(new Color(255, 140, 0));
        deleteButton.setForeground(Color.WHITE);

        JButton resetButton = new JButton("Reset");
        resetButton.setFont(new Font("Arial", Font.BOLD, 16));
        resetButton.setBackground(new Color(220, 20, 60));
        resetButton.setForeground(Color.WHITE);

        bottomPanel.add(totalLabel);
        bottomPanel.add(deleteButton);
        bottomPanel.add(resetButton);

       
        frame.add(inputPanel, BorderLayout.WEST);
        frame.add(tableScrollPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        double[] totalAmount = {0.0}; 

  
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedItem = (String) itemComboBox.getSelectedItem(); 
                String quantityText = quantityField.getText();
                String priceText = priceField.getText();
                String discountText = discountField.getText();

                if (selectedItem == null || quantityText.isEmpty() || priceText.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please fill all required fields!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    int quantity = Integer.parseInt(quantityText);
                    double price = Double.parseDouble(priceText);
                    double discount = discountText.isEmpty() ? 0.0 : Double.parseDouble(discountText);

                    if (discount < 0 || discount > 100) {
                        JOptionPane.showMessageDialog(frame, "Discount must be between 0% and 100%", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    double itemTotal = quantity * price;
                    double discountAmount = itemTotal * (discount / 100);
                    double finalTotal = itemTotal - discountAmount;
                    totalAmount[0] += finalTotal;

                    
                    tableModel.addRow(new Object[] {
                        selectedItem,
                        quantity,
                        String.format("Rs%.2f", price),
                        String.format("%.1f%%", discount),
                        String.format("Rs%.2f", finalTotal)
                    });

               
                    dbConnection.addItemToDatabase(selectedItem, quantity, price, discount, finalTotal);

                    totalLabel.setText("Total: Rs" + String.format("%.2f", totalAmount[0]));

                   
                    quantityField.setText("");
                    priceField.setText("");
                    discountField.setText("");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid input in quantity, price, or discount fields!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

       
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = billTable.getSelectedRow();
                if (selectedRow >= 0) {
                    double itemTotal = Double.parseDouble(
                            tableModel.getValueAt(selectedRow, 4).toString().replace("Rs", "").trim()
                    );
                    totalAmount[0] -= itemTotal;

                   
                    tableModel.removeRow(selectedRow);

              
                    totalLabel.setText("Total: Rs" + String.format("%.2f", totalAmount[0]));
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select an item to delete!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                tableModel.setRowCount(0);
                
                totalAmount[0] = 0.0;
              
                totalLabel.setText("Total: Rs0.00");
            }
        });
    }

    
    public JFrame getFrame() {
        return frame;
    }
}
