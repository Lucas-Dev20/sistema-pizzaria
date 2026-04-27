package br.edu.ufersa.pizzaria.model;

public class Pizza
{
    private String tipo;
    private double valor;

    // Construtor
    public Pizza (String tipo, double valor){
        setTipo(tipo);
        setValor(valor);
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        if (valor > 0) {
            this.valor = valor;
        }
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        if (tipo != null && !tipo.trim().isEmpty()) {
            this.tipo = tipo;
        }
    }

}
