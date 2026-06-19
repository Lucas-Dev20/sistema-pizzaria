package br.edu.ufersa.poo.pizzaria.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtSenha;

    @FXML
    private void fazerLogin(ActionEvent event) {
        String usuario = txtUsuario.getText();
        String senha = txtSenha.getText();

        if (usuario.isBlank() || senha.isBlank()) {
            mostrarAviso("Preencha usuário e senha.");
            return;
        }

        System.out.println("Usuário: " + usuario);
        // TODO: chamar service de autenticação
    }

    @FXML
    private void esqueceuSenha(ActionEvent event) {
        trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/RecuperarSenha.fxml", "Recuperar Senha");
    }

    @FXML
    private void abrirCadastro(ActionEvent event) {
        trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/CriarConta.fxml", "Criar Conta");
    }

    // Troca apenas o root da Scene existente — Stage não é tocado, logo o maximize é preservado
    static void trocarConteudo(ActionEvent event, String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    LoginController.class.getResource(fxmlPath)
            );
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);   // <-- NÃO cria nova Scene
            stage.setTitle(titulo);

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText(null);
            alert.setContentText("Não foi possível carregar: " + fxmlPath);
            alert.showAndWait();
        }
    }

    private void mostrarAviso(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
