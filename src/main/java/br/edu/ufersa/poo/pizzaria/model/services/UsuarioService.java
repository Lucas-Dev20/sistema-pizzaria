package br.edu.ufersa.poo.pizzaria.model.services;

import br.edu.ufersa.poo.pizzaria.DAO.UsuarioDAO;
import br.edu.ufersa.poo.pizzaria.model.entities.Usuario;

public class UsuarioService {

    public static boolean cadastrarUsuario(String login, String senha, String cargo) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("Login não pode ser vazio");
        }
        if (senha == null || senha.length() < 6) {
            throw new IllegalArgumentException("Senha deve ter pelo menos 6 caracteres");
        }
        Usuario usuario = new Usuario(login, senha, cargo);
        return UsuarioDAO.inserir(usuario);
    }

    public static boolean excluirUsuario(String login) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("Login não pode ser vazio");
        }
        return UsuarioDAO.excluir(login);
    }

    // ── NOVO MÉTODO ────────────────────────────────────────────────────────
    // Recebe o login e senha digitados na tela e consulta o banco via DAO.
    // Retorna o objeto Usuario (com cargo) se autenticado, ou null se errado.
    public static Usuario autenticar(String login, String senha) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("Login não pode ser vazio");
        }
        if (senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("Senha não pode ser vazia");
        }
        // Delega a consulta ao banco para o DAO
        return UsuarioDAO.buscarPorLoginESenha(login, senha);
    }
}