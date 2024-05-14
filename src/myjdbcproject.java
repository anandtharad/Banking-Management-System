import java.sql.*;
import java.util.Scanner;

public class myjdbcproject {
    static Connection conn = null;
    static Scanner scanner = new Scanner(System.in);
    static PreparedStatement pmt = null;
    private static void showCustomerRecords() throws Exception {
        // Code to display customer details from database
        Statement smt = conn.createStatement();
        ResultSet rst = smt.executeQuery("SELECT * FROM CUSTOMER");
        System.out.println("\nCUSTOMER RECORDS ARE AS FOLLOWS\n");
        System.out.printf("| %-10s | %-25s | %-10s | %-10s |\n", "Cust No", "Name", "Phone No", "City");
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
        // Code to get user input and insert data into database
        pmt = conn.prepareStatement("INSERT INTO CUSTOMER VALUES (?,?,?,?)");
        pmt.setString(1, custNo);
        pmt.setString(2, name);
        pmt.setLong(3, phoneNo);
        pmt.setString(4, city);
        int r = pmt.executeUpdate();
        if (r > 0) {
            showCustomerRecords();
        } else {
            System.out.println("Error in inserting data");
        }
    }

    private static void deleteCustomerRecord(String custNo) throws Exception {
        // Code to get customer number and delete record from database
        pmt = conn.prepareStatement("DELETE FROM CUSTOMER WHERE CUST_NO=?");
        pmt.setString(1, custNo);
        int r = pmt.executeUpdate();
        if (r > 0) {
            showCustomerRecords();
        } else {
            System.out.println("DATA DOESNOT EXIST OR ERROR IN DELETING DATA");
        }
    }

    private static void updateCustomerInformation(String custNo) throws Exception {
        // Code to get customer number, update choice, and update data
        int updateChoice;

        System.out.println("\nUpdate Options:");
        System.out.println("1. Update Name");
        System.out.println("2. Update Phone Number");
        System.out.println("3. Update City");
        System.out.print("Enter your choice: ");
        updateChoice = scanner.nextInt();
        scanner.nextLine();
        String sql;
        int r;
        switch (updateChoice) {
            case 1:
                System.out.print("Enter new name: ");
                String newName = scanner.nextLine();
                scanner.nextLine();
                sql = "UPDATE CUSTOMER SET name = ? WHERE cust_no = ?";
                pmt = conn.prepareStatement(sql);
                pmt.setString(1, newName);
                pmt.setString(2, custNo);
                r = pmt.executeUpdate();
                if (r == 1) {
                    System.out.println("Customer name updated successfully!");
                    System.out.println("New data is\n");
                    showCustomerRecords();
                }else
                    System.out.println("ERROR IN UPDATING DATA");
                break;
            case 2:
                System.out.print("Enter new phone number: ");
                long newPhoneNo = scanner.nextLong();
                sql = "UPDATE CUSTOMER SET phone_no = ? WHERE cust_no = ?";
                pmt = conn.prepareStatement(sql);
                pmt.setLong(1, newPhoneNo);
                pmt.setString(2, custNo);
                r = pmt.executeUpdate();
                if (r > 1) {
                    System.out.println("Customer name updated successfully!");
                    System.out.println("New data is\n");
                    showCustomerRecords();
                }else
                    System.out.println("ERROR IN UPDATING DATA");
                break;
            case 3:
                System.out.print("Enter new city: ");
                String newCity = scanner.nextLine();
                scanner.nextLine();
                sql = "UPDATE CUSTOMER SET city = ? WHERE cust_no = ?";
                pmt = conn.prepareStatement(sql);
                pmt.setString(1, newCity);
                pmt.setString(2, custNo);
                r = pmt.executeUpdate();
                if (r > 1) {
                    System.out.println("Customer name updated successfully!");
                    System.out.println("New data is\n");
                    showCustomerRecords();
                }else
                    System.out.println("ERROR IN UPDATING DATA");
                break;
            default:
                System.out.println("Invalid choice!");
        }
    }

    private static void showAccountDetails(String custNo) throws Exception{
        // Code to get customer number and display account details
        if(custNo.charAt(0)=='C') {
            pmt = conn.prepareStatement("SELECT * FROM ACCOUNT WHERE ACCOUNT_NO=(SELECT ACCOUNT_NO FROM DEPOSITOR WHERE CUST_NO=?)");
            pmt.setString(1, custNo);
        }else{
            pmt = conn.prepareStatement("SELECT * FROM ACCOUNT WHERE ACCOUNT_NO=?");
            pmt.setString(1, custNo);

        }
        ResultSet rst=pmt.executeQuery();
        System.out.println("\nACCOUNT DETAILS ARE AS FOLLOWS\n");
        System.out.printf("| %-15s | %-5s | %-10s | %-15s |\n", "Account No", "Type", "Balance", "Branch Code");
        while (rst.next()) {
            String accountNo = String.format("%-15s", rst.getString(1));
            String type = String.format("%-5s", rst.getString(2));
            String balance = String.format("%10d", rst.getLong(3));
            String brCode = String.format("%-15s", rst.getString(4));
            System.out.println("| " + accountNo + " | " + type + " | " + balance + " | " + brCode + " |");
        }
        rst.close();
    }

