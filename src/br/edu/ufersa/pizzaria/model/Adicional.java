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

    //adicionar quantidade se o valor não for negativo
    public void setQtd(int quantidade){
        if (quantidade <= 0) {
            System.out.println("Quantidade deve ser positiva");
        } 
        this.quantidade = quantidade;
    }

    //ira calcular o valor do adicional (preço * quantidade)
    public double getTotal() {
        return valor * quantidade;
    }
}
