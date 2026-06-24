package br.edu.ufersa.poo.pizzaria.controllers;

import br.edu.ufersa.poo.pizzaria.model.entities.Adicional;
import br.edu.ufersa.poo.pizzaria.model.services.AdicionalService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditarAdicionalController {

    @FXML private TextField txtNome;
    @FXML private TextField txtCategoria;
    @FXML private TextField txtValor;
    @FXML private TextField txtUnidade;
    @FXML private TextField txtQuantidade;

    private final AdicionalService adicionalService = new AdicionalService();

    // Guarda o nome antigo que veio do banco para a Service usar na busca do SQL
    private String nomeAntigoAdicional;
    private int idAdicionalAtual;

    // Método que recebe os dados da linha clicada na tabela de fundo
    public void preencherCampos(Adicional adicional) {
        if (adicional != null) {
            this.idAdicionalAtual = adicional.getIdAdicional();
            this.nomeAntigoAdicional = adicional.getNome(); // Salva o nome original antes da edição

            txtNome.setText(adicional.getNome());
            txtValor.setText(String.valueOf(adicional.getValor()));
            txtQuantidade.setText(String.valueOf(adicional.getQtd())); // Corrigido para getQtd()

            // Layout fields mockados conforme design
            txtCategoria.setText("Queijos");
            txtUnidade.setText("Gramas (g)");
        }
    }

    @FXML
    private void handleSalvar(ActionEvent event) {
        try {
            String novoNome = txtNome.getText().trim();
            String valorTxt = txtValor.getText().trim().toLowerCase().replace("r$", "").trim();
            String qtdTxt = txtQuantidade.getText().trim();

            if (novoNome.isEmpty() || valorTxt.isEmpty() || qtdTxt.isEmpty()) {
                throw new IllegalArgumentException("Todos os campos devem ser preenchidos.");
            }

            double novoValor = Double.parseDouble(valorTxt.replace(",", "."));
            int novaQuantidade = Integer.parseInt(qtdTxt);

            // Correção dos Sets: Como a entidade não tem setNome/setValor, instanciamos o objeto atualizado
            Adicional adicionalAlterado = new Adicional(idAdicionalAtual, novoNome, novoValor, novaQuantidade);

            // Correção da Service: Passa o objeto novo E o nome antigo exigido pela assinatura do método
            adicionalService.atualizarAdicional(adicionalAlterado, nomeAntigoAdicional);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Adicional atualizado com sucesso!");
            fecharModal(event);

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Erro de Formato", "Por favor, insira valores numéricos válidos.");
        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validação", e.getMessage());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro no Banco", "Houve uma falha inesperada ao atualizar os dados.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        fecharModal(event);
    }

    private void fecharModal(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}