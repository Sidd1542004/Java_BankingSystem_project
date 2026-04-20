
import java.sql.*;
import javax.swing.*;
import java.awt.*;

class DBConnection {
    public static Connection getConnection() {
        try {
             Class.forName("com.mysql.cj.jdbc.Driver"); 
            return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bank_system",
                "root",
                "Sid@2004"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

// ===============================
// LOGIN GUI
// ===============================
class LoginFrame extends JFrame {
    JTextField userField;
    JPasswordField passField;

    
    LoginFrame() {
        setTitle("Bank Login");
        setSize(300, 200);
        setLayout(new GridLayout(3,2));

        add(new JLabel("Username:"));
        userField = new JTextField();
        add(userField);

        add(new JLabel("Password:"));
        passField = new JPasswordField();
        add(passField);

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");

        add(loginBtn);
        add(registerBtn);

        loginBtn.addActionListener(e -> login());
        registerBtn.addActionListener(e -> register());

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    void login() {
        String username = userField.getText();
        String password = new String(passField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Username and Password cannot be empty!");
        return;
        }
        if (password.length() < 4) {
    JOptionPane.showMessageDialog(this, "Password must be at least 4 characters!");
    return;
}

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM users WHERE username=? AND password=?"
            );
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

    // ✅ Save login history
    PreparedStatement logPs = con.prepareStatement(
        "INSERT INTO login_history(username) VALUES (?)"
    );
    logPs.setString(1, username);
    logPs.executeUpdate();

    JOptionPane.showMessageDialog(this, "Login Successful");
    new DashboardFrame(username);
    dispose();
} else {
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void register() {
        String username = userField.getText();
        String password = new String(passField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Username and Password cannot be empty!");
        return;
        }
        if (password.length() < 4) {
    JOptionPane.showMessageDialog(this, "Password must be at least 4 characters!");
    return;
}

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO users(username, password) VALUES (?,?)",
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int userId = rs.getInt(1);

                PreparedStatement accPs = con.prepareStatement(
                    "INSERT INTO accounts(user_id, balance) VALUES (?,0)"
                );
                accPs.setInt(1, userId);
                accPs.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Registered Successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// ===============================
// DASHBOARD GUI
// ===============================
class DashboardFrame extends JFrame {
    String username;

    DashboardFrame(String username) {
        this.username = username;

        setTitle("Dashboard - " + username);
        setSize(400, 300);
        setLayout(new GridLayout(5,1));

        JButton depositBtn = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");
        JButton balanceBtn = new JButton("Check Balance");
        JButton historyBtn = new JButton("Transaction History");
        JButton transferBtn = new JButton("Transfer Money");

        add(depositBtn);
        add(withdrawBtn);
        add(balanceBtn);
        add(historyBtn);
        add(transferBtn);

        depositBtn.addActionListener(e -> deposit());
        withdrawBtn.addActionListener(e -> withdraw());
        balanceBtn.addActionListener(e -> checkBalance());
        historyBtn.addActionListener(e -> showTransactionTable());
        transferBtn.addActionListener(e -> transfer());

        setVisible(true);
    }

    int getUserId(Connection con) throws Exception {
        PreparedStatement ps = con.prepareStatement(
            "SELECT id FROM users WHERE username=?"
        );
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt("id");
    }


    void deposit() {
        String input = JOptionPane.showInputDialog("Enter amount:");
        double amount = Double.parseDouble(input);

        try (Connection con = DBConnection.getConnection()) {
            int userId = getUserId(con);

            PreparedStatement ps = con.prepareStatement(
                "UPDATE accounts SET balance = balance + ? WHERE user_id=?"
            );
            ps.setDouble(1, amount);
            ps.setInt(2, userId);
            ps.executeUpdate();

            saveTransaction(con, userId, "DEPOSIT", amount, "SELF");

            JOptionPane.showMessageDialog(this, "Deposited!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void withdraw() {
        String input = JOptionPane.showInputDialog("Enter amount:");
        double amount = Double.parseDouble(input);

        try (Connection con = DBConnection.getConnection()) {
            int userId = getUserId(con);

            PreparedStatement check = con.prepareStatement(
                "SELECT balance FROM accounts WHERE user_id=?"
            );
            check.setInt(1, userId);
            ResultSet rs = check.executeQuery();
            rs.next();

            if (rs.getDouble("balance") >= amount) {
                PreparedStatement ps = con.prepareStatement(
                    "UPDATE accounts SET balance = balance - ? WHERE user_id=?"
                );
                ps.setDouble(1, amount);
                ps.setInt(2, userId);
                ps.executeUpdate();

                saveTransaction(con, userId, "WITHDRAW", amount,"SELF");

                JOptionPane.showMessageDialog(this, "Withdrawn!");
            } else {
                JOptionPane.showMessageDialog(this, "Insufficient balance");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void checkBalance() {
        try (Connection con = DBConnection.getConnection()) {
            int userId = getUserId(con);

            PreparedStatement ps = con.prepareStatement(
                "SELECT balance FROM accounts WHERE user_id=?"
            );
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            rs.next();

            JOptionPane.showMessageDialog(this,
                "Balance: " + rs.getDouble("balance"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void history() {
        try (Connection con = DBConnection.getConnection()) {
            int userId = getUserId(con);

            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM transactions WHERE user_id=?"
            );
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append(rs.getString("type"))
                  .append(" - ")
                  .append(rs.getDouble("amount"))
                  .append("\n");
            }

            JOptionPane.showMessageDialog(this, sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void transfer() {
    String receiver = JOptionPane.showInputDialog("Enter receiver username:");
    String amtStr = JOptionPane.showInputDialog("Enter amount:");

    double amount = Double.parseDouble(amtStr);

    try (Connection con = DBConnection.getConnection()) {

        int senderId = getUserId(con);

        // 🔍 Get receiver ID
        PreparedStatement findUser = con.prepareStatement(
            "SELECT id FROM users WHERE username=?"
        );
        findUser.setString(1, receiver);
        ResultSet rs = findUser.executeQuery();

        if (!rs.next()) {
            JOptionPane.showMessageDialog(this, "Receiver not found!");
            return;
        }

        int receiverId = rs.getInt("id");

        // 💰 Check sender balance
        PreparedStatement check = con.prepareStatement(
            "SELECT balance FROM accounts WHERE user_id=?"
        );
        check.setInt(1, senderId);
        ResultSet rs2 = check.executeQuery();
        rs2.next();

        if (rs2.getDouble("balance") < amount) {
            JOptionPane.showMessageDialog(this, "Insufficient balance!");
            return;
        }

        // 🔄 Deduct from sender
        PreparedStatement debit = con.prepareStatement(
            "UPDATE accounts SET balance = balance - ? WHERE user_id=?"
        );
        debit.setDouble(1, amount);
        debit.setInt(2, senderId);
        debit.executeUpdate();

        // ➕ Add to receiver
        PreparedStatement credit = con.prepareStatement(
            "UPDATE accounts SET balance = balance + ? WHERE user_id=?"
        );
        credit.setDouble(1, amount);
        credit.setInt(2, receiverId);
        credit.executeUpdate();

        // 📝 Save transaction (sender)
        saveTransaction(con, senderId, "TRANSFER_OUT", amount, receiver);
        saveTransaction(con, receiverId, "TRANSFER_IN", amount, username);
        JOptionPane.showMessageDialog(this, "Transfer Successful!");

    } catch (Exception e) {
        e.printStackTrace();
    }
}

    void saveTransaction(Connection con, int userId, String type, double amount, String receiver) throws Exception {
    PreparedStatement ps = con.prepareStatement(
        "INSERT INTO transactions(user_id, type, amount, receiver) VALUES (?,?,?,?)"
    );
    ps.setInt(1, userId);
    ps.setString(2, type);
    ps.setDouble(3, amount);
    ps.setString(4, receiver);
    ps.executeUpdate();
}
    
    void showTransactionTable() {
    try (Connection con = DBConnection.getConnection()) {

        int userId = getUserId(con);

        PreparedStatement ps = con.prepareStatement(
            "SELECT type, amount, receiver, date FROM transactions WHERE user_id=?"
        );
        ps.setInt(1, userId);

        ResultSet rs = ps.executeQuery();

        // Table columns
        String[] columns = {"Type", "Amount", "Receiver", "Date"};

        // Table data
        java.util.List<Object[]> dataList = new java.util.ArrayList<>();

        while (rs.next()) {
            dataList.add(new Object[]{
                rs.getString("type"),
                rs.getDouble("amount"),
                rs.getString("receiver"),
                rs.getTimestamp("date")
            });
        }

        // Convert list to array
        Object[][] data = new Object[dataList.size()][3];
        for (int i = 0; i < dataList.size(); i++) {
            data[i] = dataList.get(i);
        }

        // Create JTable
        javax.swing.JTable table = new javax.swing.JTable(data, columns);

        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(table);

        // Show in new window
        javax.swing.JFrame frame = new javax.swing.JFrame("Transaction History");
        frame.add(scrollPane);
        frame.setSize(500, 300);
        frame.setVisible(true);

    } catch (Exception e) {
        e.printStackTrace();
    }
}

}

// ===============================
// MAIN CLASS
// ===============================
public class MainApp {
    public static void main(String[] args) {
        new LoginFrame();
    }
}
