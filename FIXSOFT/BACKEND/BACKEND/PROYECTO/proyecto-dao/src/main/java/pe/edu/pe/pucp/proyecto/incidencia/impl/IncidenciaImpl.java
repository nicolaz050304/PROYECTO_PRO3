package pe.edu.pe.pucp.proyecto.incidencia.impl;

import pe.edu.pe.pucp.proyecto.incidencia.Incidencia;
import pe.edu.pe.pucp.proyecto.incidencia.dao.IncidenciaDAO;
import pe.edu.pe.pucp.proyecto.manager.DBManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC del DAO de incidencias. La tabla se crea automáticamente la primera
 * vez (CREATE TABLE IF NOT EXISTS), igual que Favoritos/Bloqueos/Denuncias.
 */
public class IncidenciaImpl implements IncidenciaDAO {

    private static volatile boolean tablaLista = false;

    public IncidenciaImpl() {
        asegurarTabla();
    }

    private static synchronized void asegurarTabla() {
        if (tablaLista) return;
        String ddl = "CREATE TABLE IF NOT EXISTS incidencia (" +
                "id_incidencia INT NOT NULL AUTO_INCREMENT, " +
                "id_usuario INT NOT NULL, " +
                "asunto VARCHAR(150) NOT NULL, " +
                "descripcion VARCHAR(1000) NULL, " +
                "prioridad VARCHAR(10) NOT NULL DEFAULT 'MEDIA', " +
                "estado VARCHAR(15) NOT NULL DEFAULT 'ABIERTO', " +
                "fecha DATE NOT NULL, " +
                "PRIMARY KEY (id_incidencia), " +
                "KEY idx_incidencia_estado (estado)" +
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
    public List<Incidencia> listarTodas() {
        List<Incidencia> lista = new ArrayList<>();
        String sql = "SELECT id_incidencia, id_usuario, asunto, descripcion, prioridad, estado, fecha " +
                "FROM incidencia ORDER BY fecha DESC, id_incidencia DESC";
        try (Connection c = DBManager.getInstance().getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Incidencia(
                        rs.getInt("id_incidencia"),
                        rs.getInt("id_usuario"),
                        rs.getString("asunto"),
                        rs.getString("descripcion"),
                        rs.getString("prioridad"),
                        rs.getString("estado"),
                        rs.getDate("fecha")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en IncidenciaImpl (listar): " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public int agregar(Incidencia inc) {
        String sql = "INSERT INTO incidencia (id_usuario, asunto, descripcion, prioridad, estado, fecha) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = DBManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, inc.getIdUsuario());
            ps.setString(2, inc.getAsunto());
            if (inc.getDescripcion() != null && !inc.getDescripcion().trim().isEmpty()) {
                ps.setString(3, inc.getDescripcion().trim());
            } else {
                ps.setNull(3, java.sql.Types.VARCHAR);
            }
            ps.setString(4, inc.getPrioridad());
            ps.setString(5, inc.getEstado());
            ps.setDate(6, new java.sql.Date(inc.getFecha().getTime()));
            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en IncidenciaImpl (agregar): " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public void actualizarEstado(int idIncidencia, String estado) {
        String sql = "UPDATE incidencia SET estado = ? WHERE id_incidencia = ?";
        try (Connection c = DBManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, idIncidencia);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error en IncidenciaImpl (actualizar estado): " + e.getMessage(), e);
        }
    }
}
