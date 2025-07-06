import javax.swing.SwingUtilities;

public class main {
    public static void main(String[] args) {
      
        JDBCExample dbConnection = new JDBCExample();

       
        dbConnection.connect();

      
        dbConnection.addItemToDatabase("Apple", 5, 20.0, 10.0, 90.0); 

      
        dbConnection.close();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
               
                GroceryBillingWithDelete g = new GroceryBillingWithDelete();
                g.getFrame().setVisible(true);
            }
        });

    }
}
//javac -cp .;"C:\Ruzki\New folder\mysql\mysql.jar" main.java
//java -cp .;"C:\Ruzki\New folder\mysql\mysql.jar" main
