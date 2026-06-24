package br.edu.ufersa.poo.pizzaria.session;

import br.edu.ufersa.poo.pizzaria.model.entities.Usuario;

/**
 * PADRÃO SINGLETON — Sessão do usuário logado.
 * Armazena o usuário autenticado e o torna acessível a qualquer
 * controller sem precisar passar parâmetros entre telas.
 * Uso:
   // Após login:SessaoUsuario.getInstance().iniciar(usuario);
   // Em qualquer controller:
    Usuario u = SessaoUsuario.getInstance().getUsuarioLogado();
    if (u.isAdmin()) { ... }
  // Ao fazer logout: SessaoUsuario.getInstance().encerrar(); */

public class SessaoUsuario {

    // ── Única instância (Singleton) ───────────────────────────────────────────
    private static SessaoUsuario instance;

    private Usuario usuarioLogado;

    // Construtor privado — ninguém pode instanciar de fora
    private SessaoUsuario() {}

    /** Retorna (ou cria) a única instância da sessão. */
    public static SessaoUsuario getInstance() {
        if (instance == null) {
            instance = new SessaoUsuario();
        }
        return instance;
    }

    // ── Controle de sessão /** Inicia a sessão com o usuário recém-autenticado. */

    public void iniciar(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    /** Encerra a sessão (chamado no logout). */
    public void encerrar() {
        this.usuarioLogado = null;
    }

    /** Retorna o usuário atualmente logado, ou null se não há sessão. */
    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    /** Atalho: retorna true se há um usuário logado. */
    public boolean estaLogado() {
        return usuarioLogado != null;
    }

    /* Atalho: retorna true se o usuário logado é ADMIN.
     * Retorna false se não há sessão ativa.*/
    public boolean usuarioEhAdmin() {
        return estaLogado() && usuarioLogado.isAdmin();
    }
}