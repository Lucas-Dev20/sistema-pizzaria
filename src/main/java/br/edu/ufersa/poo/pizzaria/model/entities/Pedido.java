package br.edu.ufersa.poo.pizzaria.model.entities;

import java.time.LocalDate;
import java.util.List;


public class Pedido {

    private int idPedido;
    private Cliente cliente;
    private Pizza pizza;
    private List<Adicional> adicionais;
    private String tamanho;
    private String estado;
    private LocalDate data;
    private double valorTotal;
    private String formaPagamento;

    // Construtor completo
    public Pedido(int idPedido, Cliente cliente, Pizza pizza, List<Adicional> adicionais,
                  String tamanho, String estado, LocalDate data) {

        setIdPedido(idPedido);
        setCliente(cliente);
        setPizza(pizza);
        setAdicionais(adicionais);
        setTamanho(tamanho);
        setEstado(estado);
        setData(data);

        calcularTotal();
    }

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


    public int getIdPedido() {return idPedido;}


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

    public String getFormaPagamento() { return formaPagamento; }

    // Setters

    public void setIdPedido(int idPedido) {

        if (idPedido > 0) {
            this.idPedido = idPedido;
        }
    }

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

    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }

    // Para calcular


    public void calcularTotal() {

        if (pizza == null) {
            this.valorTotal = 0;
            return;
        }

        // O banco agora armazena os 3 preços reais por tamanho.
        // Escolhe o preço real correspondente ao tamanho do pedido.
        double total;

        if (tamanho != null) {
            total = switch (tamanho.trim()) {
                case "Pequena", "P", "p" -> pizza.getValorPequena();
                case "Grande",  "G", "g" -> pizza.getValorGrande();
                default -> pizza.getValorMedia();
            };
        } else {
            total = pizza.getValorMedia();
        }

        // soma valor de cada adicional selecionado
        if (adicionais != null) {
            for (Adicional a : adicionais) {
                if (a != null) {
                    total += a.getValor();
                }
            }
        }

        this.valorTotal = total;
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "idPedido=" + idPedido +
                ", cliente=" + (cliente != null ? cliente.getNome() : "null") +
                ", pizza=" + (pizza != null ? pizza.getTipo() : "null") +
                ", adicionais=" + (adicionais != null ? adicionais.size() : 0) +
                ", tamanho='" + tamanho + '\'' +
                ", estado='" + estado + '\'' +
                ", data=" + data +
                ", valorTotal=" + valorTotal +
                '}';
    }
}