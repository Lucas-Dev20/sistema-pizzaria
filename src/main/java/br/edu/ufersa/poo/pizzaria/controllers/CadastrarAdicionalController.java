package br.edu.ufersa.poo.pizzaria.controllers;

import br.edu.ufersa.poo.pizzaria.model.services.AdicionalService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CadastrarAdicionalController {

    @FXML private TextField txtNome;
    @FXML private TextField txtCategoria;
    @FXML private TextField txtValor;
    @FXML private TextField txtUnidade;
    @FXML private TextField txtQuantidade;

    private final AdicionalService adicionalService = new AdicionalService();

    @FXML
    private void handleSalvar(ActionEvent event) {
        try {
            String nome = txtNome.getText().trim();
            String categoria = txtCategoria.getText().trim();
            String unidade = txtUnidade.getText().trim();
            String valorTxt = txtValor.getText().trim().toLowerCase().replace("r$", "").trim();
            String qtdTxt = txtQuantidade.getText().trim();

            // Validação de campos obrigatórios
            if (nome.isEmpty() || categoria.isEmpty() || unidade.isEmpty() || valorTxt.isEmpty() || qtdTxt.isEmpty()) {
                throw new IllegalArgumentException("Todos os campos devem ser preenchidos.");
            }

            double valor = Double.parseDouble(valorTxt.replace(",", "."));
            int quantidade = Integer.parseInt(qtdTxt);

            // cadastra E já registra na reposicao_estoque para o relatório contabilizar
            adicionalService.cadastrarAdicionalComReposicao(nome, valor, quantidade);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Adicional cadastrado com êxito!");
            fecharModal(event);

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Erro de Preço/Estoque",
                    "Por favor, use valores numéricos válidos para preço e quantidade.");
        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validação", e.getMessage());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Houve um erro inesperado ao salvar.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        fecharModal(event);
    }

    private void fecharModal(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}