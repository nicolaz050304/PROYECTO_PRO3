package pe.edu.pe.pucp.appproyecto;

import pe.edu.pe.pucp.proyecto.manager.DBManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Migración de UNA sola vez: cambio de modelo de negocio. Ahora cualquier cuenta es
 * ANFITRION (puede publicar) y, como Anfitrion extiende Cliente, también puede reservar.
 *
 * Convierte a todos los usuarios NO administradores a rol "ANFITRION". A los
 * ADMINISTRADOR no los toca. Es IDEMPOTENTE: correrlo de nuevo no cambia nada
 * (los que ya son ANFITRION quedan igual).
 *
 * Nota: el registro nuevo YA crea anfitriones (UsuarioMapper.toEntity crea un Anfitrion),
 * así que esto solo corrige las cuentas EXISTENTES.
 */
public class MigrarAAnfitrion {
    public static void main(String[] args) {
        System.out.println("=== MIGRACION: todos los usuarios no-admin -> ANFITRION (una sola vez) ===");

        // No tocamos administradores (tipo_usuario contiene 'ADMINISTRADOR').
        String sql = "UPDATE usuario SET tipo_usuario = 'ANFITRION' " +
                "WHERE tipo_usuario IS NULL OR tipo_usuario NOT LIKE '%ADMINISTRADOR%'";

        try (Connection con = DBManager.getInstance().getConnection()) {

            // Conteo previo (informativo) por rol.
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(
                         "SELECT tipo_usuario, COUNT(*) c FROM usuario GROUP BY tipo_usuario")) {
                System.out.println("-- Antes --");
                while (rs.next()) {
                    System.out.println("  " + rs.getString("tipo_usuario") + " = " + rs.getInt("c"));
                }
            }

            int filas;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                filas = ps.executeUpdate();
            }
            System.out.println("Usuarios actualizados a ANFITRION: " + filas);

            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(
                         "SELECT tipo_usuario, COUNT(*) c FROM usuario GROUP BY tipo_usuario")) {
                System.out.println("-- Despues --");
                while (rs.next()) {
                    System.out.println("  " + rs.getString("tipo_usuario") + " = " + rs.getInt("c"));
                }
            }

            System.out.println("Migracion lista.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
