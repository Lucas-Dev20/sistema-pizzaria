package br.edu.ufersa.poo.pizzaria.DAO;

import br.edu.ufersa.poo.pizzaria.model.entities.Usuario;
import java.sql.*;
import br.edu.ufersa.poo.pizzaria.util.ConnectionFactory;

public class UsuarioDAO {

    public static boolean inserir(Usuario usuario) {
        String sql = "INSERT INTO usuario(login, senha, cargo) VALUES (?, ?, ?)";
        try {
            Connection con = ConnectionFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, usuario.getLogin());
            stmt.setString(2, usuario.getSenha());
            stmt.setString(3, usuario.getCargo());
            stmt.executeUpdate();
            stmt.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean excluir(String login) {
        String sql = "DELETE FROM usuario WHERE login = ?";
        try {
            Connection con = ConnectionFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, login);
            int linhasAfetadas = stmt.executeUpdate();
            stmt.close();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── NOVO MÉTODO ────────────────────────────────────────────────────────
    // Busca no banco um usuário com o login E senha informados.
    // SQL: SELECT * FROM usuario WHERE login = ? AND senha = ?
    // Retorna o objeto Usuario completo (com o cargo) se encontrado,
    // ou null se login/senha estiverem errados.
    public static Usuario buscarPorLoginESenha(String login, String senha) {
        String sql = "SELECT * FROM usuario WHERE login = ? AND senha = ?";
        try {
            Connection con = ConnectionFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, login);
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Usuário encontrado — lê o cargo da linha retornada
                String loginBanco = rs.getString("login");
                String senhaBanco = rs.getString("senha");
                String cargo      = rs.getString("cargo");
                rs.close();
                stmt.close();
                return new Usuario(loginBanco, senhaBanco, cargo);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // login ou senha incorretos
    }
}