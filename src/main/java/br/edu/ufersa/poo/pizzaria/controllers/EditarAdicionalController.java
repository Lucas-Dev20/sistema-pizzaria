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

    private String nomeAntigoAdicional;
    private int    idAdicionalAtual;
    private int    quantidadeAtual; // ← NOVO: guarda a quantidade antes da edição

    // Recebe o adicional selecionado na tabela e preenche os campos
    public void preencherCampos(Adicional adicional) {
        if (adicional != null) {
            this.idAdicionalAtual    = adicional.getIdAdicional();
            this.nomeAntigoAdicional = adicional.getNome();
            this.quantidadeAtual     = adicional.getQtd(); // ← NOVO: salva a qtd atual

            txtNome.setText(adicional.getNome());
            txtValor.setText(String.valueOf(adicional.getValor()));
            txtQuantidade.setText(String.valueOf(adicional.getQtd()));
            txtCategoria.setText("Queijos");
            txtUnidade.setText("Gramas (g)");
        }
    }

    @FXML
    private void handleSalvar(ActionEvent event) {
        try {
            String novoNome  = txtNome.getText().trim();
            String valorTxt  = txtValor.getText().trim()
                    .toLowerCase()
                    .replace("r$", "")
                    .trim();
            String qtdTxt    = txtQuantidade.getText().trim();

            if (novoNome.isEmpty() || valorTxt.isEmpty() || qtdTxt.isEmpty()) {
                throw new IllegalArgumentException("Todos os campos devem ser preenchidos.");
            }

            double novoValor      = Double.parseDouble(valorTxt.replace(",", "."));
            int    novaQuantidade  = Integer.parseInt(qtdTxt);

            if (novaQuantidade < 0) {
                throw new IllegalArgumentException("A quantidade não pode ser negativa.");
            }

            // Calcula a diferença entre a quantidade nova e a atual
            int diferenca = novaQuantidade - quantidadeAtual;

            if (diferenca > 0) {
                // Quantidade AUMENTOU → é uma reposição de estoque
                // valor_unitario, valor_total) → aparece no custo do relatório
                adicionalService.creditarEstoque(idAdicionalAtual, diferenca);

            } else if (diferenca < 0) {
                // Quantidade DIMINUIU → é um consumo manual
                adicionalService.consumirEstoque(idAdicionalAtual, Math.abs(diferenca));
            }

            Adicional adicionalAlterado = new Adicional(
                    idAdicionalAtual, novoNome, novoValor, novaQuantidade
            );
            adicionalService.atualizarAdicional(adicionalAlterado, nomeAntigoAdicional);

            mostrarAlerta(Alert.AlertType.INFORMATION,
                    "Sucesso", "Adicional atualizado com sucesso!");
            fecharModal(event);

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING,
                    "Erro de Formato", "Por favor, insira valores numéricos válidos.");
        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validação", e.getMessage());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR,
                    "Erro no Banco", "Houve uma falha inesperada ao atualizar os dados.");
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