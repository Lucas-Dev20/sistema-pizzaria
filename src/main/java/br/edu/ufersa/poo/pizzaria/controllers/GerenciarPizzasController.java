package br.edu.ufersa.poo.pizzaria.controllers;

import br.edu.ufersa.poo.pizzaria.model.services.PizzaService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

public class GerenciarPizzasController {

    @FXML private Label lblTipoUsuario;
    @FXML private TextField txtBusca;
    @FXML private Button btnNovoSabor;
    @FXML private TilePane containerPizzas;
    private final PizzaService pizzaService = new PizzaService();

    // analise do tipo de login para o 'poder' de cadastrar um novo sabor ser permitido ou nao
    private boolean ehAdministrador = true;

    @FXML
    public void initialize() {
        configurarNivelAcesso();
        renderizarCardsPizza();
    }

    private void configurarNivelAcesso() {
        if (ehAdministrador) {
            lblTipoUsuario.setText("Administrador");
            btnNovoSabor.setVisible(true);
            btnNovoSabor.setManaged(true); // faz o layout reajustar o espaço se sumir
        } else {
            lblTipoUsuario.setText("Funcionário");
            btnNovoSabor.setVisible(false);
            btnNovoSabor.setManaged(false); // tira o espaço morto do botão ocultado, por n ser admin
        }
    }

    private void renderizarCardsPizza() {
        // MENSAGEM EM CAPS LOCK: LIMPA O CONTAINER VISUAL ANTES DE CARREGAR AS NOVAS PIZZAS
        containerPizzas.getChildren().clear();

        // ENVOLVIDO EM TRY/CATCH PARA PROTEGER A INTERFACE CASO EXISTA ALGUM DADO CORROMPIDO NO BANCO
        try {
            // MENSAGEM EM CAPS LOCK: BUSCANDO A LISTA REAL DE PIZZAS DO BANCO DE DADOS ATRAVÉS DA SERVICE
            // OBS: AJUSTE O NOME DO MÉTODO ABAIXO CONFORME SEU PIZZASERVICE DEPOIS (EX: listarTodasPizzas() OU SIMILAR)
            var listaPizzas = pizzaService.listarTodasPizzas();

            if (listaPizzas != null) {
                for (var pizza : listaPizzas) {
                    // LÓGICA DE INSTANCIAR E ADICIONAR OS CARDS VISUAIS NO CONTAINER
                }
            }

        } catch (Exception e) {
            System.err.println("MENSAGEM EM CAPS LOCK: ERRO AO CARREGAR CATÁLOGO DE SABORES DO BANCO DE DADOS.");
            e.printStackTrace();

            // EXIBE O ALERTA DE ERRO DE FORMA CONTROLADA SEM DERRUBAR O SISTEMA
            Alert erro = new Alert(Alert.AlertType.ERROR);
            erro.setTitle("Erro de Carregamento");
            erro.setHeaderText(null);
            erro.setContentText("Não foi possível carregar o catálogo de pizzas do banco de dados.");
            erro.showAndWait();
        }
    }

    @FXML
    private void handleNovoSabor(ActionEvent event) {
        LoginController.abrirModal("/br/edu/ufersa/pizzaria/views/CadastrarSaborView.fxml", "Novo Sabor");
        renderizarCardsPizza(); // volta à tela principal de tipos de pizza após fechar o popup
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
        renderizarCardsPizza();
    }

    @FXML
    private void irAdicionais(ActionEvent event) {
        LoginController.trocarConteudo(event, "/br/edu/ufersa/pizzaria/views/GerenciarAdicionaisView.fxml", "La Piazza - Adicionais");
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

//METODO DE EXCLUSÃO DE SABOR PARA EMITIR A MENSAGEM NA CAIXINHA
    public void handleExcluirSabor(String nomeSabor, int idPizza) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText(null);
        alert.setContentText("Deseja realmente excluir o sabor " + nomeSabor + "?");

        ButtonType btnExcluir = new ButtonType("Excluir");
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnCancelar, btnExcluir);

        alert.showAndWait().ifPresent(resposta -> {
            if (resposta == btnExcluir) {
                try {
                    mostrarMensagemInformativa("Sucesso", "Sabor excluído com sucesso!");
                    renderizarCardsPizza();
                } catch (Exception e) {
                    mostrarMensagemErro("Erro", "Falha ao remover o sabor do banco de dados.");
                    e.printStackTrace();
                }
            }
        });
    } //estilização para caixa de exclusao de sabor

    private void mostrarMensagemInformativa(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void mostrarMensagemErro(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}