package pe.edu.pe.pucp.proyecto.reservation.bl;

import pe.edu.pe.pucp.proyecto.bl.IBL;
import pe.edu.pe.pucp.proyecto.reservation.Reserva;

public interface ReservaBL extends IBL<Reserva, Integer> {
    // Aquí puedes añadir métodos específicos de negocio para reservas
    void marcarReservaComoCalificada(int idReserva, String tipoAutor);
    int finalizarReservasVencidas();
}