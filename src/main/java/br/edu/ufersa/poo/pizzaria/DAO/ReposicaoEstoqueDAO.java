package br.edu.ufersa.poo.pizzaria.DAO;

import br.edu.ufersa.poo.pizzaria.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReposicaoEstoqueDAO {

    public void registrarReposicao(
            int idAdicional,
            int quantidade,
            double valorUnitario
    ) {

        String sql = """
                INSERT INTO reposicao_estoque
                (id_adicional, quantidade,
                 valor_unitario, valor_total)
                VALUES (?, ?, ?, ?)
                """;

        double valorTotal = quantidade * valorUnitario;

        try (
                Connection conn = ConnectionFactory.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, idAdicional);
            stmt.setInt(2, quantidade);
            stmt.setDouble(3, valorUnitario);
            stmt.setDouble(4, valorTotal);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double calcularGastosReposicao() {

        String sql = "SELECT SUM(valor_total) AS total FROM reposicao_estoque";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery())
        {

            if (rs.next()) {
                return rs.getDouble("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}