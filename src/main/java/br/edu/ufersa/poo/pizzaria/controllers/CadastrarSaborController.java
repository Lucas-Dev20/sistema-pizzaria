package br.edu.ufersa.poo.pizzaria.controllers;

import br.edu.ufersa.poo.pizzaria.model.entities.Usuario;
import br.edu.ufersa.poo.pizzaria.session.SessaoUsuario;
import br.edu.ufersa.poo.pizzaria.model.services.PizzaService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class CadastrarSaborController {

    @FXML private TextField txtSabor;
    @FXML private TextField txtIngredientes;
    @FXML private TextField txtPrecoPequena;
    @FXML private TextField txtPrecoMedia;
    @FXML private TextField txtPrecoGrande;

    private PizzaService pizzaService = new PizzaService();

    // Atributo para armazenar o usuário que está operando o sistema
    private Usuario usuarioOperador;

    // Método público para a tela anterior passar o usuário logado para este modal
    public void setUsuarioOperador(Usuario usuario) {
        this.usuarioOperador = usuario;
    }

    @FXML
    private void handleSalvar(ActionEvent event) {
        try {
            String sabor = txtSabor.getText().trim();
            String ingredientes = txtIngredientes.getText().trim();

            if (sabor.isEmpty() || ingredientes.isEmpty()) {
                throw new IllegalArgumentException("Todos os campos devem ser preenchidos.");
            }

            double precoPequena = Double.parseDouble(txtPrecoPequena.getText().replace(",", "."));
            double precoMedia = Double.parseDouble(txtPrecoMedia.getText().replace(",", "."));
            double precoGrande = Double.parseDouble(txtPrecoGrande.getText().replace(",", "."));

            // Usa o usuário do operador recebido, ou busca direto da sessão ativa
            if (usuarioOperador == null) {
                usuarioOperador = SessaoUsuario.getInstance().getUsuarioLogado();
            }
            if (usuarioOperador == null) {
                throw new IllegalArgumentException("Sessão inválida: faça login novamente.");
            }

            // Validação dos preços
            if (precoPequena <= 0 || precoMedia <= 0 || precoGrande <= 0) {
                throw new IllegalArgumentException("Os preços devem ser maiores que zero.");
            }
            if (precoPequena >= precoMedia || precoMedia >= precoGrande) {
                throw new IllegalArgumentException("Os preços devem ser: Pequena < Média < Grande.");
            }

            // Salva os 3 preços reais (Pequena, Média, Grande) informados pelo usuário.
            pizzaService.cadastrarPizza(sabor, precoPequena, precoMedia, precoGrande, usuarioOperador);

            mostrarMensagem(Alert.AlertType.INFORMATION, "Sucesso", "Novo sabor cadastrado com êxito!");

            // Modo Pop-up: Apenas fecha a janelinha flutuante
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            mostrarMensagem(Alert.AlertType.WARNING, "Erro de Preço", "Por favor, preencha os preços usando valores numéricos válidos.");
        } catch (IllegalArgumentException e) {
            mostrarMensagem(Alert.AlertType.WARNING, "Validação", e.getMessage());
        } catch (Exception e) {
            mostrarMensagem(Alert.AlertType.ERROR, "Erro no Banco", "Houve um erro inesperado ao salvar no banco de dados.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void fecharFormulario(ActionEvent event) {
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/GerenciarPizzasView.fxml", "La Piazza - Tipos de Pizza");
    }

    private void mostrarMensagem(Alert.AlertType tipo, String titulo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}