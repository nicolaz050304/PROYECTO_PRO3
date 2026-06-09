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
        // SQL corregido: Solo las 4 columnas existentes en la tabla. id_mensaje es AUTO_INCREMENT.
        String sql = "INSERT INTO mensaje (texto, fecha_envio, emisor_id, id_reserva) VALUES (?, ?, ?, ?)";

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
        String sql = "UPDATE mensaje SET texto = ?, fecha_envio = ?, emisor_id = ?, id_reserva = ? WHERE id_mensaje = ?";

        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, msj.getTexto());
            ps.setTimestamp(2, new java.sql.Timestamp(msj.getFechaEnvio().getTime()));
            ps.setInt(3, msj.getEmisor().getIdUsuario());
            ps.setInt(4, msj.getReserva().getIdReserva());
            ps.setInt(5, msj.getIdMensaje());

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