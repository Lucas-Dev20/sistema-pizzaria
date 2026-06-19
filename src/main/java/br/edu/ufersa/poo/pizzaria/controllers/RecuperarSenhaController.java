package br.edu.ufersa.poo.pizzaria.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class RecuperarSenhaController {

    @FXML private TextField txtEmail;

    @FXML
    private void recuperarSenha(ActionEvent event) {
        String email = txtEmail.getText();

        if (email == null || email.isBlank()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText(null);
            alert.setContentText("Informe um email.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Recuperação");
        alert.setHeaderText(null);
        alert.setContentText("Se o email estiver cadastrado, as instruções serão enviadas.");
        alert.showAndWait();

        voltarLogin(event);
    }

    @FXML
    private void voltarLogin(ActionEvent event) {
        LoginController.trocarConteudo(
                event,
                "/br/edu/ufersa/pizzaria/views/LoginView.fxml",
                "La Piazza Pizzaria"
        );
    }
}
