package pe.edu.pe.pucp.proyecto.validacion.impl;

import pe.edu.pe.pucp.proyecto.manager.DBManager;
import pe.edu.pe.pucp.proyecto.validacion.DocumentoValidacion;
import pe.edu.pe.pucp.proyecto.validacion.dao.DocumentoValidacionDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC del DAO de validación documentaria (RF02). La tabla se crea
 * automáticamente la primera vez (CREATE TABLE IF NOT EXISTS), igual que
 * Favoritos/Bloqueos/Denuncias, sin necesidad de un script de BD manual.
 *
 * id_usuario es UNIQUE: hay una sola fila por usuario y "guardar" hace upsert
 * (INSERT ... ON DUPLICATE KEY UPDATE), así un reenvío reemplaza la subida anterior.
 */
public class DocumentoValidacionImpl implements DocumentoValidacionDAO {

    private static volatile boolean tablaLista = false;

    public DocumentoValidacionImpl() {
        asegurarTabla();
    }

    private static synchronized void asegurarTabla() {
        if (tablaLista) return;
        String ddl = "CREATE TABLE IF NOT EXISTS documento_validacion (" +
                "id_documento INT NOT NULL AUTO_INCREMENT, " +
                "id_usuario INT NOT NULL, " +
                "tipo_documento VARCHAR(20) NOT NULL, " +
                "numero_documento VARCHAR(30) NOT NULL, " +
                "archivo_base64 LONGTEXT NULL, " +
                "estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE', " +
                "motivo_rechazo VARCHAR(500) NULL, " +
                "id_admin_validador INT NULL, " +
                "fecha_subida DATETIME NOT NULL, " +
                "fecha_revision DATETIME NULL, " +
                "PRIMARY KEY (id_documento), " +
                "UNIQUE KEY uk_docval_usuario (id_usuario), " +
                "KEY idx_docval_estado (estado)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
        try (Connection c = DBManager.getInstance().getConnection();
             Statement st = c.createStatement()) {
            st.execute(ddl);
            tablaLista = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static final String COLUMNS =
            "id_documento, id_usuario, tipo_documento, numero_documento, archivo_base64, " +
            "estado, motivo_rechazo, id_admin_validador, fecha_subida, fecha_revision";

    private DocumentoValidacion mapear(ResultSet rs) throws SQLException {
        DocumentoValidacion d = new DocumentoValidacion();
        d.setIdDocumento(rs.getInt("id_documento"));
        d.setIdUsuario(rs.getInt("id_usuario"));
        d.setTipoDocumento(rs.getString("tipo_documento"));
        d.setNumeroDocumento(rs.getString("numero_documento"));
        d.setArchivoBase64(rs.getString("archivo_base64"));
        d.setEstado(rs.getString("estado"));
        d.setMotivoRechazo(rs.getString("motivo_rechazo"));
        d.setIdAdminValidador(rs.getInt("id_admin_validador"));
        d.setFechaSubida(rs.getTimestamp("fecha_subida"));
        d.setFechaRevision(rs.getTimestamp("fecha_revision"));
        return d;
    }

    @Override
    public DocumentoValidacion buscarPorUsuario(int idUsuario) {
        String sql = "SELECT " + COLUMNS + " FROM documento_validacion WHERE id_usuario = ?";
        try (Connection c = DBManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en DocumentoValidacionImpl (buscarPorUsuario): " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public DocumentoValidacion buscarPorId(int idDocumento) {
        String sql = "SELECT " + COLUMNS + " FROM documento_validacion WHERE id_documento = ?";
        try (Connection c = DBManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idDocumento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en DocumentoValidacionImpl (buscarPorId): " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<DocumentoValidacion> listarPendientes() {
        List<DocumentoValidacion> lista = new ArrayList<>();
        String sql = "SELECT " + COLUMNS + " FROM documento_validacion WHERE estado = 'PENDIENTE' " +
                "ORDER BY fecha_subida DESC, id_documento DESC";
        try (Connection c = DBManager.getInstance().getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en DocumentoValidacionImpl (listarPendientes): " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public int guardar(DocumentoValidacion d) {
        // Upsert por id_usuario: si el usuario reenvía, se reemplaza la subida anterior y
        // vuelve a PENDIENTE, limpiando la revisión previa (motivo/admin/fecha_revision).
        String sql = "INSERT INTO documento_validacion " +
                "(id_usuario, tipo_documento, numero_documento, archivo_base64, estado, " +
                " motivo_rechazo, id_admin_validador, fecha_subida, fecha_revision) " +
                "VALUES (?, ?, ?, ?, 'PENDIENTE', NULL, NULL, ?, NULL) " +
                "ON DUPLICATE KEY UPDATE " +
                "tipo_documento = VALUES(tipo_documento), " +
                "numero_documento = VALUES(numero_documento), " +
                "archivo_base64 = VALUES(archivo_base64), " +
                "estado = 'PENDIENTE', motivo_rechazo = NULL, id_admin_validador = NULL, " +
                "fecha_subida = VALUES(fecha_subida), fecha_revision = NULL";
        try (Connection c = DBManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, d.getIdUsuario());
            ps.setString(2, d.getTipoDocumento());
            ps.setString(3, d.getNumeroDocumento());
            ps.setString(4, d.getArchivoBase64());
            Timestamp ahora = new Timestamp(
                    d.getFechaSubida() != null ? d.getFechaSubida().getTime() : System.currentTimeMillis());
            ps.setTimestamp(5, ahora);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next() && rs.getInt(1) > 0) return rs.getInt(1);
            }
            // En un UPDATE (upsert) getGeneratedKeys puede no traer la clave: la resolvemos por usuario.
            DocumentoValidacion existente = buscarPorUsuario(d.getIdUsuario());
            return existente != null ? existente.getIdDocumento() : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error en DocumentoValidacionImpl (guardar): " + e.getMessage(), e);
        }
    }

    @Override
    public void actualizarDecision(int idDocumento, String estado, String motivoRechazo, int idAdminValidador) {
        String sql = "UPDATE documento_validacion SET estado = ?, motivo_rechazo = ?, " +
                "id_admin_validador = ?, fecha_revision = ? WHERE id_documento = ?";
        try (Connection c = DBManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, estado);
            if (motivoRechazo != null && !motivoRechazo.trim().isEmpty()) {
                ps.setString(2, motivoRechazo.trim());
            } else {
                ps.setNull(2, java.sql.Types.VARCHAR);
            }
            if (idAdminValidador > 0) {
                ps.setInt(3, idAdminValidador);
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            ps.setInt(5, idDocumento);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error en DocumentoValidacionImpl (actualizarDecision): " + e.getMessage(), e);
        }
    }
}
