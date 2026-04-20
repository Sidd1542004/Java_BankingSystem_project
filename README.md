# 🏦 Java Banking System (GUI + MySQL)

## 📌 Overview

This project is a **Java-based Banking System Simulation** built using **Core Java, Swing GUI, and MySQL (JDBC)**.
It simulates real-world banking operations such as account management, transactions, authentication, and transaction tracking.

---

## 🚀 Features

### 👤 Authentication

* User Registration
* Secure Login System
* Input validation (no empty username/password)
* Login History Tracking (with timestamp)

---

### 💰 Banking Operations

* Deposit Money
* Withdraw Money
* Transfer Money between users
* Balance Inquiry

---

### 📊 Transaction Management

* Transaction history stored in database
* JTable UI for displaying transactions
* Tracks:

  * Deposit
  * Withdraw
  * Transfer (Sender & Receiver)

---

### 🗄️ Database Integration

* MySQL Database
* JDBC Connectivity
* Tables:

  * `users`
  * `accounts`
  * `transactions`
  * `login_history`

---

## 🛠️ Technologies Used

* **Java (Core Java, OOP)**
* **Swing (GUI)**
* **JDBC (Database Connectivity)**
* **MySQL**
* **VS Code**

---

## 📂 Project Structure

```
BankingSystem/
├── src/
│   └── MainApp.java
├── lib/
│   └── mysql-connector-j-8.x.x.jar
├── .vscode/
│   └── settings.json
└── README.md
```

---

## ⚙️ Database Setup

Run the following SQL queries:

```sql
CREATE DATABASE bank_system;
USE bank_system;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE,
    password VARCHAR(50)
);

CREATE TABLE accounts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    balance DOUBLE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    type VARCHAR(20),
    amount DOUBLE,
    receiver VARCHAR(50),
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE login_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50),
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## ▶️ How to Run

### 1️⃣ Clone the repository

```
git clone https://github.com/your-username/banking-system.git
cd banking-system
```

### 2️⃣ Add MySQL Connector

* Download MySQL Connector/J
* Place `.jar` file inside `lib/`

---

### 3️⃣ Configure Database

Update in `DBConnection`:

```java
"jdbc:mysql://localhost:3306/bank_system",
"root",
"YOUR_PASSWORD"
```

---

### 4️⃣ Compile and Run

```bash
javac -cp "lib/*" src/MainApp.java
java -cp "lib/*;src" MainApp
```

---

## 🧪 How to Use

1. Register a new user
2. Login using credentials
3. Perform:

   * Deposit
   * Withdraw
   * Transfer
4. View transaction history (table UI)
5. Check login history in database

---

## 🎯 Key Concepts Used

* Object-Oriented Programming (OOP)
* JDBC (PreparedStatement, ResultSet)
* GUI Development (Swing)
* Database Design & Normalization
* Event Handling in Java

---

## 🔥 Advanced Features

* Login History Tracking
* Transfer with dual transaction recording
* JTable UI for professional display
* Input validation for security
* Multi-user support

---

## 📈 Future Enhancements

* Password Encryption (SHA-256)
* Transaction rollback (ACID properties)
* JavaFX UI upgrade
* REST API (Spring Boot)
* Mobile app integration

---

## 🎓 Conclusion

This project demonstrates a **real-world banking system simulation** with persistent storage, GUI interaction, and secure operations using Java and MySQL.

---

## 👨‍💻 Author

* Siddharth V
* B.E CSE Student
* Passionate about Web Development & Secure Systems

---

## ⭐ If you like this project

Give it a ⭐ on GitHub!
