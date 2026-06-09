package pe.edu.pe.pucp.proyecto.economy.impl;

import pe.edu.pe.pucp.proyecto.economy.Pago;
import pe.edu.pe.pucp.proyecto.economy.TipoMoneda;
import pe.edu.pe.pucp.proyecto.economy.dao.PagoIDAO;
import pe.edu.pe.pucp.proyecto.manager.DBManager;
import pe.edu.pe.pucp.proyecto.reservation.Reserva;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PagoImpl implements PagoIDAO {

    // Consultas SQL actualizadas con los campos de auditoría y envíos
    private static final String SQL_SELECT_BY_ID =
            "SELECT id_pago, monto_neto, monto_bruto, moneda, porcentaje_comision, " +
                    "tipo_cambio, estado_transaccion, fecha_envio_anfitrion, id_cuenta_destino, id_reserva " +
                    "FROM pago WHERE id_pago = ?";

    private static final String SQL_INSERT =
            "INSERT INTO pago (monto_neto, monto_bruto, moneda, porcentaje_comision, " +
                    "tipo_cambio, estado_transaccion, fecha_envio_anfitrion, id_cuenta_destino, id_reserva) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE =
            "UPDATE pago SET monto_neto = ?, monto_bruto = ?, moneda = ?, porcentaje_comision = ?, " +
                    "tipo_cambio = ?, estado_transaccion = ?, fecha_envio_anfitrion = ?, id_cuenta_destino = ?, id_reserva = ? " +
                    "WHERE id_pago = ?";

    private static final String SQL_DELETE =
            "DELETE FROM pago WHERE id_pago = ?";

    private static final String SQL_SELECT_ALL =
            "SELECT * FROM pago";

    @Override
    public Pago load(Integer id) {
        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return construirPago(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error al cargar pago: " + e.getMessage(), e);
        }
    }

    @Override
    public Pago save(Pago pago) {
        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDouble(1, pago.getMontoNeto());
            ps.setDouble(2, pago.getMontoBruto());
            ps.setString(3, pago.getMoneda().name());
            ps.setDouble(4, pago.getPorcenComision());

            // --- NUEVOS CAMPOS PARA EL JP ---
            ps.setDouble(5, pago.getTipoCambio());
            ps.setString(6, pago.getEstadoTransaccion());

            if (pago.getFechaEnvioAnfitrion() != null) {
                ps.setDate(7, new java.sql.Date(pago.getFechaEnvioAnfitrion().getTime()));
            } else {
                ps.setNull(7, Types.DATE);
            }

            // Si el ID es 0 o nulo, lo guardamos como NULL en la BD
            if (pago.getIdCuentaDestino() > 0) {
                ps.setInt(8, pago.getIdCuentaDestino());
            } else {
                ps.setNull(8, Types.INTEGER);
            }

            ps.setInt(9, pago.getReserva().getIdReserva());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) pago.setIdPago(rs.getInt(1));
            }
            return pago;
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar pago: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Pago> listAll() {
        List<Pago> lista = new ArrayList<>();
        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(construirPago(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar pagos: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public Pago update(Pago pago) {
        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {

            ps.setDouble(1, pago.getMontoNeto());
            ps.setDouble(2, pago.getMontoBruto());
            ps.setString(3, pago.getMoneda().name());
            ps.setDouble(4, pago.getPorcenComision());
            ps.setDouble(5, pago.getTipoCambio());
            ps.setString(6, pago.getEstadoTransaccion());

            if (pago.getFechaEnvioAnfitrion() != null) {
                ps.setDate(7, new java.sql.Date(pago.getFechaEnvioAnfitrion().getTime()));
            } else {
                ps.setNull(7, Types.DATE);
            }

            ps.setInt(8, pago.getIdCuentaDestino());
            ps.setInt(9, pago.getReserva().getIdReserva());
            ps.setInt(10, pago.getIdPago());

            ps.executeUpdate();
            return pago;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar pago: " + e.getMessage(), e);
        }
    }

    private Pago construirPago(ResultSet rs) throws SQLException {
        Reserva reservaProxy = new Reserva();
        reservaProxy.setIdReserva(rs.getInt("id_reserva"));

        Pago pago = new Pago();
        pago.setIdPago(rs.getInt("id_pago"));
        pago.setMontoNeto(rs.getDouble("monto_neto"));
        pago.setMontoBruto(rs.getDouble("monto_bruto"));
        pago.setMoneda(TipoMoneda.valueOf(rs.getString("moneda")));
        pago.setPorcenComision(rs.getDouble("porcentaje_comision"));
        pago.setReserva(reservaProxy);

        // --- MAPEANDO LOS CAMPOS DE LA RÚBRICA ---
        pago.setTipoCambio(rs.getDouble("tipo_cambio"));
        pago.setEstadoTransaccion(rs.getString("estado_transaccion"));
        pago.setFechaEnvioAnfitrion(rs.getDate("fecha_envio_anfitrion"));
        pago.setIdCuentaDestino(rs.getInt("id_cuenta_destino"));

        return pago;
    }

    @Override
    public void remove(Pago pago) {
        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, pago.getIdPago());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar pago: " + e.getMessage(), e);
        }
    }
}