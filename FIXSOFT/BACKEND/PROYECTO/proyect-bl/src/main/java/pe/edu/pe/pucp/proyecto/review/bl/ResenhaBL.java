package pe.edu.pe.pucp.proyecto.review.bl;

import pe.edu.pe.pucp.proyecto.bl.IBL;
import pe.edu.pe.pucp.proyecto.reviews.Resenha;

import java.util.List;

public interface ResenhaBL extends IBL<Resenha, Integer> {
    // Aquí puedes agregar métodos como listarResenhasPorAlojamiento si lo necesitas luego

    // Reseñas que recibió un usuario como CLIENTE (las que le dejaron los anfitriones).
    List<Resenha> listarRecibidasPorCliente(int idInvitado);

    // Moderación admin: todas las reseñas (activas e inactivas) y reactivar una oculta.
    List<Resenha> listarTodasIncluidasInactivas();
    int reactivar(Resenha resenha);

    // Promedio (AVG) y conteo de reseñas ACTIVAS por alojamiento, en UNA query (anti N+1).
    java.util.Map<Integer, double[]> calificacionesPorAlojamiento();
}