package br.edu.ufersa.poo.pizzaria.DAO;

import br.edu.ufersa.poo.pizzaria.util.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EstoqueDAO {

    // Método que você fez, agora isolado no DAO de estoque
    public void baixarEstoque(int idAdicional, int quantidade) {
        String sql = "UPDATE adicional SET quantidade = quantidade - ? WHERE id_adicional = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantidade);
            stmt.setInt(2, idAdicional);

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.println("-> [EstoqueDAO] Estoque do adicional (ID: " + idAdicional + ") reduzido em " + quantidade);
            } else {
                System.out.println("-> [EstoqueDAO] Nenhum adicional encontrado com o ID: " + idAdicional);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao baixar estoque no EstoqueDAO:");
            e.printStackTrace();
        }
    }

    // Método de reposição também migrado para cá
    public void reporEstoque(int idAdicional, int quantidade) {
        String sql = "UPDATE adicional SET quantidade = quantidade + ? WHERE id_adicional = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantidade);
            stmt.setInt(2, idAdicional);

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.println("-> [EstoqueDAO] Estoque do adicional (ID: " + idAdicional + ") reposto em " + quantidade);
            } else {
                System.out.println("-> [EstoqueDAO] Nenhum adicional encontrado com o ID: " + idAdicional);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao repor estoque no EstoqueDAO:");
            e.printStackTrace();
        }
    }
}