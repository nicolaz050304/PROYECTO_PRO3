package pe.edu.pe.pucp.proyecto.reservation.bl;

import pe.edu.pe.pucp.proyecto.bl.IBL;
import pe.edu.pe.pucp.proyecto.reservation.Reserva;

import java.util.List;

public interface ReservaBL extends IBL<Reserva, Integer> {
    // Aquí puedes añadir métodos específicos de negocio para reservas
    void marcarReservaComoCalificada(int idReserva, String tipoAutor);
    int finalizarReservasVencidas();

    // Reservas que ocupan fechas (CONFIRMADA o PENDIENTE) de un alojamiento.
    List<Reserva> listarOcupadasPorAlojamiento(int idAlojamiento);
}