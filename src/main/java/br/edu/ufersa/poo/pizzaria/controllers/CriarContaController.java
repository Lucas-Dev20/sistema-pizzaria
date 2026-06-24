package br.edu.ufersa.poo.pizzaria.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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
        LoginController.trocarConteudo(
                event,
                "/br/edu/ufersa/pizzaria/views/LoginView.fxml",
                "La Piazza Pizzaria"
        );
    }

    private void mostrarAviso(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
