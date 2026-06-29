package br.edu.ufersa.poo.pizzaria.model.entities;

public class Pizza {

    private int idPizza;
    private String tipo;
    private double valorPequena;
    private double valorMedia;
    private double valorGrande;

    // Construtor completo
    public Pizza(int idPizza, String tipo, double valorPequena, double valorMedia, double valorGrande) {
        setIdPizza(idPizza);
        setTipo(tipo);
        setValorPequena(valorPequena);
        setValorMedia(valorMedia);
        setValorGrande(valorGrande);
    }

    // Construtor sem ID
    public Pizza(String tipo, double valorPequena, double valorMedia, double valorGrande) {
        setTipo(tipo);
        setValorPequena(valorPequena);
        setValorMedia(valorMedia);
        setValorGrande(valorGrande);
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

    public double getValorPequena() {
        return valorPequena;
    }

    public void setValorPequena(double valorPequena) {
        if (valorPequena > 0) {
            this.valorPequena = valorPequena;
        }
    }

    public double getValorMedia() {
        return valorMedia;
    }

    public void setValorMedia(double valorMedia) {
        if (valorMedia > 0) {
            this.valorMedia = valorMedia;
        }
    }

    public double getValorGrande() {
        return valorGrande;
    }

    public void setValorGrande(double valorGrande) {
        if (valorGrande > 0) {
            this.valorGrande = valorGrande;
        }
    }

    @Override
    public String toString() {
        return "Pizza{" +
                "idPizza=" + idPizza +
                ", tipo='" + tipo + '\'' +
                ", valorPequena=" + valorPequena +
                ", valorMedia=" + valorMedia +
                ", valorGrande=" + valorGrande +
                '}';
    }
}