package pe.edu.pe.pucp.proyecto.review.implbl;

import pe.edu.pe.pucp.proyecto.reservation.bl.ReservaBL;
import pe.edu.pe.pucp.proyecto.reservation.implbl.ReservaBLImpl;
import pe.edu.pe.pucp.proyecto.review.bl.ResenhaBL;
import pe.edu.pe.pucp.proyecto.review.dao.ResenhaIDAO;
import pe.edu.pe.pucp.proyecto.review.impl.ResenhaImpl;
import pe.edu.pe.pucp.proyecto.reviews.Resenha;

import java.util.List;

public class ResenhaBLImpl implements ResenhaBL {

    private ResenhaIDAO daoResenha = new ResenhaImpl();
    private ReservaBL reservaBL = new ReservaBLImpl();

    @Override
    public int insertar(Resenha resenha) {
        // --- LÓGICA DE NEGOCIO: VALIDACIONES DE RESEÑAS ---

        // 1. Validar que la calificación esté en el rango de 1 a 5
        if (resenha.getCalificacion() < 1 || resenha.getCalificacion() > 5) {
            throw new RuntimeException("Error: La calificación debe estar entre 1 y 5 estrellas.");
        }

        // 2. Validar que el comentario no esté vacío ni sea muy corto
        if (resenha.getComentario() == null || resenha.getComentario().trim().length() < 10) {
            throw new RuntimeException("Error: El comentario de la reseña debe tener al menos 10 caracteres.");
        }

        // 3. Validar que esté vinculada a una reserva
        if (resenha.getReserva() == null || resenha.getReserva().getIdReserva() <= 0) {
            throw new RuntimeException("Error: No se puede dejar una reseña sin una reserva asociada.");
        }

        // Si pasa las reglas, se guarda en la base de datos
        // 1. Guardamos la reseña en la base de datos
        Resenha resenhaGuardada = daoResenha.save(resenha);

        // Llamamos al método que creamos en ReservaBL para marcar que ya se calificó
        try {
            reservaBL.marcarReservaComoCalificada(resenha.getReserva().getIdReserva(), resenha.getTipoAutor());
        } catch (Exception e) {
            System.err.println("Advertencia: La reseña se guardó, pero no se pudo actualizar el estado en la Reserva: " + e.getMessage());
        }

        return resenhaGuardada.getIdResenha();
    }

    @Override
    public List<Resenha> listarTodos() {
        return daoResenha.listAll();
    }

    @Override
    public Resenha obtenerPorId(Integer id) {
        return daoResenha.load(id);
    }

    @Override
    public int modificar(Resenha resenha) {
        // Al modificar, también aseguramos que no pongan notas imposibles
        if (resenha.getCalificacion() < 1 || resenha.getCalificacion() > 5) {
            throw new RuntimeException("Error: La calificación debe estar entre 1 y 5 estrellas.");
        }
        return daoResenha.update(resenha).getIdResenha();
    }

    @Override
    public int eliminar(Resenha resenha) {
        daoResenha.remove(resenha);
        return 1;
    }

    @Override
    public List<Resenha> listarTodasIncluidasInactivas() {
        return daoResenha.listarTodasIncluidasInactivas();
    }

    @Override
    public int reactivar(Resenha resenha) {
        daoResenha.reactivar(resenha);
        return 1;
    }

    @Override
    public java.util.Map<Integer, double[]> calificacionesPorAlojamiento() {
        return daoResenha.calificacionesPorAlojamiento();
    }
}