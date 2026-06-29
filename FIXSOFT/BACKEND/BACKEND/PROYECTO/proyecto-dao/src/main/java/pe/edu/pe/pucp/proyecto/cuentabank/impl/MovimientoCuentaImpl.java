package pe.edu.pe.pucp.proyecto.cuentabank.impl;

import pe.edu.pe.pucp.proyecto.cuentabank.dao.MovimientoCuentaDAO;
import pe.edu.pe.pucp.proyecto.economy.MovimientoCuenta;
import pe.edu.pe.pucp.proyecto.manager.DBManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JDBC del DAO de movimientos (RF15). La tabla se crea automáticamente
 * la primera vez (CREATE TABLE IF NOT EXISTS), igual que Favoritos/Bloqueos/Denuncias,
 * sin necesidad de un script de BD manual.
 */
public class MovimientoCuentaImpl implements MovimientoCuentaDAO {

    private static volatile boolean tablaLista = false;

    public MovimientoCuentaImpl() {
        asegurarTabla();
    }

    private static synchronized void asegurarTabla() {
        if (tablaLista) return;
        String ddl = "CREATE TABLE IF NOT EXISTS movimiento_cuenta (" +
                "id_movimiento INT NOT NULL AUTO_INCREMENT, " +
                "id_cuenta INT NOT NULL, " +
                "tipo VARCHAR(20) NOT NULL, " +
                "monto DECIMAL(12,2) NOT NULL, " +
                "descripcion VARCHAR(255) NULL, " +
                "saldo_resultante DECIMAL(12,2) NOT NULL, " +
                "fecha DATETIME NOT NULL, " +
                "id_reserva INT NOT NULL DEFAULT 0, " +
                "PRIMARY KEY (id_movimiento), " +
                "KEY idx_mov_cuenta (id_cuenta), " +
                "KEY idx_mov_reserva (id_reserva)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
        try (Connection c = DBManager.getInstance().getConnection();
             Statement st = c.createStatement()) {
            st.execute(ddl);
            // Para tablas YA creadas en un deploy anterior (sin id_reserva): la agregamos en caliente.
            boolean tieneCol = false;
            String check = "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() " +
                    "AND table_name = 'movimiento_cuenta' AND column_name = 'id_reserva'";
            try (ResultSet rs = st.executeQuery(check)) {
                if (rs.next() && rs.getInt(1) > 0) tieneCol = true;
            }
            if (!tieneCol) {
                st.execute("ALTER TABLE movimiento_cuenta ADD COLUMN id_reserva INT NOT NULL DEFAULT 0");
            }
            tablaLista = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int agregar(MovimientoCuenta m) {
        String sql = "INSERT INTO movimiento_cuenta (id_cuenta, tipo, monto, descripcion, saldo_resultante, fecha, id_reserva) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DBManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, m.getIdCuenta());
            ps.setString(2, m.getTipo());
            ps.setDouble(3, m.getMonto());
            if (m.getDescripcion() != null && !m.getDescripcion().trim().isEmpty()) {
                ps.setString(4, m.getDescripcion().trim());
            } else {
                ps.setNull(4, java.sql.Types.VARCHAR);
            }
            ps.setDouble(5, m.getSaldoResultante());
            Timestamp ts = new Timestamp(
                    m.getFecha() != null ? m.getFecha().getTime() : System.currentTimeMillis());
            ps.setTimestamp(6, ts);
            ps.setInt(7, m.getIdReserva());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en MovimientoCuentaImpl (agregar): " + e.getMessage(), e);
        }
        return 0;
    }

    private MovimientoCuenta mapear(ResultSet rs) throws SQLException {
        MovimientoCuenta m = new MovimientoCuenta(
                rs.getInt("id_movimiento"),
                rs.getInt("id_cuenta"),
                rs.getString("tipo"),
                rs.getDouble("monto"),
                rs.getString("descripcion"),
                rs.getDouble("saldo_resultante"),
                rs.getTimestamp("fecha"));
        m.setIdReserva(rs.getInt("id_reserva"));
        return m;
    }

    @Override
    public List<MovimientoCuenta> listarPorCuenta(int idCuenta) {
        List<MovimientoCuenta> lista = new ArrayList<>();
        String sql = "SELECT id_movimiento, id_cuenta, tipo, monto, descripcion, saldo_resultante, fecha, id_reserva " +
                "FROM movimiento_cuenta WHERE id_cuenta = ? ORDER BY fecha DESC, id_movimiento DESC";
        try (Connection c = DBManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idCuenta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en MovimientoCuentaImpl (listar): " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public MovimientoCuenta buscarPorReservaYTipo(int idReserva, String tipo) {
        String sql = "SELECT id_movimiento, id_cuenta, tipo, monto, descripcion, saldo_resultante, fecha, id_reserva " +
                "FROM movimiento_cuenta WHERE id_reserva = ? AND tipo = ? ORDER BY id_movimiento ASC LIMIT 1";
        try (Connection c = DBManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ps.setString(2, tipo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en MovimientoCuentaImpl (buscarPorReservaYTipo): " + e.getMessage(), e);
        }
        return null;
    }
}
