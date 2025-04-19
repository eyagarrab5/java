package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {
    public static MyDatabase instance;

    private final String URL = "jdbc:mysql://127.0.0.1:3306/forum";
    private final String USER = "root";
    private final String PASSWORD = "";
    private Connection conn;

    private MyDatabase() {
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            conn.setAutoCommit(true);
            System.out.println("Connected to database (auto-commit = true)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static MyDatabase getInstance() {
        if (instance == null) {
            instance = new MyDatabase();
        }
        return instance;
    }

    public Connection getConnection() {
        return conn;
    }
}
