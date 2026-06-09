package pe.edu.pe.pucp.proyecto;
import pe.edu.pe.pucp.proyecto.manager.DBManager;

import java.sql.Connection;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        try {
            Connection conn = DBManager.getInstance().getConnection();
            System.out.println("✅ Conexión exitosa");
            conn.close();
        } catch (Exception e) {
            System.out.println("❌ Error al conectar");
            e.printStackTrace();
        }
    }
}