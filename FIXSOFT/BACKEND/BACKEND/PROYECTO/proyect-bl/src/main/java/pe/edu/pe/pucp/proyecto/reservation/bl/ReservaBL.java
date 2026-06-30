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

    // Reservas de un huésped / de un anfitrión (filtradas en SQL, para no traer toda la tabla).
    List<Reserva> listarPorInvitado(int idInvitado);
    List<Reserva> listarPorAnfitrion(int idAnfitrion);
}