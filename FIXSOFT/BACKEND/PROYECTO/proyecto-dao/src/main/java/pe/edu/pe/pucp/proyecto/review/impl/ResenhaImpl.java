package pe.edu.pe.pucp.proyecto.review.impl;

import pe.edu.pe.pucp.proyecto.manager.DBManager;
import pe.edu.pe.pucp.proyecto.review.dao.ResenhaIDAO;
import pe.edu.pe.pucp.proyecto.reviews.Resenha;
import pe.edu.pe.pucp.proyecto.reservation.Reserva;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResenhaImpl implements ResenhaIDAO {

    @Override
    public Resenha save(Resenha resenha) {
        String sql = "INSERT INTO resenha(calificacion, comentario, fecha_publicacion, id_reserva, tipo_autor, activo) " +
                "VALUES (?, ?, ?, ?, ?, 1)";

        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setInt(1, resenha.getCalificacion());
            pst.setString(2, resenha.getComentario());
            pst.setDate(3, new java.sql.Date(resenha.getFechaPublicacion().getTime()));

            // MODIFICADO: Sacamos el ID desde el objeto Reserva dentro de Resenha
            pst.setInt(4, resenha.getReserva().getIdReserva());
            pst.setString(5, resenha.getTipoAutor());

            pst.executeUpdate();

            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) {
                    resenha.setIdResenha(rs.getInt(1));
                }
            }
        } catch (SQLException ex) {
            System.err.println("ERROR al guardar reseña: " + ex.getMessage());
        }
        return resenha;
    }

    @Override
    public Resenha update(Resenha resenha) {
        String sql = "UPDATE resenha SET calificacion = ?, comentario = ?, fecha_publicacion = ?, " +
                "id_reserva = ?, tipo_autor = ? WHERE id_resenha = ?";

        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, resenha.getCalificacion());
            pst.setString(2, resenha.getComentario());
            pst.setDate(3, new java.sql.Date(resenha.getFechaPublicacion().getTime()));

            // MODIFICADO: Uso del objeto Reserva
            pst.setInt(4, resenha.getReserva().getIdReserva());
            pst.setString(5, resenha.getTipoAutor());
            pst.setInt(6, resenha.getIdResenha());

            pst.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("ERROR al actualizar reseña: " + ex.getMessage());
        }
        return resenha;
    }

    @Override
    public Resenha load(Integer id) {
        String sql = "SELECT id_resenha, calificacion, comentario, fecha_publicacion, activo, " +
                "id_reserva, tipo_autor FROM resenha WHERE id_resenha = ? AND activo = 1";

        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Resenha res = new Resenha();
                    res.setIdResenha(rs.getInt("id_resenha"));
                    res.setCalificacion(rs.getInt("calificacion"));
                    res.setComentario(rs.getString("comentario"));
                    res.setFechaPublicacion(rs.getDate("fecha_publicacion"));
                    res.setActivo(rs.getBoolean("activo"));
                    res.setTipoAutor(rs.getString("tipo_autor"));

                    // MODIFICADO: Creamos el objeto Reserva y le asignamos el ID
                    Reserva r = new Reserva();
                    r.setIdReserva(rs.getInt("id_reserva"));
                    res.setReserva(r);

                    return res;
                }
            }
        } catch (SQLException ex) {
            System.err.println("ERROR al cargar reseña: " + ex.getMessage());
        }
        return null;
    }

    @Override
    public List<Resenha> listarPorAlojamiento(int idAlojamiento) {
        String sql = "SELECT r.* FROM resenha r " +
                "INNER JOIN reserva res ON r.id_reserva = res.id_reserva " +
                "WHERE res.id_alojamiento = ? AND r.activo = 1";

        List<Resenha> list = new ArrayList<>();

        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, idAlojamiento);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Resenha res = new Resenha();
                    res.setIdResenha(rs.getInt("id_resenha"));
                    res.setCalificacion(rs.getInt("calificacion"));
                    res.setComentario(rs.getString("comentario"));
                    res.setFechaPublicacion(rs.getDate("fecha_publicacion"));
                    res.setActivo(rs.getBoolean("activo"));
                    res.setTipoAutor(rs.getString("tipo_autor"));

                    // MODIFICADO: Reconstruimos el objeto Reserva para cada reseña
                    Reserva r = new Reserva();
                    r.setIdReserva(rs.getInt("id_reserva"));
                    res.setReserva(r);

                    list.add(res);
                }
            }
        } catch (SQLException ex) {
            System.err.println("ERROR al listar reseñas: " + ex.getMessage());
        }
        return list;
    }

    @Override
    public void remove(Resenha resenha) {
        String sql = "UPDATE resenha SET activo = 0 WHERE id_resenha = ?";
        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, resenha.getIdResenha());
            pst.executeUpdate();
            resenha.setActivo(false);

        } catch (SQLException ex) {
            System.err.println("ERROR al eliminar reseña: " + ex.getMessage());
        }
    }

    @Override
    public List<Resenha> listAll() {
        String sql = "SELECT id_resenha, calificacion, comentario, fecha_publicacion, activo, " +
                "id_reserva, tipo_autor FROM resenha WHERE activo = 1";

        List<Resenha> list = new ArrayList<>();

        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Resenha res = new Resenha();
                res.setIdResenha(rs.getInt("id_resenha"));
                res.setCalificacion(rs.getInt("calificacion"));
                res.setComentario(rs.getString("comentario"));
                res.setFechaPublicacion(rs.getDate("fecha_publicacion"));
                res.setActivo(rs.getBoolean("activo"));
                res.setTipoAutor(rs.getString("tipo_autor"));

                // Creamos el objeto Reserva y le asignamos el ID
                Reserva r = new Reserva();
                r.setIdReserva(rs.getInt("id_reserva"));
                res.setReserva(r);

                list.add(res);
            }
        } catch (SQLException ex) {
            System.err.println("ERROR al listar todas las reseñas: " + ex.getMessage());
        }
        return list;
    }

    /**
     * Igual que listAll() pero SIN el filtro "WHERE activo = 1": trae activas e
     * inactivas (para la moderación admin). Reconstruye cada Resenha igual que listAll,
     * incluyendo el flag activo y el objeto Reserva con su id.
     */
    @Override
    public List<Resenha> listarTodasIncluidasInactivas() {
        String sql = "SELECT id_resenha, calificacion, comentario, fecha_publicacion, activo, " +
                "id_reserva, tipo_autor FROM resenha";

        List<Resenha> list = new ArrayList<>();

        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Resenha res = new Resenha();
                res.setIdResenha(rs.getInt("id_resenha"));
                res.setCalificacion(rs.getInt("calificacion"));
                res.setComentario(rs.getString("comentario"));
                res.setFechaPublicacion(rs.getDate("fecha_publicacion"));
                res.setActivo(rs.getBoolean("activo"));
                res.setTipoAutor(rs.getString("tipo_autor"));

                Reserva r = new Reserva();
                r.setIdReserva(rs.getInt("id_reserva"));
                res.setReserva(r);

                list.add(res);
            }
        } catch (SQLException ex) {
            System.err.println("ERROR al listar todas las reseñas (incl. inactivas): " + ex.getMessage());
        }
        return list;
    }

    /** Reactiva una reseña oculta (activo = 1). Inverso de remove(). */
    @Override
    public void reactivar(Resenha resenha) {
        String sql = "UPDATE resenha SET activo = 1 WHERE id_resenha = ?";
        try (Connection con = DBManager.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, resenha.getIdResenha());
            pst.executeUpdate();
            resenha.setActivo(true);

        } catch (SQLException ex) {
            System.err.println("ERROR al reactivar reseña: " + ex.getMessage());
        }
    }
}