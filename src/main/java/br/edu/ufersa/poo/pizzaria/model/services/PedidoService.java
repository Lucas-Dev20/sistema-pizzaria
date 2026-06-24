package br.edu.ufersa.poo.pizzaria.model.services;

import br.edu.ufersa.poo.pizzaria.DAO.PedidoDAO;
import br.edu.ufersa.poo.pizzaria.model.entities.Adicional;
import br.edu.ufersa.poo.pizzaria.model.entities.Pedido;

import java.util.List;

public class PedidoService {
    private ObserverVenda observadorEstoque;
    private final PedidoDAO pedidoDAO = new PedidoDAO();

    private final AdicionalService adicionalService =
            new AdicionalService();

    // construtor para o observer
    public PedidoService(ObserverVenda observadorEstoque) {
        this.observadorEstoque = observadorEstoque;
    }

    // construtor vazio
    public PedidoService() {
    }

    // CADASTRAR PEDIDO
    public void cadastrarPedido(Pedido pedido) {

        if (pedido == null) {
            throw new IllegalArgumentException("Pedido não pode ser nulo.");
        }

        if (pedido.getCliente() == null) {
            throw new IllegalArgumentException("Cliente não informado.");
        }

        if (pedido.getPizza() == null) {
            throw new IllegalArgumentException("Pizza não informada.");
        }

        if (pedido.getTamanho() == null ||
                pedido.getTamanho().trim().isEmpty()) {

            throw new IllegalArgumentException("Tamanho não informado.");
        }

        if (pedido.getEstado() == null ||
                pedido.getEstado().trim().isEmpty()) {

            throw new IllegalArgumentException("Estado não informado.");
        }

        if (pedido.getData() == null) {
            throw new IllegalArgumentException("Data não informada.");
        }

        // salva pedido
        pedidoDAO.salvar(pedido);

        // REGRA DO TRABALHO:
        // ao vender pizza com adicionais,
        // baixa estoque automaticamente

        if (pedido.getAdicionais() != null) {

            for (Adicional adicional : pedido.getAdicionais()) {

                adicionalService.consumirEstoque(adicional.getIdAdicional(),
                        1);
            }
        }
    }

    // ATUALIZAR PEDIDO
    public void atualizarPedido(Pedido pedido) {

        if (pedido == null) {
            throw new IllegalArgumentException("Pedido inválido.");
        }

        if (pedido.getIdPedido() <= 0) {
            throw new IllegalArgumentException("ID do pedido inválido.");
        }

        pedidoDAO.atualizar(pedido);
    }

    // REMOVER PEDIDO
    public void removerPedido(int idPedido) {

        if (idPedido <= 0) {
            throw new IllegalArgumentException("ID inválido.");
        }

        pedidoDAO.remover(idPedido);
    }

    // BUSCAR POR ID
    public Pedido buscarPedidoPorId(int idPedido) {

        if (idPedido <= 0) {
            throw new IllegalArgumentException("ID inválido.");
        }

        return pedidoDAO.buscarPorId(idPedido);
    }

    // BUSCAR POR CLIENTE
    public List<Pedido> buscarPedidosPorCliente(int idCliente) {

        if (idCliente <= 0) {
            throw new IllegalArgumentException("ID do cliente inválido.");
        }

        return pedidoDAO.buscarPorCliente(idCliente);
    }

    // BUSCAR POR PIZZA
    public List<Pedido> buscarPedidosPorPizza(int idPizza) {

        if (idPizza <= 0) {
            throw new IllegalArgumentException("ID da pizza inválido.");
        }

        return pedidoDAO.buscarPorPizza(idPizza);
    }

    // BUSCAR POR ESTADO
    public List<Pedido> buscarPedidosPorEstado(String estado) {

        if (estado == null || estado.trim().isEmpty()) {

            throw new IllegalArgumentException("Estado inválido.");
        }

        return pedidoDAO.buscarPorEstado(estado);
    }

    // LISTAR TODOS
    public List<Pedido> listarTodosPedidos() {

        return pedidoDAO.listarTodos();
    }

    // ALTERAR ESTADO
    public void atualizarEstado(int idPedido, String novoEstado) {

        if (idPedido <= 0) {
            throw new IllegalArgumentException("ID inválido.");
        }

        if (novoEstado == null || novoEstado.trim().isEmpty()) {

            throw new IllegalArgumentException("Estado inválido.");
        }

        pedidoDAO.atualizarEstado(idPedido, novoEstado);
    }

    // FINALIZAR PEDIDO
    public void finalizarPedido(Pedido pedidoParametro) {
        if (pedidoParametro.getIdPedido() <= 0) {
            throw new IllegalArgumentException("ID inválido.");
        }

        // usei pedidoParametro para pegar o ID e salva em pedidoCompleto
        Pedido pedidoCompleto = pedidoDAO.buscarPorId(pedidoParametro.getIdPedido());

        if (pedidoCompleto == null) {
            throw new IllegalArgumentException("Pedido não encontrado.");
        }

        pedidoDAO.atualizarEstado(pedidoCompleto.getIdPedido(), "FINALIZADO");

        if (this.observadorEstoque != null) {
            this.observadorEstoque.notificarVendaFinalizada(pedidoCompleto);
        }
    }
}