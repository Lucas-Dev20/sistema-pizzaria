package br.edu.ufersa.poo.pizzaria.controllers;

import br.edu.ufersa.poo.pizzaria.model.entities.Adicional;
import br.edu.ufersa.poo.pizzaria.model.services.AdicionalService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class GerenciarAdicionaisController {

    @FXML private TextField txtBusca;
    @FXML private Button btnNovoAdicional;
    @FXML private TableView<?> tabelaAdicionais; // Ajuste o tipo <?> para sua entidade Adicional depois
    @FXML private TableColumn<?, String> colNome;
    @FXML private TableColumn<?, Double> colPreco;
    @FXML private TableColumn<?, String> colEstoque;
    @FXML private TableColumn<?, Void> colAcoes;
    private final AdicionalService adicionalService = new AdicionalService();

    @FXML
    public void initialize() {
        // Inicializa mapeamento das colunas e carrega dados
        atualizarTabela();
    }

    private void atualizarTabela() {
        System.out.println("Buscando adicionais do banco de dados...");
    }

    @FXML
    private void handleNovoAdicional(ActionEvent event) {
        LoginController.abrirModal("/br/edu/ufersa/pizzaria/views/CadastrarAdicionalView.fxml", "Novo Adicional");
        atualizarTabela(); // volta à tela de adicionais principal assim que o popup fechar!
    }

    private void handleExcluirAdicional(Adicional adicionalSelecionado) {
        //  Cria o alerta de confirmação customizado para o pop-up
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deseja excluir?");
        alert.setHeaderText(null);
        alert.setContentText("Você tem certeza que deseja deletar o adicional \"" + adicionalSelecionado.getNome() + "\"?");

        // Define os botões de opção
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType btnExcluir = new ButtonType("Excluir", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(btnCancelar, btnExcluir);

        // Abre o pop-up na tela e espera o clique
        java.util.Optional<ButtonType> resultado = alert.showAndWait();

        if (resultado.isPresent() && resultado.get() == btnExcluir) {
            try {
                // PROTEÇÃO: Evita que o sistema quebre se não houver linha selecionada
                if (adicionalSelecionado == null) {
                    Alert avisoSelection = new Alert(Alert.AlertType.WARNING);
                    avisoSelection.setTitle("Aviso");
                    avisoSelection.setHeaderText(null);
                    avisoSelection.setContentText("Por favor, selecione um adicional na tabela para poder excluir.");
                    avisoSelection.showAndWait();
                    return;
                }
                // 4. Dispara a regra de remoção passando o Nome do adicional conforme sua Service exige
                adicionalService.removerAdicional(adicionalSelecionado.getNome());

                // 5. Exibe o feedback de sucesso ao usuário
                Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
                sucesso.setTitle("Sucesso");
                sucesso.setHeaderText(null);
                sucesso.setContentText("Adicional removido com sucesso!");
                sucesso.showAndWait();

                // 6. Atualiza a tabela do painel de fundo imediatamente
                atualizarTabela();

            } catch (IllegalArgumentException e) {
                // Captura o erro caso o nome seja vazio ou inválido pela Service
                Alert aviso = new Alert(Alert.AlertType.WARNING);
                aviso.setTitle("Aviso de Validação");
                aviso.setHeaderText(null);
                aviso.setContentText(e.getMessage());
                aviso.showAndWait();
            } catch (Exception e) {
                // Tratamento geral para falhas de conexão ou SQL
                Alert erro = new Alert(Alert.AlertType.ERROR);
                erro.setTitle("Erro");
                erro.setHeaderText(null);
                erro.setContentText("Não foi possível excluir o adicional do banco de dados.");
                erro.showAndWait();
                e.printStackTrace();
            }
        }
    }

//METODOS DE NAVEGAÇÃO
    @FXML
    private void irPedidos(ActionEvent event) {
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/GerenciarPedidosView.fxml", "La Piazza - Pedidos");
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
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/GerenciarEstoqueView.fxml", "La Piazza - Estoque");
    }

    @FXML
    private void irRelatorios(ActionEvent event) {
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/GerenciarRelatoriosView.fxml", "La Piazza - Relatórios");
    }

    @FXML
    private void sair(ActionEvent event) {
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/LoginView.fxml", "La Piazza Pizzaria");
    }
}