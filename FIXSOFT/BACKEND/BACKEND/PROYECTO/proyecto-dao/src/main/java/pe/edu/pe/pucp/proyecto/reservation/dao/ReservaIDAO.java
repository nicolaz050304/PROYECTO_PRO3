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
    int finalizarReservasVencidas();

    // Devuelve las reservas que OCUPAN fechas en un alojamiento: solo las que están
    // CONFIRMADA o PENDIENTE (las CANCELADA y FINALIZADA ya no bloquean el calendario).
    List<Reserva> listarOcupadasPorAlojamiento(int idAlojamiento);
}