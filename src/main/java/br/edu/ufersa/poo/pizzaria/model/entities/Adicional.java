package br.edu.ufersa.poo.pizzaria.model.entities;

//representa um adicional para a pizza (ex: queijo, borda...)
public class Adicional {
    private int idAdicional; // NOVO ATRIBUTO
    private String nome;    //nome do adicional
    private double valor;   //valor do adicional
    private int quantidade; //quantidade do adicional


    public Adicional(int idAdicional, String nome, double valor, int quantidade) {
        setIdAdicional(idAdicional);
        this.nome = nome;
        this.valor = valor;
        this.quantidade = quantidade;
    }

    //construtor
    public Adicional(String nome, double valor, int quantidade){
        this.nome = nome;
        this.valor = valor;
        this.quantidade = quantidade;
    }

    //retorna o idAdicional
    public int getIdAdicional() {return idAdicional;}


    //retorna o nome
    public String getNome(){
        return nome;
    }

    //retorna o valor
    public double getValor(){
        return valor;
    }

    //retorna a quantidade
    public int getQtd(){
        return quantidade;
    }

    public void setIdAdicional(int idAdicional) {
        if (idAdicional > 0) {
            this.idAdicional = idAdicional;
        }
    }

    //adicionar quantidade se o valor não for negativo
    public void setQtd(int quantidade){
        if (quantidade < 0){
            throw new IllegalArgumentException(
                    "Quantidade não pode ser negativa."
            );
        }

        this.quantidade = quantidade;
    }

    //ira calcular o valor do adicional (preço * quantidade)
    public double getTotal() {
        return valor * quantidade;
    }
}
