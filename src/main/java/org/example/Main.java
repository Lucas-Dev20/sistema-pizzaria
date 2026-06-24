package org.example;

/*import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/br/edu/ufersa/pizzaria/views/PainelDeControle.fxml")
        );

        Scene scene = new Scene(loader.load());

        stage.setTitle("La Piazza Pizzaria");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}*/

import br.edu.ufersa.poo.pizzaria.DAO.EstoqueDAO;
import br.edu.ufersa.poo.pizzaria.model.entities.Adicional;
import br.edu.ufersa.poo.pizzaria.model.entities.Pedido;
import br.edu.ufersa.poo.pizzaria.model.services.EstoqueService;
import br.edu.ufersa.poo.pizzaria.model.services.PedidoService;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        // 1. Roda a automação do banco de dados antes de tudo!
        br.edu.ufersa.poo.pizzaria.util.DatabaseInitializer.inicializarBanco();

        System.out.println("=== CONFIGURANDO O SISTEMA DA PIZZARIA ===");

        // 1. Instancia o DAO que mexe diretamente no banco de dados
        EstoqueDAO estoqueDAO = new EstoqueDAO();

        // 2. Instancia o serviço de estoque passando o DAO como dependência
        EstoqueService estoqueService = new EstoqueService(estoqueDAO);

        // 3. Instancia o serviço de pedidos, passando o estoqueService como observador
        PedidoService pedidoService = new PedidoService(estoqueService);

        System.out.println("=== SIMULANDO A CRIAÇÃO DE UM PEDIDO ===");

        // 4. Cria os adicionais de teste
        // Dica: Use IDs que realmente existam na sua tabela 'adicional' do seu banco!
        Adicional queijoExtra = new Adicional(1, "Queijo Extra", 5.00, 20);
        Adicional ovos = new Adicional(2, "Ovos", 3.50, 15);

        // 5. Cria a lista de adicionais que o construtor do Pedido exige
        List<Adicional> listaAdicionais = new ArrayList<>();
        listaAdicionais.add(queijoExtra);
        listaAdicionais.add(ovos);

        // 6. IMPORTANTE: Esse ID de teste precisa existir na tabela de pedidos do seu banco de dados,
        // senão a busca do seu pedidoDAO dentro do service vai retornar nula.
        int idDoPedidoNoBanco = 1;

        // 7. Instancia o novo pedido usando o seu construtor de 7 parâmetros
        Pedido novoPedido = new Pedido(
                idDoPedidoNoBanco,
                null,                          // Cliente (passando null apenas para o teste)
                null,                          // Pizza (passando null apenas para o teste)
                listaAdicionais,               // A lista de adicionais que criamos acima
                "Cartão",                      // Forma de pagamento
                "Sem cebola",                  // Observação
                LocalDate.now()                // Data atual
        );

        System.out.println("\n=== EXECUTANDO O PROCESSO DE FINALIZAÇÃO ===");

        // 8. Chama o método usando a variável 'pedidoService' em minúscula
        pedidoService.finalizarPedido(novoPedido);

        System.out.println("\n=== TESTE CONCLUÍDO ===");
    }
}