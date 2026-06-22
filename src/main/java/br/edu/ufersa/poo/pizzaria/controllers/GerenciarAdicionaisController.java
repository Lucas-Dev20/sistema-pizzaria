package br.edu.ufersa.poo.pizzaria.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class GerenciarAdicionaisController {

    @FXML private TextField txtBusca;
    @FXML private Button btnNovoAdicional;
    @FXML private TableView<?> tabelaAdicionais; // Ajuste o tipo <?> para sua entidade Adicional depois
    @FXML private TableColumn<?, String> colNome;
    @FXML private TableColumn<?, Double> colPreco;
    @FXML private TableColumn<?, String> colEstoque;
    @FXML private TableColumn<?, Void> colAcoes;

    @FXML
    public void initialize() {
        // Inicializa mapeamento das colunas e carrega dados
        atualizarTabela();
    }

    private void atualizarTabela() {
        System.out.println("Buscando adicionais do banco de dados...");
    }

    @FXML
    private void handleNovoAdicional(ActionEvent event) {
        System.out.println("Abrindo tela de novo adicional...");
    }
//METODOS DE NAVEGAÇÃO
    @FXML
    private void irPedidos(ActionEvent event) {
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/GerenciarPedidosView.fxml", "La Piazza - Pedidos");
    }

    @FXML
    private void irClientes(ActionEvent event) {
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/GerenciarClientesView.fxml", "La Piazza - Clientes");
    }

    @FXML
    private void irTiposPizza(ActionEvent event) {
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/GerenciarPizzasView.fxml", "La Piazza - Tipos de Pizza");
    }

    @FXML
    private void irAdicionais(ActionEvent event) {
        atualizarTabela();
    }

    @FXML
    private void irEstoque(ActionEvent event) {
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/GerenciarEstoqueView.fxml", "La Piazza - Estoque");
    }

    @FXML
    private void irRelatorios(ActionEvent event) {
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/GerenciarRelatoriosView.fxml", "La Piazza - Relatórios");
    }

    @FXML
    private void sair(ActionEvent event) {
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/LoginView.fxml", "La Piazza Pizzaria");
    }
}