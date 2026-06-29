package pe.edu.pe.pucp.proyecto.accomodations.dao;

import pe.edu.pe.pucp.proyecto.accomodations.Alojamiento;
import pe.edu.pe.pucp.proyecto.dao.IDAO;

import java.util.List;

public interface AlojamientoIDAO extends IDAO<Alojamiento,Integer> {
    List<Alojamiento> listAll();
    // Disponibles para [entrada, salida]: excluye los que tienen reserva viva solapada.
    List<Alojamiento> listarDisponibles(java.time.LocalDate entrada, java.time.LocalDate salida);

    // TOP de alojamientos (RF22): devuelve los ids ordenados por el criterio, con su métrica, hasta 'limite'.
    // El ranking se resuelve aquí (en SQL con GROUP BY/ORDER BY) por eficiencia: no se trae todo al frontend.
    // criterio: "calificacion" (AVG de calificacion de reseñas activas), "reservas" (COUNT de reservas),
    //           "resenas" (COUNT de reseñas activas). Cada int[] = {idAlojamiento, valorMetrica}.
    List<int[]> topPorCriterio(String criterio, int limite);
}
