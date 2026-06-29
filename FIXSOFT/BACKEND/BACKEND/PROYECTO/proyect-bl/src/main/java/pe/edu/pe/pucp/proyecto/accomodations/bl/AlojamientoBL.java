package pe.edu.pe.pucp.proyecto.accomodations.bl;

import pe.edu.pe.pucp.proyecto.accomodations.Alojamiento;
import pe.edu.pe.pucp.proyecto.bl.IBL;

import java.time.LocalDate;
import java.util.List;

public interface AlojamientoBL extends IBL<Alojamiento, Integer> {
    // Interfaz genérica para la capa de negocio de Alojamiento

    // Alojamientos disponibles para un rango de fechas (excluye reservas vivas solapadas).
    List<Alojamiento> listarDisponibles(LocalDate entrada, LocalDate salida);

    // TOP de alojamientos (RF22) según criterio: "calificacion" / "reservas" / "resenas".
    List<Alojamiento> topPorCriterio(String criterio, int limite);
}