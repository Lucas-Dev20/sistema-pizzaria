package br.edu.ufersa.poo.pizzaria.model.entities;

/*
 1. Classe abstrata (com método abstrato getDescricao)
 2. Herança: Cliente e Usuario estendem Pessoa → polimorfismo
 */
public abstract class Pessoa {

    private String nome;

    // ── Construtor ────────────────────────────────────────────────────────────
    public Pessoa(String nome) {
        this.nome = nome;
    }

    public abstract String getDescricao();

    // ── Getter e Setter ───────────────────────────────────────────────────────
    public String getNome()           { return nome; }
    public void setNome(String nome)  { this.nome = nome; }

    @Override
    public String toString() {
        return getDescricao();   // polimorfismo em ação
    }
}