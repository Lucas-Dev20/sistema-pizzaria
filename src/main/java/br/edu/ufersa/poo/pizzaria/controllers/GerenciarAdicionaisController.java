package br.edu.ufersa.poo.pizzaria.controllers;

import br.edu.ufersa.poo.pizzaria.model.entities.Adicional;
import br.edu.ufersa.poo.pizzaria.model.services.AdicionalService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GerenciarAdicionaisController {

    // ── FXML ──────────────────────────────────────────────────────────────
    @FXML private TextField                        txtBusca;
    @FXML private Button                           btnNovoAdicional;
    @FXML private TableView<Adicional>             tabelaAdicionais;
    @FXML private TableColumn<Adicional, String>   colNome;
    @FXML private TableColumn<Adicional, String>   colPreco;
    @FXML private TableColumn<Adicional, String>   colEstoque;
    @FXML private TableColumn<Adicional, String>   colAcoes;

    private final AdicionalService adicionalService = new AdicionalService();
    private final ObservableList<Adicional> listaAdicionaisOb = FXCollections.observableArrayList();

    // ── INICIALIZAÇÃO ──────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        configurarColunas();
        atualizarTabela();
    }

    // ── CONFIGURAÇÃO DAS COLUNAS ───────────────────────────────────────────
    private void configurarColunas() {

        // Nome → adicional.nome
        colNome.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNome()));

        // Valor → adicional.valor formatado como "R$ 5,00"
        colPreco.setCellValueFactory(data ->
                new SimpleStringProperty(
                        String.format("R$ %.2f", data.getValue().getValor())
                                .replace(".", ",")));

        // Estoque → adicional.quantidade
        colEstoque.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getQtd() + " un."));

        // Ações → botão ✏ (editar) + 🗑 (excluir) por linha
        colAcoes.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar  = new Button("✏");
            private final Button btnExcluir = new Button("🗑");

            {
                // Estilo do botão editar
                btnEditar.getStyleClass().add("btn-icone");
                btnEditar.setOnAction(e -> {
                    Adicional a = getTableView().getItems().get(getIndex());
                    abrirEdicaoAdicional(a);
                });

                // Estilo do botão excluir
                btnExcluir.getStyleClass().addAll("btn-icone", "btn-icone-excluir");
                btnExcluir.setOnAction(e -> {
                    Adicional a = getTableView().getItems().get(getIndex());
                    handleExcluirAdicional(a);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(8, btnEditar, btnExcluir);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });
    }

    // ── ABRIR MODAL DE EDIÇÃO ──────────────────────────────────────────────
    // Carrega o EditarAdicionalView.fxml que já existe no projeto,
    // passa o Adicional selecionado para o EditarAdicionalController
    // via preencherCampos(), e espera o modal fechar para atualizar a tabela.
    private void abrirEdicaoAdicional(Adicional adicional) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/br/edu/ufersa/pizzaria/views/EditarAdicionalView.fxml")
            );
            Parent root = loader.load();

            // Passa os dados do adicional selecionado para o controller do modal
            EditarAdicionalController controller = loader.getController();
            controller.preencherCampos(adicional);

            Stage modal = new Stage();
            modal.setTitle("Editar Adicional");
            modal.initStyle(StageStyle.UNDECORATED);
            modal.initModality(Modality.APPLICATION_MODAL); // bloqueia a janela de fundo
            modal.setScene(new Scene(root));
            modal.centerOnScreen();
            modal.showAndWait(); // espera fechar antes de continuar

            // Recarrega a tabela após fechar o modal
            atualizarTabela();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Não foi possível abrir a tela de edição.", ButtonType.OK).showAndWait();
        }
    }

    // ── ATUALIZAR TABELA DO BANCO ──────────────────────────────────────────
    private void atualizarTabela() {
        try {
            listaAdicionaisOb.clear();
            // AdicionalService → AdicionalDAO → SELECT * FROM adicional
            listaAdicionaisOb.addAll(adicionalService.listarTodosAdicionais());
            tabelaAdicionais.setItems(listaAdicionaisOb);
        } catch (Exception e) {
            System.err.println("Erro ao carregar adicionais do banco.");
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Não foi possível carregar os adicionais do banco de dados.",
                    ButtonType.OK).showAndWait();
        }
    }

    // ── AÇÕES ──────────────────────────────────────────────────────────────
    @FXML
    private void handleNovoAdicional(ActionEvent event) {
        LoginController.abrirModal(
                "/br/edu/ufersa/pizzaria/views/CadastrarAdicionalView.fxml",
                "Novo Adicional"
        );
        atualizarTabela();
    }

    private void handleExcluirAdicional(Adicional adicionalSelecionado) {
        if (adicionalSelecionado == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deseja excluir?");
        alert.setHeaderText(null);
        alert.setContentText("Você tem certeza que deseja excluir o adicional \""
                + adicionalSelecionado.getNome() + "\"?");

        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType btnExcluir  = new ButtonType("Excluir",  ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(btnCancelar, btnExcluir);

        alert.showAndWait().ifPresent(resultado -> {
            if (resultado == btnExcluir) {
                try {
                    // AdicionalService → AdicionalDAO → DELETE FROM adicional WHERE nome = ?
                    adicionalService.removerAdicional(adicionalSelecionado.getNome());
                    atualizarTabela();
                } catch (IllegalArgumentException e) {
                    new Alert(Alert.AlertType.WARNING, e.getMessage(), ButtonType.OK).showAndWait();
                } catch (Exception e) {
                    new Alert(Alert.AlertType.ERROR,
                            "Não foi possível excluir o adicional.", ButtonType.OK).showAndWait();
                    e.printStackTrace();
                }
            }
        });
    }

    // ── NAVEGAÇÃO ──────────────────────────────────────────────────────────
    @FXML private void irPedidos(ActionEvent e)     { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/Pedidos.fxml",                   "La Piazza - Pedidos"); }
    @FXML private void irClientes(ActionEvent e)    { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/GerenciarClientesView.fxml",     "La Piazza - Clientes"); }
    @FXML private void irTiposPizza(ActionEvent e)  { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/GerenciarPizzasView.fxml",       "La Piazza - Tipos de Pizza"); }
    @FXML private void irAdicionais(ActionEvent e)  { atualizarTabela(); }
    @FXML private void irEstoque(ActionEvent e)     { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/EstoqueView.fxml",               "La Piazza - Estoque"); }
    @FXML private void irRelatorios(ActionEvent e)  { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/RelatorioView.fxml",             "La Piazza - Relatórios"); }
    @FXML private void irFuncionarios(ActionEvent e){ LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/GerenciarFuncionariosView.fxml", "La Piazza - Funcionários"); }
    @FXML private void sair(ActionEvent e)          { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/LoginView.fxml",                 "La Piazza Pizzaria"); }
}