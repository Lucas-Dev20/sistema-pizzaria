package br.edu.ufersa.poo.pizzaria.controllers;

import br.edu.ufersa.poo.pizzaria.model.services.UsuarioService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller do modal "Cadastrar Funcionário".
 * Acessível somente pelo ADMIN (verificação feita na UsuarioService).
 */
public class CadastrarFuncionarioController {

    @FXML private TextField     txtNome;
    @FXML private TextField     txtEmail;
    @FXML private PasswordField txtSenha;
    @FXML private PasswordField txtConfirmarSenha;

    private final UsuarioService usuarioService = new UsuarioService();

    @FXML
    private void handleSalvar(ActionEvent event) {
        try {
            String nome   = txtNome.getText().trim();
            String email  = txtEmail.getText().trim();
            String senha  = txtSenha.getText();
            String conf   = txtConfirmarSenha.getText();

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || conf.isEmpty()) {
                throw new IllegalArgumentException("Todos os campos são obrigatórios.");
            }

            if (!senha.equals(conf)) {
                throw new IllegalArgumentException("As senhas não coincidem.");
            }

            // A verificação de permissão (ADMIN) ocorre dentro do service
            usuarioService.cadastrarFuncionario(nome, email, senha);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso",
                    "Funcionário \"" + nome + "\" cadastrado com sucesso!");
            fechar(event);

        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validação", e.getMessage());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao cadastrar: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        fechar(event);
    }

    private void fechar(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}