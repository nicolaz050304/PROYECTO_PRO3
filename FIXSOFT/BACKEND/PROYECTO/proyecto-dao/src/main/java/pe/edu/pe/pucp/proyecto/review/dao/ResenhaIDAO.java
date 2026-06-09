package pe.edu.pe.pucp.proyecto.review.dao;

import pe.edu.pe.pucp.proyecto.dao.IDAO;
import pe.edu.pe.pucp.proyecto.reviews.Resenha; // Asegúrate de importar bien tu clase

import java.util.List;

public interface ResenhaIDAO extends IDAO<Resenha, Integer> {
    // Si más adelante se necestite listar reseñas por Alojamiento, aqui esta:
    // Ejemplo: List<Resenha> listarPorAlojamiento(int idAlojamiento);
    List<Resenha> listarPorAlojamiento(int idAlojamiento);
    // ¡AÑADE ESTA LÍNEA!
    List<Resenha> listAll();
}