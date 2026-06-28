package br.edu.ufersa.poo.pizzaria.DAO;

import br.edu.ufersa.poo.pizzaria.model.entities.Cliente;
import br.edu.ufersa.poo.pizzaria.util.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// crud de cliente...
public class ClienteDAO implements ICrudDAO<Cliente> {
    //metodo salva clientes no banco após devida inserção
    @Override
    public void salvar(Cliente cliente) {
        String sql = "INSERT INTO cliente (nome, endereco, cpf, telefone, bairro) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getEndereco());
            stmt.setString(3, cliente.getCpf());
            stmt.setString(4, cliente.getTelefone());
            stmt.setString(5, cliente.getBairro());

            stmt.executeUpdate();
            System.out.println("Cliente salvo com sucesso!");

        } catch (SQLException e) {
            System.out.println("Erro no INSERT de Cliente:");
            e.printStackTrace();
        }
    }
    // seleciona todos os clientes *
    @Override
    public List<Cliente> listarTodos() {
        String sql = "SELECT * FROM cliente";
        List<Cliente> lista = new ArrayList<>();
// inicia a conexão, prepara o comando e armazena o resultado da consulta no ResultSet
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
// loop percorre cada linha retornada pelo banco de dados
            while (rs.next()) {
                int id = rs.getInt("id_cliente");
                String nome = rs.getString("nome");
                String endereco = rs.getString("endereco");
                String cpf = rs.getString("cpf");
                String telefone = rs.getString("telefone");
                String bairro = rs.getString("bairro");

                Cliente c = new Cliente(id, nome, endereco, cpf, telefone, bairro); //instancia o onjeto com dados da linha atual e depois add a lista
                lista.add(c); // instancia o objeto com id
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar clientes:");
            e.printStackTrace();
        }
        return lista;
    }
    //att o cliente já existente a partir do que for ser alterado
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
            stmt.setInt(6, cliente.getIdCliente()); // Puxa o ID diretamente do objeto cliente

            int linesAffected = stmt.executeUpdate();
            if (linesAffected > 0) {
                System.out.println("Cliente atualizado com sucesso!");
            } else {
                System.out.println("Nenhum cliente encontrado com o ID informado.");
            }

        } catch (SQLException e) {
            System.out.println("Erro no UPDATE de Cliente:");
            e.printStackTrace();
        }
    }
    // remove o cliente do bd pelo telefone, deletando registros filhos primeiro (foreign key)
    // Implementação do ICrudDAO<Cliente> — remove por id
    @Override
    public void remover(int id) {
        Cliente c = buscarPorId(id);
        if (c != null) remover(c.getTelefone());
    }

    // Remove por telefone (método original mantido)
    public void remover(String telefone) {

        String sqlGetId           = "SELECT id_cliente FROM cliente WHERE telefone = ?";
        String sqlGetPedidos      = "SELECT id_pedido FROM pedido WHERE id_cliente = ?";
        String sqlPedidoAdicional = "DELETE FROM pedido_adicional WHERE id_pedido = ?";
        String sqlPedido          = "DELETE FROM pedido WHERE id_cliente = ?";
        String sqlCliente         = "DELETE FROM cliente WHERE telefone = ?";

        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // 1. Busca o id_cliente pelo telefone
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

                // 2. Busca ids dos pedidos e deleta pedido_adicional de cada um
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

                // 3. Deleta os pedidos do cliente
                try (PreparedStatement stmt = conn.prepareStatement(sqlPedido)) {
                    stmt.setInt(1, idCliente);
                    stmt.executeUpdate();
                }

                // 4. Deleta o cliente
                try (PreparedStatement stmt = conn.prepareStatement(sqlCliente)) {
                    stmt.setString(1, telefone);
                    int linhasAfetadas = stmt.executeUpdate();
                    if (linhasAfetadas > 0) {
                        System.out.println("Cliente com telefone " + telefone + " removido com sucesso!");
                    }
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
    //metodo para filtrar o cliente por nome
    public List<Cliente> buscarPorNome(String nomeBusca) {
        String sql = "SELECT * FROM cliente WHERE nome LIKE ?";
        List<Cliente> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // % faz com que o SQL encontre o nome em qualquer parte do texto
            stmt.setString(1, "%" + nomeBusca + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id_cliente");
                    String nome = rs.getString("nome");
                    String endereco = rs.getString("endereco");
                    String cpf = rs.getString("cpf");
                    String telefone = rs.getString("telefone");
                    String bairro = rs.getString("bairro");

                    Cliente c = new Cliente(id, nome, endereco, cpf, telefone, bairro);
                    lista.add(c);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar cliente por nome:");
            e.printStackTrace();
        }
        return lista;
    }
    //metodo para buscar o cliente a partir do seu id
    @Override
    public Cliente buscarPorId(int idBusca) {
        String sql = "SELECT * FROM cliente WHERE id_cliente = ?";
        Cliente clienteEncontrado = null;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idBusca);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String nome = rs.getString("nome");
                    String endereco = rs.getString("endereco");
                    String cpf = rs.getString("cpf");
                    String telefone = rs.getString("telefone");
                    String bairro = rs.getString("bairro");

                    clienteEncontrado = new Cliente(idBusca, nome, endereco, cpf, telefone, bairro);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar cliente por ID:");
            e.printStackTrace();
        }
        return clienteEncontrado;
    }
}