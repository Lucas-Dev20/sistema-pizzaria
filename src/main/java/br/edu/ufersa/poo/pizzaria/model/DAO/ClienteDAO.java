package br.edu.ufersa.poo.pizzaria.model.DAO;

import br.edu.ufersa.poo.pizzaria.model.entities.Cliente;
import br.edu.ufersa.poo.pizzaria.util.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
// crud de cliente...
public class ClienteDAO {
    //metodo salva clientes no banco após devida inserção
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
    public List<Cliente> listarTodos() {
        String sql = "SELECT * FROM cliente";
        List<Cliente> lista = new ArrayList<>();
// inicia a conexão, prepara o comando e armazena o resultado da consulta no ResultSet
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
// loop percorre cada linha retornada pelo banco de dados
            while (rs.next()) {
                String nome = rs.getString("nome");
                String endereco = rs.getString("endereco");
                String cpf = rs.getString("cpf");
                String telefone = rs.getString("telefone");
                String bairro = rs.getString("bairro");

                Cliente c = new Cliente(nome, endereco, cpf, telefone, bairro); //instancia o onjeto com dados da linha atual e depois add a lista
                lista.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar clientes:");
            e.printStackTrace();
        }
        return lista;
    }
//att o cliente já existente a partir do que for ser alterado
    public void atualizar(Cliente cliente, int idCliente) {
        String sql = "UPDATE cliente SET nome = ?, endereco = ?, cpf = ?, telefone = ?, bairro = ? WHERE id_cliente = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getEndereco());
            stmt.setString(3, cliente.getCpf());
            stmt.setString(4, cliente.getTelefone());
            stmt.setString(5, cliente.getBairro());
            stmt.setInt(6, idCliente); // define qual cliente será atualizado

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.println("Cliente atualizado com sucesso!");
            } else {
                System.out.println("Nenhum cliente encontrado com o ID informado.");
            }

        } catch (SQLException e) {
            System.out.println("Erro no UPDATE de Cliente:");
            e.printStackTrace();
        }
    }
//remove o cliente do bd pelo celular
public void remover(String telefone) {
    String sql = "DELETE FROM cliente WHERE telefone = ?";

    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, telefone);

        int linhasAfetadas = stmt.executeUpdate();
        if (linhasAfetadas > 0) {
            System.out.println("Cliente com telefone " + telefone + " removido com sucesso!");
        } else {
            System.out.println("Nenhum cliente encontrado com o telefone informado.");
        }

    } catch (SQLException e) {
        System.out.println("Erro no DELETE por Telefone:");
        e.printStackTrace();
    }
}
    //metodo para filtrar o cliente por nome
    public List<Cliente> buscarPorNome(String nomeBusca) {
        String sql = "SELECT * FROM cliente WHERE nome LIKE ?";
        List<Cliente> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // O "%" faz com que o SQL encontre o nome em qualquer parte do texto
            stmt.setString(1, "%" + nomeBusca + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String nome = rs.getString("nome");
                    String endereco = rs.getString("endereco");
                    String cpf = rs.getString("cpf");
                    String telefone = rs.getString("telefone");
                    String bairro = rs.getString("bairro");

                    Cliente c = new Cliente(nome, endereco, cpf, telefone, bairro);
                    lista.add(c);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar cliente por nome:");
            e.printStackTrace();
        }
        return lista;
    }
}