package br.edu.ufersa.poo.pizzaria.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Node;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private void fazerLogin(ActionEvent event) {

        String usuario = txtUsuario.getText();
        String senha = txtSenha.getText();

        if(usuario.isBlank() || senha.isBlank()) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText(null);
            alert.setContentText("Preencha usuário e senha.");
            alert.showAndWait();

            return;
        }

        System.out.println("Usuário: " + usuario);
        System.out.println("Senha: " + senha);

        // chamar service de autenticação
    }

    @FXML
    private void esqueceuSenha(ActionEvent event) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Recuperação");
        alert.setHeaderText(null);
        alert.setContentText(
                "Entre em contato com o administrador do sistema."
        );

        alert.showAndWait();
    }


    @FXML
    private void abrirCadastro(ActionEvent event) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/br/edu/ufersa/pizzaria/views/CriarConta.fxml"
                    )
            );

            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle("Cadastro");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

