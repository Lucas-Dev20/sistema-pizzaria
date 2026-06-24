package br.edu.ufersa.poo.pizzaria.model.services;
import br.edu.ufersa.poo.pizzaria.model.entities.Pedido;

public interface ObserverVenda {
    void notificarVendaFinalizada(Pedido pedido);
}
