package br.edu.ufersa.poo.pizzaria.model.services;

import br.edu.ufersa.poo.pizzaria.DAO.UsuarioDAO;
import br.edu.ufersa.poo.pizzaria.exceptions.AcessoNegadoException;
import br.edu.ufersa.poo.pizzaria.model.entities.PerfilUsuario;
import br.edu.ufersa.poo.pizzaria.model.entities.Usuario;
import br.edu.ufersa.poo.pizzaria.session.SessaoUsuario;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class UsuarioService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // ── AUTENTICAÇÃO

    public Usuario autenticar(String email, String senha) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("E-mail é obrigatório.");
        }
        if (senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("Senha é obrigatória.");
        }

        Usuario usuario = usuarioDAO.buscarPorEmail(email.trim().toLowerCase());

        if (usuario == null) {
            throw new RuntimeException("Usuário não encontrado ou conta inativa.");
        }

        String hashInformado = hashSenha(senha);
        if (!hashInformado.equals(usuario.getSenhaHash())) {
            throw new RuntimeException("Senha incorreta.");
        }

        // Inicia a sessão global (Singleton)
        SessaoUsuario.getInstance().iniciar(usuario);

        return usuario;
    }

    public void logout() {
        SessaoUsuario.getInstance().encerrar();
    }

    // ── CADASTRO DE FUNCIONÁRIOS (somente ADMIN)

    public void cadastrarFuncionario(String nome, String email, String senha) {
        // Verificação de permissão
        verificarPermissaoAdmin("cadastrar funcionário");

        validarDadosUsuario(nome, email, senha);

        if (usuarioDAO.emailJaExiste(email.trim().toLowerCase())) {
            throw new IllegalArgumentException("Já existe um usuário com este e-mail.");
        }

        Usuario novoFuncionario = new Usuario(
                nome.trim(),
                email.trim().toLowerCase(),
                hashSenha(senha),
                PerfilUsuario.FUNCIONARIO
        );

        usuarioDAO.salvar(novoFuncionario);
    }

    /*Desativa um funcionário (soft delete) */
    public void desativarFuncionario(int idUsuario) {
        verificarPermissaoAdmin("desativar funcionário");

        if (idUsuario <= 0) {
            throw new IllegalArgumentException("ID de usuário inválido.");
        }

        usuarioDAO.remover(idUsuario);
    }

    /*Lista todos os funcionários (perfil FUNCIONARIO)
     */
    public List<Usuario> listarFuncionarios() {
        verificarPermissaoAdmin("listar funcionários");
        return usuarioDAO.listarFuncionarios();
    }

    // ── ALTERAÇÃO DE SENHA

    public void alterarSenha(int idUsuario, String senhaAtual, String novaSenha) {
        Usuario usuario = usuarioDAO.buscarPorId(idUsuario);

        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }

        if (!hashSenha(senhaAtual).equals(usuario.getSenhaHash())) {
            throw new RuntimeException("Senha atual incorreta.");
        }

        if (novaSenha == null || novaSenha.length() < 4) {
            throw new IllegalArgumentException("A nova senha deve ter pelo menos 4 caracteres.");
        }

        usuarioDAO.atualizarSenha(idUsuario, hashSenha(novaSenha));
    }

    // ── VERIFICAÇÃO DE PERMISSÃO

    public void verificarPermissaoAdmin(String operacao) {
        SessaoUsuario sessao = SessaoUsuario.getInstance();

        if (!sessao.estaLogado()) {
            throw new AcessoNegadoException("Nenhum usuário logado.");
        }

        if (!sessao.usuarioEhAdmin()) {
            String perfil = sessao.getUsuarioLogado().getPerfil().name();
            throw new AcessoNegadoException(operacao, perfil);
        }
    }

    // ── INICIALIZAÇÃO DO ADMIN PADRÃO
    public void garantirAdminPadrao() {
        String emailAdmin = "admin@lapiazza.com";

        if (!usuarioDAO.emailJaExiste(emailAdmin)) {
            Usuario admin = new Usuario(
                    "Michelangelo",
                    emailAdmin,
                    hashSenha("admin123"),
                    PerfilUsuario.ADMIN
            );
            usuarioDAO.salvar(admin);
            System.out.println("Administrador padrão criado: " + emailAdmin + " / admin123");
        }
    }


    public static String hashSenha(String senha) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(senha.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar hash da senha.", e);
        }
    }

    // ── VALIDAÇÕES PRIVADAS

    private void validarDadosUsuario(String nome, String email, String senha) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
        if (senha == null || senha.length() < 4) {
            throw new IllegalArgumentException("Senha deve ter pelo menos 4 caracteres.");
        }
    }
}