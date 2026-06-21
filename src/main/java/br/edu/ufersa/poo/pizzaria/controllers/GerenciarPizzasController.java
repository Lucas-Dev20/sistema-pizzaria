package br.edu.ufersa.poo.pizzaria.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

public class GerenciarPizzasController {

    @FXML private Label lblTipoUsuario;
    @FXML private TextField txtBusca;
    @FXML private Button btnNovoSabor;
    @FXML private TilePane containerPizzas;

    // analise do tipo de login para o 'poder' de cadastrar um novo sabor ser permitido ou nao
    private boolean ehAdministrador = true;

    @FXML
    public void initialize() {
        configurarNivelAcesso();
        renderizarCardsPizza();
    }

    private void configurarNivelAcesso() {
        if (ehAdministrador) {
            lblTipoUsuario.setText("Administrador");
            btnNovoSabor.setVisible(true);
            btnNovoSabor.setManaged(true); // faz o layout reajustar o espaço se sumir
        } else {
            lblTipoUsuario.setText("Funcionário");
            btnNovoSabor.setVisible(false);
            btnNovoSabor.setManaged(false); // tira o espaço morto do botão ocultado, por n ser admin
        }
    }

    private void renderizarCardsPizza() {
        containerPizzas.getChildren().clear();

        //um loop no bd será feito para mostrar as pizzas salvas
        System.out.println("Carregando catálogo de sabores do banco...");
    }

    @FXML
    private void handleNovoSabor(ActionEvent event) {
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/CadastrarSaborView.fxml", "La Piazza - Cadastrar Sabor");
    }

    @FXML
    private void handleIrClientes(ActionEvent event) {
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/GerenciarClientesView.fxml", "La Piazza - Clientes");
    }

    @FXML
    private void handleSair(ActionEvent event) {
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/LoginView.fxml", "La Piazza - Login");
    }

    public void handleExcluirSabor(String nomeSabor, int idPizza) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText(null);
        alert.setContentText("Deseja realmente excluir o sabor " + nomeSabor + "?");

        ButtonType btnExcluir = new ButtonType("Excluir");
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnCancelar, btnExcluir);

        alert.showAndWait().ifPresent(resposta -> {
            if (resposta == btnExcluir) {
                try {
                    mostrarMensagemInformativa("Sucesso", "Sabor excluído com sucesso!");
                    renderizarCardsPizza();
                } catch (Exception e) {
                    mostrarMensagemErro("Erro", "Falha ao remover o sabor do banco de dados.");
                    e.printStackTrace();
                }
            }
        });
    } //estilização para caixa de exclusao de sabor

    private void mostrarMensagemInformativa(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void mostrarMensagemErro(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}