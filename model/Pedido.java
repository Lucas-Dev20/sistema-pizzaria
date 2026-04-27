package br.edu.ufersa.pizzaria.model;

import java.time.LocalDate;
import java.util.List;

public class Pedido {

    private Cliente cliente;
    private Pizza pizza;
    private List<Adicional> adicionais;
    private String tamanho;
    private String estado;
    private LocalDate data;
    private double valorTotal;

    // Construtor
    public Pedido(Cliente cliente, Pizza pizza, List<Adicional> adicionais,
                  String tamanho, String estado, LocalDate data) {

        setCliente(cliente);
        setPizza(pizza);
        setAdicionais(adicionais);
        setTamanho(tamanho);
        setEstado(estado);
        setData(data);

        calcularTotal();
    }

    // Getters

    public Cliente getCliente() {
        return cliente;
    }

    public Pizza getPizza() {
        return pizza;
    }

    public List<Adicional> getAdicionais() {
        return adicionais;
    }

    public String getTamanho() {
        return tamanho;
    }

    public String getEstado() {
        return estado;
    }

    public LocalDate getData() {
        return data;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    // Setters

    public void setCliente(Cliente cliente) {
        if (cliente != null) {
            this.cliente = cliente;
        }
    }

    public void setPizza(Pizza pizza) {
        if (pizza != null) {
            this.pizza = pizza;
            calcularTotal();
        }
    }

    public void setAdicionais(List<Adicional> adicionais) {
        if (adicionais != null) {
            this.adicionais = adicionais;
            calcularTotal();
        }
    }

    public void setTamanho(String tamanho) {
        if (tamanho != null && !tamanho.trim().isEmpty()) {
            this.tamanho = tamanho;
            calcularTotal();
        }
    }

    public void setEstado(String estado) {
        if (estado != null && !estado.trim().isEmpty()) {
            this.estado = estado;
        }
    }

    public void setData(LocalDate data) {
        if (data != null) {
            this.data = data;
        }
    }

    // Para calcular

    public void calcularTotal() {

        double total = 0;

        // valor da pizza
        if (pizza != null) {
            total += pizza.getValor();
        }

        // adicionais (for-each)
        if (adicionais != null) {
            for (Adicional a : adicionais) {
                if (a != null) {
                    total += a.getValor();
                }
            }
        }

        // ajuste por tamanho
        if (tamanho != null) {
            if (tamanho.equalsIgnoreCase("M")) {
                total += 5;
            } else if (tamanho.equalsIgnoreCase("G")) {
                total += 10;
            }
        }

        this.valorTotal = total;
    }
}