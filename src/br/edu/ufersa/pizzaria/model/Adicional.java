package br.edu.ufersa.pizzaria.model;

//representa um adicional para a pizza (ex: queijo, borda...)
public class Adicional {
    private String nome;    //nome do adicional
    private double valor;   //valor do adicional
    private int quantidade; //quantidade do adicional

    //construtor
    public Adicional(String nome, double valor, int quantidade){
        this.nome = nome;
        this.valor = valor;
        this.quantidade = quantidade;
    }

    //retorna o nome do adicional
    public String getNome(){
        return nome;
    }

    //retorna o valor do adicional
    public double getValor(){
        return valor;
    }

    public int getQtd(){
        return quantidade;
    }

    public void setQtd(int quantidade){
        this.quantidade = quantidade;
    }
}
