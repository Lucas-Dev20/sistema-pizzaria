package br.edu.ufersa.poo.pizzaria.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseInitializer {

    private static final String CAMINHO_SCHEMA = "schema.sql"; // O arquivo deve ficar na raiz do projeto

    public static void inicializarBanco() {
        System.out.println("[Banco] Verificando e inicializando estrutura do banco de dados...");

        StringBuilder sqlScript = new StringBuilder();

        // 1. Ler o arquivo schema.sql linha por linha
        try (BufferedReader reader = new BufferedReader(new FileReader(CAMINHO_SCHEMA))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                // Ignora linhas de comentários do SQL
                if (!linha.trim().startsWith("--") && !linha.trim().startsWith("#")) {
                    sqlScript.append(linha).append("\n");
                }
            }
        } catch (IOException e) {
            System.out.println("[Erro - Banco] Não foi possível ler o arquivo schema.sql: " + e.getMessage());
            return;
        }

        // 2. Executar o script lido no banco de dados
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {

            // Executa todo o script de uma vez só graças ao allowMultiQueries=true
            stmt.execute(sqlScript.toString());
            System.out.println("[Banco] Estrutura do banco de dados verificada/criada com sucesso!");

        } catch (SQLException e) {
            System.out.println("[Erro - Banco] Falha ao executar o script do schema.sql:");
            e.printStackTrace();
        }
    }
}