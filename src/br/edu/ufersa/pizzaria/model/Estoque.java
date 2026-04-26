package br.edu.ufersa.pizzaria.model;
import java.util.ArrayList;
import java.util.List;

public class Estoque {
    private List<Adicional> adicionais;

    //construtor
    public Estoque(){
        adicionais = new ArrayList<>();
    }

    //adiciona um novo adicional
    public void add(Adicional adicional){
        adicionais.add(adicional);
    }

    //diminui estoque
    public void retirarDoEstoque(String nome, int qtd){
        for (Adicional a : adicionais){
            if (a.getNome().equalsIgnoreCase(nome)){  //esse .equalsIgnoreCase vai pegar todas as palavras que forem igual ao nome mesmo estando maisculo ou minusculo
                if (a.getQtd() >= qtd){
                    a.setQtd(a.getQtd() - qtd);
                } else {
                    System.out.println("Estoque insuficiente");
                }
            }
            return;
        }
        System.out.println("Adicional não encontrado");
    }

    //repor estoque
    public void reporEstoque(String nome, int qtd){
        for (Adicional a : adicionais){
            if (a.getNome().equalsIgnoreCase(nome)){
                a.setQtd(a.getQtd() + qtd);
                return;
            }
        }
        System.out.println("Adicional não encontrado");
    }

    //mostrar estoque
    public void mostrarEstoque(){
        for (Adicional a : adicionais){
            System.out.println(a.getNome() + " R$" + a.getValor() + " | Qtd: " + a.getQtd());
        }
    }
}