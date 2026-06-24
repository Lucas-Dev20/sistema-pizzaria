package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // roda a automação do banco de dados antes de tudo
        br.edu.ufersa.poo.pizzaria.util.DatabaseInitializer.inicializarBanco();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/br/edu/ufersa/pizzaria/views/LoginView.fxml")
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
}