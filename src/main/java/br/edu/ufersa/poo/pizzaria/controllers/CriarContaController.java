package br.edu.ufersa.poo.pizzaria.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CriarContaController {

    @FXML
    private TextField txtNome;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private PasswordField txtConfirmarSenha;

    @FXML
    private void avancar(ActionEvent event) {

        String nome  = txtNome.getText();
        String email = txtEmail.getText();
        String senha = txtSenha.getText();
        String confirmar = txtConfirmarSenha.getText();

        if (nome.isBlank() || email.isBlank() || senha.isBlank() || confirmar.isBlank()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText(null);
            alert.setContentText("Preencha todos os campos.");
            alert.showAndWait();
            return;
        }

        if (!senha.equals(confirmar)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText(null);
            alert.setContentText("As senhas não coincidem.");
            alert.showAndWait();
            return;
        }

        System.out.println("Nome: " + nome);
        System.out.println("Email: " + email);
        System.out.println("Senha: " + senha);

        // chamar service de cadastro
    }

    @FXML
    private void cancelar(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/br/edu/ufersa/pizzaria/views/LoginView.fxml")
            );
            Stage stage = (Stage) txtNome.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}