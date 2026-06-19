package br.edu.ufersa.poo.pizzaria.controllers;

import br.edu.ufersa.poo.pizzaria.model.services.ClienteService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class NovoClienteController {

    @FXML private TextField txtNome;
    @FXML private TextField txtCpf;
    @FXML private TextField txtTelefone;
    @FXML private TextField txtEndereco;
    @FXML private TextField txtBairro;

    private final ClienteService clienteService = new ClienteService();

    @FXML
    private void handleSalvar(ActionEvent event) {
        String nome = txtNome.getText();
        String cpf = txtCpf.getText();
        String telefone = txtTelefone.getText();
        String endereco = txtEndereco.getText();
        String bairro = txtBairro.getText();

        try {
            //chama a Service passando os dados capturados da tela
            clienteService.cadastrarCliente(nome, endereco, cpf, telefone, bairro);

            mostrarMensagem(Alert.AlertType.INFORMATION, "Sucesso", "Cliente cadastrado com sucesso!");

            //depois de salvar, volta para a tela de listagem de clientes
            fecharFormulario(event);

        } catch (IllegalArgumentException e) {
            //pega os "throws" de validação que estão na ClienteService (ex: nome em branco, CPF/telefone com menos de 11 digitos)
            mostrarMensagem(Alert.AlertType.WARNING, "Validação", e.getMessage());
        } catch (Exception e) {
            mostrarMensagem(Alert.AlertType.ERROR, "Erro", "Erro inesperado ao salvar no banco de dados.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        fecharFormulario(event);
    }

    private void fecharFormulario(ActionEvent event) {
        //usa o método estático do seu LoginController para alternar a Scene de volta
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/GerenciarClientesView.fxml", "La Piazza - Gerenciar Clientes");
    }

    private void mostrarMensagem(Alert.AlertType tipo, String titulo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}