package br.edu.ufersa.poo.pizzaria.model.entities;

public class Pizza {

    private int idPizza;
    private String tipo;
    private double valor;

    // Construtor completo
    public Pizza(int idPizza, String tipo, double valor) {
        setIdPizza(idPizza);
        setTipo(tipo);
        setValor(valor);
    }

    // Construtor sem ID
    public Pizza(String tipo, double valor) {
        setTipo(tipo);
        setValor(valor);
    }

    public int getIdPizza() {
        return idPizza;
    }

    public void setIdPizza(int idPizza) {
        if (idPizza > 0) {
            this.idPizza = idPizza;
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

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        if (valor > 0) {
            this.valor = valor;
        }
    }

    @Override
    public String toString() {
        return "Pizza{" +
                "idPizza=" + idPizza +
                ", tipo='" + tipo + '\'' +
                ", valor=" + valor +
                '}';
    }
}