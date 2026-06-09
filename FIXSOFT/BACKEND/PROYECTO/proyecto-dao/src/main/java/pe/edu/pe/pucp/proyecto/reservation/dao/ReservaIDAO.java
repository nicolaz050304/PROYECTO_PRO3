package pe.edu.pe.pucp.proyecto.reservation.dao;

import pe.edu.pe.pucp.proyecto.dao.IDAO;
import pe.edu.pe.pucp.proyecto.manager.DBManager;
import pe.edu.pe.pucp.proyecto.reservation.EstadoReserva;
import pe.edu.pe.pucp.proyecto.reservation.Reserva;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public interface ReservaIDAO extends IDAO<Reserva, Integer> {
    List<Reserva> listAll();
}