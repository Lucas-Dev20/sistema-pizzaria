package br.edu.ufersa.poo.pizzaria.DAO;
import br.edu.ufersa.poo.pizzaria.util.ConnectionFactory;
import br.edu.ufersa.poo.pizzaria.model.entities.Pizza;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PizzaDAO {
    public void salvar(Pizza pizza) {

        String sql = "INSERT INTO pizzas(tipo, valor) VALUES (?, ?)";

        try (Connection con = ConnectionFactory.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql))
                //INSERT INTO pizzas(tipo, valor)
                //VALUES ('Calabresa', 35)

                //INSERT INTO pizzas(tipo, valor)
                //VALUES (?, ?)
        {

            stmt.setString(1, pizza.getTipo());
            stmt.setDouble(2, pizza.getValor());

            stmt.executeUpdate();
            System.out.println("Pizza salva!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Pizza> listarTodas() {

        List<Pizza> pizzas = new ArrayList<>();

        String sql = "SELECT * FROM pizzas";

        try (
                Connection con = ConnectionFactory.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {

            while(rs.next()) {

                Pizza pizza = new Pizza(
                        rs.getString("tipo"),
                        rs.getDouble("valor")
                );

                pizzas.add(pizza);
            }

        } catch(SQLException e) {
            e.printStackTrace();
        }

        return pizzas;
    }

}
