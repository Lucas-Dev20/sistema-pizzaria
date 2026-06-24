package br.edu.ufersa.poo.pizzaria.controllers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class PainelController {

    @FXML
    void menuPedidos(ActionEvent event){
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/Pedidos.fxml", "La Piazza - Pedidos");
    }

    @FXML
    void menuClientes(ActionEvent event){
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/GerenciarClientesView.fxml", "La Piazza - Clientes");
    }

    @FXML
    void menuTiposPizzas(ActionEvent event){
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/GerenciarPizzasView.fxml", "La Piazza - Pizzas");
    }

    @FXML
    void menuAdicionais(ActionEvent event){
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/GerenciarAdicionaisView.fxml", "La Piazza - Adicionais");
    }

    @FXML
    void logout(ActionEvent event) {
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/LoginView.fxml", "La Piazza - Login");
    }
}
