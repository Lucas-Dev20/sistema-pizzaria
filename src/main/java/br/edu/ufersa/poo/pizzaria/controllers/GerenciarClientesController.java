package br.edu.ufersa.poo.pizzaria.controllers;

import br.edu.ufersa.poo.pizzaria.model.entities.Cliente;
import br.edu.ufersa.poo.pizzaria.model.services.ClienteService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class GerenciarClientesController {

    @FXML private TextField txtBusca;
    @FXML private TableView<Cliente> tabelaClientes;
    @FXML private TableColumn<Cliente, String> colNome;
    @FXML private TableColumn<Cliente, String> colCpf;
    @FXML private TableColumn<Cliente, String> colTelefone;
    @FXML private TableColumn<Cliente, String> colEndereco;
    @FXML private TableColumn<Cliente, String> colBairro;
    @FXML private TableColumn<Cliente, Void> colAcoes; // Coluna dos botões

    private final ClienteService clienteService = new ClienteService();
    private ObservableList<Cliente> listaClientesOb = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 1. Mapeia as colunas normais
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        colTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
        colEndereco.setCellValueFactory(new PropertyValueFactory<>("endereco"));
        colBairro.setCellValueFactory(new PropertyValueFactory<>("bairro"));

        // 2. Cria e adiciona os botões de Editar e Excluir na coluna de Ações
        configurarBotoesAcao();

        // 3. Carrega os dados do banco
        atualizarTabela();
    }

    private void configurarBotoesAcao() {
        Callback<TableColumn<Cliente, Void>, TableCell<Cliente, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Cliente, Void> call(final TableColumn<Cliente, Void> param) {
                return new TableCell<>() {
                    private final Button btnEditar = new Button("✏️");
                    private final Button btnExcluir = new Button("🗑️");
                    private final HBox container = new HBox(10, btnEditar, btnExcluir);

                    {
                        // Estilização rápida via código (ou você pode criar classes no CSS)
                        btnEditar.setStyle("-fx-cursor: hand; -fx-background-color: transparent; -fx-text-fill: #333333;");
                        btnExcluir.setStyle("-fx-cursor: hand; -fx-background-color: transparent; -fx-text-fill: #B03A2A;");
                        container.setStyle("-fx-alignment: center;");

                        // AÇÃO DO BOTÃO EDITAR
                        btnEditar.setOnAction(event -> {
                            Cliente clienteSelecionado = getTableView().getItems().get(getIndex());
                            handleEditarCliente(clienteSelecionado);
                        });

                        // AÇÃO DO BOTÃO EXCLUIR
                        btnExcluir.setOnAction(event -> {
                            Cliente clienteSelecionado = getTableView().getItems().get(getIndex());
                            handleExcluirCliente(clienteSelecionado);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(container); // Desenha os botões na linha
                        }
                    }
                };
            }
        };

        colAcoes.setCellFactory(cellFactory);
    }

    private void atualizarTabela() {
        listaClientesOb.clear();
        listaClientesOb.addAll(clienteService.listarTodosClientes());
        tabelaClientes.setItems(listaClientesOb);
    }

    private void handleEditarCliente(Cliente cliente) {
        System.out.println("Editando o cliente: " + cliente.getNome() + " (ID: " + cliente.getIdCliente() + ")");
        // Aqui você vai abrir a tela de formulário passando os dados dele para preencher os inputs!
    }

    private void handleExcluirCliente(Cliente cliente) {
        System.out.println("Deletando o cliente: " + cliente.getNome());
        // Chama a sua service para apagar pelo telefone (como está na sua DAO)
        clienteService.removerCliente(cliente.getTelefone());
        atualizarTabela(); // Recarrega a tabela na hora, sumindo com ele da tela!
    }

    @FXML
    private void handleNovoCliente(ActionEvent event) {
        System.out.println("Abrindo tela de cadastro de cliente...");
    }

    @FXML
    private void handleSair(ActionEvent event) {
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/LoginView.fxml", "La Piazza - Login");
    }
}