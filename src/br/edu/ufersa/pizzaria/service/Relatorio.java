import br.edu.ufersa.pizzaria.model.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Relatorio {

    //lista de pedidos do sistema
    private List<Pedido> pedidos;

    //lista de estoques
    private List<Estoque> estoques;

    //construtor
    public Relatorio(List<Pedido> pedidos, List<Estoque> estoques) {
        this.pedidos = pedidos;
        this.estoques = estoques;
    }

    //gera pedidos entre duas datas (inicio e fim)
    public List<Pedido> gerarPorPeriodo(Date inicio, Date fim) {    
        List<Pedido> resultado = new ArrayList<>();

        //percorre todos os pedidos
        for (Pedido p : pedidos) {

            //verifica se a data do pedido está dentro do intervalo
            if (!p.getData().before(inicio) && !p.getData().after(fim)) { //os metodos before() e after() são da própria classe Date do java
                resultado.add(p);
            }
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
            if (p.getTipo().equals(pizza)) {
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

    //soma os gastos com estoque (mecherei nisso no futuro para automatizar com a class Pedido)
    public double gastosEstoques() {
        double total = 0;

        for (Estoque e : estoques) {
            total += e.getGastoTotal();
        }

        return total;
    }
}