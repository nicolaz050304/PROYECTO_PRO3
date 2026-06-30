package pe.edu.pe.pucp.proyecto.auditoria.impl;

import pe.edu.pe.pucp.proyecto.auditoria.AuditoriaEstado;
import pe.edu.pe.pucp.proyecto.auditoria.dao.AuditoriaEstadoDAO;
import pe.edu.pe.pucp.proyecto.manager.DBManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC de la bitácora de auditoría (RNF09). La tabla se crea automáticamente la
 * primera vez (CREATE TABLE IF NOT EXISTS), igual que Incidencias/Denuncias/Bloqueos. La columna
 * 'fecha' la fija la BD (DEFAULT CURRENT_TIMESTAMP): la marca de tiempo es del servidor, no del
 * cliente, garantizando un orden cronológico fiable y persistente.
 */
public class AuditoriaEstadoImpl implements AuditoriaEstadoDAO {

    private static volatile boolean tablaLista = false;

    public AuditoriaEstadoImpl() {
        asegurarTabla();
    }

    private static synchronized void asegurarTabla() {
        if (tablaLista) return;
        String ddl = "CREATE TABLE IF NOT EXISTS auditoria_estado (" +
                "id_auditoria INT NOT NULL AUTO_INCREMENT, " +
                "entidad VARCHAR(20) NOT NULL, " +
                "id_entidad INT NOT NULL, " +
                "campo VARCHAR(40) NOT NULL, " +
                "estado_anterior VARCHAR(40) NULL, " +
                "estado_nuevo VARCHAR(40) NOT NULL, " +
                "detalle VARCHAR(255) NULL, " +
                "fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "PRIMARY KEY (id_auditoria), " +
                "KEY idx_aud_entidad (entidad, id_entidad), " +
                "KEY idx_aud_fecha (fecha)" +
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
    public int agregar(AuditoriaEstado r) {
        // No insertamos 'fecha': la pone la BD con CURRENT_TIMESTAMP.
        String sql = "INSERT INTO auditoria_estado " +
                "(entidad, id_entidad, campo, estado_anterior, estado_nuevo, detalle) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = DBManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getEntidad());
            ps.setInt(2, r.getIdEntidad());
            ps.setString(3, r.getCampo());
            if (r.getEstadoAnterior() != null) ps.setString(4, r.getEstadoAnterior());
            else ps.setNull(4, java.sql.Types.VARCHAR);
            ps.setString(5, r.getEstadoNuevo());
            if (r.getDetalle() != null) ps.setString(6, r.getDetalle());
            else ps.setNull(6, java.sql.Types.VARCHAR);
            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en AuditoriaEstadoImpl (agregar): " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public List<AuditoriaEstado> listarPorEntidad(String entidad, int idEntidad) {
        String sql = "SELECT id_auditoria, entidad, id_entidad, campo, estado_anterior, estado_nuevo, detalle, fecha " +
                "FROM auditoria_estado WHERE entidad = ? AND id_entidad = ? " +
                "ORDER BY fecha DESC, id_auditoria DESC";
        List<AuditoriaEstado> lista = new ArrayList<>();
        try (Connection c = DBManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, entidad);
            ps.setInt(2, idEntidad);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(construir(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en AuditoriaEstadoImpl (listarPorEntidad): " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public List<AuditoriaEstado> listarRecientes(int limite) {
        int lim = limite > 0 ? limite : 200;
        String sql = "SELECT id_auditoria, entidad, id_entidad, campo, estado_anterior, estado_nuevo, detalle, fecha " +
                "FROM auditoria_estado ORDER BY fecha DESC, id_auditoria DESC LIMIT ?";
        List<AuditoriaEstado> lista = new ArrayList<>();
        try (Connection c = DBManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, lim);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(construir(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en AuditoriaEstadoImpl (listarRecientes): " + e.getMessage(), e);
        }
        return lista;
    }

    private static AuditoriaEstado construir(ResultSet rs) throws SQLException {
        return new AuditoriaEstado(
                rs.getInt("id_auditoria"),
                rs.getString("entidad"),
                rs.getInt("id_entidad"),
                rs.getString("campo"),
                rs.getString("estado_anterior"),
                rs.getString("estado_nuevo"),
                rs.getString("detalle"),
                rs.getTimestamp("fecha"));
    }
}
