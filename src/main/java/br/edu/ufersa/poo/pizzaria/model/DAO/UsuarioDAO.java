package br.edu.ufersa.poo.pizzaria.model.DAO;
import java.net.URL;
import java.sql.*;

public class UsuarioDAO {
    private final static String URL =  "jdbc:mysql://localhost:3306/pizzaria";
    private final static String USER = "Lucas";
    private final static String PASS = "Lukah23@1";
    private static Connection con = null;

    public static Connection getConnection(){
        if(con == null){
            try{
                con = DriverManager.getConnection(URL,USER,PASS);
            }catch(SQLException e){e.printStackTrace();}
        }
        return con;
    }

    public static void closeConnection(){
        if(con != null){
            try{
                con.close();
            }catch(SQLException e) {e.printStackTrace();}
        }
    }
}
