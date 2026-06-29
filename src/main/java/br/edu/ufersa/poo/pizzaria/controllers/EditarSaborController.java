package br.edu.ufersa.poo.pizzaria.controllers;

import br.edu.ufersa.poo.pizzaria.model.entities.Pizza;
import br.edu.ufersa.poo.pizzaria.model.services.PizzaService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditarSaborController {

    @FXML private TextField txtSabor;
    @FXML private TextField txtIngredientes;
    @FXML private TextField txtPrecoPequena;
    @FXML private TextField txtPrecoMedia;
    @FXML private TextField txtPrecoGrande;

    private final PizzaService pizzaService = new PizzaService();
    private Pizza pizzaEmEdicao;

    @FXML
    public void initialize() {}



    public void preencherCampos(Pizza pizza) {
        this.pizzaEmEdicao = pizza;
        txtSabor.setText(pizza.getTipo());
        txtIngredientes.setText("");  // banco não tem coluna ingredientes ainda
        txtPrecoPequena.setText(String.format("%.2f", pizza.getValorPequena()));
        txtPrecoMedia.setText(String.format("%.2f", pizza.getValorMedia()));
        txtPrecoGrande.setText(String.format("%.2f", pizza.getValorGrande()));
    }

    @FXML
    private void handleSalvar(ActionEvent event) {
        try {
            String sabor = txtSabor.getText().trim();
            if (sabor.isEmpty()) {
                throw new IllegalArgumentException("O nome do sabor não pode ficar vazio.");
            }

            double precoPequena = Double.parseDouble(
                    txtPrecoPequena.getText().replace(",", "."));
            double precoMedia = Double.parseDouble(
                    txtPrecoMedia.getText().replace(",", "."));
            double precoGrande = Double.parseDouble(
                    txtPrecoGrande.getText().replace(",", "."));

            if (precoPequena <= 0 || precoMedia <= 0 || precoGrande <= 0) {
                throw new IllegalArgumentException("Os preços devem ser maiores que zero.");
            }
            if (precoPequena >= precoMedia || precoMedia >= precoGrande) {
                throw new IllegalArgumentException("Os preços devem ser: Pequena < Média < Grande.");
            }

            // Atualiza os 3 preços reais (Pequena, Média, Grande)
            pizzaEmEdicao.setTipo(sabor);
            pizzaEmEdicao.setValorPequena(precoPequena);
            pizzaEmEdicao.setValorMedia(precoMedia);
            pizzaEmEdicao.setValorGrande(precoGrande);
            pizzaService.atualizarPizza(pizzaEmEdicao);

            mostrarMensagem(Alert.AlertType.INFORMATION, "Sucesso", "Sabor atualizado com sucesso!");
            fecharModal(event);

        } catch (NumberFormatException e) {
            mostrarMensagem(Alert.AlertType.WARNING, "Erro de Preço",
                    "Digite valores numéricos válidos para os preços.");
        } catch (IllegalArgumentException e) {
            mostrarMensagem(Alert.AlertType.WARNING, "Validação", e.getMessage());
        } catch (Exception e) {
            mostrarMensagem(Alert.AlertType.ERROR, "Erro", "Erro ao salvar as modificações.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        fecharModal(event);
    }

    // fecha apenas o Stage modal — não toca na janela principal
    private void fecharModal(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource())
                .getScene().getWindow();
        stage.close();
    }

    private void mostrarMensagem(Alert.AlertType tipo, String titulo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}