package br.edu.ufersa.poo.pizzaria.model.services;

import br.edu.ufersa.poo.pizzaria.DAO.PizzaDAO;
import br.edu.ufersa.poo.pizzaria.model.entities.Pizza;
import br.edu.ufersa.poo.pizzaria.model.entities.Usuario;

import java.util.List;

public class PizzaService {

    private final PizzaDAO pizzaDAO = new PizzaDAO();

    // Apenas administrador pode cadastrar pizzas
    public void cadastrarPizza(String tipo,
                               double valor,
                               Usuario usuario) {

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "Usuário não informado."
            );
        }

        if (!usuario.getCargo().equalsIgnoreCase("admin")) {
            throw new IllegalArgumentException(
                    "Apenas administradores podem cadastrar pizzas."
            );
        }

        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Tipo da pizza não pode ser vazio."
            );
        }

        if (valor <= 0) {
            throw new IllegalArgumentException(
                    "Valor da pizza deve ser maior que zero."
            );
        }

        Pizza pizza = new Pizza(tipo, valor);

        pizzaDAO.salvar(pizza);
    }

    public void atualizarPizza(Pizza pizza) {

        if (pizza == null) {
            throw new IllegalArgumentException(
                    "Pizza inválida."
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