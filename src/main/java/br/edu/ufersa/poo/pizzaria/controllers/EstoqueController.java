package br.edu.ufersa.poo.pizzaria.controllers;

import br.edu.ufersa.poo.pizzaria.model.entities.Adicional;
import br.edu.ufersa.poo.pizzaria.model.services.AdicionalService;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class EstoqueController {

    // ── FXML ───────────────────────────────────────────────────────────────
    @FXML private TextField     campoBusca;
    @FXML private ComboBox<String> filtroStatus;
    @FXML private VBox          listaEstoque;

    // ── SERVICE ────────────────────────────────────────────────────────────
    private final AdicionalService adicionalService = new AdicionalService();

    // Cache local
    private List<Adicional> todosItens = new ArrayList<>();

    // Formatador de data para a coluna "Última atualização"
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ── INICIALIZAÇÃO ──────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        filtroStatus.setItems(FXCollections.observableArrayList(
                "Todos os status", "Disponível", "Esgotado"
        ));
        filtroStatus.getSelectionModel().selectFirst();

        carregarDados();
        renderizarLista(todosItens);
    }

    private void carregarDados() {
        try {
            todosItens = adicionalService.listarTodosAdicionais();
        } catch (Exception e) {
            mostrarErro("Erro ao carregar estoque: " + e.getMessage());
        }
    }

    // ── RENDERIZAÇÃO ───────────────────────────────────────────────────────
    private void renderizarLista(List<Adicional> itens) {
        listaEstoque.getChildren().clear();
        for (Adicional a : itens) {
            listaEstoque.getChildren().add(criarLinha(a));
            listaEstoque.getChildren().add(new Separator());
        }
    }

    /**
     * Monta uma linha da tabela para o Adicional informado.
     *
     * Colunas:
     *  - Nome             → adicional.getNome()
     *  - Quantidade       → adicional.getQtd() + unidade (g / L / un.)
     *  - Status           → Disponível (qtd > 0) | Esgotado (qtd == 0)
     *  - Última atualização → data atual formatada (dd/MM/yyyy)
     */
    private HBox criarLinha(Adicional a) {
        HBox linha = new HBox();
        linha.setAlignment(Pos.CENTER_LEFT);
        linha.setPadding(new Insets(14, 20, 14, 20));
        linha.getStyleClass().add("linha-tabela");

        // ── Nome ──
        Label lNome = celula(a.getNome(), 220, true);

        // ── Quantidade com unidade inferida ──
        String qtdFormatada = formatarQuantidade(a);
        Label lQtd = celula(qtdFormatada, 140, false);

        // ── Status colorido ──
        boolean disponivel = a.getQtd() > 0;
        Label lStatus = new Label(disponivel ? "Disponível" : "Esgotado");
        lStatus.setPrefWidth(140);
        lStatus.setStyle(disponivel
                ? "-fx-text-fill: #27AE60; -fx-font-weight: 600;"
                : "-fx-text-fill: #C0392B; -fx-font-weight: 600;");

        // ── Última atualização ──
        Label lData = celula(LocalDate.now().format(FMT), 160, false);

        // ── Botões ──
        Button btnEditar = new Button("✏");
        btnEditar.getStyleClass().add("btn-icone");
        btnEditar.setOnAction(e -> abrirEdicaoItem(a));

        Button btnExcluir = new Button("🗑");
        btnExcluir.getStyleClass().addAll("btn-icone", "btn-icone-excluir");
        btnExcluir.setOnAction(e -> confirmarExclusao(a));

        HBox acoes = new HBox(8, btnEditar, btnExcluir);
        acoes.setAlignment(Pos.CENTER);

        linha.getChildren().addAll(lNome, lQtd, lStatus, lData, acoes);
        return linha;
    }

    // Ajuste simples de unidade: valores grandes em gramas → "g.",
    // valores em litros detectados pelo nome → "L.", demais → "un."
    private String formatarQuantidade(Adicional a) {
        String nome = a.getNome().toLowerCase();
        if (nome.contains("catupiry") || nome.contains("molho") || nome.contains("refriger")) {
            return a.getQtd() + " L.";
        } else if (nome.contains("bacon") || nome.contains("cheddar") || nome.contains("queijo")
                || nome.contains("mussarela") || nome.contains("frango")) {
            return a.getQtd() + " g.";
        }
        return a.getQtd() + " un.";
    }

    private Label celula(String texto, double largura, boolean negrito) {
        Label l = new Label(texto != null ? texto : "—");
        l.setPrefWidth(largura);
        l.setStyle("-fx-text-fill: #1A1A1A;" + (negrito ? " -fx-font-weight: bold;" : ""));
        return l;
    }

    // ── FILTRO / BUSCA ─────────────────────────────────────────────────────
    @FXML
    private void filtrar() {
        String busca  = campoBusca.getText().trim().toLowerCase();
        String status = filtroStatus.getValue();

        List<Adicional> filtrados = todosItens.stream()
                .filter(a -> a.getNome().toLowerCase().contains(busca))
                .filter(a -> {
                    if (status == null || status.equals("Todos os status")) return true;
                    boolean esgotado = a.getQtd() == 0;
                    return status.equals("Esgotado") == esgotado;
                })
                .toList();

        renderizarLista(filtrados);
    }

    // ── MODAL: NOVO ITEM ───────────────────────────────────────────────────
    @FXML
    private void abrirNovoItem(ActionEvent event) {
        mostrarModalItem(null);
    }

    private void abrirEdicaoItem(Adicional item) {
        mostrarModalItem(item);
    }

    /**
     * Modal unificado de Estoque com dois comportamentos:
     *
     * ┌─ NOVO ITEM (item == null) ─────────────────────────────────────────────┐
     * │  Campos: Nome | Custo unitário de compra (R$) | Quantidade inicial      │
     * │  Ação:   cadastrarAdicional(nome, custoCompra, qtd)                     │
     * │          + registrarReposicao() para registrar o gasto inicial no       │
     * │            histórico de reposição — alimenta o relatório de custo.      │
     * └────────────────────────────────────────────────────────────────────────┘
     *
     * ┌─ EDITAR / REPOR (item != null) ───────────────────────────────────────┐
     * │  Campos: Nome (editável) | Qtd a ADICIONAR ao estoque | Custo atual    │
     * │          da reposição (pode diferir do custo original)                 │
     * │  Ação:   atualizarAdicional() para nome                                │
     * │          + creditarEstoque() → que já chama registrarReposicao()       │
     * │            gravando valor_unitario e valor_total no banco.              │
     * └────────────────────────────────────────────────────────────────────────┘
     *
     * Por que separar custo de compra do preço de venda?
     *  - adicional.getValor()  → preço cobrado do cliente ao pedir (receita)
     *  - reposicao.valor_unitario → quanto a pizzaria pagou ao comprar (custo)
     * O relatório usa SUM(reposicao_estoque.valor_total) como "gasto total"
     * e SUM(pedido_adicional) * adicional.valor como "receita dos adicionais".
     */
    private void mostrarModalItem(Adicional item) {
        boolean editando = item != null;

        // ── Campo Nome ──────────────────────────────────────────────────────
        TextField campoNome = new TextField(editando ? item.getNome() : "");
        campoNome.setPromptText("Ex: Molho de Tomate");
        campoNome.getStyleClass().add("campo");
        campoNome.setMaxWidth(Double.MAX_VALUE);

        // ── Campo Custo de compra (valor_unitario na tabela reposicao_estoque)
        // No cadastro novo: custo inicial por unidade
        // Na edição: custo desta reposição (pode ter mudado de fornecedor etc.)
        String custoAtual = editando
                ? String.format("%.2f", item.getValor()).replace(",", ".")
                : "";
        TextField campoCusto = new TextField(custoAtual);
        campoCusto.setPromptText("Ex: 3.50  (custo pago por unidade/grama/litro)");
        campoCusto.getStyleClass().add("campo");
        campoCusto.setMaxWidth(Double.MAX_VALUE);

        // ── Label dinâmico: comportamento diferente conforme contexto ───────
        String labelQtd = editando
                ? "Quantidade a adicionar ao estoque"
                : "Quantidade inicial em estoque";
        TextField campoQtd = new TextField("");
        campoQtd.setPromptText(editando
                ? "Quantas unidades estão entrando agora?"
                : "Quantidade que você tem hoje");
        campoQtd.getStyleClass().add("campo");
        campoQtd.setMaxWidth(Double.MAX_VALUE);

        // ── Nota informativa (só na edição) ──────────────────────────────
        Label notaReposicao = new Label(
                "ℹ Esta operação registra a entrada no histórico de reposição\n"
                        + "e atualiza o custo unitário para o relatório de gastos."
        );
        notaReposicao.setStyle("-fx-text-fill: #555; -fx-font-size: 11px;");
        notaReposicao.setWrapText(true);
        notaReposicao.setVisible(editando);
        notaReposicao.setManaged(editando);

        // ── Botões ──────────────────────────────────────────────────────────
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.getStyleClass().add("botao");
        Button btnSalvar   = new Button("Salvar");
        btnSalvar.getStyleClass().add("botao-primary");

        HBox rodape = new HBox(20, btnCancelar, btnSalvar);
        rodape.setAlignment(Pos.CENTER);
        VBox.setMargin(rodape, new Insets(10, 0, 0, 0));

        // ── Corpo do modal ───────────────────────────────────────────────────
        VBox corpo = new VBox(10,
                new Label("Nome"),            campoNome,
                new Label("Custo de compra (R$)"), campoCusto,
                new Label(labelQtd),          campoQtd,
                notaReposicao,
                rodape
        );
        corpo.setPadding(new Insets(20, 30, 30, 30));

        String tituloTexto = editando ? "Repor / Editar Item" : "Adicionar Item ao Estoque";
        Label titulo = new Label(tituloTexto);
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        VBox.setMargin(titulo, new Insets(28, 30, 0, 30));

        VBox card = new VBox(titulo, corpo);
        card.setMaxWidth(520);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; "
                + "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.25),20,0,0,4);");

        StackPane overlay = new StackPane(card);
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.45);");
        overlay.setAlignment(Pos.CENTER);

        BorderPane raiz = (BorderPane) listaEstoque.getScene().getRoot();
        StackPane camada = new StackPane(raiz.getCenter(), overlay);
        raiz.setCenter(camada);

        Runnable fechar = () -> raiz.setCenter(camada.getChildren().get(0));
        btnCancelar.setOnAction(e -> fechar.run());

        btnSalvar.setOnAction(e -> {
            // ── Coleta e valida campos comuns ────────────────────────────────
            String nome     = campoNome.getText().trim();
            String qtdTexto = campoQtd.getText().trim();
            String custoTexto = campoCusto.getText().trim().replace(",", ".");

            if (nome.isEmpty()) {
                mostrarAviso("Informe o nome do item.");
                return;
            }

            int quantidade;
            try {
                quantidade = Integer.parseInt(qtdTexto);
                if (quantidade <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                mostrarAviso("Informe uma quantidade válida (inteiro maior que zero).");
                return;
            }

            double custoUnitario;
            try {
                custoUnitario = Double.parseDouble(custoTexto);
                if (custoUnitario < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                mostrarAviso("Informe um custo de compra válido (ex: 3.50).");
                return;
            }

            try {
                if (editando) {
                    // 1. Atualiza o nome se mudou
                    if (!nome.equals(item.getNome())) {
                        Adicional atualizado = new Adicional(
                                item.getIdAdicional(), nome,
                                item.getValor(), item.getQtd()
                        );
                        adicionalService.atualizarAdicional(atualizado, item.getNome());
                    }
                    // 2. Repõe o estoque E registra a reposição com o custo correto
                    //    (não chama creditarEstoque pois ele registraria com valor errado)
                    adicionalService.creditarEstoqueComCusto(
                            item.getIdAdicional(), quantidade, custoUnitario
                    );

                } else {
                    // Cadastra o novo adicional E já registra a reposição inicial
                    // tudo dentro do service para garantir atomicidade
                    adicionalService.cadastrarAdicionalComReposicao(
                            nome, custoUnitario, quantidade
                    );
                }

                carregarDados();
                fechar.run();
                renderizarLista(todosItens);

            } catch (IllegalArgumentException ex) {
                mostrarAviso(ex.getMessage());
            } catch (Exception ex) {
                mostrarErro("Erro ao salvar item: " + ex.getMessage());
            }
        });
    }

    // ── MODAL: CONFIRMAR EXCLUSÃO ──────────────────────────────────────────
    private void confirmarExclusao(Adicional item) {
        Label pergunta = new Label("Deseja excluir?");
        pergunta.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.getStyleClass().add("botao");
        Button btnExcluir = new Button("Excluir");
        btnExcluir.getStyleClass().add("botao-primary");

        HBox rodape = new HBox(20, btnCancelar, btnExcluir);
        rodape.setAlignment(Pos.CENTER);

        VBox card = new VBox(28, pergunta, rodape);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40));
        card.setMaxWidth(420);
        card.setMaxHeight(180);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; "
                + "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.25),20,0,0,4);");

        StackPane overlay = new StackPane(card);
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.45);");
        overlay.setAlignment(Pos.CENTER);

        BorderPane raiz = (BorderPane) listaEstoque.getScene().getRoot();
        StackPane camada = new StackPane(raiz.getCenter(), overlay);
        raiz.setCenter(camada);

        Runnable fechar = () -> raiz.setCenter(camada.getChildren().get(0));

        btnCancelar.setOnAction(e -> fechar.run());

        btnExcluir.setOnAction(e -> {
            try {
                // Remove via AdicionalService → AdicionalDAO (DELETE por nome)
                adicionalService.removerAdicional(item.getNome());
                carregarDados();
                fechar.run();
                renderizarLista(todosItens);
            } catch (IllegalArgumentException ex) {
                mostrarAviso(ex.getMessage());
            } catch (Exception ex) {
                mostrarErro("Erro ao excluir item: " + ex.getMessage());
            }
        });
    }

    // ── HELPERS ────────────────────────────────────────────────────────────
    private void mostrarAviso(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("Aviso"); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }

    private void mostrarErro(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Erro"); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }

    // ── NAVEGAÇÃO (mesmo padrão de PedidosController) ─────────────────────
    @FXML private void irPedidos(ActionEvent e)    { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/Pedidos.fxml",               "Pedidos"); }
    @FXML private void irClientes(ActionEvent e)   { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/GerenciarClientesView.fxml", "Clientes"); }
    @FXML private void irTiposPizza(ActionEvent e) { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/GerenciarPizzasView.fxml",   "Tipos de pizza"); }
    @FXML private void irAdicionais(ActionEvent e) { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/GerenciarAdicionaisView.fxml", "Adicionais"); }
    @FXML private void irEstoque(ActionEvent e)    { /* já está aqui */ }
    @FXML private void irRelatorios(ActionEvent e) { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/RelatorioView.fxml",             "Relatórios"); }
    @FXML private void irFuncionarios(ActionEvent e) { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/GerenciarFuncionariosView.fxml", "La Piazza - Funcionários"); }
    @FXML private void sair(ActionEvent e)         { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/LoginView.fxml",              "La Piazza Pizzaria"); }
}