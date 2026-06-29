package br.edu.ufersa.poo.pizzaria.controllers;

import br.edu.ufersa.poo.pizzaria.model.entities.Adicional;
import br.edu.ufersa.poo.pizzaria.model.entities.Cliente;
import br.edu.ufersa.poo.pizzaria.model.entities.Pedido;
import br.edu.ufersa.poo.pizzaria.model.entities.Pizza;
import br.edu.ufersa.poo.pizzaria.model.services.AdicionalService;
import br.edu.ufersa.poo.pizzaria.model.services.ClienteService;
import br.edu.ufersa.poo.pizzaria.model.services.PedidoService;
import br.edu.ufersa.poo.pizzaria.model.services.PizzaService;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PedidosController {

    // ── FXML ──────────────────────────────────────────────────────────────
    @FXML private TextField campoBusca;
    @FXML private ComboBox<String> filtroEstado;
    @FXML private VBox listaPedidos;

    // ── SERVICES (camada de negócio do projeto) ────────────────────────────
    private final PedidoService   pedidoService   = new PedidoService();
    private final ClienteService  clienteService  = new ClienteService();
    private final PizzaService    pizzaService    = new PizzaService();
    private final AdicionalService adicionalService = new AdicionalService();

    // Cache local — recarregado a cada operação
    private List<Pedido>   todosPedidos  = new ArrayList<>();
    private List<Cliente>  todosClientes = new ArrayList<>();
    private List<Pizza>    todasPizzas   = new ArrayList<>();
    private List<Adicional> todosAdicionais = new ArrayList<>();

    // ── INICIALIZAÇÃO ──────────────────────────────────────────────────────
    // Chamado automaticamente pelo FXMLLoader após injetar os campos @FXML
    @FXML
    public void initialize() {

        // Estados disponíveis no banco (campo "estado" da tabela pedido)
        filtroEstado.setItems(FXCollections.observableArrayList(
                "Todos os estados", "Pendente", "Em preparo", "Pronto", "Entregue", "Cancelado"
        ));
        filtroEstado.getSelectionModel().selectFirst();

        carregarDadosDoBanco();
        renderizarLista(todosPedidos);
    }

    /** Busca todos os dados necessários via Service → DAO → banco */
    private void carregarDadosDoBanco() {
        try {
            todosPedidos    = pedidoService.listarTodosPedidos();
            todosClientes   = clienteService.listarTodosClientes();
            todasPizzas     = pizzaService.listarTodasPizzas();
            todosAdicionais = adicionalService.listarTodosAdicionais();
        } catch (Exception e) {
            mostrarErro("Erro ao carregar dados: " + e.getMessage());
        }
    }

    // ── RENDERIZAÇÃO ───────────────────────────────────────────────────────
    private void renderizarLista(List<Pedido> pedidos) {
        listaPedidos.getChildren().clear();
        for (Pedido p : pedidos) {
            listaPedidos.getChildren().add(criarLinha(p));
            listaPedidos.getChildren().add(new Separator());
        }
    }

    private HBox criarLinha(Pedido p) {
        HBox linha = new HBox();
        linha.setAlignment(Pos.CENTER_LEFT);
        linha.setPadding(new Insets(14, 20, 14, 20));
        linha.getStyleClass().add("linha-tabela");

        // ── Coluna Cliente ──
        Label lCliente = celula(
                p.getCliente() != null ? p.getCliente().getNome() : "—",
                185, false
        );

        // ── Coluna Pizza (tipo) ──
        Label lPizza = celula(
                p.getPizza() != null ? p.getPizza().getTipo() : "—",
                130, false
        );

        // ── Coluna Tamanho ──
        Label lTamanho = celula(p.getTamanho(), 100, false);

        // ── Coluna Adicionais (contagem) ──
        int qtdAdicionais = p.getAdicionais() != null ? p.getAdicionais().size() : 0;
        String textoAdic = qtdAdicionais + (qtdAdicionais == 1 ? " item" : " itens");
        Label lAdicionais = celula(textoAdic, 110, false);

        // ── Coluna Estado (colorido) ──
        Label lEstado = criarLabelEstado(p.getEstado());

        // ── Coluna Valor (valor_total do banco) ──
        String valorFormatado = String.format("R$ %.2f", p.getValorTotal())
                .replace(".", ",");
        Label lValor = celula(valorFormatado, 100, true);

        // ── Botões de ação ──
        Button btnEditar = new Button("✏");
        btnEditar.getStyleClass().add("btn-icone");
        btnEditar.setOnAction(e -> abrirEdicaoPedido(p));

        Button btnExcluir = new Button("🗑");
        btnExcluir.getStyleClass().addAll("btn-icone", "btn-icone-excluir");
        btnExcluir.setOnAction(e -> confirmarExclusao(p));

        HBox acoes = new HBox(8, btnEditar, btnExcluir);
        acoes.setAlignment(Pos.CENTER);
        acoes.setPrefWidth(70);

        linha.getChildren().addAll(lCliente, lPizza, lTamanho, lAdicionais, lEstado, lValor, acoes);
        return linha;
    }

    private Label celula(String texto, double largura, boolean negrito) {
        Label l = new Label(texto != null ? texto : "—");
        l.setPrefWidth(largura);
        l.setStyle("-fx-text-fill: #1A1A1A;" + (negrito ? " -fx-font-weight: bold;" : ""));
        return l;
    }

    private Label criarLabelEstado(String estado) {
        Label l = new Label(estado != null ? estado : "—");
        l.setPrefWidth(120);
        if (estado == null) return l;
        switch (estado) {
            case "Em preparo" -> l.setStyle("-fx-text-fill: #2980B9; -fx-font-weight: 600;");
            case "Entregue"   -> l.setStyle("-fx-text-fill: #27AE60; -fx-font-weight: 600;");
            case "Pendente"   -> l.setStyle("-fx-text-fill: #E67E22; -fx-font-weight: 600;");
            case "Cancelado"  -> l.setStyle("-fx-text-fill: #C0392B; -fx-font-weight: 600;");
            case "Pronto"     -> l.setStyle("-fx-text-fill: #8E44AD; -fx-font-weight: 600;");
            default           -> l.setStyle("-fx-text-fill: #555555;");
        }
        return l;
    }

    // ── FILTRO / BUSCA ─────────────────────────────────────────────────────
    @FXML
    private void filtrar() {
        String busca  = campoBusca.getText().trim().toLowerCase();
        String estado = filtroEstado.getValue();

        List<Pedido> filtrados = todosPedidos.stream()
                .filter(p -> {
                    String nomeCliente = p.getCliente() != null ? p.getCliente().getNome().toLowerCase() : "";
                    String tipoPizza   = p.getPizza()   != null ? p.getPizza().getTipo().toLowerCase()   : "";
                    return nomeCliente.contains(busca) || tipoPizza.contains(busca);
                })
                .filter(p -> estado == null
                        || estado.equals("Todos os estados")
                        || estado.equals(p.getEstado()))
                .toList();

        renderizarLista(filtrados);
    }

    // ── MODAL: NOVO PEDIDO ─────────────────────────────────────────────────
    @FXML
    private void abrirNovoPedido(ActionEvent event) {
        mostrarModalPedido(null);
    }

    private void abrirEdicaoPedido(Pedido pedido) {
        mostrarModalPedido(pedido);
    }

    /* Modal de Novo / Editar Pedido.*/

    private void mostrarModalPedido(Pedido pedido) {
        boolean editando = pedido != null;

        // ── Seleção de Cliente (ComboBox com nomes do banco) ──
        ComboBox<Cliente> comboCliente = new ComboBox<>();
        comboCliente.setItems(FXCollections.observableArrayList(todosClientes));
        comboCliente.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Cliente c)   { return c != null ? c.getNome() : ""; }
            @Override public Cliente fromString(String s) { return null; }
        });
        comboCliente.setPrefWidth(Double.MAX_VALUE);
        comboCliente.getStyleClass().add("combo-filtro");
        if (editando) comboCliente.setValue(pedido.getCliente());

        // ── Seleção de Pizza (ComboBox com tipos do banco) ──
        ComboBox<Pizza> comboPizza = new ComboBox<>();
        comboPizza.setItems(FXCollections.observableArrayList(todasPizzas));
        comboPizza.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Pizza p)   { return p != null ? p.getTipo() : ""; }
            @Override public Pizza fromString(String s) { return null; }
        });
        comboPizza.setPrefWidth(Double.MAX_VALUE);
        comboPizza.getStyleClass().add("combo-filtro");
        if (editando) comboPizza.setValue(pedido.getPizza());

        // ── Tamanho ──
        ComboBox<String> comboTamanho = new ComboBox<>();
        comboTamanho.setItems(FXCollections.observableArrayList("Pequena", "Média", "Grande"));
        comboTamanho.setPrefWidth(Double.MAX_VALUE);
        comboTamanho.getStyleClass().add("combo-filtro");
        if (editando) comboTamanho.setValue(pedido.getTamanho());
        else comboTamanho.setPromptText("Selecione");

        // ── Adicionais (checkboxes com os do banco) ──
        VBox boxAdicionais = new VBox(6);
        List<CheckBox> checks = new ArrayList<>();
        for (Adicional a : todosAdicionais) {
            CheckBox cb = new CheckBox(a.getNome() + "  (R$ " +
                    String.format("%.2f", a.getValor()).replace(".", ",") + ")");
            cb.setUserData(a); // guarda o objeto Adicional no checkbox
            if (editando && pedido.getAdicionais() != null) {
                cb.setSelected(pedido.getAdicionais().stream()
                        .anyMatch(ad -> ad.getIdAdicional() == a.getIdAdicional()));
            }
            checks.add(cb);
            boxAdicionais.getChildren().add(cb);
        }

        // ── Estado ──
        ComboBox<String> comboEstado = new ComboBox<>();
        comboEstado.setItems(FXCollections.observableArrayList(
                "Pendente", "Em preparo", "Pronto", "Entregue", "Cancelado"
        ));
        comboEstado.setPrefWidth(Double.MAX_VALUE);
        comboEstado.getStyleClass().add("combo-filtro");
        if (editando) comboEstado.setValue(pedido.getEstado());
        else comboEstado.setPromptText("Selecione o estado");

        // ── Layout: adicionais e estado lado a lado ──
        ScrollPane scrollAdic = new ScrollPane(boxAdicionais);
        scrollAdic.setFitToWidth(true);
        scrollAdic.setPrefHeight(130);
        scrollAdic.setMinWidth(260);
        scrollAdic.setStyle("-fx-background-color: transparent; -fx-border-color: #DEDBD4; "
                + "-fx-border-radius: 8px; -fx-background: white;");

        VBox colAdic   = new VBox(6, new Label("Adicionais"), scrollAdic);
        colAdic.setMinWidth(260);
        colAdic.setPrefWidth(280);
        VBox colEstado = new VBox(6, new Label("Estado"), comboEstado);
        colEstado.setPrefWidth(160);
        HBox linhaAE   = new HBox(24, colAdic, colEstado);
        HBox.setHgrow(colAdic,   Priority.ALWAYS);
        HBox.setHgrow(colEstado, Priority.NEVER);

        // ── Botões ──
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.getStyleClass().add("botao");
        Button btnSalvar = new Button("Salvar");
        btnSalvar.getStyleClass().add("botao-primary");
        HBox rodape = new HBox(20, btnCancelar, btnSalvar);
        rodape.setAlignment(Pos.CENTER);
        VBox.setMargin(rodape, new Insets(10, 0, 0, 0));

        // ── Corpo do modal ──
        VBox corpo = new VBox(12,
                new Label("Cliente"),    comboCliente,
                new Label("Sabor"),      comboPizza,
                new Label("Tamanho"),    comboTamanho,
                linhaAE,
                rodape
        );
        corpo.setPadding(new Insets(20, 30, 30, 30));

        Label titulo = new Label(editando ? "Editar Pedido" : "Novo Pedido");
        titulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        VBox.setMargin(titulo, new Insets(28, 30, 0, 30));

        VBox card = new VBox(titulo, corpo);
        card.setMaxWidth(580);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; "
                + "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.25),20,0,0,4);");

        StackPane overlay = new StackPane(card);
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.45);");
        overlay.setAlignment(Pos.CENTER);

        BorderPane raiz = (BorderPane) listaPedidos.getScene().getRoot();
        StackPane camada = new StackPane(raiz.getCenter(), overlay);
        raiz.setCenter(camada);

        Runnable fechar = () -> raiz.setCenter(camada.getChildren().get(0));

        btnCancelar.setOnAction(e -> fechar.run());

        btnSalvar.setOnAction(e -> {
            // ── Validação dos campos obrigatórios ──
            if (comboCliente.getValue() == null) {
                mostrarAviso("Selecione um cliente.");
                return;
            }
            if (comboPizza.getValue() == null) {
                mostrarAviso("Selecione um sabor de pizza.");
                return;
            }
            if (comboTamanho.getValue() == null) {
                mostrarAviso("Selecione o tamanho.");
                return;
            }
            if (comboEstado.getValue() == null) {
                mostrarAviso("Selecione o estado do pedido.");
                return;
            }

            // ── Coleta adicionais marcados ──
            List<Adicional> adicionaisSelecionados = new ArrayList<>();
            for (CheckBox cb : checks) {
                if (cb.isSelected()) {
                    adicionaisSelecionados.add((Adicional) cb.getUserData());
                }
            }

            try {
                if (editando) {
                    // Atualiza pedido existente via PedidoService → PedidoDAO
                    pedido.setCliente(comboCliente.getValue());
                    pedido.setPizza(comboPizza.getValue());
                    pedido.setTamanho(comboTamanho.getValue());
                    pedido.setEstado(comboEstado.getValue());
                    pedido.setAdicionais(adicionaisSelecionados);
                    pedido.calcularTotal();
                    pedidoService.atualizarPedido(pedido);
                } else {
                    // Cria novo pedido via PedidoService → PedidoDAO
                    // PedidoService.cadastrarPedido também baixa o estoque
                    // dos adicionais automaticamente (regra de negócio)
                    Pedido novo = new Pedido(
                            comboCliente.getValue(),
                            comboPizza.getValue(),
                            adicionaisSelecionados,
                            comboTamanho.getValue(),
                            comboEstado.getValue(),
                            LocalDate.now()
                    );
                    pedidoService.cadastrarPedido(novo);
                }

                // Recarrega lista do banco e fecha modal
                carregarDadosDoBanco();
                fechar.run();
                renderizarLista(todosPedidos);

            } catch (IllegalArgumentException ex) {
                mostrarAviso(ex.getMessage());
            } catch (Exception ex) {
                mostrarErro("Erro ao salvar pedido: " + ex.getMessage());
            }
        });
    }

    // ── MODAL: CONFIRMAR EXCLUSÃO ──────────────────────────────────────────
    private void confirmarExclusao(Pedido pedido) {
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

        BorderPane raiz = (BorderPane) listaPedidos.getScene().getRoot();
        StackPane camada = new StackPane(raiz.getCenter(), overlay);
        raiz.setCenter(camada);

        Runnable fechar = () -> raiz.setCenter(camada.getChildren().get(0));

        btnCancelar.setOnAction(e -> fechar.run());

        btnExcluir.setOnAction(e -> {
            try {
                // Remove via PedidoService → PedidoDAO (deleta pedido + pedido_adicional)
                pedidoService.removerPedido(pedido.getIdPedido());
                carregarDadosDoBanco();
                fechar.run();
                renderizarLista(todosPedidos);
            } catch (IllegalArgumentException ex) {
                mostrarAviso(ex.getMessage());
            } catch (Exception ex) {
                mostrarErro("Erro ao excluir: " + ex.getMessage());
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

    // ── NAVEGAÇÃO ──────────────────────────────────────────────────────────
    @FXML private void irPedidos(ActionEvent e)    { /* já está aqui */ }
    @FXML private void irClientes(ActionEvent e)   { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/GerenciarClientesView.fxml",    "Clientes"); }
    @FXML private void irTiposPizza(ActionEvent e) { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/GerenciarPizzasView.fxml",  "Tipos de pizza"); }
    @FXML private void irAdicionais(ActionEvent e) { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/GerenciarAdicionaisView.fxml",  "Adicionais"); }
    @FXML private void irEstoque(ActionEvent e)    { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/EstoqueView.fxml",     "Estoque"); }
    @FXML private void irRelatorios(ActionEvent e) { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/RelatorioView.fxml",  "Relatórios"); }
    @FXML private void irFuncionarios(ActionEvent e) { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/GerenciarFuncionariosView.fxml", "La Piazza - Funcionários"); }
    @FXML private void sair(ActionEvent e)         { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/LoginView.fxml",   "La Piazza Pizzaria"); }
}