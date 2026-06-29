package br.edu.ufersa.poo.pizzaria.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseInitializer {

    public static void inicializarBanco() {
        System.out.println("[Banco] Verificando e inicializando estrutura do banco de dados...");

        StringBuilder sqlScript = new StringBuilder();

        // Lê schema.sql de dentro do JAR/classpath (src/main/resources/schema.sql)
        try (InputStream is = DatabaseInitializer.class.getClassLoader().getResourceAsStream("schema.sql")) {

            if (is == null) {
                System.out.println("[Erro - Banco] Arquivo schema.sql não encontrado em src/main/resources/");
                return;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String linha;
                while ((linha = reader.readLine()) != null) {
                    if (!linha.trim().startsWith("--") && !linha.trim().startsWith("#")) {
                        sqlScript.append(linha).append("\n");
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("[Erro - Banco] Não foi possível ler o schema.sql: " + e.getMessage());
            return;
        }

        // Executa o script no banco
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlScript.toString());
            System.out.println("[Banco] Estrutura do banco de dados verificada/criada com sucesso!");

        } catch (SQLException e) {
            System.out.println("[Erro - Banco] Falha ao executar o script do schema.sql:");
            e.printStackTrace();
        }
    }
}