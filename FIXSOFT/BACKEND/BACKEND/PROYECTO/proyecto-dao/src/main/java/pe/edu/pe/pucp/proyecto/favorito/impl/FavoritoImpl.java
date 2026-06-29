package pe.edu.pe.pucp.proyecto.favorito.impl;

import pe.edu.pe.pucp.proyecto.favorito.dao.FavoritoDAO;
import pe.edu.pe.pucp.proyecto.manager.DBManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC del DAO de Favoritos (RF27).
 * La tabla se crea automáticamente la primera vez (CREATE TABLE IF NOT EXISTS), así la
 * funcionalidad queda operativa sin tener que correr un script de BD a mano.
 */
public class FavoritoImpl implements FavoritoDAO {

    private static volatile boolean tablaLista = false;

    public FavoritoImpl() {
        asegurarTabla();
    }

    private static synchronized void asegurarTabla() {
        if (tablaLista) return;
        String ddl = "CREATE TABLE IF NOT EXISTS favorito (" +
                "id_favorito INT NOT NULL AUTO_INCREMENT, " +
                "id_usuario INT NOT NULL, " +
                "id_alojamiento INT NOT NULL, " +
                "PRIMARY KEY (id_favorito), " +
                "UNIQUE KEY uk_fav (id_usuario, id_alojamiento)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
        try (Connection c = DBManager.getInstance().getConnection();
             Statement st = c.createStatement()) {
            st.execute(ddl);
            tablaLista = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Integer> listarIdsPorUsuario(int idUsuario) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT id_alojamiento FROM favorito WHERE id_usuario = ?";
        try (Connection c = DBManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }

    @Override
    public void agregar(int idUsuario, int idAlojamiento) {
        // INSERT IGNORE: si ya existe (UNIQUE), no falla ni duplica.
        String sql = "INSERT IGNORE INTO favorito (id_usuario, id_alojamiento) VALUES (?, ?)";
        try (Connection c = DBManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idAlojamiento);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void quitar(int idUsuario, int idAlojamiento) {
        String sql = "DELETE FROM favorito WHERE id_usuario = ? AND id_alojamiento = ?";
        try (Connection c = DBManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idAlojamiento);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
