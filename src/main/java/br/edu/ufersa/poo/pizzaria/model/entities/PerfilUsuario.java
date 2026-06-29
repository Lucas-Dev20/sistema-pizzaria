package br.edu.ufersa.poo.pizzaria.model.entities;

/* ADMIN  → Sr. Michelangelo: acesso total, incluindo cadastro de tipos de pizza.
   FUNCIONARIO → acesso a pedidos, clientes, adicionais, estoque e relatórios.*/

public enum PerfilUsuario {
    ADMIN,
    FUNCIONARIO
}