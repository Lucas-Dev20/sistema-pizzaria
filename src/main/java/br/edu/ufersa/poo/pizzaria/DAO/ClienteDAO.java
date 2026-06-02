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
}