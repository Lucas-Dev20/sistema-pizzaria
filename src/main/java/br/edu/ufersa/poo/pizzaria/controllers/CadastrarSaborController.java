package br.edu.ufersa.poo.pizzaria.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class CadastrarSaborController {

    @FXML private TextField txtSabor;
    @FXML private TextField txtIngredientes;
    @FXML private TextField txtPrecoPequena;
    @FXML private TextField txtPrecoMedia;
    @FXML private TextField txtPrecoGrande;

    // instancia o sabor quando pronto
    // private PizzaService pizzaService = new PizzaService();

    @FXML
    private void handleSalvar(ActionEvent event) {
        try {
            String sabor = txtSabor.getText().trim();
            String ingredientes = txtIngredientes.getText().trim();

            if (sabor.isEmpty() || ingredientes.isEmpty()) {
                throw new IllegalArgumentException("Todos os campos devem ser preenchidos.");
            }

            // tratamento e conversão de preços informados
            double precoPequena = Double.parseDouble(txtPrecoPequena.getText().replace(",", "."));
            double precoMedia = Double.parseDouble(txtPrecoMedia.getText().replace(",", "."));
            double precoGrande = Double.parseDouble(txtPrecoGrande.getText().replace(",", "."));

            //pizzaService.cadastrarNovaPizza(sabor, ingredientes, precoPequena, precoMedia, precoGrande);

            mostrarMensagem(Alert.AlertType.INFORMATION, "Sucesso", "Novo sabor cadastrado com êxito!");
            fecharFormulario(event);

        } catch (NumberFormatException e) {
            mostrarMensagem(Alert.AlertType.WARNING, "Erro de Preço", "Por favor, preencha os preços usando valores numéricos válidos.");
        } catch (IllegalArgumentException e) {
            mostrarMensagem(Alert.AlertType.WARNING, "Validação", e.getMessage());
        } catch (Exception e) {
            mostrarMensagem(Alert.AlertType.ERROR, "Erro", "Houve um erro inesperado ao salvar o novo sabor.");
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