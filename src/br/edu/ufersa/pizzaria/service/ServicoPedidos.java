package br.edu.ufersa.pizzaria.service;

import java.util.ArrayList;
import java.util.List;
import br.edu.ufersa.pizzaria.model.*;


public class ServicoPedidos {


    private List<Pedido> pedidos;        // Armazenar pedidos do sistema
    private List<Estoque> estoques;     // Lista de estoques


    //construtor
    public ServicoPedidos() {
        this.pedidos = new ArrayList<>();
        this.estoques = new ArrayList<>();
    }


    // Cadastra um novo pedido
    public void cadastrarPedido(Pedido p) {
        if (p != null) {
            pedidos.add(p);
        }
    }


    // Edita um pedido
    public void editarPedido(Pedido p) {
        for (int i = 0; i < pedidos.size(); i++) {
            if (pedidos.get(i).equals(p)) {
                pedidos.set(i, p);
                break;
            }
        }
    }


    //Excluir pedido
    public void excluirPedido(Pedido p) {
        pedidos.remove(p);
    }


    // Buscar pedidos de um cliente
    public List<Pedido> buscarPorCliente(Cliente c) {
        List<Pedido> resultado = new ArrayList<>();
        for (Pedido p : pedidos) {
            if (p.getCliente().getCpf().equals(c.getCpf())) {
                resultado.add(p);
            }
        }
        return resultado;
    }


    // Buscar pedidos por tipo de pizza
    public List<Pedido> buscarPorPizza(Pizza p) {
        List<Pedido> resultado = new ArrayList<>();
        for (Pedido ped : pedidos) {
            if (ped.getPizza() != null && ped.getPizza().equals(p)) {
                resultado.add(ped);
            }
        }
        return resultado;
    }


    // Buscar pedidos por estado
    public List<Pedido> buscarPorEstado(String e) {
        List<Pedido> resultado = new ArrayList<>();
        for (Pedido p : pedidos) {
            if (p.getEstado() != null && p.getEstado().equalsIgnoreCase(e)) {
                resultado.add(p);
            }
        }
        return resultado;
    }


    // Finalizae um pedido e atualizar o estoque
    public void finalizarPedido(Pedido pedido) {
        if (pedido != null) {
            pedido.setEstado("Finalizado");


            if (pedido.getAdicionais() != null) {


                for (Adicional a : pedido.getAdicionais()) {


                    for (Estoque e : estoques) {


                        e.retirarDoEstoque(a.getNome(), 1);


                    }
                }
            }
        }
    }}

