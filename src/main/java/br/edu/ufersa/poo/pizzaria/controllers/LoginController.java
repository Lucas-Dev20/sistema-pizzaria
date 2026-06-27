package br.edu.ufersa.poo.pizzaria.controllers;

import br.edu.ufersa.poo.pizzaria.model.entities.Usuario;
import br.edu.ufersa.poo.pizzaria.model.services.UsuarioService;
import br.edu.ufersa.poo.pizzaria.session.SessaoUsuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller da tela de Login.
 *
 * Responsabilidades:
 *   1. Autenticar o usuário com e-mail + senha.
 *   2. Após login bem-sucedido, redirecionar para o Painel de controle.
 *   3. Métodos estáticos utilitários reutilizados pelos outros controllers
 *      (trocarConteudo, abrirModal) — já referenciados no código existente.
 */
public class LoginController {

    // ── Componentes da tela ───────────────────────────────────────────────────
    @FXML private TextField     txtEmail;
    @FXML private PasswordField txtSenha;
    @FXML private Label         lblErro;

    // ── Perfil selecionado pelos botões laterais ───────────────────────────────
    // (os botões "Administrador" e "Funcionário" do design são apenas visuais/dica;
    //  o perfil real vem do banco conforme o e-mail cadastrado)
    private String perfilSelecionado = null; // "ADMIN" ou "FUNCIONARIO"

    private final UsuarioService usuarioService = new UsuarioService();

    // ── Inicialização ─────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        lblErro.setVisible(false);
        // Garante que o admin padrão existe no banco
        try {
            usuarioService.garantirAdminPadrao();
        } catch (Exception e) {
            // Silencia na tela; log no console
            System.err.println("Aviso: não foi possível garantir admin padrão: " + e.getMessage());
        }
    }

    // ── Seleção de perfil (botões laterais) ───────────────────────────────────

    @FXML private Button btnAdmin;
    @FXML private Button btnFuncionario;

    @FXML
    private void selecionarAdmin(ActionEvent event) {
        perfilSelecionado = "ADMIN";
        btnAdmin.setStyle("-fx-background-color: #8B2318; -fx-text-fill: white; -fx-background-radius: 20;");
        btnFuncionario.setStyle(""); // reseta o outro
    }

    @FXML
    private void selecionarFuncionario(ActionEvent event) {
        perfilSelecionado = "FUNCIONARIO";
        btnFuncionario.setStyle("-fx-background-color: #8B2318; -fx-text-fill: white; -fx-background-radius: 20;");
        btnAdmin.setStyle(""); // reseta o outro
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    @FXML
    private void handleEntrar(ActionEvent event) {
        lblErro.setVisible(false);

        String email = txtEmail.getText().trim();
        String senha = txtSenha.getText();

        try {
            // Autentica e inicia a sessão
            Usuario usuario = usuarioService.autenticar(email, senha);

            // Redireciona para o painel principal
            trocarConteudo(event,
                    "/br/edu/ufersa/pizzaria/views/PainelDeControle.fxml",
                    "La Piazza — Painel de Controle");

        } catch (Exception e) {
            lblErro.setText(e.getMessage());
            lblErro.setVisible(true);
            txtSenha.clear();
        }
    }

    // ── Criar conta ───────────────────────────────────────────────────────────
    // Conforme o design, "Criar conta" abre um modal de registro.
    // Contudo, pelo enunciado, apenas o ADM cadastra funcionários.
    // Esta ação abre o formulário de auto-cadastro (caso habilitado),
    // ou pode ser ocultada e substituída pelo fluxo de ADM cadastrar funcionário.

    @FXML
    private void handleCriarConta(ActionEvent event) {
        abrirModal("/br/edu/ufersa/pizzaria/views/CriarConta.fxml",
                "Criar Conta");
    }

    // ── Recuperar senha ───────────────────────────────────────────────────────

    @FXML
    private void handleEsqueceuSenha(ActionEvent event) {
        abrirModal("/br/edu/ufersa/pizzaria/views/RecuperarSenha.fxml",
                "Recuperar Senha");
    }

    // =========================================================================
    // MÉTODOS ESTÁTICOS UTILITÁRIOS
    // Reutilizados por TODOS os outros controllers para navegar entre telas.
    // (Já referenciados no GerenciarAdicionaisController existente)
    // =========================================================================

    /**
     * Troca o conteúdo da janela atual por outro FXML.
     * Mantém a mesma janela (Stage) aberta.
     *
     * @param event   Evento JavaFX (para obter o Stage atual)
     * @param fxmlPath Caminho do FXML de destino (ex: "/views/PedidosView.fxml")
     * @param titulo  Título da nova janela
     */
    public static void trocarConteudo(ActionEvent event, String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(LoginController.class.getResource(fxmlPath));
            Parent novoRoot = loader.load();

            // pega o Stage e a Scene atuais
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene cenaAtual = stage.getScene();

            // substitui o conteúdo da janela instantaneamente
            cenaAtual.setRoot(novoRoot);

            // atualiza o título
            stage.setTitle(titulo);

            // garante que o foco vá para o novo painel
            novoRoot.requestFocus();
        } catch (IOException e) {
            System.err.println("Erro ao carregar tela: " + fxmlPath);
            e.printStackTrace();
            mostrarAlertaEstatico(Alert.AlertType.ERROR, "Erro de Navegação",
                    "Não foi possível abrir a tela solicitada.\nArquivo: " + fxmlPath);
        }
    }

    /**
     * Abre um FXML em uma nova janela modal (pop-up).
     * Bloqueia a janela pai até o modal ser fechado.
     *
     * @param fxmlPath Caminho do FXML do modal
     * @param titulo   Título da janela modal
     */
    public static void abrirModal(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    LoginController.class.getResource(fxmlPath));
            Parent root = loader.load();

            Stage modal = new Stage();
            modal.setTitle(titulo);
            modal.setScene(new Scene(root));
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.showAndWait();

        } catch (IOException e) {
            System.err.println("Erro ao abrir modal: " + fxmlPath);
            e.printStackTrace();
            mostrarAlertaEstatico(Alert.AlertType.ERROR, "Erro",
                    "Não foi possível abrir o formulário.");
        }
    }

    /**
     * Abre um modal e retorna o controller carregado.
     * Útil quando o controller do modal precisa receber dados (ex: edição).
     *
     * @param fxmlPath Caminho do FXML
     * @param titulo   Título da janela
     * @param <T>      Tipo do controller esperado
     * @return O controller do FXML carregado, ou null em caso de erro
     */
    public static <T> T abrirModalComController(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    LoginController.class.getResource(fxmlPath));
            Parent root = loader.load();

            Stage modal = new Stage();
            modal.setTitle(titulo);
            modal.setScene(new Scene(root));
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.showAndWait();

            return loader.getController();

        } catch (IOException e) {
            System.err.println("Erro ao abrir modal com controller: " + fxmlPath);
            e.printStackTrace();
            return null;
        }
    }

    // ── Helper de alerta estático ─────────────────────────────────────────────

    private static void mostrarAlertaEstatico(Alert.AlertType tipo, String titulo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

}