    private static void showLoanDetails(String custNo) throws Exception{
        // Code to get customer number and display loan details

        pmt=conn.prepareStatement("SELECT * FROM CUSTOMER JOIN LOAN ON CUSTOMER.CUST_NO=LOAN.CUST_NO WHERE CUSTOMER.CUST_NO=?");
        pmt.setString(1,custNo);
        ResultSet rst=pmt.executeQuery();
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
            System.out.println("Customer not found!");
        }
    }

    private static void depositMoney(String accountNo, String depositorCustNo, double depositAmount) throws Exception {
        // Code to get account number, amount, and update balance
        ResultSet rst=null;
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
        pmt = conn.prepareStatement("INSERT INTO DEPOSITOR (cust_no, account_no) VALUES (?, ?)");
        pmt.setString(1, depositorCustNo);
        pmt.setString(2, accountNo);
        pmt.executeUpdate();
    }

    private static void withdrawMoney(String accountNo, double withdrawalAmount) throws Exception {
        // Code to get account number, amount, and update balance
        ResultSet rst=null;

        pmt = conn.prepareStatement("SELECT * FROM ACCOUNT WHERE account_no=?");
        pmt.setString(1, accountNo);
        rst = pmt.executeQuery();
        if (!rst.next()) {
            System.out.println("Account not found!");
            return;
        }
        double currentBalance = rst.getDouble("balance");
        if (currentBalance < withdrawalAmount) {
            System.out.println("Insufficient funds. Available balance: " + currentBalance);
            return;
        }
        double newBalance = currentBalance - withdrawalAmount;
        pmt = conn.prepareStatement("UPDATE ACCOUNT SET balance=? WHERE account_no=?");
        pmt.setDouble(1, newBalance);
        pmt.setString(2, accountNo);
        pmt.executeUpdate();
        showAccountDetails(accountNo);
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String url = "jdbc:oracle:thin:@localhost:1521:orcl";
        String user = "sys as sysdba";
        String password = "root1234";
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(url, user, password);
            x:
            do {
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
                switch (choice) {
                    case 1:
                        // Show customer records method call
                        showCustomerRecords();
                        break;
                    case 2:
                        // Add customer record method call
                        scanner.nextLine();
                        System.out.println("Enter custmer no");
                        String custNo = scanner.nextLine().toUpperCase();
                        System.out.println("Enter custmer name");
                        String name = scanner.nextLine().toUpperCase();
                        System.out.println("Enter custmer phone no");
                        long phoneNo = scanner.nextLong();
                        scanner.nextLine();
                        System.out.println("Enter custmer city");
                        String city = scanner.next().toUpperCase();
                        addCustomerRecord(custNo,name,phoneNo,city);
                        break;
                    case 3:
                        // Delete customer record method call
                        scanner.nextLine();
                        System.out.println("Enter custmer no");
                        custNo = scanner.nextLine().toUpperCase();
                        deleteCustomerRecord(custNo);
                        break;
                    case 4:
                        scanner.nextLine();
                        System.out.println("Enter Customer number to be updated");
                        custNo = scanner.nextLine().toUpperCase();
                        updateCustomerInformation(custNo);
                        break;
                    case 5:
                        // Show account details method call
                        scanner.nextLine();
                        System.out.println("Enter Customer number");
                        custNo = scanner.nextLine().toUpperCase();
                        showAccountDetails(custNo);
                        break;
                    case 6:
                        // Show loan details method call
                        scanner.nextLine();
                        System.out.println("Enter Customer number to be updated");
                        custNo = scanner.nextLine().toUpperCase();
                        showLoanDetails(custNo);
                        break;
                    case 7:
                        // Deposit money method call
                        scanner.nextLine();
                        System.out.print("Enter account number to deposit to: ");
                        String accountNo = scanner.nextLine();
                        System.out.print("Enter your customer number (depositor): ");
                        String depositorCustNo = scanner.nextLine();
                        System.out.print("Enter amount to deposit: ");
                        double depositAmount = Double.parseDouble(scanner.nextLine());
                        if (depositAmount <= 0) {
                            System.out.println("Invalid amount. Deposit amount must be positive.");
                            return;
                        }
                        depositMoney(accountNo,depositorCustNo,depositAmount);
                        break;
                    case 8:
                        // Withdraw money method call
                        scanner.nextLine();
                        System.out.print("Enter account number to withdraw from: ");
                        accountNo = scanner.nextLine();
                        System.out.print("Enter amount to withdraw: ");
                        double withdrawalAmount = Double.parseDouble(scanner.nextLine());
                        if (withdrawalAmount <= 0) {
                            System.out.println("Invalid amount. Withdrawal amount must be positive.");
                            return;
                        }
                        withdrawMoney(accountNo,withdrawalAmount);
                        break;
                    case 9:
                        System.out.println("Exiting program...");
                        break x;
                    default:
                        System.out.println("Invalid choice!");
                }
            } while (true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}