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
            "12345";

    public static Connection getConnection() {

        try {

            return DriverManager.getConnection(URL, USER, PASS);

        } catch (SQLException e) {

            throw new RuntimeException("Erro ao conectar ao banco de dados", e);
        }
    }

    public static void closeConnection(Connection conn) {

        try {
            if (conn != null) {
                conn.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}