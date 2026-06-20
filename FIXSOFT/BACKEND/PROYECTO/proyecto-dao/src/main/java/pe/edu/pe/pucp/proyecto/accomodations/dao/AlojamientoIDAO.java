package pe.edu.pe.pucp.proyecto.accomodations.dao;

import pe.edu.pe.pucp.proyecto.accomodations.Alojamiento;
import pe.edu.pe.pucp.proyecto.dao.IDAO;

import java.util.List;

public interface AlojamientoIDAO extends IDAO<Alojamiento,Integer> {
    List<Alojamiento> listAll();
    // Disponibles para [entrada, salida]: excluye los que tienen reserva viva solapada.
    List<Alojamiento> listarDisponibles(java.time.LocalDate entrada, java.time.LocalDate salida);
}
