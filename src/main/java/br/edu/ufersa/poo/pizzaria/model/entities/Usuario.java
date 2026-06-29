package br.edu.ufersa.poo.pizzaria.model.entities;

public class Usuario extends Pessoa {

    private int          idUsuario;
    private String       email;
    private String       senhaHash;   // armazenada com hash
    private PerfilUsuario perfil;
    private boolean      ativo;

    // ── Construtor completo -  buscar do banco
    public Usuario(int idUsuario, String nome, String email,
                   String senhaHash, PerfilUsuario perfil, boolean ativo) {
        super(nome);           // chama construtor de Pessoa
        this.idUsuario = idUsuario;
        this.email     = email;
        this.senhaHash = senhaHash;
        this.perfil    = perfil;
        this.ativo     = ativo;
    }

    // ── Construtor sem id - usado ao cadastrar novo usuário
    public Usuario(String nome, String email, String senhaHash, PerfilUsuario perfil) {
        super(nome);
        this.email     = email;
        this.senhaHash = senhaHash;
        this.perfil    = perfil;
        this.ativo     = true;
    }

    // ── Polimorfismo: sobrescreve metodo abstrato de Pessoa
    @Override
    public String getDescricao() {
        return "Usuário [" + perfil + "]: " + getNome() + " <" + email + ">";
    }

    // ── Helpers de autorização

    /* Retorna true se este usuário é administrador. */
    public boolean isAdmin() {
        return PerfilUsuario.ADMIN.equals(this.perfil);
    }

    /* Retorna true se este usuário é funcionário comum. */
    public boolean isFuncionario() {
        return PerfilUsuario.FUNCIONARIO.equals(this.perfil);
    }

    // ── Getters e Setters

    public int getIdUsuario()             { return idUsuario; }
    public void setIdUsuario(int id)      { this.idUsuario = id; }

    public String getEmail()              { return email; }
    public void setEmail(String email)    { this.email = email; }

    public String getSenhaHash()          { return senhaHash; }
    public void setSenhaHash(String h)    { this.senhaHash = h; }

    public PerfilUsuario getPerfil()                  { return perfil; }
    public void setPerfil(PerfilUsuario perfil)       { this.perfil = perfil; }

    public boolean isAtivo()              { return ativo; }
    public void setAtivo(boolean ativo)   { this.ativo = ativo; }

    @Override
    public String toString() {
        return getNome() + " (" + perfil + ")";
    }
}