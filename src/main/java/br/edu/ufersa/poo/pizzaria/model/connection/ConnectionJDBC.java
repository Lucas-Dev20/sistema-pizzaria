package br.edu.ufersa.poo.pizzaria.model.connection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionJDBC {

    private static final String URL =
            "jdbc:mysql://localhost:3306/pizzaria";
    private static final String USER = "Lucas";
    private static final String PASSWORD = "Lukah23@1";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}