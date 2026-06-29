package pe.edu.pe.pucp.proyecto.reservation.impl;

import pe.edu.pe.pucp.proyecto.reservation.Reserva;
import pe.edu.pe.pucp.proyecto.reservation.dao.ReservaIDAO;
import pe.edu.pe.pucp.proyecto.reservation.EstadoReserva;
import pe.edu.pe.pucp.proyecto.manager.DBManager;
import pe.edu.pe.pucp.proyecto.users.Invitado;
import pe.edu.pe.pucp.proyecto.accomodations.Alojamiento;
import pe.edu.pe.pucp.proyecto.accomodations.Casa;
import pe.edu.pe.pucp.proyecto.economy.TipoMoneda;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaImpl implements ReservaIDAO {

    private static volatile boolean columnaHuespedesLista = false;

    public ReservaImpl() {
        asegurarColumnaHuespedes();
    }

    /**
     * La tabla `reserva` ya existía sin columna de nº de huéspedes. La agregamos en caliente
     * (una sola vez) si falta, consultando information_schema porque MySQL no soporta
     * ALTER TABLE ... ADD COLUMN IF NOT EXISTS. Default 1 para las filas existentes.
     */
    private static synchronized void asegurarColumnaHuespedes() {
        if (columnaHuespedesLista) return;
        String check = "SELECT COUNT(*) FROM information_schema.COLUMNS " +
                "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'reserva' AND COLUMN_NAME = 'num_huespedes'";
        try (Connection con = DBManager.getInstance().getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(check)) {
            boolean existe = rs.next() && rs.getInt(1) > 0;
            if (!existe) {
                st.executeUpdate("ALTER TABLE reserva ADD COLUMN num_huespedes INT NOT NULL DEFAULT 1");
            }
            columnaHuespedesLista = true;
        } catch (SQLException e) {
            e.printStackTrace();   // si falla, el SELECT/INSERT que use la columna lo reportará
        }
    }

    @Override
    public List<Reserva> listAll() {
        List<Reserva> reservas = new ArrayList<>();
        // SQL con los 9 campos necesarios
        String sql = "SELECT id_reserva, fecha_inicio, fecha_fin, monto_total, id_invitado, " +
                "id_alojamiento, fecha_contacto, estado, moneda, calificado_por_invitado, calificado_por_anfitrion, num_huespedes FROM reserva";

        try (Connection con = DBManager.getInstance().getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Invitado inv = new Invitado();
                inv.setIdUsuario(rs.getInt("id_invitado"));

                Alojamiento alo = new Casa();
                alo.setIdAlojamiento(rs.getInt("id_alojamiento"));

                // Usamos el constructor de 9 parámetros de tu clase Reserva
                Reserva r = new Reserva(
                        rs.getInt("id_reserva"),
                        rs.getDate("fecha_inicio"),
                        rs.getDate("fecha_fin"),
                        rs.getDouble("monto_total"),
                        inv,
                        alo,
                        rs.getDate("fecha_contacto"),
                        EstadoReserva.valueOf(rs.getString("estado")),
                        TipoMoneda.valueOf(rs.getString("moneda"))
                );
                r.setCalificadoPorInvitado(rs.getBoolean("calificado_por_invitado"));
                r.setCalificadoPorAnfitrion(rs.getBoolean("calificado_por_anfitrion"));
                r.setNumHuespedes(rs.getInt("num_huespedes"));
                reservas.add(r);
            }
        } catch (SQLException e) {
            // Propagamos el error en vez de tragarlo: si una operación de BD falla, el backend debe
            // enterarse y responder con error, no fingir éxito. Antes estos catch solo imprimían y
            // devolvían el objeto/null/lista igual, lo que hacía que el frontend mostrara "éxito"
            // (p. ej. "pago completado") sin que se guardara ninguna fila. (Mismo estilo que AlojamientoImpl/PagoImpl.)
            throw new RuntimeException("Error en ReservaImpl (listar): " + e.getMessage(), e);
        }
        return reservas;
    }

    @Override
    public Reserva load(Integer id) {
        String sql = "SELECT id_reserva, fecha_inicio, fecha_fin, monto_total, id_invitado, " +
                "id_alojamiento, fecha_contacto, estado, moneda, calificado_por_invitado, calificado_por_anfitrion, num_huespedes "+
                "FROM reserva WHERE id_reserva = ?";
        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Invitado inv = new Invitado();
                    inv.setIdUsuario(rs.getInt("id_invitado"));
                    Alojamiento alo = new Casa();
                    alo.setIdAlojamiento(rs.getInt("id_alojamiento"));

                    Reserva r =new Reserva(
                            rs.getInt("id_reserva"),
                            rs.getDate("fecha_inicio"),
                            rs.getDate("fecha_fin"),
                            rs.getDouble("monto_total"),
                            inv,
                            alo,
                            rs.getDate("fecha_contacto"),
                            EstadoReserva.valueOf(rs.getString("estado")),
                            TipoMoneda.valueOf(rs.getString("moneda"))
                    );
                    r.setCalificadoPorInvitado(rs.getBoolean("calificado_por_invitado"));
                    r.setCalificadoPorAnfitrion(rs.getBoolean("calificado_por_anfitrion"));
                    r.setNumHuespedes(rs.getInt("num_huespedes"));

                    return r;
                }
            }
        } catch (SQLException e) {
            // Propagar en vez de ocultar (ver nota en listAll).
            throw new RuntimeException("Error en ReservaImpl (cargar): " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Reserva save(Reserva reserva) {
        // Corregido: 8 parámetros para el INSERT
        String sql = "INSERT INTO reserva (fecha_inicio, fecha_fin, monto_total, estado, " +
                "id_invitado, id_alojamiento, fecha_contacto, moneda, num_huespedes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, new java.sql.Date(reserva.getFechaInicio().getTime()));
            ps.setDate(2, new java.sql.Date(reserva.getFechaFin().getTime()));
            ps.setDouble(3, reserva.getMontoTotal());
            ps.setString(4, reserva.getEstadoReserva().name());
            ps.setInt(5, reserva.getInvitado().getIdUsuario());
            ps.setInt(6, reserva.getAlojamiento().getIdAlojamiento());

            // Manejo de nulos para fecha de contacto
            if (reserva.getFechaContacto() != null) {
                ps.setDate(7, new java.sql.Date(reserva.getFechaContacto().getTime()));
            } else {
                ps.setNull(7, Types.DATE);
            }

            ps.setString(8, reserva.getMoneda().name());
            ps.setInt(9, reserva.getNumHuespedes() > 0 ? reserva.getNumHuespedes() : 1);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) reserva.setIdReserva(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            // Propagar en vez de ocultar (ver nota en listAll). Crítico aquí: antes un INSERT fallido
            // devolvía la reserva con id=0 y el frontend lo tomaba como reserva creada.
            throw new RuntimeException("Error en ReservaImpl (guardar): " + e.getMessage(), e);
        }
        return reserva;
    }

    @Override
    public Reserva update(Reserva reserva) {
        // Actualizamos también la moneda y la fecha de contacto por si hubo cambios
        String sql = "UPDATE reserva SET fecha_inicio = ?, fecha_fin = ?, monto_total = ?, " +
                "estado = ?, fecha_contacto = ?, moneda = ?, calificado_por_invitado = ?, calificado_por_anfitrion = ?, num_huespedes = ? WHERE id_reserva = ?";
        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, new java.sql.Date(reserva.getFechaInicio().getTime()));
            ps.setDate(2, new java.sql.Date(reserva.getFechaFin().getTime()));
            ps.setDouble(3, reserva.getMontoTotal());
            ps.setString(4, reserva.getEstadoReserva().name());

            if (reserva.getFechaContacto() != null) {
                ps.setDate(5, new java.sql.Date(reserva.getFechaContacto().getTime()));
            } else {
                ps.setNull(5, Types.DATE);
            }

            ps.setString(6, reserva.getMoneda().name());
            ps.setBoolean(7, reserva.isCalificadoPorInvitado());
            ps.setBoolean(8, reserva.isCalificadoPorAnfitrion());
            ps.setInt(9, reserva.getNumHuespedes() > 0 ? reserva.getNumHuespedes() : 1);

            ps.setInt(10, reserva.getIdReserva());

            ps.executeUpdate();
        } catch (SQLException e) {
            // Propagar en vez de ocultar (ver nota en listAll).
            throw new RuntimeException("Error en ReservaImpl (actualizar): " + e.getMessage(), e);
        }
        return reserva;
    }

    @Override
    public List<Reserva> listarOcupadasPorAlojamiento(int idAlojamiento) {
        List<Reserva> reservas = new ArrayList<>();
        // Solo CONFIRMADA y PENDIENTE ocupan fechas: una reserva confirmada o aún por
        // confirmar bloquea el calendario; las CANCELADA y FINALIZADA ya no lo hacen.
        String sql = "SELECT id_reserva, fecha_inicio, fecha_fin, monto_total, id_invitado, " +
                "id_alojamiento, fecha_contacto, estado, moneda, calificado_por_invitado, calificado_por_anfitrion " +
                "FROM reserva WHERE id_alojamiento = ? AND estado IN ('CONFIRMADA','PENDIENTE')";

        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idAlojamiento);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Invitado inv = new Invitado();
                    inv.setIdUsuario(rs.getInt("id_invitado"));

                    Alojamiento alo = new Casa();
                    alo.setIdAlojamiento(rs.getInt("id_alojamiento"));

                    // Mismo patrón de construcción que load/listAll (constructor de 9 parámetros).
                    Reserva r = new Reserva(
                            rs.getInt("id_reserva"),
                            rs.getDate("fecha_inicio"),
                            rs.getDate("fecha_fin"),
                            rs.getDouble("monto_total"),
                            inv,
                            alo,
                            rs.getDate("fecha_contacto"),
                            EstadoReserva.valueOf(rs.getString("estado")),
                            TipoMoneda.valueOf(rs.getString("moneda"))
                    );
                    r.setCalificadoPorInvitado(rs.getBoolean("calificado_por_invitado"));
                    r.setCalificadoPorAnfitrion(rs.getBoolean("calificado_por_anfitrion"));
                    reservas.add(r);
                }
            }
        } catch (SQLException e) {
            // Propagar en vez de ocultar (ver nota en listAll).
            throw new RuntimeException("Error en ReservaImpl (ocupadas por alojamiento): " + e.getMessage(), e);
        }
        return reservas;
    }

    @Override
    public int finalizarReservasVencidas() {
        // Un solo UPDATE masivo e idempotente: las reservas CONFIRMADAS cuya estadía
        // ya terminó (fecha_fin estrictamente anterior a hoy) pasan a FINALIZADA.
        String sql = "UPDATE reserva SET estado = 'FINALIZADA' " +
                "WHERE estado = 'CONFIRMADA' AND fecha_fin < CURDATE()";
        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            return ps.executeUpdate();   // nº de filas actualizadas
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public void remove(Reserva reserva) {
        // Eliminación lógica: Marcamos como CANCELADA
        String sql = "UPDATE reserva SET estado = 'CANCELADA' WHERE id_reserva = ?";
        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, reserva.getIdReserva());
            ps.executeUpdate();
            reserva.setEstadoReserva(EstadoReserva.CANCELADA);
        } catch (SQLException e) {
            // Propagar en vez de ocultar (ver nota en listAll).
            throw new RuntimeException("Error en ReservaImpl (eliminar): " + e.getMessage(), e);
        }
    }
}