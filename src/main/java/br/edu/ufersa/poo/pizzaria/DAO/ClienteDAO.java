package br.edu.ufersa.poo.pizzaria.DAO;

import br.edu.ufersa.poo.pizzaria.model.entities.Cliente;
import br.edu.ufersa.poo.pizzaria.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ClienteDAO extends AbstractDAO<Cliente> {

    @Override
    protected String getInsertSQL() {
        return "INSERT INTO cliente (nome, endereco, cpf, telefone, bairro) VALUES (?, ?, ?, ?, ?)";
    }

    @Override
    protected void preencherInsert(PreparedStatement ps, Cliente cliente) throws SQLException {
        ps.setString(1, cliente.getNome());
        ps.setString(2, cliente.getEndereco());
        ps.setString(3, cliente.getCpf());
        ps.setString(4, cliente.getTelefone());
        ps.setString(5, cliente.getBairro());
    }

    @Override
    protected String getTabela() {
        return "cliente";
    }

    @Override
    protected Cliente mapear(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id_cliente"),
                rs.getString("nome"),
                rs.getString("endereco"),
                rs.getString("cpf"),
                rs.getString("telefone"),
                rs.getString("bairro")
        );
    }

    /* buscarPorId — implementação obrigatória de ICrudDAO - via AbstractDAO */
    @Override
    public Cliente buscarPorId(int idBusca) {
        String sql = "SELECT * FROM cliente WHERE id_cliente = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idBusca);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar cliente por ID:");
            e.printStackTrace();
        }
        return null;
    }

    /* Busca clientes pelo nome */
    public List<Cliente> buscarPorNome(String nomeBusca) {
        String sql = "SELECT * FROM cliente WHERE nome LIKE ?";
        List<Cliente> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nomeBusca + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar cliente por nome:");
            e.printStackTrace();
        }
        return lista;
    }

    /* atualizar — implementação obrigatória de ICrudDAO - via AbstractDAO */
    @Override
    public void atualizar(Cliente cliente) {
        String sql = "UPDATE cliente SET nome = ?, endereco = ?, cpf = ?, telefone = ?, bairro = ? WHERE id_cliente = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getEndereco());
            stmt.setString(3, cliente.getCpf());
            stmt.setString(4, cliente.getTelefone());
            stmt.setString(5, cliente.getBairro());
            stmt.setInt(6, cliente.getIdCliente());

            int linhas = stmt.executeUpdate();
            if (linhas > 0) {
                System.out.println("Cliente atualizado com sucesso!");
            } else {
                System.out.println("Nenhum cliente encontrado com o ID informado.");
            }

        } catch (SQLException e) {
            System.out.println("Erro no UPDATE de Cliente:");
            e.printStackTrace();
        }
    }

    @Override
    public void remover(int id) {
        String sqlGetPedidos      = "SELECT id_pedido FROM pedido WHERE id_cliente = ?";
        String sqlPedidoAdicional = "DELETE FROM pedido_adicional WHERE id_pedido = ?";
        String sqlPedido          = "DELETE FROM pedido WHERE id_cliente = ?";
        String sqlCliente         = "DELETE FROM cliente WHERE id_cliente = ?";

        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // 1. Para cada pedido do cliente, remove os adicionais do pedido
                try (PreparedStatement stmtPedidos = conn.prepareStatement(sqlGetPedidos)) {
                    stmtPedidos.setInt(1, id);
                    try (ResultSet rsPedidos = stmtPedidos.executeQuery()) {
                        while (rsPedidos.next()) {
                            int idPedido = rsPedidos.getInt("id_pedido");
                            try (PreparedStatement stmtPA = conn.prepareStatement(sqlPedidoAdicional)) {
                                stmtPA.setInt(1, idPedido);
                                stmtPA.executeUpdate();
                            }
                        }
                    }
                }

                // 2. Remove os pedidos do cliente
                try (PreparedStatement stmt = conn.prepareStatement(sqlPedido)) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                }

                // 3. Remove o cliente
                try (PreparedStatement stmt = conn.prepareStatement(sqlCliente)) {
                    stmt.setInt(1, id);
                    int linhas = stmt.executeUpdate();
                    if (linhas > 0) System.out.println("Cliente id=" + id + " removido com sucesso!");
                }

                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Erro no DELETE de Cliente — rollback executado:");
                e.printStackTrace();
                throw new RuntimeException("Falha ao excluir cliente: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (RuntimeException e) {
            throw e;
        } catch (SQLException e) {
            throw new RuntimeException("Erro de conexão ao excluir cliente: " + e.getMessage(), e);
        }
    }

    public void remover(String telefone) {
        String sqlGetId           = "SELECT id_cliente FROM cliente WHERE telefone = ?";
        String sqlGetPedidos      = "SELECT id_pedido FROM pedido WHERE id_cliente = ?";
        String sqlPedidoAdicional = "DELETE FROM pedido_adicional WHERE id_pedido = ?";
        String sqlPedido          = "DELETE FROM pedido WHERE id_cliente = ?";
        String sqlCliente         = "DELETE FROM cliente WHERE telefone = ?";

        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // 1. Resolve o id_cliente pelo telefone
                int idCliente = -1;
                try (PreparedStatement stmt = conn.prepareStatement(sqlGetId)) {
                    stmt.setString(1, telefone);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) idCliente = rs.getInt("id_cliente");
                    }
                }

                if (idCliente == -1) {
                    System.out.println("Nenhum cliente encontrado com o telefone: " + telefone);
                    conn.rollback();
                    return;
                }

                // 2. Para cada pedido do cliente, remove os adicionais do pedido
                try (PreparedStatement stmtPedidos = conn.prepareStatement(sqlGetPedidos)) {
                    stmtPedidos.setInt(1, idCliente);
                    try (ResultSet rsPedidos = stmtPedidos.executeQuery()) {
                        while (rsPedidos.next()) {
                            int idPedido = rsPedidos.getInt("id_pedido");
                            try (PreparedStatement stmtPA = conn.prepareStatement(sqlPedidoAdicional)) {
                                stmtPA.setInt(1, idPedido);
                                stmtPA.executeUpdate();
                            }
                        }
                    }
                }

                // 3. Remove os pedidos do cliente
                try (PreparedStatement stmt = conn.prepareStatement(sqlPedido)) {
                    stmt.setInt(1, idCliente);
                    stmt.executeUpdate();
                }

                // 4. Remove o cliente pelo telefone
                try (PreparedStatement stmt = conn.prepareStatement(sqlCliente)) {
                    stmt.setString(1, telefone);
                    int linhas = stmt.executeUpdate();
                    if (linhas > 0) System.out.println("Cliente com telefone " + telefone + " removido com sucesso!");
                }

                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Erro no DELETE de Cliente — rollback executado:");
                e.printStackTrace();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao conectar para remover cliente:");
            e.printStackTrace();
        }
    }
}