package pe.edu.pe.pucp.proyecto.accomodations.dao;

import pe.edu.pe.pucp.proyecto.accomodations.Alojamiento;
import pe.edu.pe.pucp.proyecto.dao.IDAO;

import java.util.List;

public interface AlojamientoIDAO extends IDAO<Alojamiento,Integer> {
    List<Alojamiento> listAll();
}
