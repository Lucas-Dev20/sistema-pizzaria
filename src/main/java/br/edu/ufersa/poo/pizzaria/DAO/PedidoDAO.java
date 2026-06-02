package br.edu.ufersa.poo.pizzaria.DAO;

import br.edu.ufersa.poo.pizzaria.model.entities.Adicional;
import br.edu.ufersa.poo.pizzaria.model.entities.Pedido;
import br.edu.ufersa.poo.pizzaria.util.ConnectionFactory;
import br.edu.ufersa.poo.pizzaria.model.entities.Cliente;
import br.edu.ufersa.poo.pizzaria.model.entities.Pizza;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class PedidoDAO {

    private ClienteDAO clienteDAO = new ClienteDAO();
    private PizzaDAO pizzaDAO = new PizzaDAO();
    private AdicionalDAO adicionalDAO = new AdicionalDAO();

    public void salvar(Pedido pedido) {

        String sqlPedido =
                "INSERT INTO pedido " +
                        "(id_cliente, id_pizza, tamanho, estado, data_pedido, valor_total) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)
        ) {

            stmt.setInt(1, pedido.getCliente().getIdCliente());

            stmt.setInt(2, pedido.getPizza().getIdPizza());

            stmt.setString(3, pedido.getTamanho());

            stmt.setString(4, pedido.getEstado());

            stmt.setDate(5, Date.valueOf(pedido.getData()));

            stmt.setDouble(6, pedido.getValorTotal());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();

            if (rs.next()) {

                int idPedido = rs.getInt(1);

                String sqlAdicional =
                        "INSERT INTO pedido_adicional " + "(id_pedido, id_adicional) " + "VALUES (?, ?)";

                try (PreparedStatement stmtAdicional = conn.prepareStatement(sqlAdicional)) {

                    List<Adicional> adicionais =
                            pedido.getAdicionais();

                    if (adicionais != null) {

                        for (Adicional adicional : adicionais) {

                            stmtAdicional.setInt(1, idPedido);

                            stmtAdicional.setInt(2, adicional.getIdAdicional());

                            stmtAdicional.executeUpdate();
                        }
                    }
                }
            }

            System.out.println("Pedido salvo com sucesso!");

        } catch (SQLException e) {
            e.printStackTrace();}
    }

    private List<Adicional> carregarAdicionais(int idPedido) {

        List<Adicional> adicionais = new ArrayList<>();

        String sql = "SELECT id_adicional " + "FROM pedido_adicional " + "WHERE id_pedido = ?";

        try (Connection conn = ConnectionFactory.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPedido);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    int idAdicional = rs.getInt("id_adicional");

                    Adicional adicional = adicionalDAO.buscarPorId(idAdicional);

                    if (adicional != null) {
                        adicionais.add(adicional);}
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return adicionais;
    }

    public List<Pedido> listarTodos() {

        List<Pedido> pedidos = new ArrayList<>();

        String sql = "SELECT * FROM pedido";

        try (Connection conn = ConnectionFactory.getConnection();

                PreparedStatement stmt = conn.prepareStatement(sql);

                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                int idPedido = rs.getInt("id_pedido");

                int idCliente = rs.getInt("id_cliente");

                int idPizza = rs.getInt("id_pizza");

                Cliente cliente = clienteDAO.buscarPorId(idCliente);

                Pizza pizza = pizzaDAO.buscarPorId(idPizza);

                List<Adicional> adicionais = carregarAdicionais(idPedido);

                Pedido pedido = new Pedido(
                                idPedido,
                                cliente,
                                pizza,
                                adicionais,
                                rs.getString("tamanho"),
                                rs.getString("estado"),
                                rs.getDate("data_pedido")
                                        .toLocalDate());

                pedidos.add(pedido);
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return pedidos;
    }

    public Pedido buscarPorId(int id) {

        String sql = "SELECT * FROM pedido WHERE id_pedido = ?";

        try (Connection conn = ConnectionFactory.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    int idCliente = rs.getInt("id_cliente");

                    int idPizza = rs.getInt("id_pizza");

                    Cliente cliente = clienteDAO.buscarPorId(idCliente);

                    Pizza pizza = pizzaDAO.buscarPorId(idPizza);

                    List<Adicional> adicionais = carregarAdicionais(id);

                    return new Pedido(
                            rs.getInt("id_pedido"),
                            cliente, pizza, adicionais,
                            rs.getString("tamanho"),
                            rs.getString("estado"),
                            rs.getDate("data_pedido").toLocalDate()
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void atualizarEstado(int idPedido, String novoEstado) {

        String sql = "UPDATE pedido " + "SET estado = ? " + "WHERE id_pedido = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, novoEstado);
            stmt.setInt(2, idPedido);

            int linhas = stmt.executeUpdate();

            if (linhas > 0) {

                System.out.println("Estado atualizado!");

            } else {

                System.out.println("Pedido não encontrado.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Pedido> buscarPorEstado(String estado) {

        List<Pedido> pedidos = new ArrayList<>();

        String sql = "SELECT * FROM pedido " + "WHERE estado = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, estado);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    int idPedido = rs.getInt("id_pedido");

                    Cliente cliente = clienteDAO.buscarPorId(rs.getInt("id_cliente"));

                    Pizza pizza = pizzaDAO.buscarPorId(rs.getInt("id_pizza"));

                    List<Adicional> adicionais = carregarAdicionais(idPedido);

                    Pedido pedido =
                            new Pedido(idPedido, cliente, pizza, adicionais,
                                    rs.getString("tamanho"),
                                    rs.getString("estado"),
                                    rs.getDate("data_pedido").toLocalDate());

                    pedidos.add(pedido);
                }
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return pedidos;
    }

    public List<Pedido> buscarPorCliente(int idCliente) {

        List<Pedido> pedidos = new ArrayList<>();

        String sql = "SELECT * FROM pedido " + "WHERE id_cliente = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCliente);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    int idPedido = rs.getInt("id_pedido");

                    Cliente cliente = clienteDAO.buscarPorId(idCliente);

                    Pizza pizza = pizzaDAO.buscarPorId(rs.getInt("id_pizza"));

                    List<Adicional> adicionais = carregarAdicionais(idPedido);

                    Pedido pedido =
                            new Pedido(idPedido, cliente, pizza, adicionais,
                                    rs.getString("tamanho"),
                                    rs.getString("estado"),
                                    rs.getDate("data_pedido").toLocalDate());

                    pedidos.add(pedido);
                }
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return pedidos;
    }

    public List<Pedido> buscarPorPizza(int idPizza) {

        List<Pedido> pedidos = new ArrayList<>();

        String sql = "SELECT * FROM pedido " + "WHERE id_pizza = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPizza);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    int idPedido = rs.getInt("id_pedido");

                    Cliente cliente = clienteDAO.buscarPorId(rs.getInt("id_cliente"));

                    Pizza pizza = pizzaDAO.buscarPorId(idPizza);

                    List<Adicional> adicionais = carregarAdicionais(idPedido);

                    Pedido pedido =
                            new Pedido(idPedido, cliente, pizza, adicionais,
                                    rs.getString("tamanho"),
                                    rs.getString("estado"),
                                    rs.getDate("data_pedido").toLocalDate());

                    pedidos.add(pedido);
                }
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return pedidos;
    }

    public void remover(int idPedido) {

        String sqlPedidoAdicional = "DELETE FROM pedido_adicional " + "WHERE id_pedido = ?";

        String sqlPedido = "DELETE FROM pedido " + "WHERE id_pedido = ?";

        try (Connection conn = ConnectionFactory.getConnection()) {

            try (PreparedStatement stmt = conn.prepareStatement(sqlPedidoAdicional)) {

                stmt.setInt(1, idPedido);
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(sqlPedido)) {

                stmt.setInt(1, idPedido);

                int linhas = stmt.executeUpdate();

                if (linhas > 0) {

                    System.out.println("Pedido removido com sucesso!");

                }
                else {
                    System.out.println("Pedido não encontrado.");
                }
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void atualizar(Pedido pedido) {

        String sqlPedido = "UPDATE pedido " + "SET id_cliente = ?, " +
                        "id_pizza = ?, " +
                        "tamanho = ?, " +
                        "estado = ?, " +
                        "data_pedido = ?, " +
                        "valor_total = ? " +
                        "WHERE id_pedido = ?";

        try (Connection conn = ConnectionFactory.getConnection();

             PreparedStatement stmt = conn.prepareStatement(sqlPedido)) {

            stmt.setInt(1, pedido.getCliente().getIdCliente());

            stmt.setInt(2, pedido.getPizza().getIdPizza());

            stmt.setString(3, pedido.getTamanho());

            stmt.setString(4, pedido.getEstado());

            stmt.setDate(5, Date.valueOf(pedido.getData()));

            stmt.setDouble(6, pedido.getValorTotal());

            stmt.setInt(7,pedido.getIdPedido());

            int linhas = stmt.executeUpdate();

            if (linhas > 0) {

                String sqlDelete = "DELETE FROM pedido_adicional " + "WHERE id_pedido = ?";

                try (PreparedStatement stmtDelete = conn.prepareStatement(sqlDelete)) {

                    stmtDelete.setInt(1, pedido.getIdPedido());
                    stmtDelete.executeUpdate();
                }

                String sqlInsert = "INSERT INTO pedido_adicional " + "(id_pedido, id_adicional) " + "VALUES (?, ?)";

                try (PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert)) {

                    for (Adicional adicional : pedido.getAdicionais()) {

                        stmtInsert.setInt(1, pedido.getIdPedido());

                        stmtInsert.setInt(2, adicional.getIdAdicional());

                        stmtInsert.executeUpdate();
                    }
                }

                System.out.println("Pedido atualizado com sucesso!");

            }
            else {
                System.out.println("Pedido não encontrado.");
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
