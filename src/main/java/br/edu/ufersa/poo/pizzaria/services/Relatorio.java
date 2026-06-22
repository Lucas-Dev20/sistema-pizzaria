package br.edu.ufersa.poo.pizzaria.services;
import br.edu.ufersa.poo.pizzaria.model.entities.*;
import br.edu.ufersa.poo.pizzaria.DAO.ReposicaoEstoqueDAO;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;

public class Relatorio {

    //lista de pedidos do sistema
    private List<Pedido> pedidos;

    //lista de estoques
    private List<Estoque> estoques;

    private final ReposicaoEstoqueDAO reposicaoDAO = new ReposicaoEstoqueDAO();

    //construtor
    public Relatorio(List<Pedido> pedidos, List<Estoque> estoques) {
        this.pedidos = pedidos;
        this.estoques = estoques;
    }

    //gera pedidos entre duas datas (inicio e fim)
    public List<Pedido> gerarPorPeriodo(LocalDate inicio, LocalDate fim) {
        List<Pedido> resultado = new ArrayList<>();

        for (Pedido p : pedidos) {

            if (!p.getData().isBefore(inicio) && !p.getData().isAfter(fim)) {
                resultado.add(p);}
        }
        return resultado;
    }

    //filtra pedidos por cliente
    public List<Pedido> gerarPorCliente(Cliente cliente) {
        List<Pedido> resultado = new ArrayList<>();

        for (Pedido p : pedidos) {

            //compara o cliente do pedido com o cliente informado
            if (p.getCliente().equals(cliente)) {
                resultado.add(p);
            }
        }

        return resultado;
    }

    //filtra pedidos por pizza
    public List<Pedido> gerarPorPizza(Pizza pizza) {
        List<Pedido> resultado = new ArrayList<>();

        for (Pedido p : pedidos) {

            //filtra por tipo de pizza (ex: calabresa, nordestina)
            if (p.getPizza().getTipo().equalsIgnoreCase(pizza.getTipo())) {
                resultado.add(p);
            }
        }

        return resultado;
    }

    //filtra pedidos por estado (ex: "ENTREGUE", "CANCELADO")
    public List<Pedido> gerarPorEstado(String estado) {
        List<Pedido> resultado = new ArrayList<>();

        for (Pedido p : pedidos) {

            //compara ignorando maiúsculas/minúsculas
            if (p.getEstado().equalsIgnoreCase(estado)) {
                resultado.add(p);
            }
        }

        return resultado;
    }

    //soma o valor total de todos os pedidos
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