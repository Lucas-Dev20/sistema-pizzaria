package br.edu.ufersa.poo.pizzaria.DAO;
import br.edu.ufersa.poo.pizzaria.model.entities.Usuario;
import java.sql.*;
import br.edu.ufersa.poo.pizzaria.util.ConnectionFactory;

public class UsuarioDAO {
    public static boolean inserir(Usuario usuario) {
        String sql =
                "INSERT INTO usuario(login, senha, cargo) VALUES (?, ?, ?)";
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
}