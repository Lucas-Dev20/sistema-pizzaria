package br.edu.ufersa.poo.pizzaria.DAO;

import br.edu.ufersa.poo.pizzaria.model.entities.Pizza;
import br.edu.ufersa.poo.pizzaria.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PizzaDAO {

    // CREATE
    public void salvar(Pizza pizza) {

        String sql = "INSERT INTO pizzas(tipo, valor) VALUES (?, ?)";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, pizza.getTipo());
            stmt.setDouble(2, pizza.getValor());

            stmt.executeUpdate();

            System.out.println("Pizza salva com sucesso!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // READ - LISTAR TODAS
    public List<Pizza> listarTodas() {

        List<Pizza> pizzas = new ArrayList<>();

        String sql = "SELECT * FROM pizzas";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                Pizza pizza = new Pizza(
                        rs.getInt("id_pizza"),
                        rs.getString("tipo"),
                        rs.getDouble("valor")
                );

                pizzas.add(pizza);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pizzas;
    }

    // READ - BUSCAR POR ID
    public Pizza buscarPorId(int id) {

        String sql = "SELECT * FROM pizzas WHERE id_pizza = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    return new Pizza(
                            rs.getInt("id_pizza"),
                            rs.getString("tipo"),
                            rs.getDouble("valor")
                    );
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

                    pizzas.add(
                            new Pizza(
                                    rs.getInt("id_pizza"),
                                    rs.getString("tipo"),
                                    rs.getDouble("valor")
                            )
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pizzas;
    }

    // UPDATE
    public void atualizar(Pizza pizza) {

        String sql =
                "UPDATE pizzas SET tipo = ?, valor = ? WHERE id_pizza = ?";

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