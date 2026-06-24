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
//popup de telas de editar e cadastrar, q abre a subtela
    public static void abrirModal(String fxmlPath, String titulo) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(LoginController.class.getResource(fxmlPath));
            javafx.scene.Parent root = loader.load();

            javafx.stage.Stage modalStage = new javafx.stage.Stage();
            modalStage.setTitle(titulo);

            // Deixa a janela sem a barra de título padrão do sistema operacional
            modalStage.initStyle(javafx.stage.StageStyle.UNDECORATED);

            // Bloqueia interações na tela de trás até fechar o pop-up
            modalStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);

            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            modalStage.setScene(scene);

            // Centraliza e exibe
            modalStage.centerOnScreen();
            modalStage.showAndWait();

        } catch (java.io.IOException e) {
            System.err.println("Erro ao carregar o modal: " + fxmlPath);
            e.printStackTrace();
        }
    }
}
