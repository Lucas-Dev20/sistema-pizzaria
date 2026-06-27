package br.edu.ufersa.poo.pizzaria.controllers;

import br.edu.ufersa.poo.pizzaria.viewmodel.OrderViewModel;
import br.edu.ufersa.poo.pizzaria.util.ConnectionFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.util.ResourceBundle;

public class PainelController implements Initializable {

    @FXML private TableView<OrderViewModel> tableViewOrders;
    @FXML private TableColumn<OrderViewModel, String> tableColumnCliente;
    @FXML private TableColumn<OrderViewModel, String> tableColumnDescricao;
    @FXML private TableColumn<OrderViewModel, Integer> tableColumnID;
    @FXML private TableColumn<OrderViewModel, Double> tableColumnValor;

    @FXML private Label pedidosPendentes;
    @FXML private Label emPreparo;
    @FXML private Label faturamento;
    @FXML private Label totalClientes;

    @FXML void menuPedidos(ActionEvent event) { LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/Pedidos.fxml", "La Piazza - Pedidos"); }
    @FXML void menuClientes(ActionEvent event) { LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/GerenciarClientesView.fxml", "La Piazza - Clientes"); }
    @FXML void menuTiposPizzas(ActionEvent event) { LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/GerenciarPizzasView.fxml", "La Piazza - Pizzas"); }
    @FXML void menuAdicionais(ActionEvent event) { LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/GerenciarAdicionaisView.fxml", "La Piazza - Adicionais"); }
    @FXML void menuEstoque(ActionEvent event) { LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/EstoqueView.fxml", "La Piazza - Estoque"); }
    @FXML void menuRelatorio(ActionEvent event) { LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/RelatorioView.fxml", "La Piazza - Relatório"); }
    @FXML void menuFuncionarios(ActionEvent event) { LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/GerenciarFuncionariosView.fxml", "La Piazza - Funcionários"); }
    @FXML void logout(ActionEvent event) { LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/LoginView.fxml", "La Piazza - Login"); }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initLabels();
        initTableViewOrders();
    }

    private void initTableViewOrders() {
        setTableColumns();
        List<OrderViewModel> orderViewModels = getOrderList();
        ObservableList<OrderViewModel> observableList = FXCollections.observableList(orderViewModels);
        tableViewOrders.getItems().clear();
        tableViewOrders.getItems().addAll(observableList);
    }

    // BUSCA OS DADOS CONECTANDO AS TABELAS PEDIDO, CLIENTE E PIZZAS
    private List<OrderViewModel> getOrderList() {
        List<OrderViewModel> modelList = new ArrayList<>();

        // SQL com INNER JOIN para pegar o nome do cliente e o tipo da pizza
        String sql = "SELECT p.id_pedido, c.nome AS nome_cliente, piz.tipo AS descricao_pizza, piz.valor " +
                "FROM pedido p " +
                "INNER JOIN cliente c ON p.id_cliente = c.id_cliente " +
                "INNER JOIN pizzas piz ON p.id_pizza = piz.id_pizza " +
                "ORDER BY p.id_pedido DESC LIMIT 10";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                OrderViewModel model = new OrderViewModel();

                // Mapeia as colunas do banco para as propriedades do seu ViewModel
                model.setIdCliente(rs.getInt("id_pedido")); // Exibe o número do pedido na coluna de ID
                model.setNomeCliente(rs.getString("nome_cliente"));
                model.setDescricao(rs.getString("descricao_pizza"));
                model.setValor(rs.getDouble("valor"));

                modelList.add(model);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao carregar lista de pedidos: " + e.getMessage());
            e.printStackTrace();
        }

        return modelList;
    }

    private void setTableColumns() {
        tableColumnID.setCellValueFactory(cell -> cell.getValue().idClienteProperty().asObject());
        tableColumnCliente.setCellValueFactory(cell -> cell.getValue().nomeClienteProperty());
        tableColumnDescricao.setCellValueFactory(cell -> cell.getValue().descricaoProperty());
        tableColumnValor.setCellValueFactory(cell -> cell.getValue().valorProperty().asObject());
    }

    // ATUALIZA OS INDICADORES COM BASE NAS SUAS TABELAS
    private void initLabels() {
        // Conta os pedidos que estão com o estado padrão 'EM_ANDAMENTO'
        String sqlPendentes = "SELECT COUNT(*) FROM pedido WHERE estado = 'EM_ANDAMENTO'";
        String sqlClientes = "SELECT COUNT(*) FROM cliente";

        // Soma o valor de todas as pizzas que foram pedidas no sistema
        String sqlFaturamento = "SELECT SUM(piz.valor) FROM pedido p " +
                "INNER JOIN pizzas piz ON p.id_pizza = piz.id_pizza";

        try (Connection conn = ConnectionFactory.getConnection()) {

            // 1. Pedidos Pendentes / Em Andamento
            try (PreparedStatement stmt = conn.prepareStatement(sqlPendentes); ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    pedidosPendentes.setText(String.valueOf(rs.getInt(1)));
                    // Como seu banco só tem 'EM_ANDAMENTO' por padrão, deixei o preparo zerado ou igual temporariamente
                    emPreparo.setText("0");
                }
            }

            // 2. Total de Clientes
            try (PreparedStatement stmt = conn.prepareStatement(sqlClientes); ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    totalClientes.setText(String.valueOf(rs.getInt(1)));
                }
            }

            // 3. Faturamento Total (Baseado no valor das pizzas pedidas)
            try (PreparedStatement stmt = conn.prepareStatement(sqlFaturamento); ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double totalFaturado = rs.getDouble(1);
                    faturamento.setText(String.format("R$ %.2f", totalFaturado));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao inicializar os indicadores do painel: " + e.getMessage());
            e.printStackTrace();
        }
    }
}