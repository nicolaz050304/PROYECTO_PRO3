package pe.edu.pe.pucp.appproyecto; // <--- No borres esto

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Main {
    public static void main(String[] args) {
        // ResourceBundle busca en la raíz de 'resources' el archivo 'db.properties'
        ResourceBundle db = ResourceBundle.getBundle("db");

        String host = db.getString("db.host");
        // Revisa que en tu db.properties diga "db.port" (si pusiste "db.puerto" cámbialo aquí)
        int port = Integer.parseInt(db.getString("db.puerto"));
        String esquema = db.getString("db.esquema");
        String usuario = db.getString("db.usuario");
        String password = db.getString("db.password");

        String url = "jdbc:mysql://" + host + ":" + port + "/" + esquema;

        System.out.println("Intentando conectar a AWS...");

        try (Connection connection = DriverManager.getConnection(url, usuario, password)) {
            if (connection != null && !connection.isClosed()) {
                System.out.println("¡Conexión con la base de datos exitosa!");
            }
        } catch (SQLException e) {
            System.err.println("Error de conectividad JDBC:");
            System.err.println("Mensaje: " + e.getMessage());
            System.err.println("Estado SQL: " + e.getSQLState());
        }
    }
}