package br.edu.ufersa.poo.pizzaria.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class EditarSaborController {

    @FXML private TextField txtSabor;
    @FXML private TextField txtIngredientes;
    @FXML private TextField txtPrecoPequena;
    @FXML private TextField txtPrecoMedia;
    @FXML private TextField txtPrecoGrande;

    // private PizzaService pizzaService = new PizzaService();
    // private Pizza pizzaEmEdicao;

    @FXML
    public void initialize() {}

    // metodo vai ser chamado pelo container de pizzas ao clicar no botão de editar - lapis -
    public void preencherCampos(String sabor, String ingredientes, double pPequena, double pMedia, double pGrande) {
        txtSabor.setText(sabor);
        txtIngredientes.setText(ingredientes);
        txtPrecoPequena.setText(String.valueOf(pPequena));
        txtPrecoMedia.setText(String.valueOf(pMedia));
        txtPrecoGrande.setText(String.valueOf(pGrande));
    }

    @FXML
    private void handleSalvar(ActionEvent event) {
        try {
            String sabor = txtSabor.getText();
            String ingredientes = txtIngredientes.getText();

            // converte os campos de texto para Double (tratando erros de digitação)
            double precoPequena = Double.parseDouble(txtPrecoPequena.getText().replace(",", "."));
            double precoMedia = Double.parseDouble(txtPrecoMedia.getText().replace(",", "."));
            double precoGrande = Double.parseDouble(txtPrecoGrande.getText().replace(",", "."));

            if (sabor.isEmpty() || ingredientes.isEmpty()) {
                throw new IllegalArgumentException("Todos os campos devem ser preenchidos.");
            }

            // pizzaService.atualizarPizza(...);

            mostrarMensagem(Alert.AlertType.INFORMATION, "Sucesso", "Sabor atualizado com sucesso!");
            fecharFormulario(event);

        } catch (NumberFormatException e) {
            mostrarMensagem(Alert.AlertType.WARNING, "Erro de Preço", "Por favor, digite valores numéricos válidos para os preços.");
        } catch (IllegalArgumentException e) {
            mostrarMensagem(Alert.AlertType.WARNING, "Validação", e.getMessage());
        } catch (Exception e) {
            mostrarMensagem(Alert.AlertType.ERROR, "Erro", "Erro ao salvar as modificações do sabor.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        fecharFormulario(event);
    }

    private void fecharFormulario(ActionEvent event) {
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/GerenciarPizzasView.fxml", "La Piazza - Tipos de Pizza");
    }

    private void mostrarMensagem(Alert.AlertType tipo, String titulo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}