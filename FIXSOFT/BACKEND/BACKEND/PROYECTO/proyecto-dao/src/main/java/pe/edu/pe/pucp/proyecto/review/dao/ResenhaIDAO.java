package pe.edu.pe.pucp.proyecto.review.dao;

import pe.edu.pe.pucp.proyecto.dao.IDAO;
import pe.edu.pe.pucp.proyecto.reviews.Resenha; // Asegúrate de importar bien tu clase

import java.util.List;

public interface ResenhaIDAO extends IDAO<Resenha, Integer> {
    // Si más adelante se necestite listar reseñas por Alojamiento, aqui esta:
    // Ejemplo: List<Resenha> listarPorAlojamiento(int idAlojamiento);
    List<Resenha> listarPorAlojamiento(int idAlojamiento);
    // Reseñas que recibió un usuario como CLIENTE (tipo_autor='ANFITRION' en sus reservas como invitado).
    List<Resenha> listarRecibidasPorCliente(int idInvitado);
    // ¡AÑADE ESTA LÍNEA!
    List<Resenha> listAll();

    // Moderación admin: todas las reseñas (activas e inactivas), sin filtrar por activo.
    List<Resenha> listarTodasIncluidasInactivas();
    // Moderación admin: reactiva una reseña oculta (activo = 1). Inverso de remove().
    void reactivar(Resenha resenha);

    // Promedio (AVG) y conteo de reseñas ACTIVAS por alojamiento, en UNA query (anti N+1).
    // Devuelve idAlojamiento -> [promedio, total].
    java.util.Map<Integer, double[]> calificacionesPorAlojamiento();
}