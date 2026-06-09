package pe.edu.pe.pucp.proyecto.economy.dao;

import pe.edu.pe.pucp.proyecto.dao.IDAO;
import pe.edu.pe.pucp.proyecto.economy.Pago;

import java.util.List;

public interface PagoIDAO extends IDAO<Pago,Integer> {
    List <Pago> listAll();
}
