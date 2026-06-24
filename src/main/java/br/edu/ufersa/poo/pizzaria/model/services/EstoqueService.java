package br.edu.ufersa.poo.pizzaria.model.services;

import br.edu.ufersa.poo.pizzaria.model.entities.Pedido;
import br.edu.ufersa.poo.pizzaria.model.entities.Adicional;
import br.edu.ufersa.poo.pizzaria.DAO.EstoqueDAO;

public class EstoqueService implements ObserverVenda {
    private EstoqueDAO estoqueDAO;

    public EstoqueService(EstoqueDAO estoqueDAO) {
            this.estoqueDAO = estoqueDAO;
    }

    @Override
    public void notificarVendaFinalizada(Pedido pedido) {
        System.out.println("[EstoqueService] Processando baixa de materiais...");
        System.out.println("\n[EstoqueService] Notificação recebida! O pedido #" + pedido.getIdPedido() + " foi finalizado.");
        System.out.println("[EstoqueService] Buscando adicionais consumidos...");

        // Varre a lista de adicionais do pedido e manda o DAO dar baixa de 1 unidade para cada um
        if (pedido.getAdicionais() != null) {
            for (Adicional adicional : pedido.getAdicionais()) {
                // Chama o metodo JDBC na classe EstoqueDAO
                estoqueDAO.baixarEstoque(adicional.getIdAdicional(), 1);
            }
        }
    }
}

