package pe.edu.pe.pucp.proyecto.review.bl;

import pe.edu.pe.pucp.proyecto.bl.IBL;
import pe.edu.pe.pucp.proyecto.reviews.Resenha;

import java.util.List;

public interface ResenhaBL extends IBL<Resenha, Integer> {
    // Aquí puedes agregar métodos como listarResenhasPorAlojamiento si lo necesitas luego

    // Moderación admin: todas las reseñas (activas e inactivas) y reactivar una oculta.
    List<Resenha> listarTodasIncluidasInactivas();
    int reactivar(Resenha resenha);
}