package pe.edu.pe.pucp.proyecto.menssage.impl;

import pe.edu.pe.pucp.proyecto.menssage.dao.MensajeIDAO;
import pe.edu.pe.pucp.proyecto.manager.DBManager;
import pe.edu.pe.pucp.proyecto.messages.Mensaje;
import pe.edu.pe.pucp.proyecto.reservation.Reserva;
import pe.edu.pe.pucp.proyecto.users.Invitado;
import pe.edu.pe.pucp.proyecto.users.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MensajeImpl implements MensajeIDAO {

    @Override
    public Mensaje save(Mensaje msj) {
        // Incluimos 'leido': un mensaje nuevo nace NO leído hasta que el destinatario abre el chat.
        String sql = "INSERT INTO mensaje (texto, fecha_envio, emisor_id, id_reserva, leido) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, msj.getTexto());
            ps.setTimestamp(2, new java.sql.Timestamp(msj.getFechaEnvio().getTime()));

            // Relación con Usuario (emisor_id)
            ps.setInt(3, msj.getEmisor().getIdUsuario());

            // Relación con Reserva (id_reserva)
            if (msj.getReserva() != null) {
                ps.setInt(4, msj.getReserva().getIdReserva());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            // Estado de lectura inicial (false por defecto en la entidad).
            ps.setBoolean(5, msj.isLeido());

            ps.executeUpdate();

            // Obtenemos el ID autogenerado
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    msj.setIdMensaje(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al guardar Mensaje: " + e.getMessage());
        }
        return msj;
    }

    @Override
    public Mensaje load(Integer id) {
        String sql = "SELECT id_mensaje, texto, fecha_envio, emisor_id, id_reserva FROM mensaje WHERE id_mensaje = ?";

        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return construirMensaje(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar Mensaje: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Mensaje update(Mensaje msj) {
        String sql = "UPDATE mensaje SET texto = ?, fecha_envio = ?, emisor_id = ?, id_reserva = ?, leido = ? WHERE id_mensaje = ?";

        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, msj.getTexto());
            ps.setTimestamp(2, new java.sql.Timestamp(msj.getFechaEnvio().getTime()));
            ps.setInt(3, msj.getEmisor().getIdUsuario());
            ps.setInt(4, msj.getReserva().getIdReserva());
            ps.setBoolean(5, msj.isLeido());
            ps.setInt(6, msj.getIdMensaje());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al actualizar Mensaje: " + e.getMessage());
        }
        return msj;
    }

    @Override
    public List<Mensaje> listarPorChat(int idReserva) {
        List<Mensaje> lista = new ArrayList<>();
        String sql = "SELECT * FROM mensaje WHERE id_reserva = ? ORDER BY fecha_envio ASC";

        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idReserva);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(construirMensaje(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar Mensajes: " + e.getMessage());
        }
        return lista;
    }
    /**
     * Marca como leídos los mensajes de una reserva dirigidos al lector. "No leído para un usuario"
     * son los mensajes que le ENVIARON y aún no abrió, por eso excluimos los suyos (emisor_id <> lector):
     * uno no "lee" sus propios mensajes. Solo tocamos los que estaban en false (idempotente y barato).
     */
    @Override
    public void marcarLeidos(int idReserva, int idUsuarioLector) {
        String sql = "UPDATE mensaje SET leido = TRUE WHERE id_reserva = ? AND emisor_id <> ? AND leido = FALSE";
        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ps.setInt(2, idUsuarioLector);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al marcar mensajes como leídos: " + e.getMessage());
        }
    }

    /**
     * Cuenta los mensajes no leídos dirigidos al usuario en esa reserva (los que le enviaron y no abrió).
     * Mismo criterio que marcarLeidos: excluye los mensajes propios del usuario (emisor_id <> usuario).
     */
    @Override
    public int contarNoLeidos(int idReserva, int idUsuario) {
        String sql = "SELECT COUNT(*) FROM mensaje WHERE id_reserva = ? AND emisor_id <> ? AND leido = FALSE";
        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ps.setInt(2, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al contar mensajes no leídos: " + e.getMessage());
        }
        return 0;
    }

    // ============================================================
    // NUEVO MÉTODO AÑADIDO: listAll()
    // ============================================================
    @Override
    public List<Mensaje> listAll() {
        List<Mensaje> lista = new ArrayList<>();
        String sql = "SELECT id_mensaje, texto, fecha_envio, emisor_id, id_reserva FROM mensaje ORDER BY fecha_envio ASC";

        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(construirMensaje(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar todos los Mensajes: " + e.getMessage());
        }
        return lista;
    }
    /**
     * Método auxiliar para mapear el ResultSet al objeto Mensaje
     */
    private Mensaje construirMensaje(ResultSet rs) throws SQLException {
        Mensaje msj = new Mensaje();
        msj.setIdMensaje(rs.getInt("id_mensaje"));
        msj.setTexto(rs.getString("texto"));
        msj.setFechaEnvio(new java.util.Date(rs.getTimestamp("fecha_envio").getTime()));

        // Usuario Proxy (Emisor)
        Usuario emisor = new Invitado();
        emisor.setIdUsuario(rs.getInt("emisor_id"));
        msj.setEmisor(emisor);

        // Reserva Proxy
        Reserva reserva = new Reserva();
        reserva.setIdReserva(rs.getInt("id_reserva"));
        msj.setReserva(reserva);

        // Estado de lectura (TINYINT(1) en MySQL -> getBoolean lo lee como true/false).
        msj.setLeido(rs.getBoolean("leido"));

        return msj;
    }

    @Override
    public void remove(Mensaje msj) {
        String sql = "DELETE FROM mensaje WHERE id_mensaje = ?";
        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, msj.getIdMensaje());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar Mensaje: " + e.getMessage());
        }
    }
}