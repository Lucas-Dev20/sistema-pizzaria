package br.edu.ufersa.poo.pizzaria.controllers;

import br.edu.ufersa.poo.pizzaria.exceptions.AcessoNegadoException;
import br.edu.ufersa.poo.pizzaria.model.entities.Usuario;
import br.edu.ufersa.poo.pizzaria.model.services.UsuarioService;
import br.edu.ufersa.poo.pizzaria.session.SessaoUsuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class GerenciarFuncionariosController {

    @FXML private TableView<Usuario>           tabelaFuncionarios;
    @FXML private TableColumn<Usuario, String> colNome;
    @FXML private TableColumn<Usuario, String> colEmail;
    @FXML private TableColumn<Usuario, String> colPerfil;
    @FXML private TableColumn<Usuario, String> colStatus;
    @FXML private TextField                    txtBusca;
    @FXML private Button                       btnNovoFuncionario;

    private final UsuarioService usuarioService = new UsuarioService();
    private final ObservableList<Usuario> listaFuncionarios = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarColunas();
        carregarFuncionarios();

        // Oculta o botão "Novo Funcionário" para quem não é admin.
        boolean ehAdmin = SessaoUsuario.getInstance().usuarioEhAdmin();
        btnNovoFuncionario.setVisible(ehAdmin);
        btnNovoFuncionario.setManaged(ehAdmin);
    }

    private void configurarColunas() {
        colNome.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getNome()));
        colEmail.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        colPerfil.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getPerfil().name()));
        colStatus.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().isAtivo() ? "Ativo" : "Inativo"));

        tabelaFuncionarios.setItems(listaFuncionarios);
    }

    private void carregarFuncionarios() {
        try {
            listaFuncionarios.clear();
            listaFuncionarios.addAll(usuarioService.listarFuncionarios());
        } catch (Exception e) {

        }
    }

    // ── Cadastrar novo funcionário - somente ADMIN

    @FXML
    private void handleNovoFuncionario(ActionEvent event) {
        try {
            LoginController.abrirModal(
                    "/br/edu/ufersa/pizzaria/views/CadastrarFuncionarioView.fxml",
                    "Cadastrar Funcionário");
            carregarFuncionarios();

        } catch (AcessoNegadoException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Acesso Negado", e.getMessage());
        }
    }

    // ── Desativar funcionário

    @FXML
    private void handleDesativarFuncionario(ActionEvent event) {
        Usuario selecionado = tabelaFuncionarios.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso",
                    "Selecione um funcionário na tabela.");
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Desativar funcionário?");
        confirmacao.setHeaderText(null);
        confirmacao.setContentText("Deseja desativar o acesso de \"" + selecionado.getNome() + "\"?");

        ButtonType btnSim = new ButtonType("Desativar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnNao = new ButtonType("Cancelar",  ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmacao.getButtonTypes().setAll(btnNao, btnSim);

        confirmacao.showAndWait().ifPresent(btn -> {
            if (btn == btnSim) {
                try {
                    usuarioService.desativarFuncionario(selecionado.getIdUsuario());
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso",
                            "Funcionário desativado com sucesso.");
                    carregarFuncionarios();
                } catch (Exception e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro", e.getMessage());
                }
            }
        });
    }

    // ── Navegação (caminhos corrigidos para os nomes reais dos arquivos) ──────

    @FXML private void irPedidos(ActionEvent e)      { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/Pedidos.fxml",              "La Piazza - Pedidos"); }
    @FXML private void irClientes(ActionEvent e)     { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/GerenciarClientesView.fxml", "La Piazza - Clientes"); }
    @FXML private void irTiposPizza(ActionEvent e)   { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/GerenciarPizzasView.fxml",   "La Piazza - Tipos de Pizza"); }
    @FXML private void irAdicionais(ActionEvent e)   { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/GerenciarAdicionaisView.fxml","La Piazza - Adicionais"); }
    @FXML private void irEstoque(ActionEvent e)      { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/EstoqueView.fxml",            "La Piazza - Estoque"); }
    @FXML private void irRelatorios(ActionEvent e)   { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/RelatorioView.fxml",          "La Piazza - Relatórios"); }
    @FXML private void irFuncionarios(ActionEvent e) { carregarFuncionarios(); }
    @FXML private void sair(ActionEvent e)           { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/LoginView.fxml",              "La Piazza Pizzaria"); }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}