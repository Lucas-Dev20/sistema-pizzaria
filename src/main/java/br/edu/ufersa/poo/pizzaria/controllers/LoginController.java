package br.edu.ufersa.poo.pizzaria.controllers;

import br.edu.ufersa.poo.pizzaria.model.entities.Usuario;
import br.edu.ufersa.poo.pizzaria.model.services.UsuarioService;

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

    @FXML private TextField     txtUsuario;
    @FXML private PasswordField txtSenha;

    // ── SESSÃO GLOBAL ──────────────────────────────────────────────────────
    // Guarda o usuário logado para ser consultado por qualquer outro
    // controller (ex: GerenciarPizzasController verifica se é admin).
    // É static para durar enquanto a aplicação estiver aberta.
    private static Usuario usuarioLogado = null;

    public static Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    // ── LOGIN ──────────────────────────────────────────────────────────────
    @FXML
    private void fazerLogin(ActionEvent event) {
        String login = txtUsuario.getText().trim();
        String senha = txtSenha.getText();

        // Validação básica de campos vazios (sem consultar o banco)
        if (login.isBlank() || senha.isBlank()) {
            mostrarAviso("Preencha usuário e senha.");
            return;
        }

        try {
            // Chama o service, que chama o DAO, que faz:
            // SELECT * FROM usuario WHERE login = ? AND senha = ?
            Usuario usuario = UsuarioService.autenticar(login, senha);

            if (usuario == null) {
                // Banco não encontrou nenhum usuário com esse login+senha
                mostrarAviso("Usuário ou senha incorretos.");
                return;
            }

            // Autenticação OK — salva o usuário na sessão global
            usuarioLogado = usuario;

            // Redireciona para o painel de controle
            trocarConteudo(
                    event,
                    "/br/edu/ufersa/pizzaria/views/PainelDeControle.fxml",
                    "La Piazza - Painel de Controle"
            );

        } catch (IllegalArgumentException e) {
            mostrarAviso(e.getMessage());
        } catch (Exception e) {
            mostrarErro("Erro ao conectar ao banco de dados. Verifique a conexão.");
            e.printStackTrace();
        }
    }

    @FXML
    private void esqueceuSenha(ActionEvent event) {
        trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/RecuperarSenha.fxml", "Recuperar Senha");
    }

    @FXML
    private void abrirCadastro(ActionEvent event) {
        trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/CriarConta.fxml", "Criar Conta");
    }

    // ── NAVEGAÇÃO (estático — usado por todos os controllers) ──────────────
    static void trocarConteudo(ActionEvent event, String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    LoginController.class.getResource(fxmlPath)
            );
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
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

    // ── MODAL (usado por controllers para abrir subjanelas) ────────────────
    public static void abrirModal(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    LoginController.class.getResource(fxmlPath)
            );
            Parent root = loader.load();

            Stage modalStage = new Stage();
            modalStage.setTitle(titulo);
            modalStage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            modalStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);

            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            modalStage.setScene(scene);
            modalStage.centerOnScreen();
            modalStage.showAndWait();

        } catch (IOException e) {
            System.err.println("Erro ao carregar o modal: " + fxmlPath);
            e.printStackTrace();
        }
    }

    // ── HELPERS ────────────────────────────────────────────────────────────
    private void mostrarAviso(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void mostrarErro(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}