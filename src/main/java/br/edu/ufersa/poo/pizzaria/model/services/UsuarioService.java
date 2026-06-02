package br.edu.ufersa.poo.pizzaria.model.services;
import br.edu.ufersa.poo.pizzaria.DAO.UsuarioDAO;
import br.edu.ufersa.poo.pizzaria.model.entities.Usuario;

public class UsuarioService {
    public static boolean cadastrarUsuario(String login, String senha, String cargo) {
        // regras para cadrastrar
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("Login não pode ser vazio");
        }

        if (senha == null || senha.length() < 6) {
            throw new IllegalArgumentException("Senha deve ter pelo menos 6 caracteres");
        }

        // cria um objeto Usuario com os dados informados e o envia para inserção no banco.
        Usuario usuario = new Usuario(login, senha, cargo);
        return UsuarioDAO.inserir(usuario);
    }

    // verifica se o usuario não é vazio e manda para o banco excluir
    public static boolean excluirUsuario(String login) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException(
                    "Login não pode ser vazio");
        }
        return UsuarioDAO.excluir(login);
    }
}
