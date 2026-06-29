package pe.edu.pe.pucp.proyecto.bloqueo.impl;

import pe.edu.pe.pucp.proyecto.bloqueo.BloqueoFecha;
import pe.edu.pe.pucp.proyecto.bloqueo.dao.BloqueoFechaDAO;
import pe.edu.pe.pucp.proyecto.manager.DBManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC del DAO de bloqueos de fecha (RF30).
 * La tabla se crea automáticamente la primera vez (CREATE TABLE IF NOT EXISTS), igual que
 * Favoritos, así la funcionalidad queda operativa sin correr un script de BD a mano.
 */
public class BloqueoFechaImpl implements BloqueoFechaDAO {

    private static volatile boolean tablaLista = false;

    public BloqueoFechaImpl() {
        asegurarTabla();
    }

    private static synchronized void asegurarTabla() {
        if (tablaLista) return;
        String ddl = "CREATE TABLE IF NOT EXISTS bloqueo_fecha (" +
                "id_bloqueo INT NOT NULL AUTO_INCREMENT, " +
                "id_alojamiento INT NOT NULL, " +
                "fecha_inicio DATE NOT NULL, " +
                "fecha_fin DATE NOT NULL, " +
                "motivo VARCHAR(255) NULL, " +
                "PRIMARY KEY (id_bloqueo), " +
                "KEY idx_bloqueo_aloj (id_alojamiento)" +
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
    public List<BloqueoFecha> listarPorAlojamiento(int idAlojamiento) {
        List<BloqueoFecha> lista = new ArrayList<>();
        String sql = "SELECT id_bloqueo, id_alojamiento, fecha_inicio, fecha_fin, motivo " +
                "FROM bloqueo_fecha WHERE id_alojamiento = ? ORDER BY fecha_inicio";
        try (Connection c = DBManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idAlojamiento);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new BloqueoFecha(
                            rs.getInt("id_bloqueo"),
                            rs.getInt("id_alojamiento"),
                            rs.getDate("fecha_inicio"),
                            rs.getDate("fecha_fin"),
                            rs.getString("motivo")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en BloqueoFechaImpl (listar): " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public int agregar(BloqueoFecha bloqueo) {
        String sql = "INSERT INTO bloqueo_fecha (id_alojamiento, fecha_inicio, fecha_fin, motivo) " +
                "VALUES (?, ?, ?, ?)";
        try (Connection c = DBManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, bloqueo.getIdAlojamiento());
            ps.setDate(2, new java.sql.Date(bloqueo.getFechaInicio().getTime()));
            ps.setDate(3, new java.sql.Date(bloqueo.getFechaFin().getTime()));
            if (bloqueo.getMotivo() != null && !bloqueo.getMotivo().trim().isEmpty()) {
                ps.setString(4, bloqueo.getMotivo().trim());
            } else {
                ps.setNull(4, java.sql.Types.VARCHAR);
            }
            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en BloqueoFechaImpl (agregar): " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public void eliminar(int idBloqueo) {
        String sql = "DELETE FROM bloqueo_fecha WHERE id_bloqueo = ?";
        try (Connection c = DBManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idBloqueo);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error en BloqueoFechaImpl (eliminar): " + e.getMessage(), e);
        }
    }
}
