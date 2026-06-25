package br.edu.ufersa.poo.pizzaria.controllers;

import br.edu.ufersa.poo.pizzaria.model.entities.Adicional;
import br.edu.ufersa.poo.pizzaria.model.services.AdicionalService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class GerenciarAdicionaisController {

    @FXML private TextField txtBusca;
    @FXML private Button btnNovoAdicional;
    @FXML private TableView<?> tabelaAdicionais; // Mantido o original <?>
    @FXML private TableColumn<?, String> colNome;
    @FXML private TableColumn<?, Double> colPreco;
    @FXML private TableColumn<?, String> colEstoque;
    @FXML private TableColumn<?, Void> colAcoes;
    private final AdicionalService adicionalService = new AdicionalService();
    private ObservableList<Adicional> listaAdicionaisOb = FXCollections.observableArrayList();
    @FXML
    public void initialize() {
        // carrega o bd
        atualizarTabela();
    }

    private void atualizarTabela() {
        // ENVOLVIDO EM TRY/CATCH PARA GARANTIR QUE ERROS NO BANCO NÃO TRAVEM A INTERFACE
        try {
            listaAdicionaisOb.clear();

            // MENSAGEM EM CAPS LOCK: BUSCANDO OS DADOS REAIS DO BANCO ATRAVÉS DA SERVICE
            listaAdicionaisOb.addAll(adicionalService.listarTodosAdicionais());

            // JOGA OS DADOS DENTRO DA TABELA DA TELA
            tabelaAdicionais.setItems((ObservableList) listaAdicionaisOb);
        } catch (Exception e) {
            System.err.println("MENSAGEM EM CAPS LOCK: ERRO AO CARREGAR DADOS DE ADICIONAIS DO BANCO DE DADOS.");
            e.printStackTrace();

            // ALERTA PARA AVISAR O USUÁRIO SEM DERRUBAR A NAVEGAÇÃO DA TELA
            Alert erro = new Alert(Alert.AlertType.ERROR);
            erro.setTitle("Erro de Carregamento");
            erro.setHeaderText(null);
            erro.setContentText("Não foi possível carregar os adicionais do banco de dados.");
            erro.showAndWait();
        }
    }

    @FXML
    private void handleNovoAdicional(ActionEvent event) {
        LoginController.abrirModal("/br/edu/ufersa/pizzaria/views/CadastrarAdicionalView.fxml", "Novo Adicional");
        atualizarTabela();
    }

    private void handleExcluirAdicional(Adicional adicionalSelecionado) {
        // PROTEÇÃO: Adicionada aqui no topo de forma segura
        if (adicionalSelecionado == null) {
            Alert avisoSelection = new Alert(Alert.AlertType.WARNING);
            avisoSelection.setTitle("Aviso");
            avisoSelection.setHeaderText(null);
            avisoSelection.setContentText("Por favor, selecione um adicional na tabela para poder excluir.");
            avisoSelection.showAndWait();
            return;
        }

        // Cria o alerta de confirmação customizado para o pop-up
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deseja excluir?");
        alert.setHeaderText(null);
        alert.setContentText("Você tem certeza que deseja deletar o adicional \"" + adicionalSelecionado.getNome() + "\"?");

        // Define os botões de opção corretamente com ButtonData
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType btnExcluir = new ButtonType("Excluir", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(btnCancelar, btnExcluir);

        // Abre o pop-up na tela e espera o clique
        java.util.Optional<ButtonType> resultado = alert.showAndWait();

        if (resultado.isPresent() && resultado.get() == btnExcluir) {
            try {
                adicionalService.removerAdicional(adicionalSelecionado.getNome());

                Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
                sucesso.setTitle("Sucesso");
                sucesso.setHeaderText(null);
                sucesso.setContentText("Adicional removido com sucesso!");
                sucesso.showAndWait();

                atualizarTabela();

            } catch (IllegalArgumentException e) {
                Alert aviso = new Alert(Alert.AlertType.WARNING);
                aviso.setTitle("Aviso de Validação");
                aviso.setHeaderText(null);
                aviso.setContentText(e.getMessage());
                aviso.showAndWait();
            } catch (Exception e) {
                Alert erro = new Alert(Alert.AlertType.ERROR);
                erro.setTitle("Erro");
                erro.setHeaderText(null);
                erro.setContentText("Não foi possível excluir o adicional do banco de dados.");
                erro.showAndWait();
                e.printStackTrace();
            }
        }
    }

    //METODOS DE NAVEGAÇÃO ORIGINAIS
    @FXML
    private void irPedidos(ActionEvent event) {
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/Pedidos.fxml", "La Piazza - Pedidos");
    }

    @FXML
    private void irClientes(ActionEvent event) {
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/GerenciarClientesView.fxml", "La Piazza - Clientes");
    }

    @FXML
    private void irTiposPizza(ActionEvent event) {
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/GerenciarPizzasView.fxml", "La Piazza - Tipos de Pizza");
    }

    @FXML
    private void irAdicionais(ActionEvent event) {
        atualizarTabela();
    }

    @FXML
    private void irEstoque(ActionEvent event) {
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/EstoqueView.fxml", "La Piazza - Estoque");
    }

    @FXML
    private void irRelatorios(ActionEvent event) {
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/RelatorioView.fxml", "La Piazza - Relatórios");
    }

    @FXML
    private void sair(ActionEvent event) {
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/LoginView.fxml", "La Piazza Pizzaria");
    }
}