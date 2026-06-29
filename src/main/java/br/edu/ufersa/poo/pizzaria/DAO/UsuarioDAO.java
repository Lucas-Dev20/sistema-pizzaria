package br.edu.ufersa.poo.pizzaria.DAO;

import br.edu.ufersa.poo.pizzaria.model.entities.PerfilUsuario;
import br.edu.ufersa.poo.pizzaria.model.entities.Usuario;
import br.edu.ufersa.poo.pizzaria.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class UsuarioDAO implements ICrudDAO<Usuario> {

    // ── SALVAR
    @Override
    public void salvar(Usuario usuario) {
        String sql = "INSERT INTO usuario (nome, email, senha, perfil, ativo) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenhaHash());
            stmt.setString(4, usuario.getPerfil().name());
            stmt.setBoolean(5, usuario.isAtivo());

            stmt.executeUpdate();

            // Recupera o id gerado pelo banco e seta no objeto
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    usuario.setIdUsuario(keys.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao salvar usuário: " + e.getMessage());
            throw new RuntimeException("Erro ao salvar usuário no banco.", e);
        }
    }

    // ── BUSCAR POR ID
    @Override
    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM usuario WHERE id_usuario = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por ID: " + e.getMessage());
        }
        return null;
    }

    // ── Bucsar o usuário pelo e-mail — usado pela autenticação de login.

    public Usuario buscarPorEmail(String email) {
        String sql = "SELECT * FROM usuario WHERE email = ? AND ativo = TRUE";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por email: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Usuario> listarTodos() {
        String sql = "SELECT * FROM usuario ORDER BY nome";
        List<Usuario> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar usuários: " + e.getMessage());
        }
        return lista;
    }


    public List<Usuario> listarFuncionarios() {
        String sql = "SELECT * FROM usuario WHERE perfil = 'FUNCIONARIO' ORDER BY nome";
        List<Usuario> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar funcionários: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public void atualizar(Usuario usuario) {
        String sql = "UPDATE usuario SET nome = ?, email = ?, perfil = ?, ativo = ? WHERE id_usuario = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getPerfil().name());
            stmt.setBoolean(4, usuario.isAtivo());
            stmt.setInt(5, usuario.getIdUsuario());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar usuário: " + e.getMessage());
            throw new RuntimeException("Erro ao atualizar usuário no banco.", e);
        }
    }

    public void atualizarSenha(int idUsuario, String novaSenhaHash) {
        String sql = "UPDATE usuario SET senha = ? WHERE id_usuario = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, novaSenhaHash);
            stmt.setInt(2, idUsuario);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar senha: " + e.getMessage());
        }
    }

    // ── REMOVER (desativa; não apaga fisicamente) ─────────────────────────────
    @Override
    public void remover(int id) {
        // Soft delete: marca como inativo para preservar histórico de pedidos
        String sql = "UPDATE usuario SET ativo = FALSE WHERE id_usuario = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao desativar usuário: " + e.getMessage());
        }
    }


    public boolean emailJaExiste(String email) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE email = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao verificar email: " + e.getMessage());
        }
        return false;
    }

    private Usuario mapearResultSet(ResultSet rs) throws SQLException {
        int    id      = rs.getInt("id_usuario");
        String nome    = rs.getString("nome");
        String email   = rs.getString("email");
        String hash    = rs.getString("senha");
        PerfilUsuario perfil = PerfilUsuario.valueOf(rs.getString("perfil"));
        boolean ativo  = rs.getBoolean("ativo");

        return new Usuario(id, nome, email, hash, perfil, ativo);
    }
}