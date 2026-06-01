package br.edu.ufersa.poo.pizzaria.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final String URL =
            "jdbc:mysql://localhost:3306/pizzaria";

    private static final String USER =
            "root";

    private static final String PASS =
            "Rian333nc!";

    public static Connection getConnection()
            throws SQLException {

        return DriverManager.getConnection(
                URL,
                USER,
                PASS
        );
    }
}
