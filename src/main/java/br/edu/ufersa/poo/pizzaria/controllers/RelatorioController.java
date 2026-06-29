package br.edu.ufersa.poo.pizzaria.controllers;

import br.edu.ufersa.poo.pizzaria.DAO.ReposicaoEstoqueDAO;
import br.edu.ufersa.poo.pizzaria.model.entities.Cliente;
import br.edu.ufersa.poo.pizzaria.model.entities.Pedido;
import br.edu.ufersa.poo.pizzaria.model.entities.Pizza;
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
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class RelatorioController {

    // ── FXML — filtros
    @FXML private ComboBox<String> filtroPeriodo;
    @FXML private ComboBox<String> filtroEstado;
    @FXML private ComboBox<String> filtroSabor;
    @FXML private ComboBox<String> filtroCliente;

    // ── FXML — métricas
    @FXML private Label labelFaturamento;
    @FXML private Label labelCusto;
    @FXML private Label labelLucro;
    @FXML private Label labelTotalPedidos;
    @FXML private VBox  boxSaboresMaisVendidos;

    // ── FXML — seções inferiores
    @FXML private HBox boxEstados;
    @FXML private VBox listaVendasCliente;

    // ── Services / DAOs
    private final PedidoService      pedidoService  = new PedidoService();
    private final ClienteService     clienteService = new ClienteService();
    private final PizzaService       pizzaService   = new PizzaService();
    private final ReposicaoEstoqueDAO reposicaoDAO  = new ReposicaoEstoqueDAO();

    // ── Cache local
    private List<Pedido>  todosPedidos  = new ArrayList<>();
    private List<Cliente> todosClientes = new ArrayList<>();
    private List<Pizza>   todasPizzas   = new ArrayList<>();

    // Pedidos após filtros — também usados no CSV
    private List<Pedido> pedidosFiltrados = new ArrayList<>();

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ── INICIALIZAÇÃO
    @FXML
    public void initialize() {
        carregarDadosDoBanco();
        popularCombos();
        aplicarFiltros();
    }

    private void carregarDadosDoBanco() {
        try {
            todosPedidos  = pedidoService.listarTodosPedidos();
            todosClientes = clienteService.listarTodosClientes();
            todasPizzas   = pizzaService.listarTodasPizzas();
        } catch (Exception e) {
            mostrarErro("Erro ao carregar dados: " + e.getMessage());
        }
    }

    private void popularCombos() {
        // Período
        filtroPeriodo.setItems(FXCollections.observableArrayList(
                "Todos", "Hoje", "Últimos 7 dias", "Últimos 30 dias",
                "Últimos 90 dias", "Este mês", "Este ano"
        ));
        filtroPeriodo.getSelectionModel().selectFirst();

        // Estado
        filtroEstado.setItems(FXCollections.observableArrayList(
                "Todos", "Pendente", "Em preparo", "Pronto", "Entregue", "Cancelado"
        ));
        filtroEstado.getSelectionModel().selectFirst();

        // Sabor — vem do banco via PizzaService
        List<String> sabores = new ArrayList<>();
        sabores.add("Todos");
        todasPizzas.stream()
                .map(Pizza::getTipo)
                .filter(Objects::nonNull)
                .sorted()
                .forEach(sabores::add);
        filtroSabor.setItems(FXCollections.observableArrayList(sabores));
        filtroSabor.getSelectionModel().selectFirst();

        // Cliente — vem do banco via ClienteService
        List<String> nomes = new ArrayList<>();
        nomes.add("Todos");
        todosClientes.stream()
                .map(Cliente::getNome)
                .filter(Objects::nonNull)
                .sorted()
                .forEach(nomes::add);
        filtroCliente.setItems(FXCollections.observableArrayList(nomes));
        filtroCliente.getSelectionModel().selectFirst();
    }

    // ── FILTROS ───────────────────────────────────────────────────────────
    @FXML
    public void aplicarFiltros() {
        LocalDate hoje    = LocalDate.now();
        String periodo    = filtroPeriodo.getValue();
        String estado     = filtroEstado.getValue();
        String sabor      = filtroSabor.getValue();
        String nomeCliente = filtroCliente.getValue();

        pedidosFiltrados = todosPedidos.stream()
                // filtro período
                .filter(p -> {
                    if (periodo == null || periodo.equals("Todos")) return true;
                    LocalDate data = p.getData();
                    if (data == null) return false;
                    return switch (periodo) {
                        case "Hoje"            -> data.isEqual(hoje);
                        case "Últimos 7 dias"  -> !data.isBefore(hoje.minusDays(7));
                        case "Últimos 30 dias" -> !data.isBefore(hoje.minusDays(30));
                        case "Últimos 90 dias" -> !data.isBefore(hoje.minusDays(90));
                        case "Este mês"        -> data.getMonthValue() == hoje.getMonthValue()
                                && data.getYear() == hoje.getYear();
                        case "Este ano"        -> data.getYear() == hoje.getYear();
                        default                -> true;
                    };
                })
                // filtro estado
                .filter(p -> estado == null || estado.equals("Todos")
                        || estado.equals(p.getEstado()))
                // filtro sabor
                .filter(p -> sabor == null || sabor.equals("Todos")
                        || (p.getPizza() != null && sabor.equals(p.getPizza().getTipo())))
                // filtro cliente
                .filter(p -> nomeCliente == null || nomeCliente.equals("Todos")
                        || (p.getCliente() != null && nomeCliente.equals(p.getCliente().getNome())))
                .collect(Collectors.toList());

        renderizarTudo();
    }

    // ── MODAL CONFIRMAR LIMPAR (mesmo padrão do PedidosController) ────────
    @FXML
    public void confirmarLimparFiltros() {
        Label pergunta = new Label("Deseja limpar filtro?");
        pergunta.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.getStyleClass().add("botao");
        Button btnLimpar = new Button("Limpar filtro");
        btnLimpar.getStyleClass().add("botao-primary");

        HBox rodape = new HBox(20, btnCancelar, btnLimpar);
        rodape.setAlignment(Pos.CENTER);

        VBox card = new VBox(28, pergunta, rodape);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40));
        card.setMaxWidth(440);
        card.setMaxHeight(180);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; "
                + "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.25),20,0,0,4);");

        StackPane overlay = new StackPane(card);
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.45);");
        overlay.setAlignment(Pos.CENTER);

        BorderPane raiz = (BorderPane) listaVendasCliente.getScene().getRoot();
        StackPane camada = new StackPane(raiz.getCenter(), overlay);
        raiz.setCenter(camada);

        Runnable fechar = () -> raiz.setCenter(camada.getChildren().get(0));

        btnCancelar.setOnAction(e -> fechar.run());
        btnLimpar.setOnAction(e -> {
            filtroPeriodo.getSelectionModel().selectFirst();
            filtroEstado.getSelectionModel().selectFirst();
            filtroSabor.getSelectionModel().selectFirst();
            filtroCliente.getSelectionModel().selectFirst();
            fechar.run();
            aplicarFiltros();
        });
    }

    // ── RENDERIZAÇÃO COMPLETA ─────────────────────────────────────────────
    private void renderizarTudo() {
        renderizarMetricas();
        renderizarSaboresMaisVendidos();
        renderizarPedidosPorEstado();
        renderizarVendasPorCliente();
    }

    /** Cards superiores: Faturamento, Custo de reposição, Lucro e Total */
    private void renderizarMetricas() {
        double faturamento = pedidosFiltrados.stream()
                .mapToDouble(Pedido::getValorTotal)
                .sum();

        double custo = 0;
        try {
            custo = reposicaoDAO.calcularGastosReposicao();
        } catch (Exception ignored) {}

        double lucro = faturamento - custo;

        labelFaturamento.setText(formatarReais(faturamento));
        labelCusto.setText(formatarReais(custo));
        labelTotalPedidos.setText(String.valueOf(pedidosFiltrados.size()));

        labelLucro.setText(formatarReais(lucro));
        // cor dinâmica do lucro
        labelLucro.setStyle(lucro >= 0
                ? "-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #27AE60;"
                : "-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #C0392B;");
    }

    /** Top-3 sabores mais pedidos dentro dos filtros */
    private void renderizarSaboresMaisVendidos() {
        boxSaboresMaisVendidos.getChildren().clear();

        Map<String, Long> contagem = pedidosFiltrados.stream()
                .filter(p -> p.getPizza() != null)
                .collect(Collectors.groupingBy(
                        p -> p.getPizza().getTipo(),
                        Collectors.counting()
                ));

        List<Map.Entry<String, Long>> ranking = contagem.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .toList();

        if (ranking.isEmpty()) {
            Label vazio = new Label("—");
            vazio.setStyle("-fx-text-fill: #888; -fx-font-size: 13px;");
            boxSaboresMaisVendidos.getChildren().add(vazio);
            return;
        }

        String[] posicoes = {"1º", "2º", "3º"};
        for (int i = 0; i < ranking.size(); i++) {
            Label l = new Label(posicoes[i] + " " + ranking.get(i).getKey());
            l.setStyle("-fx-font-size: 13px; -fx-text-fill: #1A1A1A;");
            boxSaboresMaisVendidos.getChildren().add(l);
        }
    }

    /** Mini-card por estado com cor igual a criarLabelEstado do PedidosController */
    private void renderizarPedidosPorEstado() {
        boxEstados.getChildren().clear();

        String[] estados = {"Pendente", "Em preparo", "Pronto", "Entregue", "Cancelado"};
        String[] cores   = {"#E67E22",  "#2980B9",    "#8E44AD","#27AE60",  "#C0392B"};

        for (int i = 0; i < estados.length; i++) {
            final String est = estados[i];
            long qtd = pedidosFiltrados.stream()
                    .filter(p -> est.equals(p.getEstado()))
                    .count();

            Label lEstado = new Label(est);
            lEstado.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; "
                    + "-fx-text-fill: " + cores[i] + ";");

            Label lQtd = new Label(String.valueOf(qtd));
            lQtd.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1A1A1A;");

            VBox card = new VBox(4, lEstado, lQtd);
            card.setAlignment(Pos.CENTER);
            card.setPadding(new Insets(14));
            card.setPrefWidth(120);
            card.setStyle("-fx-background-color: #F2F0EB; -fx-background-radius: 12;");
            HBox.setHgrow(card, Priority.ALWAYS);

            boxEstados.getChildren().add(card);
        }
    }

    /** Tabela de vendas agrupadas por cliente, ordenada por valor total */
    private void renderizarVendasPorCliente() {
        listaVendasCliente.getChildren().clear();

        // cliente → [qtd_pedidos, soma_centavos]
        Map<String, long[]> agrupado = new LinkedHashMap<>();
        for (Pedido p : pedidosFiltrados) {
            String nome = p.getCliente() != null ? p.getCliente().getNome() : "Desconhecido";
            agrupado.computeIfAbsent(nome, k -> new long[]{0, 0});
            agrupado.get(nome)[0]++;
            agrupado.get(nome)[1] += Math.round(p.getValorTotal() * 100);
        }

        List<Map.Entry<String, long[]>> lista = agrupado.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue()[1], a.getValue()[1]))
                .toList();

        for (Map.Entry<String, long[]> entry : lista) {
            HBox linha = new HBox();
            linha.setPadding(new Insets(10, 0, 10, 0));
            linha.getStyleClass().add("linha-tabela");

            Label lNome = new Label(entry.getKey());
            lNome.setPrefWidth(260);
            lNome.setStyle("-fx-text-fill: #1A1A1A; -fx-font-weight: bold;");

            Label lQtd = new Label(String.valueOf(entry.getValue()[0]));
            lQtd.setPrefWidth(120);
            lQtd.setStyle("-fx-text-fill: #555;");

            Label lValor = new Label(formatarReais(entry.getValue()[1] / 100.0));
            lValor.setPrefWidth(140);
            lValor.setStyle("-fx-text-fill: #1A1A1A; -fx-font-weight: 600;");

            linha.getChildren().addAll(lNome, lQtd, lValor);
            listaVendasCliente.getChildren().add(linha);
            listaVendasCliente.getChildren().add(new Separator());
        }

        if (lista.isEmpty()) {
            Label vazio = new Label("Nenhum pedido encontrado para os filtros selecionados.");
            vazio.setStyle("-fx-text-fill: #888; -fx-font-size: 13px;");
            listaVendasCliente.getChildren().add(vazio);
        }
    }

    // ── EXPORTAR CSV ──────────────────────────────────────────────────────

    @FXML
    private void exportarRelatorio(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Salvar relatório");
        chooser.setInitialFileName("relatorio_pizzaria_"
                + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv"),
                new FileChooser.ExtensionFilter("Texto (*.txt)", "*.txt")
        );

        javafx.stage.Window janela = listaVendasCliente.getScene().getWindow();
        File arquivo = chooser.showSaveDialog(janela);
        if (arquivo == null) return; // usuário cancelou

        try (PrintWriter pw = new PrintWriter(
                new FileWriter(arquivo, StandardCharsets.UTF_8))) {
            escreverCsv(pw);
            mostrarInfo("Relatório exportado com sucesso!\n" + arquivo.getAbsolutePath());
        } catch (Exception e) {
            mostrarErro("Erro ao exportar: " + e.getMessage());
        }
    }

    private void escreverCsv(PrintWriter pw) {
        // ── Cabeçalho ────────────────────────────────────────────────────
        pw.println("LA PIAZZA PIZZARIA - RELATÓRIO GERENCIAL");
        pw.println("Gerado em:," + LocalDate.now().format(FMT));
        pw.println();
        pw.println("FILTROS APLICADOS");
        pw.println("Período:,"   + filtroPeriodo.getValue());
        pw.println("Estado:,"    + filtroEstado.getValue());
        pw.println("Sabor:,"     + filtroSabor.getValue());
        pw.println("Cliente:,"   + filtroCliente.getValue());
        pw.println();

        // ── Resumo financeiro ─────────────────────────────────────────────
        double faturamento = pedidosFiltrados.stream()
                .mapToDouble(Pedido::getValorTotal).sum();
        double custo = 0;
        try { custo = reposicaoDAO.calcularGastosReposicao(); } catch (Exception ignored) {}
        double lucro = faturamento - custo;

        pw.println("RESUMO FINANCEIRO");
        pw.println("Faturamento (vendas):," + formatarReais(faturamento));
        pw.println("Custo de reposição de estoque:," + formatarReais(custo));
        pw.println("Lucro (Faturamento - Custo):," + formatarReais(lucro));
        pw.println("Total de pedidos:," + pedidosFiltrados.size());
        pw.println();

        // ── Pedidos por estado ────────────────────────────────────────────
        pw.println("PEDIDOS POR ESTADO");
        pw.println("Estado,Quantidade");
        for (String est : new String[]{"Pendente","Em preparo","Pronto","Entregue","Cancelado"}) {
            long qtd = pedidosFiltrados.stream()
                    .filter(p -> est.equals(p.getEstado())).count();
            pw.println(est + "," + qtd);
        }
        pw.println();

        // ── Sabores mais vendidos ─────────────────────────────────────────
        pw.println("SABORES MAIS VENDIDOS");
        pw.println("Posição,Sabor,Quantidade de pedidos");
        Map<String, Long> contagem = pedidosFiltrados.stream()
                .filter(p -> p.getPizza() != null)
                .collect(Collectors.groupingBy(
                        p -> p.getPizza().getTipo(), Collectors.counting()));
        int pos = 1;
        for (Map.Entry<String, Long> e : contagem.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()).toList()) {
            pw.println(pos++ + "º," + e.getKey() + "," + e.getValue());
        }
        pw.println();

        // ── Vendas por cliente ────────────────────────────────────────────
        pw.println("VENDAS POR CLIENTE");
        pw.println("Cliente,Quantidade de pedidos,Valor total");
        Map<String, long[]> agrupado = new LinkedHashMap<>();
        for (Pedido p : pedidosFiltrados) {
            String nome = p.getCliente() != null ? p.getCliente().getNome() : "Desconhecido";
            agrupado.computeIfAbsent(nome, k -> new long[]{0, 0});
            agrupado.get(nome)[0]++;
            agrupado.get(nome)[1] += Math.round(p.getValorTotal() * 100);
        }
        agrupado.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue()[1], a.getValue()[1]))
                .forEach(e -> pw.println(
                        csv(e.getKey()) + ","
                                + e.getValue()[0] + ","
                                + formatarReais(e.getValue()[1] / 100.0)));
        pw.println();

        // ── Detalhamento dos pedidos ──────────────────────────────────────
        pw.println("DETALHAMENTO DOS PEDIDOS");
        pw.println("ID,Cliente,Pizza,Tamanho,Adicionais,Estado,Data,Valor Total");
        for (Pedido p : pedidosFiltrados) {
            String nomeCliente = p.getCliente() != null ? p.getCliente().getNome() : "—";
            String tipoPizza   = p.getPizza()   != null ? p.getPizza().getTipo()   : "—";
            int qtdAdic = p.getAdicionais() != null ? p.getAdicionais().size() : 0;
            String dataStr = p.getData() != null ? p.getData().format(FMT) : "—";
            pw.println(p.getIdPedido() + ","
                    + csv(nomeCliente) + ","
                    + csv(tipoPizza)   + ","
                    + p.getTamanho()   + ","
                    + qtdAdic + " item(s),"
                    + p.getEstado()    + ","
                    + dataStr          + ","
                    + formatarReais(p.getValorTotal()));
        }
    }

    // ── UTILITÁRIOS ───────────────────────────────────────────────────────
    private String formatarReais(double valor) {
        return String.format("R$ %.2f", valor).replace(".", ",");
    }

    /** Escapa campo CSV: envolve em aspas se tiver vírgula, aspas ou quebra de linha */
    private String csv(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    private void mostrarErro(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Erro"); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }

    private void mostrarInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Exportado"); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }

    // ── NAVEGAÇÃO (mesmo padrão exato de PedidosController) ──────────────
    @FXML private void irPedidos(ActionEvent e)    { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/Pedidos.fxml",                  "Pedidos"); }
    @FXML private void irClientes(ActionEvent e)   { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/GerenciarClientesView.fxml",    "Clientes"); }
    @FXML private void irTiposPizza(ActionEvent e) { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/GerenciarPizzasView.fxml",      "Tipos de pizza"); }
    @FXML private void irAdicionais(ActionEvent e) { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/GerenciarAdicionaisView.fxml",  "Adicionais"); }
    @FXML private void irEstoque(ActionEvent e)    { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/EstoqueView.fxml",              "Estoque"); }
    @FXML private void irRelatorios(ActionEvent e) { /* já está aqui */ }
    @FXML private void irFuncionarios(ActionEvent e) { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/GerenciarFuncionariosView.fxml", "La Piazza - Funcionários"); }
    @FXML private void sair(ActionEvent e)         { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/LoginView.fxml",                "La Piazza Pizzaria"); }
}