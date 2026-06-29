package br.edu.ufersa.poo.pizzaria.DAO;

import br.edu.ufersa.poo.pizzaria.model.entities.Pizza;
import br.edu.ufersa.poo.pizzaria.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/* PizzaDAO estende AbstractDAO<Pizza> — padrão Template Method  */

public class PizzaDAO extends AbstractDAO<Pizza> {

    // ── Implementação dos hooks do Template Method ────────────────────────────

    @Override
    protected String getInsertSQL() {
        return "INSERT INTO pizzas(tipo, valor_pequena, valor_media, valor_grande) VALUES (?, ?, ?, ?)";
    }

    @Override
    protected void preencherInsert(PreparedStatement ps, Pizza pizza) throws SQLException {
        ps.setString(1, pizza.getTipo());
        ps.setDouble(2, pizza.getValorPequena());
        ps.setDouble(3, pizza.getValorMedia());
        ps.setDouble(4, pizza.getValorGrande());
    }

    @Override
    protected String getTabela() {
        return "pizzas";
    }

    @Override
    protected Pizza mapear(ResultSet rs) throws SQLException {
        return new Pizza(
                rs.getInt("id_pizza"),
                rs.getString("tipo"),
                rs.getDouble("valor_pequena"),
                rs.getDouble("valor_media"),
                rs.getDouble("valor_grande")
        );
    }

    // ── Métodos específicos de Pizza
    // READ - BUSCAR POR ID
    @Override
    public Pizza buscarPorId(int id) {

        String sql = "SELECT * FROM pizzas WHERE id_pizza = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public List<Pizza> buscarPorTipo(String tipo) {

        List<Pizza> pizzas = new ArrayList<>();

        String sql = "SELECT * FROM pizzas WHERE tipo = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, tipo);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pizzas.add(mapear(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pizzas;
    }

    public List<Pizza> listarTodas() {
        return listarTodos();  // delega ao template de AbstractDAO
    }


    @Override
    public void atualizar(Pizza pizza) {

        String sql = "UPDATE pizzas SET tipo = ?, valor_pequena = ?, valor_media = ?, valor_grande = ? WHERE id_pizza = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, pizza.getTipo());
            stmt.setDouble(2, pizza.getValorPequena());
            stmt.setDouble(3, pizza.getValorMedia());
            stmt.setDouble(4, pizza.getValorGrande());
            stmt.setInt(5, pizza.getIdPizza());

            int linhas = stmt.executeUpdate();

            if (linhas > 0) {
                System.out.println("Pizza atualizada com sucesso!");
            } else {
                System.out.println("Pizza não encontrada.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void remover(int id) {

        String sql = "DELETE FROM pizzas WHERE id_pizza = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int linhas = stmt.executeUpdate();

            if (linhas > 0) {
                System.out.println("Pizza removida com sucesso!");
            } else {
                System.out.println("Pizza não encontrada.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}