package br.edu.ufersa.poo.pizzaria.model.services;

import br.edu.ufersa.poo.pizzaria.DAO.PedidoDAO;
import br.edu.ufersa.poo.pizzaria.model.entities.Pedido;

import java.util.ArrayList;
import java.util.List;

public class PedidoService {

    /*
     * PADRÃO OBSERVER (GoF, Cap. 5, pág. 293) — implementação canônica.
     *
     * Antes: um único campo "ObserverVenda observadorEstoque"
     *        → só um observer possível, não é GoF de verdade.
     *
     * Agora: lista de observers + addObserver() / removeObserver()
     *        → qualquer número de observers pode ser registrado,
     *          o Subject (PedidoService) não sabe quem são os concretos.
     */
    private final List<ObserverVenda> observers = new ArrayList<>();
    private final PedidoDAO pedidoDAO = new PedidoDAO();

    // ── Construtor padrão (observers registrados via addObserver)
    public PedidoService() {}

    // ── Registro de observers (GoF: attach / detach) ──────────────────────────
    public void addObserver(ObserverVenda observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(ObserverVenda observer) {
        observers.remove(observer);
    }

    // ── Notificação (GoF: notify) ─────────────────────────────────────────────
    private void notificarObservers(Pedido pedido) {
        for (ObserverVenda observer : observers) {
            observer.notificarVendaFinalizada(pedido);
        }
    }

    // ── CADASTRAR ─────────────────────────────────────────────────────────────
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

        pedido.calcularTotal();
        pedidoDAO.salvar(pedido);

        // Notifica todos os observers registrados (ex: EstoqueService baixa o estoque)
        notificarObservers(pedido);
    }

    // ── ATUALIZAR ─────────────────────────────────────────────────────────────
    public void atualizarPedido(Pedido pedido) {

        if (pedido == null)
            throw new IllegalArgumentException("Pedido inválido.");
        if (pedido.getIdPedido() <= 0)
            throw new IllegalArgumentException("ID do pedido inválido.");
        if (pedido.getFormaPagamento() == null || pedido.getFormaPagamento().trim().isEmpty())
            throw new IllegalArgumentException("Forma de pagamento não informada.");

        pedido.calcularTotal();
        pedidoDAO.atualizar(pedido);
    }

    // ── REMOVER ───────────────────────────────────────────────────────────────
    public void removerPedido(int idPedido) {
        if (idPedido <= 0)
            throw new IllegalArgumentException("ID inválido.");
        pedidoDAO.remover(idPedido);
    }

    // ── BUSCAR POR ID ─────────────────────────────────────────────────────────
    public Pedido buscarPedidoPorId(int idPedido) {
        if (idPedido <= 0)
            throw new IllegalArgumentException("ID inválido.");
        return pedidoDAO.buscarPorId(idPedido);
    }

    // ── BUSCAR POR CLIENTE ────────────────────────────────────────────────────
    public List<Pedido> buscarPedidosPorCliente(int idCliente) {
        if (idCliente <= 0)
            throw new IllegalArgumentException("ID do cliente inválido.");
        return pedidoDAO.buscarPorCliente(idCliente);
    }

    // ── BUSCAR POR PIZZA ──────────────────────────────────────────────────────
    public List<Pedido> buscarPedidosPorPizza(int idPizza) {
        if (idPizza <= 0)
            throw new IllegalArgumentException("ID da pizza inválido.");
        return pedidoDAO.buscarPorPizza(idPizza);
    }

    // ── BUSCAR POR ESTADO ─────────────────────────────────────────────────────
    public List<Pedido> buscarPedidosPorEstado(String estado) {
        if (estado == null || estado.trim().isEmpty())
            throw new IllegalArgumentException("Estado inválido.");
        return pedidoDAO.buscarPorEstado(estado);
    }

    // ── LISTAR TODOS ──────────────────────────────────────────────────────────
    public List<Pedido> listarTodosPedidos() {
        return pedidoDAO.listarTodos();
    }

    // ── ALTERAR ESTADO ────────────────────────────────────────────────────────
    public void atualizarEstado(int idPedido, String novoEstado) {
        if (idPedido <= 0)
            throw new IllegalArgumentException("ID inválido.");
        if (novoEstado == null || novoEstado.trim().isEmpty())
            throw new IllegalArgumentException("Estado inválido.");
        pedidoDAO.atualizarEstado(idPedido, novoEstado);
    }

    // ── FINALIZAR ─────────────────────────────────────────────────────────────
    public void finalizarPedido(Pedido pedido) {
        if (pedido.getIdPedido() <= 0)
            throw new IllegalArgumentException("ID inválido.");
        Pedido completo = pedidoDAO.buscarPorId(pedido.getIdPedido());
        if (completo == null)
            throw new IllegalArgumentException("Pedido não encontrado.");
        pedidoDAO.atualizarEstado(completo.getIdPedido(), "Entregue");
    }
}