package pe.edu.pe.pucp.proyecto.notif.impl;

import pe.edu.pe.pucp.proyecto.manager.DBManager;
import pe.edu.pe.pucp.proyecto.notif.Notificaciones;
import pe.edu.pe.pucp.proyecto.notif.dao.NotificacionIDAO;
import pe.edu.pe.pucp.proyecto.users.Usuario;
import pe.edu.pe.pucp.proyecto.users.Invitado; // Usamos una clase concreta para el Proxy

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificacionImpl implements NotificacionIDAO {

    // Consultas SQL sincronizadas con la tabla 'notificaciones'
    private static final String SQL_INSERT = "INSERT INTO notificaciones (titulo, mensaje, leido, id_usuario) VALUES (?, ?, ?, ?)";
    private static final String SQL_LOAD = "SELECT id_notificacion, titulo, mensaje, leido, id_usuario FROM notificaciones WHERE id_notificacion = ?";
    private static final String SQL_UPDATE = "UPDATE notificaciones SET id_usuario = ?, titulo = ?, mensaje = ?, leido = ? WHERE id_notificacion = ?";
    private static final String SQL_DELETE = "DELETE FROM notificaciones WHERE id_notificacion = ?";
    private static final String SQL_SELECT_ALL = "SELECT id_notificacion, titulo, mensaje, leido, id_usuario FROM notificaciones";

    @Override
    public Notificaciones save(Notificaciones notificacion) {
        validarNotificacion(notificacion);

        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, notificacion.getTitulo());
            ps.setString(2, notificacion.getMensaje());
            ps.setBoolean(3, notificacion.isLeido());
            ps.setInt(4, notificacion.getUsuario().getIdUsuario());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    notificacion.setIdNotificacion(rs.getInt(1));
                }
            }
            return notificacion;
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar la notificación.", e);
        }
    }

    @Override
    public Notificaciones load(Integer id) {
        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_LOAD)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return construirNotificacion(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al cargar la notificación con id " + id, e);
        }
        return null;
    }

    @Override
    public List<Notificaciones> listAll() {
        List<Notificaciones> lista = new ArrayList<>();
        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(construirNotificacion(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar notificaciones: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public Notificaciones update(Notificaciones notificacion) {
        validarNotificacion(notificacion);

        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {

            ps.setInt(1, notificacion.getUsuario().getIdUsuario());
            ps.setString(2, notificacion.getTitulo());
            ps.setString(3, notificacion.getMensaje());
            ps.setBoolean(4, notificacion.isLeido());
            ps.setInt(5, notificacion.getIdNotificacion());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new RuntimeException("No existe una notificación con id " + notificacion.getIdNotificacion());
            }
            return notificacion;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar la notificación con id " + notificacion.getIdNotificacion(), e);
        }
    }

    @Override
    public void remove(Notificaciones notificacion) {
        if (notificacion == null) {
            throw new IllegalArgumentException("La notificación no puede ser null.");
        }

        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_DELETE)) {

            ps.setInt(1, notificacion.getIdNotificacion());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar la notificación con id " + notificacion.getIdNotificacion(), e);
        }
    }

    // Notificaciones de un usuario, más recientes primero (id desc): es el feed que ve el usuario.
    @Override
    public List<Notificaciones> listarPorUsuario(int idUsuario) {
        String sql = "SELECT id_notificacion, titulo, mensaje, leido, id_usuario FROM notificaciones " +
                     "WHERE id_usuario = ? ORDER BY id_notificacion DESC";
        List<Notificaciones> lista = new ArrayList<>();
        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(construirNotificacion(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar notificaciones del usuario " + idUsuario, e);
        }
        return lista;
    }

    // Marca una notificación como leída (cuando el usuario la abre/ve).
    @Override
    public void marcarLeida(int idNotificacion) {
        String sql = "UPDATE notificaciones SET leido = TRUE WHERE id_notificacion = ?";
        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idNotificacion);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al marcar como leída la notificación " + idNotificacion, e);
        }
    }

    // Nº de notificaciones no leídas del usuario (para el badge).
    @Override
    public int contarNoLeidas(int idUsuario) {
        String sql = "SELECT COUNT(*) FROM notificaciones WHERE id_usuario = ? AND leido = FALSE";
        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar notificaciones no leídas del usuario " + idUsuario, e);
        }
        return 0;
    }

    /**
     * Método auxiliar para mapear el ResultSet al objeto Notificaciones
     */
    private Notificaciones construirNotificacion(ResultSet rs) throws SQLException {
        // Proxy para Usuario (Usamos Invitado para evitar el error de clase abstracta)
        Usuario usuario = new Invitado();
        usuario.setIdUsuario(rs.getInt("id_usuario"));

        Notificaciones n = new Notificaciones();
        n.setIdNotificacion(rs.getInt("id_notificacion"));
        n.setTitulo(rs.getString("titulo"));
        n.setMensaje(rs.getString("mensaje"));
        n.setLeido(rs.getBoolean("leido"));
        n.setUsuario(usuario);

        return n;
    }

    private void validarNotificacion(Notificaciones notificacion) {
        if (notificacion == null) {
            throw new IllegalArgumentException("La notificación no puede ser null.");
        }
        if (notificacion.getUsuario() == null) {
            throw new IllegalArgumentException("El usuario destinatario es obligatorio.");
        }
        if (notificacion.getTitulo() == null || notificacion.getTitulo().isBlank()) {
            throw new IllegalArgumentException("El título es obligatorio.");
        }
        if (notificacion.getMensaje() == null || notificacion.getMensaje().isBlank()) {
            throw new IllegalArgumentException("El mensaje es obligatorio.");
        }
    }
}