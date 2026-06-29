package br.edu.ufersa.poo.pizzaria.model.services;

import br.edu.ufersa.poo.pizzaria.DAO.PizzaDAO;
import br.edu.ufersa.poo.pizzaria.exceptions.AcessoNegadoException;
import br.edu.ufersa.poo.pizzaria.model.entities.Pizza;
import br.edu.ufersa.poo.pizzaria.model.entities.Usuario;

import java.util.List;

public class PizzaService {

    private final PizzaDAO pizzaDAO = new PizzaDAO();

    // Apenas administrador pode cadastrar pizzas
    public void cadastrarPizza(String tipo,
                               double precoPequena,
                               double precoMedia,
                               double precoGrande,
                               Usuario usuario) {

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "Usuário não informado."
            );
        }


        if (!usuario.isAdmin()) {
            throw new AcessoNegadoException("cadastrar tipo de pizza", usuario.getPerfil().name());
        }

        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Tipo da pizza não pode ser vazio."
            );
        }

        if (precoPequena <= 0 || precoMedia <= 0 || precoGrande <= 0) {
            throw new IllegalArgumentException(
                    "Os preços devem ser maiores que zero."
            );
        }

        if (precoPequena >= precoMedia || precoMedia >= precoGrande) {
            throw new IllegalArgumentException(
                    "Os preços devem ser: Pequena < Média < Grande."
            );
        }

        Pizza pizza = new Pizza(tipo, precoPequena, precoMedia, precoGrande);

        pizzaDAO.salvar(pizza);
    }

    public void atualizarPizza(Pizza pizza) {

        if (pizza == null) {
            throw new IllegalArgumentException(
                    "Pizza inválida."
            );
        }

        if (pizza.getValorPequena() >= pizza.getValorMedia() || pizza.getValorMedia() >= pizza.getValorGrande()) {
            throw new IllegalArgumentException(
                    "Os preços devem ser: Pequena < Média < Grande."
            );
        }

        if (pizza.getIdPizza() <= 0) {
            throw new IllegalArgumentException(
                    "ID da pizza inválido."
            );
        }

        pizzaDAO.atualizar(pizza);
    }

    public void removerPizza(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "ID inválido."
            );
        }

        pizzaDAO.remover(id);
    }

    public Pizza buscarPizzaPorId(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "ID inválido."
            );
        }

        return pizzaDAO.buscarPorId(id);
    }

    public List<Pizza> buscarPizzaPorTipo(String tipo) {

        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Tipo inválido."
            );
        }

        return pizzaDAO.buscarPorTipo(tipo);
    }

    public List<Pizza> listarTodasPizzas() {

        return pizzaDAO.listarTodas();
    }
}