package br.edu.ufersa.poo.pizzaria.controllers;

import br.edu.ufersa.poo.pizzaria.exceptions.AcessoNegadoException;
import br.edu.ufersa.poo.pizzaria.model.entities.Pizza;
import br.edu.ufersa.poo.pizzaria.model.services.PizzaService;
import br.edu.ufersa.poo.pizzaria.session.SessaoUsuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class GerenciarPizzaController {

    // ── FXML ─────────────────────────────────────────────────────────────────
    @FXML private Button    btnNovoSabor;
    @FXML private TextField txtBusca;
    @FXML private TilePane  containerPizzas;

    private final PizzaService pizzaService = new PizzaService();
    private List<Pizza> todasPizzas;

    // ── INICIALIZAÇÃO ─────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        boolean ehAdmin = SessaoUsuario.getInstance().usuarioEhAdmin();
        btnNovoSabor.setVisible(ehAdmin);
        btnNovoSabor.setManaged(ehAdmin);

        // filtra enquanto digita
        txtBusca.textProperty().addListener((obs, antigo, novo) -> filtrarPizzas(novo));

        carregarPizzas();
    }

    // ── CARREGAR E EXIBIR ─────────────────────────────────────────────────────
    private void carregarPizzas() {
        todasPizzas = pizzaService.listarTodasPizzas();
        renderizarCards(todasPizzas);
    }

    private void filtrarPizzas(String busca) {
        if (busca == null || busca.isBlank()) {
            renderizarCards(todasPizzas);
            return;
        }
        String lower = busca.toLowerCase();
        List<Pizza> filtradas = todasPizzas.stream()
                .filter(p -> p.getTipo().toLowerCase().contains(lower))
                .toList();
        renderizarCards(filtradas);
    }

    /**
     * Gera um card visual para cada pizza — igual ao design do PDF:
     *   ┌─────────────────────────────────┐
     *   │  Margherita              ✏ 🗑   │
     *   │  Molho de tomate, ...           │
     *   │ ─────────────────────────────   │
     *   │  Preços por Tamanho             │
     *   │  [Pequena  R$35] [Média R$44]   │
     *   │  [Grande   R$52]                │
     *   └─────────────────────────────────┘
     */
    private void renderizarCards(List<Pizza> pizzas) {
        containerPizzas.getChildren().clear();

        for (Pizza pizza : pizzas) {
            // ── linha do título + botões ──
            Label lblNome = new Label(pizza.getTipo());
            lblNome.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            HBox.setHgrow(lblNome, Priority.ALWAYS);

            HBox cabecalho = new HBox(8, lblNome);
            cabecalho.setStyle("-fx-alignment: CENTER_LEFT;");

            // botões visíveis para todos (admin e funcionário)
            // apenas o botão "+ Novo Sabor" é restrito ao admin (controlado no initialize)
            Button btnEditar = new Button("✏");
            btnEditar.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 14px;");
            btnEditar.setOnAction(e -> abrirEdicao(pizza));

            Button btnExcluir = new Button("🗑");
            btnExcluir.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 14px; -fx-text-fill: #B03A2A;");
            btnExcluir.setOnAction(e -> confirmarExclusao(pizza));

            cabecalho.getChildren().addAll(btnEditar, btnExcluir);

            // ── separador ──
            Separator sep = new Separator();
            sep.setStyle("-fx-padding: 2 0 2 0;");

            // ── preços por tamanho ──
            Label lblPrecoTitulo = new Label("Preço por tamanho");
            lblPrecoTitulo.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");

            // O banco armazena o preço da Média como valor base.
            // Pequena = 80% do preço médio | Grande = 125% do preço médio
            double valorMedio = pizza.getValor();
            Label lblPeq  = rotuloBadge("Pequena", valorMedio * 0.80);
            Label lblMed  = rotuloBadge("Média",   valorMedio);
            Label lblGra  = rotuloBadge("Grande",  valorMedio * 1.25);

            HBox precos = new HBox(8, lblPeq, lblMed, lblGra);
            precos.setStyle("-fx-alignment: CENTER_LEFT;");

            // ── card ──
            VBox card = new VBox(8, cabecalho, sep, lblPrecoTitulo, precos);
            card.setStyle("""
                    -fx-background-color: #F2F0EB;
                    -fx-background-radius: 12;
                    -fx-padding: 14 16 14 16;
                    -fx-pref-width: 340;
                    """);

            containerPizzas.getChildren().add(card);
        }

        if (pizzas.isEmpty()) {
            Label vazio = new Label("Nenhum sabor cadastrado.");
            vazio.setStyle("-fx-text-fill: #888; -fx-font-size: 14px;");
            containerPizzas.getChildren().add(vazio);
        }
    }

    /** Badge cinza com texto "Tamanho  R$ valor" */
    private Label rotuloBadge(String tamanho, double valor) {
        Label l = new Label(String.format("%s  R$%.2f", tamanho, valor));
        l.setStyle("""
                -fx-background-color: #DEDAD4;
                -fx-background-radius: 20;
                -fx-padding: 4 10 4 10;
                -fx-font-size: 12px;
                """);
        return l;
    }

    // ── AÇÕES RESTRITAS AO ADMIN ──────────────────────────────────────────────
    @FXML
    private void handleNovoSabor(ActionEvent event) {
        try {
            verificarAdmin("cadastrar novo tipo de pizza");
            abrirModal("/br/edu/ufersa/pizzaria/views/CadastrarSaborView.fxml",
                    "Cadastrar Novo Sabor");
            carregarPizzas();
        } catch (AcessoNegadoException e) {
            mostrarAcessoNegado(e.getMessage());
        }
    }

    private void abrirEdicao(Pizza pizza) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/br/edu/ufersa/pizzaria/views/EditarSaborView.fxml"));
            Parent root = loader.load();

            EditarSaborController ctrl = loader.getController();
            ctrl.preencherCampos(pizza);

            Stage modal = new Stage();
            modal.setTitle("Editar Sabor");
            modal.setScene(new javafx.scene.Scene(root));
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.initOwner(containerPizzas.getScene().getWindow());
            modal.showAndWait();

            carregarPizzas();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void confirmarExclusao(Pizza pizza) {
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Deseja excluir?");
        confirmacao.setHeaderText(null);
        confirmacao.setContentText("Excluir o sabor \"" + pizza.getTipo() + "\"?");

        ButtonType btnExcluir  = new ButtonType("Excluir",  ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmacao.getButtonTypes().setAll(btnCancelar, btnExcluir);

        confirmacao.showAndWait().ifPresent(btn -> {
            if (btn == btnExcluir) {
                pizzaService.removerPizza(pizza.getIdPizza());
                carregarPizzas();
            }
        });
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────
    private void abrirModal(String fxmlPath, String titulo) {
        LoginController.abrirModal(fxmlPath, titulo);
    }

    private void verificarAdmin(String operacao) {
        if (!SessaoUsuario.getInstance().usuarioEhAdmin()) {
            String perfil = SessaoUsuario.getInstance()
                    .getUsuarioLogado().getPerfil().name();
            throw new AcessoNegadoException(operacao, perfil);
        }
    }

    private void mostrarAcessoNegado(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Acesso Negado");
        alert.setHeaderText("Permissão insuficiente");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    // ── NAVEGAÇÃO ─────────────────────────────────────────────────────────────
    @FXML private void irPedidos(ActionEvent e)    { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/Pedidos.fxml",                  "La Piazza - Pedidos"); }
    @FXML private void irClientes(ActionEvent e)   { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/GerenciarClientesView.fxml",    "La Piazza - Clientes"); }
    @FXML private void irTiposPizza(ActionEvent e) { carregarPizzas(); }
    @FXML private void irAdicionais(ActionEvent e) { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/GerenciarAdicionaisView.fxml",  "La Piazza - Adicionais"); }
    @FXML private void irEstoque(ActionEvent e)    { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/EstoqueView.fxml",              "La Piazza - Estoque"); }
    @FXML private void irRelatorios(ActionEvent e) { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/RelatorioView.fxml",            "La Piazza - Relatórios"); }
    @FXML private void irFuncionarios(ActionEvent e){ LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/GerenciarFuncionariosView.fxml","La Piazza - Funcionários"); }
    @FXML private void sair(ActionEvent e)         { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/LoginView.fxml",                "La Piazza Pizzaria"); }
}