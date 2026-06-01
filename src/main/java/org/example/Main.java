package org.example;
import br.edu.ufersa.poo.pizzaria.model.DAO.UsuarioDAO;

public class Main {
    public static void main(String [] args){
        System.out.println(UsuarioDAO.getConnection());
    }
}
