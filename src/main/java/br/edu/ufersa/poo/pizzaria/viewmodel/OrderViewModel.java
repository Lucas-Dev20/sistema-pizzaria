package br.edu.ufersa.poo.pizzaria.viewmodel;

import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class OrderViewModel {
    IntegerProperty idCliente = new SimpleIntegerProperty();
    StringProperty nomeCliente = new SimpleStringProperty();
    StringProperty descricao = new SimpleStringProperty();
    DoubleProperty valor = new SimpleDoubleProperty();

    public int getIdCliente() {
        return idCliente.get();
    }

    public IntegerProperty idClienteProperty() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente.set(idCliente);
    }

    public String getNomeCliente() {
        return nomeCliente.get();
    }

    public StringProperty nomeClienteProperty() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente.set(nomeCliente);
    }

    public String getDescricao() {
        return descricao.get();
    }

    public StringProperty descricaoProperty() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao.set(descricao);
    }

    public double getValor() {
        return valor.get();
    }

    public DoubleProperty valorProperty() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor.set(valor);
    }
}