package br.edu.ufersa.poo.pizzaria.model.services;

import br.edu.ufersa.poo.pizzaria.DAO.PedidoDAO;
import br.edu.ufersa.poo.pizzaria.model.entities.Pedido;

import java.util.List;

public class PedidoService {

    private ObserverVenda observadorEstoque;
    private final PedidoDAO pedidoDAO = new PedidoDAO();

    // ── Construtor com Observer - EstoqueService é passado como Observer e baixa o estoque ao cadastrar
    public PedidoService(ObserverVenda observadorEstoque) {
        this.observadorEstoque = observadorEstoque;
    }

    // ── Construtor sem Observer - usado pelo RelatorioController
    public PedidoService() {}

    // ── CADASTRAR
    public void cadastrarPedido(Pedido pedido) {

        if (pedido == null)
            throw new IllegalArgumentException("Pedido não pode ser nulo.");
        if (pedido.getCliente() == null)
            throw new IllegalArgumentException("Cliente não informado.");
        if (pedido.getPizza() == null)
            throw new IllegalArgumentException("Pizza não informada.");
        if (pedido.getTamanho() == null || pedido.getTamanho().trim().isEmpty())
            throw new IllegalArgumentException("Tamanho não informado.");
        if (pedido.getEstado() == null || pedido.getEstado().trim().isEmpty())
            throw new IllegalArgumentException("Estado não informado.");
        if (pedido.getData() == null)
            throw new IllegalArgumentException("Data não informada.");
        if (pedido.getFormaPagamento() == null || pedido.getFormaPagamento().trim().isEmpty())
            throw new IllegalArgumentException("Forma de pagamento não informada.");

        // Calcula o total antes de salvar (tamanho + adicionais)
        pedido.calcularTotal();

        // Persiste no banco
        pedidoDAO.salvar(pedido);

        // observer: notifica o EstoqueService para baixar o estoque dos adicionais
        if (this.observadorEstoque != null) {
            this.observadorEstoque.notificarVendaFinalizada(pedido);
        }
    }

    // ── ATUALIZAR
    public void atualizarPedido(Pedido pedido) {

        if (pedido == null)
            throw new IllegalArgumentException("Pedido inválido.");
        if (pedido.getIdPedido() <= 0)
            throw new IllegalArgumentException("ID do pedido inválido.");
        if (pedido.getFormaPagamento() == null || pedido.getFormaPagamento().trim().isEmpty())
            throw new IllegalArgumentException("Forma de pagamento não informada.");

        // Recalcula o total ao editar (usuário pode ter mudado tamanho ou adicionais)
        pedido.calcularTotal();

        pedidoDAO.atualizar(pedido);
    }

    // ── REMOVER
    public void removerPedido(int idPedido) {
        if (idPedido <= 0)
            throw new IllegalArgumentException("ID inválido.");
        pedidoDAO.remover(idPedido);
    }

    // ── BUSCAR POR ID
    public Pedido buscarPedidoPorId(int idPedido) {
        if (idPedido <= 0)
            throw new IllegalArgumentException("ID inválido.");
        return pedidoDAO.buscarPorId(idPedido);
    }

    // ── BUSCAR POR CLIENTE
    public List<Pedido> buscarPedidosPorCliente(int idCliente) {
        if (idCliente <= 0)
            throw new IllegalArgumentException("ID do cliente inválido.");
        return pedidoDAO.buscarPorCliente(idCliente);
    }

    // ── BUSCAR POR PIZZA
    public List<Pedido> buscarPedidosPorPizza(int idPizza) {
        if (idPizza <= 0)
            throw new IllegalArgumentException("ID da pizza inválido.");
        return pedidoDAO.buscarPorPizza(idPizza);
    }

    // ── BUSCAR POR ESTADO
    public List<Pedido> buscarPedidosPorEstado(String estado) {
        if (estado == null || estado.trim().isEmpty())
            throw new IllegalArgumentException("Estado inválido.");
        return pedidoDAO.buscarPorEstado(estado);
    }

    // ── LISTAR TODOS
    public List<Pedido> listarTodosPedidos() {
        return pedidoDAO.listarTodos();
    }

    // ── ALTERAR ESTADO
    public void atualizarEstado(int idPedido, String novoEstado) {
        if (idPedido <= 0)
            throw new IllegalArgumentException("ID inválido.");
        if (novoEstado == null || novoEstado.trim().isEmpty())
            throw new IllegalArgumentException("Estado inválido.");
        pedidoDAO.atualizarEstado(idPedido, novoEstado);
    }

    // ── FINALIZAR
    public void finalizarPedido(Pedido pedido) {
        if (pedido.getIdPedido() <= 0)
            throw new IllegalArgumentException("ID inválido.");
        Pedido completo = pedidoDAO.buscarPorId(pedido.getIdPedido());
        if (completo == null)
            throw new IllegalArgumentException("Pedido não encontrado.");
        pedidoDAO.atualizarEstado(completo.getIdPedido(), "Entregue");
    }
}