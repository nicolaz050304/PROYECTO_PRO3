package pe.edu.pe.pucp.proyecto.denuncia.impl;

import pe.edu.pe.pucp.proyecto.denuncia.Denuncia;
import pe.edu.pe.pucp.proyecto.denuncia.dao.DenunciaDAO;
import pe.edu.pe.pucp.proyecto.manager.DBManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC del DAO de denuncias (RF31). La tabla se crea automáticamente la
 * primera vez (CREATE TABLE IF NOT EXISTS), igual que Favoritos/Bloqueos, así la
 * funcionalidad queda operativa sin correr un script de BD a mano.
 */
public class DenunciaImpl implements DenunciaDAO {

    private static volatile boolean tablaLista = false;

    public DenunciaImpl() {
        asegurarTabla();
    }

    private static synchronized void asegurarTabla() {
        if (tablaLista) return;
        String ddl = "CREATE TABLE IF NOT EXISTS denuncia (" +
                "id_denuncia INT NOT NULL AUTO_INCREMENT, " +
                "id_denunciante INT NOT NULL, " +
                "id_alojamiento INT NOT NULL, " +
                "id_anfitrion INT NOT NULL, " +
                "motivo VARCHAR(120) NOT NULL, " +
                "descripcion VARCHAR(1000) NULL, " +
                "estado VARCHAR(20) NOT NULL DEFAULT 'EN_REVISION', " +
                "fecha DATE NOT NULL, " +
                "PRIMARY KEY (id_denuncia), " +
                "KEY idx_denuncia_estado (estado)" +
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
    public List<Denuncia> listarTodas() {
        List<Denuncia> lista = new ArrayList<>();
        String sql = "SELECT id_denuncia, id_denunciante, id_alojamiento, id_anfitrion, " +
                "motivo, descripcion, estado, fecha FROM denuncia ORDER BY fecha DESC, id_denuncia DESC";
        try (Connection c = DBManager.getInstance().getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Denuncia(
                        rs.getInt("id_denuncia"),
                        rs.getInt("id_denunciante"),
                        rs.getInt("id_alojamiento"),
                        rs.getInt("id_anfitrion"),
                        rs.getString("motivo"),
                        rs.getString("descripcion"),
                        rs.getString("estado"),
                        rs.getDate("fecha")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en DenunciaImpl (listar): " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public int agregar(Denuncia d) {
        String sql = "INSERT INTO denuncia (id_denunciante, id_alojamiento, id_anfitrion, " +
                "motivo, descripcion, estado, fecha) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DBManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, d.getIdDenunciante());
            ps.setInt(2, d.getIdAlojamiento());
            ps.setInt(3, d.getIdAnfitrion());
            ps.setString(4, d.getMotivo());
            if (d.getDescripcion() != null && !d.getDescripcion().trim().isEmpty()) {
                ps.setString(5, d.getDescripcion().trim());
            } else {
                ps.setNull(5, java.sql.Types.VARCHAR);
            }
            ps.setString(6, d.getEstado());
            ps.setDate(7, new java.sql.Date(d.getFecha().getTime()));
            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en DenunciaImpl (agregar): " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public void actualizarEstado(int idDenuncia, String estado) {
        String sql = "UPDATE denuncia SET estado = ? WHERE id_denuncia = ?";
        try (Connection c = DBManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, idDenuncia);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error en DenunciaImpl (actualizar estado): " + e.getMessage(), e);
        }
    }
}
