package br.edu.ufersa.poo.pizzaria.controllers;

import br.edu.ufersa.poo.pizzaria.model.entities.Cliente;
import br.edu.ufersa.poo.pizzaria.model.services.ClienteService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class EditarClienteController {

    @FXML private TextField txtNome;
    @FXML private TextField txtCpf;
    @FXML private TextField txtTelefone;
    @FXML private TextField txtEndereco;
    @FXML private TextField txtBairro;

    private final ClienteService clienteService = new ClienteService();
    private Cliente clienteEmEdicao; // Guarda a entidade original com o ID do banco

    // MÉTODOS QUE NASCE APENAS NA TELA DE EDIÇÃO para receber os dados antigos da tabela
    public void preencherCampos(Cliente cliente) {
        this.clienteEmEdicao = cliente;

        txtNome.setText(cliente.getNome());
        txtCpf.setText(cliente.getCpf());
        txtTelefone.setText(cliente.getTelefone());
        txtEndereco.setText(cliente.getEndereco());
        txtBairro.setText(cliente.getBairro());
    }

    @FXML
    private void handleSalvar(ActionEvent event) {
        try {
            //atualiza os dados do objeto temporário com o que o usuário alterou na tela
            clienteEmEdicao.setNome(txtNome.getText());
            clienteEmEdicao.setTelefone(txtTelefone.getText());
            clienteEmEdicao.setEndereco(txtEndereco.getText());
            clienteEmEdicao.setBairro(txtBairro.getText());

            //manda para a service rodar o UPDATE na DAO
            clienteService.atualizarCliente(clienteEmEdicao);

            mostrarMensagem(Alert.AlertType.INFORMATION, "Sucesso", "Dados do cliente atualizados!");
            fecharFormulario(event);

        } catch (IllegalArgumentException e) {
            mostrarMensagem(Alert.AlertType.WARNING, "Validação", e.getMessage());
        } catch (Exception e) {
            mostrarMensagem(Alert.AlertType.ERROR, "Erro", "Erro ao atualizar dados.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        fecharFormulario(event);
    }

    private void fecharFormulario(ActionEvent event) {
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