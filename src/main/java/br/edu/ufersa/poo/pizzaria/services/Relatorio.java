package br.edu.ufersa.poo.pizzaria.services;

import br.edu.ufersa.poo.pizzaria.model.entities.*;
import br.edu.ufersa.poo.pizzaria.DAO.ReposicaoEstoqueDAO;
import br.edu.ufersa.poo.pizzaria.DAO.PedidoDAO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Relatorio {

    private List<Pedido> pedidos;
    private List<Estoque> estoques;

    private final ReposicaoEstoqueDAO reposicaoDAO = new ReposicaoEstoqueDAO();

    public Relatorio(List<Pedido> pedidos, List<Estoque> estoques) {
        // busca pedidos do banco — ignora a lista em memória do ServicoPedidos que esvazia ao reiniciar
        PedidoDAO pedidoDAO = new PedidoDAO();
        List<Pedido> pedidosBanco = pedidoDAO.listarTodos();

        this.pedidos  = (pedidosBanco != null && !pedidosBanco.isEmpty()) ? pedidosBanco : pedidos;
        this.estoques = estoques; // estoque não é usado nos cálculos do relatório
    }

    public List<Pedido> gerarPorPeriodo(LocalDate inicio, LocalDate fim) {
        List<Pedido> resultado = new ArrayList<>();
        for (Pedido p : pedidos) {
            if (!p.getData().isBefore(inicio) && !p.getData().isAfter(fim)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public List<Pedido> gerarPorCliente(Cliente cliente) {
        List<Pedido> resultado = new ArrayList<>();
        for (Pedido p : pedidos) {
            if (p.getCliente().equals(cliente)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public List<Pedido> gerarPorPizza(Pizza pizza) {
        List<Pedido> resultado = new ArrayList<>();
        for (Pedido p : pedidos) {
            if (p.getPizza().getTipo().equalsIgnoreCase(pizza.getTipo())) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public List<Pedido> gerarPorEstado(String estado) {
        List<Pedido> resultado = new ArrayList<>();
        for (Pedido p : pedidos) {
            if (p.getEstado().equalsIgnoreCase(estado)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public double totalVendas() {
        double total = 0;
        for (Pedido p : pedidos) {
            total += p.getValorTotal();
        }
        return total;
    }

    public double gastosEstoques() {
        return reposicaoDAO.calcularGastosReposicao();
    }

    public double calcularLucro() {
        return totalVendas() - gastosEstoques();
    }
}
