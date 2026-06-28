package br.edu.ufersa.poo.pizzaria.DAO;

import br.edu.ufersa.poo.pizzaria.model.entities.Pizza;
import br.edu.ufersa.poo.pizzaria.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/* PizzaDAO estende AbstractDAO<Pizza> — padrão Template Method (GoF, pág. 325). */

public class PizzaDAO extends AbstractDAO<Pizza> {

    // ── Implementação dos hooks do Template Method ────────────────────────────

    @Override
    protected String getInsertSQL() {
        return "INSERT INTO pizzas(tipo, valor) VALUES (?, ?)";
    }

    @Override
    protected void preencherInsert(PreparedStatement ps, Pizza pizza) throws SQLException {
        ps.setString(1, pizza.getTipo());
        ps.setDouble(2, pizza.getValor());
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
                rs.getDouble("valor")
        );
    }

    // ── Métodos específicos de Pizza (não cobertos pelo template) ─────────────

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

    // READ - BUSCAR POR TIPO
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

    // READ - LISTAR TODAS (alias mantido para compatibilidade com PizzaService)
    public List<Pizza> listarTodas() {
        return listarTodos();  // delega ao template de AbstractDAO
    }

    // UPDATE
    @Override
    public void atualizar(Pizza pizza) {

        String sql = "UPDATE pizzas SET tipo = ?, valor = ? WHERE id_pizza = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, pizza.getTipo());
            stmt.setDouble(2, pizza.getValor());
            stmt.setInt(3, pizza.getIdPizza());

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

    // DELETE
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