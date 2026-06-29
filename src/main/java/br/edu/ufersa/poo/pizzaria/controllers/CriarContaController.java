package br.edu.ufersa.poo.pizzaria.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CriarContaController {

    @FXML private TextField txtNome;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtSenha;
    @FXML private PasswordField txtConfirmarSenha;

    @FXML
    private void avancar(ActionEvent event) {
        String nome      = txtNome.getText();
        String email     = txtEmail.getText();
        String senha     = txtSenha.getText();
        String confirmar = txtConfirmarSenha.getText();

        if (nome.isBlank() || email.isBlank() || senha.isBlank() || confirmar.isBlank()) {
            mostrarAviso("Preencha todos os campos.");
            return;
        }
        if (!senha.equals(confirmar)) {
            mostrarAviso("As senhas não coincidem.");
            return;
        }
        System.out.println("Cadastro: " + nome + " / " + email);
        // TODO: chamar service de cadastro
    }

    @FXML
    private void cancelar(ActionEvent event) {
        // usa o 'event' para descobrir qual componente foi clicado e pegar o Stage dele
        Stage stageAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // fecha apenas essa janela
        stageAtual.close();
    }

    private void mostrarAviso(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
