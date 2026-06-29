package br.edu.ufersa.poo.pizzaria.controllers;

import br.edu.ufersa.poo.pizzaria.model.entities.Cliente;
import br.edu.ufersa.poo.pizzaria.model.services.ClienteService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class GerenciarClientesController {

    @FXML
    private TextField txtBusca;
    @FXML
    private TableView<Cliente> tabelaClientes;
    @FXML
    private TableColumn<Cliente, String> colNome;
    @FXML
    private TableColumn<Cliente, String> colCpf;
    @FXML
    private TableColumn<Cliente, String> colTelefone;
    @FXML
    private TableColumn<Cliente, String> colEndereco;
    @FXML
    private TableColumn<Cliente, String> colBairro;
    @FXML
    private TableColumn<Cliente, Void> colAcoes; // Coluna dos botões

    private final ClienteService clienteService = new ClienteService();
    private ObservableList<Cliente> listaClientesOb = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        //mapeia as colunas normais
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        colTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
        colEndereco.setCellValueFactory(new PropertyValueFactory<>("endereco"));
        colBairro.setCellValueFactory(new PropertyValueFactory<>("bairro"));

        //cria e adiciona os botões de Editar e Excluir na coluna de Ações
        configurarBotoesAcao();

        //carrega os dados do banco
        atualizarTabela();
    }


    private void configurarBotoesAcao() {
        Callback<TableColumn<Cliente, Void>, TableCell<Cliente, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Cliente, Void> call(final TableColumn<Cliente, Void> param) {
                return new TableCell<>() {
                    private final Button btnEditar = new Button("✏");
                    private final Button btnExcluir = new Button("🗑");
                    private final HBox container = new HBox(8, btnEditar, btnExcluir);

                    {
                        // Estilo do botão editar
                        btnEditar.getStyleClass().add("btn-icone");
                        btnEditar.setOnAction(event -> {
                            Cliente clienteSelecionado = getTableView().getItems().get(getIndex());
                            handleEditarCliente(clienteSelecionado);
                        });

                        // Estilo do botão excluir
                        btnExcluir.getStyleClass().addAll("btn-icone", "btn-icone-excluir");
                        btnExcluir.setOnAction(event -> {
                            Cliente clienteSelecionado = getTableView().getItems().get(getIndex());
                            handleExcluirCliente(clienteSelecionado);
                        });

                        container.setStyle("-fx-alignment: center;");
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/br/edu/ufersa/pizzaria/views/EditarClienteView.fxml"));
            Parent root = loader.load();

            // passa o cliente para o controller antess de exibir o modal
            EditarClienteController controllerEdicao = loader.getController();
            controllerEdicao.preencherCampos(cliente);

            // abre como modal (Stage filho)
            javafx.stage.Stage modal = new javafx.stage.Stage();
            modal.setTitle("La Piazza - Editar Cliente");
            modal.setScene(new javafx.scene.Scene(root));
            modal.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            modal.initOwner(tabelaClientes.getScene().getWindow());
            modal.showAndWait(); // bloqueia até fechar

            // após fechar, atualiza a tabela automaticamente
            atualizarTabela();

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private void handleExcluirCliente(Cliente cliente) {
        //alerta de confirmação para excluir
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText(null);
        alert.setContentText("Deseja realmente excluir o cliente " + cliente.getNome() + "?");

        //opções de escolha 'sim ou nao'
        ButtonType btnExcluir = new ButtonType("Excluir");
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnCancelar, btnExcluir);

        //tela para resposta
        alert.showAndWait().ifPresent(resposta -> {
            if (resposta == btnExcluir) {
                try {
                    //chama a service para fazer o DELETE no MySQL pelo id do cliente
                    clienteService.removerCliente(cliente.getIdCliente());
                    mostrarAvisoInformativo("Sucesso", "Cliente excluído com sucesso!");

                    atualizarTabela(); //excluido e atualizado

                } catch (Exception e) {
                    mostrarAvisoErro("Erro", "Não foi possível excluir o cliente do banco de dados.");
                    e.printStackTrace();
                }
            }
            // caso clique em cancelar, fecha sozinho e nada acontece
        });
    }

    // outros metodos auxiliares para os alertas de feedback
    private void mostrarAvisoInformativo(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void mostrarAvisoErro(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void handleNovoCliente(ActionEvent event) {
        LoginController.abrirModal("/br/edu/ufersa/pizzaria/views/CadastrarClienteView.fxml", "La Piazza - Novo Cliente");
        atualizarTabela();
    }
    //METODOS DE NAVEGAÇÃO

    @FXML private void irPainel(ActionEvent event) {LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/PainelDeControle.fxml", "La Piazza - Painel de Controle");}

    @FXML
    private void irPedidos(ActionEvent event) {
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/Pedidos.fxml", "La Piazza - Pedidos");
    }

    @FXML
    private void irClientes(ActionEvent event) {
        atualizarTabela(); // já está na tela clientes entao seria um f5 digamos
    }

    @FXML
    private void irTiposPizza(ActionEvent event) {
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/GerenciarPizzasView.fxml", "La Piazza - Tipos de Pizza");
    }

    @FXML
    private void irAdicionais(ActionEvent event) {
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/GerenciarAdicionaisView.fxml", "La Piazza - Adicionais");
    }

    @FXML
    private void irEstoque(ActionEvent event) {
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/EstoqueView.fxml", "La Piazza - Estoque");
    }

    @FXML
    private void irRelatorios(ActionEvent event) {
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/RelatorioView.fxml", "La Piazza - Relatórios");
    }

    @FXML private void irFuncionarios(ActionEvent event){LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/GerenciarFuncionariosView.fxml", "La Piazza - Funcionários");}

    @FXML
    private void sair(ActionEvent event) {
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/LoginView.fxml", "La Piazza Pizzaria");
    }
}