package pe.edu.pe.pucp.proyecto.web.rs;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import pe.edu.pe.pucp.proyecto.economy.Pago;
import pe.edu.pe.pucp.proyecto.economy.bl.PagoBL;
import pe.edu.pe.pucp.proyecto.economy.implbl.PagoBLImpl;
import pe.edu.pe.pucp.proyecto.reservation.Reserva;
import pe.edu.pe.pucp.proyecto.reservation.bl.ReservaBL;
import pe.edu.pe.pucp.proyecto.reservation.implbl.ReservaBLImpl;
import pe.edu.pe.pucp.proyecto.web.dto.PagoDTO;
import pe.edu.pe.pucp.proyecto.web.mapper.PagoMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Recurso REST de Pago (RF13). Solo expone DTOs planos (nunca la entidad Pago,
 * cuya Reserva tiene ciclos) y delega en la BL existente.
 *
 * El pago se registra a partir del id de la reserva (no de un body): cuando se llega
 * a pagar, la reserva YA existe en BD, así que basta su id; la BL carga sus datos
 * (monto y moneda) y aplica la regla de negocio. La comisión (10%), el monto
 * neto/bruto y el estado los calcula PagoBL.registrarPagoDeReserva, no este recurso.
 *
 * Base de despliegue:
 *   http://localhost:8080/BunkiBackend/webresources/PagoRS
 */
@Path("PagoRS")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PagoRS {

    private final PagoBL pagoBL = new PagoBLImpl();
    private final ReservaBL reservaBL = new ReservaBLImpl();

    /**
     * POST PagoRS/reserva/{idReserva} -> registra el pago de esa reserva.
     * Carga la reserva por id (obtenerPorId de la BL), delega el cálculo a la BL
     * y devuelve el PagoDTO creado. 404 si la reserva no existe; 400 ante error de negocio.
     */
    @POST
    @Path("reserva/{idReserva}")
    public Response registrarDeReserva(@PathParam("idReserva") int idReserva) {
        try {
            // La reserva ya existe cuando se paga: la cargamos por su id.
            Reserva reserva = reservaBL.obtenerPorId(idReserva);
            if (reserva == null) {
                return Response.status(Response.Status.NOT_FOUND)
                               .entity("{\"error\":\"Reserva no encontrada\"}").build();
            }

            // La BL aplica la comisión del 10% y guarda el pago; nos devuelve su id.
            int idPago = pagoBL.registrarPagoDeReserva(reserva);
            Pago creado = pagoBL.obtenerPorId(idPago);

            return Response.status(Response.Status.CREATED)
                           .entity(PagoMapper.toDTO(creado)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    /**
     * GET PagoRS/anfitrion/{idAnfitrion} -> pagos recibidos por un anfitrión.
     * Son los pagos de las reservas de SUS alojamientos (la BL/DAO cruza pago->reserva->alojamiento
     * y filtra por el dueño). Alimenta el panel de ganancias del anfitrión: monto_neto es lo que
     * le queda tras la comisión.
     */
    @GET
    @Path("anfitrion/{idAnfitrion}")
    public List<PagoDTO> listarPorAnfitrion(@PathParam("idAnfitrion") int idAnfitrion) {
        List<PagoDTO> salida = new ArrayList<>();
        try {
            for (Pago p : pagoBL.listarPorAnfitrion(idAnfitrion)) {
                salida.add(PagoMapper.toDTO(p));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return salida;
    }

    /** GET PagoRS -> lista todos los pagos (útil para verificar). */
    @GET
    public List<PagoDTO> listar() {
        List<PagoDTO> salida = new ArrayList<>();
        List<Pago> lista = pagoBL.listarTodos();
        if (lista != null) {
            for (Pago p : lista) {
                salida.add(PagoMapper.toDTO(p));
            }
        }
        return salida;
    }
}
