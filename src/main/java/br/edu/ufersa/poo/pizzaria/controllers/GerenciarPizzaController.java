package br.edu.ufersa.poo.pizzaria.controllers;

import br.edu.ufersa.poo.pizzaria.exceptions.AcessoNegadoException;
import br.edu.ufersa.poo.pizzaria.session.SessaoUsuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller da tela "Tipos de Pizza".
 *
 * REGRA DE ACESSO:
 *   - Todos os usuários podem VER os tipos de pizza.
 *   - APENAS o ADMIN pode cadastrar, editar ou excluir tipos de pizza.
 *
 * O controle é feito de duas formas:
 *   1. Na inicialização: oculta/desativa o botão "Novo Sabor" para funcionários.
 *   2. Nas ações: relança AcessoNegadoException se um funcionário tentar mesmo assim.
 */
public class GerenciarPizzaController {

    @FXML private Button btnNovoSabor;

    // ── Inicialização: configura visibilidade dos botões por perfil ───────────
    @FXML
    public void initialize() {
        boolean ehAdmin = SessaoUsuario.getInstance().usuarioEhAdmin();

        // Oculta o botão "Novo Sabor" para funcionários
        btnNovoSabor.setVisible(ehAdmin);
        btnNovoSabor.setManaged(ehAdmin); // não ocupa espaço se invisível

        carregarPizzas();
    }

    private void carregarPizzas() {
        // TODO: carregar lista de pizzas do PizzaService e exibir na tela
        // Exemplo: listaPizzas.setAll(pizzaService.listarTodas());
    }

    // ── Ações restritas ao ADMIN ──────────────────────────────────────────────

    @FXML
    private void handleNovoSabor(ActionEvent event) {
        try {
            verificarAdmin("cadastrar novo tipo de pizza");
            LoginController.abrirModal(
                    "/br/edu/ufersa/pizzaria/views/CadastrarSaborView.fxml",
                    "Cadastrar Novo Sabor");
            carregarPizzas();

        } catch (AcessoNegadoException e) {
            mostrarAcessoNegado(e.getMessage());
        }
    }

    @FXML
    private void handleEditarPizza(/* Pizza pizza */ ActionEvent event) {
        try {
            verificarAdmin("editar tipo de pizza");
            // TODO: abrir modal de edição passando a pizza selecionada
            LoginController.abrirModal(
                    "/br/edu/ufersa/pizzaria/views/EditarSaborView.fxml",
                    "Editar Sabor");
            carregarPizzas();

        } catch (AcessoNegadoException e) {
            mostrarAcessoNegado(e.getMessage());
        }
    }

    @FXML
    private void handleExcluirPizza(ActionEvent event) {
        try {
            verificarAdmin("excluir tipo de pizza");

            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setTitle("Deseja excluir?");
            confirmacao.setHeaderText(null);
            confirmacao.setContentText("Tem certeza que deseja excluir este sabor?");

            ButtonType btnExcluir  = new ButtonType("Excluir",  ButtonBar.ButtonData.OK_DONE);
            ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
            confirmacao.getButtonTypes().setAll(btnCancelar, btnExcluir);

            confirmacao.showAndWait().ifPresent(btn -> {
                if (btn == btnExcluir) {
                    // TODO: pizzaService.removerPizza(pizzaSelecionada.getId());
                    carregarPizzas();
                }
            });

        } catch (AcessoNegadoException e) {
            mostrarAcessoNegado(e.getMessage());
        }
    }

    // ── Navegação (mesmo padrão dos outros controllers) ───────────────────────

    @FXML private void irPedidos(ActionEvent e)   { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/Pedidos.fxml",   "La Piazza - Pedidos"); }
    @FXML private void irClientes(ActionEvent e)  { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/GerenciarClientesView.fxml",  "La Piazza - Clientes"); }
    @FXML private void irTiposPizza(ActionEvent e){ carregarPizzas(); }
    @FXML private void irAdicionais(ActionEvent e){ LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/GerenciarAdicionaisView.fxml","La Piazza - Adicionais"); }
    @FXML private void irEstoque(ActionEvent e)   { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/EstoqueView.fxml",   "La Piazza - Estoque"); }
    @FXML private void irRelatorios(ActionEvent e){ LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/RelatorioView.fxml","La Piazza - Relatórios"); }
    @FXML private void irFuncionarios(ActionEvent e){ LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/GerenciarFuncionariosView.fxml", "La Piazza - Funcionários"); }
    @FXML private void sair(ActionEvent e)        { LoginController.trocarConteudo(e, "/br/edu/ufersa/pizzaria/views/LoginView.fxml",               "La Piazza Pizzaria"); }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Verifica se o usuário logado é ADMIN. Lança AcessoNegadoException se não for.
     */
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
}