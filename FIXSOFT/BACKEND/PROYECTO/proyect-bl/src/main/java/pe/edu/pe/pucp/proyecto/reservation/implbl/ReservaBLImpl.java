package pe.edu.pe.pucp.proyecto.reservation.implbl;

import org.jetbrains.annotations.NotNull;
import pe.edu.pe.pucp.proyecto.reservation.Reserva;
import pe.edu.pe.pucp.proyecto.reservation.bl.ReservaBL;
import pe.edu.pe.pucp.proyecto.reservation.dao.ReservaIDAO;
import pe.edu.pe.pucp.proyecto.reservation.impl.ReservaImpl;

import java.util.List;

public class ReservaBLImpl implements ReservaBL {

    private ReservaIDAO daoReserva = new ReservaImpl();

    @Override
    public int insertar(Reserva reserva) {
        // --- LÓGICA DE NEGOCIO: VALIDACIONES ---
        //PRIMERO VEO QUE MI ALOJAMIENTO NO SEA NULO CON ESO PUEDO DAR PIE A OTRAS VALIDACIONES
        if(reserva.getAlojamiento() == null){
            throw new RuntimeException("Error: DEBE DE HABER UN ALOJAMIENTO ASOCIADO NO PUEDE SER NULA");
        }
        if (reserva.getFechaFin().before(reserva.getFechaInicio())) {
            throw new RuntimeException("Error: La fecha de fin no puede ser anterior a la de inicio.");
        }
        if (reserva.getMontoTotal() <= 0) {
            throw new RuntimeException("Error: El monto de la reserva debe ser mayor a cero.");
        }
        // Si pasa las validaciones, se guarda
        return daoReserva.save(reserva).getIdReserva();
    }

    @Override
    public List<Reserva> listarTodos() {
        return daoReserva.listAll();
    }

    @Override
    public Reserva obtenerPorId(Integer id) {
        return daoReserva.load(id);
    }

    @Override
    public int modificar(Reserva reserva) {
        // También validamos al modificar
        if (reserva.getFechaFin().before(reserva.getFechaInicio())) {
            throw new RuntimeException("Error: Rango de fechas inválido.");
        }
        return daoReserva.update(reserva).getIdReserva();
    }

    @Override
    public int eliminar(Reserva reserva) {
        daoReserva.remove(reserva);
        return 1;
    }

    @Override
    public void marcarReservaComoCalificada(int idReserva, String tipoAutor) {
        // Obtenemos la reserva actual
        Reserva reservaBD = daoReserva.load(idReserva);

        if (reservaBD != null) {
            boolean debeActualizar = false;

            // Dependiendo del autor, cambiamos la bandera
            if ("INVITADO".equalsIgnoreCase(tipoAutor)) {
                reservaBD.setCalificadoPorInvitado(true);
                debeActualizar = true;
            } else if ("ANFITRION".equalsIgnoreCase(tipoAutor)) {
                reservaBD.setCalificadoPorAnfitrion(true);
                debeActualizar = true;
            }

            // Si hubo un cambio válido, la actualizamos en la BD
            if (debeActualizar) {
                daoReserva.update(reservaBD);
            }
        } else {
            throw new RuntimeException("Error: No se encontró la reserva con ID: " + idReserva);
        }
    }
}