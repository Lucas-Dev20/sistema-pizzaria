package br.edu.ufersa.poo.pizzaria.DAO;

import br.edu.ufersa.poo.pizzaria.model.entities.Adicional;
import br.edu.ufersa.poo.pizzaria.util.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// crud de adicional...
public class AdicionalDAO {

    //METODO SALVA OS ADICIONAIS APÓS INSERÇÃO DE DADOS
    public void salvar(Adicional adicional) {
        String sql = "INSERT INTO adicional (nome, valor, quantidade) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, adicional.getNome());
            stmt.setDouble(2, adicional.getValor());
            stmt.setInt(3, adicional.getQtd());
            // substituição das '?' do comando sql

            stmt.executeUpdate();
            System.out.println("Adicional salvo com sucesso!");

        } catch (SQLException e) {
            System.out.println("Erro no INSERT de Adicional:");
            e.printStackTrace();
        }
    }

    // seleciona todos os adicionais para listar (*)
    public List<Adicional> listarTodos() {
        String sql = "SELECT * FROM adicional";
        List<Adicional> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_adicional");
                String nome = rs.getString("nome");
                double valor = rs.getDouble("valor");
                int quantidade = rs.getInt("quantidade");

                Adicional a = new Adicional(id, nome, valor, quantidade); // instancia o objeto com dados da linha atual e depois add a lista
                lista.add(a);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar adicionais:");
            e.printStackTrace();
        }
        return lista;
    }

    //atualiza os dados de um adicional ja existente
    public void atualizar(Adicional adicional, String nomeAntigo) {
        String sql = "UPDATE adicional SET nome = ?, valor = ?, quantidade = ? WHERE nome = ?";
// recebe o nome antigo para o sql achar a devida linha do adicional
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, adicional.getNome());
            stmt.setDouble(2, adicional.getValor());
            stmt.setInt(3, adicional.getQtd());
            stmt.setString(4, nomeAntigo); // identifica o registro pelo nome original antes da mudança
// substituição das '?' do comando sql

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.println("Adicional atualizado com sucesso!");
            } else {
                System.out.println("Nenhum adicional encontrado com o nome informado.");
            }

        } catch (SQLException e) {
            System.out.println("Erro no UPDATE desse Adicional:");
            e.printStackTrace();
        }
    }

    //metodo para remover um adicional do bd a partir do nome
    public void remover(String nomeAdicional) {
        String sql = "DELETE FROM adicional WHERE nome = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nomeAdicional); // substituição das '?' do comando sql

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.println("Adicional '" + nomeAdicional + "' removido com sucesso!");
            } else {
                System.out.println("Nenhum adicional encontrado com o nome informado.");
            }

        } catch (SQLException e) {
            System.out.println("Erro no DELETE de Adicional:");
            e.printStackTrace();
        }
    }

// busca o adicional por id
    public Adicional buscarPorId(int idBusca) {
        String sql = "SELECT * FROM adicional WHERE id_adicional = ?";
        Adicional adicionalEncontrado = null;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idBusca); // substituição das '?' do comando sql

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String nome = rs.getString("nome");
                    double valor = rs.getDouble("valor");
                    int quantidade = rs.getInt("quantidade");

                    adicionalEncontrado = new Adicional(idBusca, nome, valor, quantidade);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar adicional por ID:");
            e.printStackTrace();
        }
        return adicionalEncontrado;
    }

    // metodo para baixar o estoque do adicional direto no bd
    public void baixarEstoque(int idAdicional, int quantidade) {
        String sql = "UPDATE adicional SET quantidade = quantidade - ? WHERE id_adicional = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantidade);
            stmt.setInt(2, idAdicional); // substituição das '?' do comando sql

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.println("Estoque do adicional (ID: " + idAdicional + ") reduzido em " + quantidade);
            } else {
                System.out.println("Nenhum adicional encontrado com o ID: " + idAdicional);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao baixar estoque do adicional:");
            e.printStackTrace();
        }
    }

    // metodo para repor o estoque do adicional direto no bd
    public void reporEstoque(int idAdicional, int quantidade) {
        String sql = "UPDATE adicional SET quantidade = quantidade + ? WHERE id_adicional = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantidade);
            stmt.setInt(2, idAdicional); // substituição das '?' do comando sql

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.println("Estoque do adicional (ID: " + idAdicional + ") reposto em " + quantidade);
            } else {
                System.out.println("Nenhum adicional encontrado com o ID: " + idAdicional);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao repor estoque do adicional:");
            e.printStackTrace();
        }
    }
}