import java.sql.*;
import java.util.Scanner;
public class myjdbcproject {
    static Connection conn = null;
    static Scanner scanner = new Scanner(System.in);
    static PreparedStatement pmt = null;
    private static void showCustomerRecords() throws Exception {
        Statement stmt = conn.createStatement();
        ResultSet rst = stmt.executeQuery("SELECT * FROM CUSTOMER");
        System.out.println("\nCUSTOMER RECORDS\n");
        System.out.printf("| %-10s | %-25s | %-15s | %-10s |\n", "Cust No", "Name", "Phone No", "City");
        while (rst.next()) {
            String custNo = String.format("%-10s", rst.getString(1));
            String name = String.format("%-25s", rst.getString(2));
            String phoneNo = String.format("%10d", rst.getLong(3));
            String city = String.format("%-10s", rst.getString(4));
            System.out.println("| " + custNo + " | " + name + " | " + phoneNo + " | " + city + " |");
        }
        rst.close();
    }
    private static void addCustomerRecord(String custNo, String name, long phoneNo, String city) throws Exception {
        pmt = conn.prepareStatement("INSERT INTO CUSTOMER VALUES (?,?,?,?)");
        pmt.setString(1, custNo);
        pmt.setString(2, name);
        pmt.setLong(3, phoneNo);
        pmt.setString(4, city);
        int rowsAffected = pmt.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Customer record added successfully!");
            showCustomerRecords(); // Display updated customer list
        } else {
            System.out.println("Error adding customer record. Please check the data and try again.");
        }
    }
    private static void deleteCustomerRecord(String custNo) throws Exception {
        try {
            pmt = conn.prepareStatement("DELETE FROM CUSTOMER WHERE CUST_NO=?");
            pmt.setString(1, custNo);
            int rowsAffected = pmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Customer record deleted successfully.");
                showCustomerRecords(); // Display updated customer list
            } else {
                System.out.println("Customer record not found or deletion prevented due to dependent data. " +
                        "A customer record cannot be deleted if there are related entries in other tables.");
            }
        } catch (Exception e) {
            System.out.println("Error deleting customer record: " + e.getMessage());
        }
    }
    private static void updateCustomerInformation(String custNo) throws Exception {
        int updateChoice;
        System.out.println("\nUpdate Options:");
        System.out.println("1. Update Name");
        System.out.println("2. Update Phone Number");
        System.out.println("3. Update City");
        System.out.print("Enter your choice: ");
        updateChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline character after integer input
        String sql;
        int rowsAffected;
        switch (updateChoice) {
            case 1:
                System.out.print("Enter new name: ");
                String newName = scanner.nextLine();
                scanner.nextLine(); // Consume extra newline if user enters multiple lines for name
                sql = "UPDATE CUSTOMER SET name = ? WHERE cust_no = ?";
                pmt = conn.prepareStatement(sql);
                pmt.setString(1, newName);
                pmt.setString(2, custNo);
                rowsAffected = pmt.executeUpdate();
                if (rowsAffected == 1) {
                    System.out.println("Customer name updated successfully!");
                    System.out.println("New data is\n");
                    showCustomerRecords(); // Display updated customer list
                } else {
                    System.out.println("Error updating customer name. Please try again.");
                }
                break;
            case 2:
                System.out.print("Enter new phone number: ");
                String newPhoneNo = scanner.next();
                sql = "UPDATE CUSTOMER SET phone_no = ? WHERE cust_no = ?";
                pmt = conn.prepareStatement(sql);
                pmt.setString(1, newPhoneNo);
                pmt.setString(2, custNo);
                rowsAffected = pmt.executeUpdate();
                if (rowsAffected == 1) {
                    System.out.println("Customer phone number updated successfully!");
                    System.out.println("New data is\n");
                    showCustomerRecords(); // Display updated customer list
                } else {
                    System.out.println("Error updating customer phone number. Please try again.");
                }
                break;
            case 3:
                System.out.print("Enter new city: ");
                String newCity = scanner.nextLine();
                scanner.nextLine(); // Consume extra newline if user enters multiple lines for city
                sql = "UPDATE CUSTOMER SET city = ? WHERE cust_no = ?";
                pmt = conn.prepareStatement(sql);
                pmt.setString(1, newCity);
                pmt.setString(2, custNo);
                rowsAffected = pmt.executeUpdate();
                if (rowsAffected == 1) {
                    System.out.println("Customer city updated successfully!");
                    System.out.println("New data is\n");
                    showCustomerRecords(); // Display updated customer list
                } else {
                    System.out.println("Error updating customer city. Please try again.");
                }
                break;
            default:
                System.out.println("Invalid choice!");
        }
    }

    private static void showAccountDetails(String custNo) throws Exception {
        String accountNoQuery;
        if (custNo.charAt(0) == 'C') {
            accountNoQuery = "SELECT * FROM ACCOUNT WHERE ACCOUNT_NO=(SELECT ACCOUNT_NO FROM DEPOSITOR WHERE CUST_NO=?)";
        } else {
            accountNoQuery = "SELECT * FROM ACCOUNT WHERE ACCOUNT_NO=?";
        }
        pmt = conn.prepareStatement(accountNoQuery);
        pmt.setString(1, custNo);
        ResultSet rst = pmt.executeQuery();
        System.out.println("\nACCOUNT DETAILS\n");
        System.out.printf("| %-15s | %-5s | %-10s | %-15s |\n", "Account No", "Type", "Balance", "Branch Code");
        if (rst.next()) {
            String accountNo = String.format("%-15s", rst.getString(1));
            String type = String.format("%-5s", rst.getString(2));
            String balance = String.format("%10.2f", rst.getDouble(3)); // Format balance with two decimal places
            String brCode = String.format("%-15s", rst.getString(4));
            System.out.println("| " + accountNo + " | " + type + " | " + balance + " | " + brCode + " |");
        } else {
            System.out.println("Customer does not have an associated account.");
        }
        rst.close();
    }

    private static void showLoanDetails(String custNo) throws Exception {
        pmt = conn.prepareStatement("SELECT * FROM CUSTOMER JOIN LOAN ON CUSTOMER.CUST_NO=LOAN.CUST_NO WHERE CUSTOMER.CUST_NO=?");
        pmt.setString(1, custNo);
        ResultSet rst = pmt.executeQuery();
        if (rst.next()) {
            int columnCount = rst.getMetaData().getColumnCount();
            System.out.print("| ");
            System.out.printf("%-20s | ", "CUST_NO");
            for (int i = 1; i <= columnCount; i++) {
                if (!rst.getMetaData().getColumnName(i).equalsIgnoreCase("CUST_NO")) {
                    System.out.printf("%-20s | ", rst.getMetaData().getColumnName(i));
                }
            }
            System.out.println();
            System.out.printf("| %-20s ", rst.getString(1));
            do {
                System.out.print("| ");
                for (int i = 1; i <= columnCount; i++) {
                    if (!rst.getMetaData().getColumnName(i).equalsIgnoreCase("CUST_NO")) {
                        System.out.printf("%-20s | ", rst.getString(i));
                    }
                }
                System.out.println();
            } while (rst.next());

        } else {
            System.out.println("Customer not found or has no loans.");
        }
        rst.close();
    }

    private static void depositMoney(String accountNo, double depositAmount) throws Exception {
        ResultSet rst = null;
        pmt = conn.prepareStatement("SELECT * FROM ACCOUNT WHERE account_no=?");
        pmt.setString(1, accountNo);
        rst = pmt.executeQuery();
        if (!rst.next()) {
            System.out.println("Account not found!");
            return;
        }
        double currentBalance = rst.getDouble("balance");
        double newBalance = currentBalance + depositAmount;
        pmt = conn.prepareStatement("UPDATE ACCOUNT SET balance=? WHERE account_no=?");
        pmt.setDouble(1, newBalance);
        pmt.setString(2, accountNo);
        pmt.executeUpdate();
        showAccountDetails(accountNo);
        System.out.println("Deposit successful!");
    }
    private static void withdrawMoney(String accountNo, double withdrawalAmount) throws Exception {
        ResultSet rst = null;
        pmt = conn.prepareStatement("SELECT * FROM ACCOUNT WHERE account_no=?");
        pmt.setString(1, accountNo);
        rst = pmt.executeQuery();
        if (!rst.next()) {
            System.out.println("Account not found!");
            return;
        }
        double currentBalance = rst.getDouble("balance");
        if (withdrawalAmount <= 0) {
            System.out.println("Invalid amount. Withdrawal amount must be positive.");
            return;
        } else if (currentBalance < withdrawalAmount) {
            System.out.println("Insufficient funds. Available balance: " + currentBalance);
            return;
        }
        double newBalance = currentBalance - withdrawalAmount;
        pmt = conn.prepareStatement("UPDATE ACCOUNT SET balance=? WHERE account_no=?");
        pmt.setDouble(1, newBalance);
        pmt.setString(2, accountNo);
        pmt.executeUpdate();
        showAccountDetails(accountNo);
        System.out.println("Withdrawal successful!");
    }
    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@localhost:1521:orcl";
        String user = "sys as sysdba";
        String password = "root1234";
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(url, user, password);
            while (true) {
                System.out.println("\n\n*******BANKING MANAGEMENT SYSTEM*******\n\n");
                int choice;
                System.out.println("1. Show Customer Records");
                System.out.println("2. Add Customer Record");
                System.out.println("3. Delete Customer Record");
                System.out.println("4. Update Customer Information");
                System.out.println("5. Show Account Details of a Customer");
                System.out.println("6. Show Loan Details of a Customer");
                System.out.println("7. Deposit Money to an Account");
                System.out.println("8. Withdraw Money from an Account");
                System.out.println("9. Exit");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 1:
                        showCustomerRecords();
                        break;
                    case 2:
                        System.out.print("Enter customer number (must start with 'C'): ");
                        String custNo = scanner.nextLine();
                        System.out.print("Enter customer name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter phone number: ");
                        long phoneNo = scanner.nextLong();
                        scanner.nextLine();
                        System.out.print("Enter city: ");
                        String city = scanner.nextLine();
                        addCustomerRecord(custNo, name, phoneNo, city);
                        break;
                    case 3:
                        System.out.print("Enter customer number to delete: ");
                        custNo = scanner.nextLine();
                        deleteCustomerRecord(custNo);
                        break;
                    case 4:
                        System.out.print("Enter customer number to update: ");
                        custNo = scanner.nextLine();
                        updateCustomerInformation(custNo);
                        break;
                    case 5:
                        System.out.print("Enter customer number to show account details: ");
                        custNo = scanner.nextLine();
                        showAccountDetails(custNo);
                        break;
                    case 6:
                        System.out.print("Enter customer number to show loan details: ");
                        custNo = scanner.nextLine();
                        showLoanDetails(custNo);
                        break;
                    case 7:
                        System.out.print("Enter account number: ");
                        String accountNo = scanner.nextLine();
                        System.out.print("Enter amount to deposit: ");
                        double depositAmount = scanner.nextDouble();
                        scanner.nextLine();
                        depositMoney(accountNo, depositAmount);
                        break;
                    case 8:
                        System.out.print("Enter account number: ");
                        accountNo = scanner.nextLine();
                        System.out.print("Enter amount to withdraw: ");
                        double withdrawalAmount = scanner.nextDouble();
                        scanner.nextLine();
                        withdrawMoney(accountNo, withdrawalAmount);
                        break;
                    case 9:
                        System.out.println("Exiting program...");
                        conn.close();
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }
}