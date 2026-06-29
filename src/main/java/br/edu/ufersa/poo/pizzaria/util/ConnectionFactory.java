package br.edu.ufersa.poo.pizzaria.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    // Adicionados os parâmetros para criar o banco automaticamente e aceitar múltiplas queries
    private static final String URL =
            "jdbc:mysql://localhost:3306/pizzaria?createDatabaseIfNotExist=true&allowMultiQueries=true";

    private static final String USER =
            "root";

    private static final String PASS =
            "Rian333nc!";

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