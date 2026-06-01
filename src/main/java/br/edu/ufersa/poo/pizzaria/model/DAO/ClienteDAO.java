package br.edu.ufersa.poo.pizzaria.model.DAO;

import br.edu.ufersa.poo.pizzaria.model.entities.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    // Método para Salvar o Cliente no Banco
    public void salvar(Cliente cliente) {
        String sql = "INSERT INTO clientes (nome, endereco, cpf, telefone, bairro) VALUES (?, ?, ?, ?, ?)";

        Connection conn = UsuarioDAO.getConnection();

        if (conn != null) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, cliente.getNome());
                stmt.setString(2, cliente.getEndereco());
                stmt.setString(3, cliente.getCpf());
                stmt.setString(4, cliente.getTelefone());
                stmt.setString(5, cliente.getBairro());

                stmt.executeUpdate();
                System.out.println("Cliente salvo com sucesso!");

            } catch (SQLException e) {
                System.out.println("Erro ao executar o INSERT de Cliente:");
                e.printStackTrace();
            }
        } else {
            System.out.println("Erro de conexão de rede.");
        }
    }

    // Método para Listar Todos os Clientes
    public List<Cliente> listarTodos() {
        String sql = "SELECT * FROM clientes";
        List<Cliente> lista = new ArrayList<>();

        Connection conn = UsuarioDAO.getConnection();

        if (conn != null) {
            try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String nome = rs.getString("nome");
                    String endereco = rs.getString("endereco");
                    String cpf = rs.getString("cpf");
                    String telefone = rs.getString("telefone");
                    String bairro = rs.getString("bairro");

                    Cliente c = new Cliente(nome, endereco, cpf, telefone, bairro);
                    lista.add(c);
                }
            } catch (SQLException e) {
                System.out.println("Erro ao listar clientes:");
                e.printStackTrace();
            }
        }
        return lista;
    }
}