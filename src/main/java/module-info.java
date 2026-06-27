module sistema.pizzaria {
    requires java.sql;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    opens br.edu.ufersa.poo.pizzaria.controllers to javafx.fxml;
    opens br.edu.ufersa.poo.pizzaria.model.entities to javafx.base, javafx.fxml;

    exports org.example;
}