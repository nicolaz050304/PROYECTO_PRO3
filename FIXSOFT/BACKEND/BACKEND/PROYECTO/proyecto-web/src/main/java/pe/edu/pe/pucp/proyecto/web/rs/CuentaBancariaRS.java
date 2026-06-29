package pe.edu.pe.pucp.proyecto.web.rs;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import pe.edu.pe.pucp.proyecto.cuentaBank.CuentaBancaria;
import pe.edu.pe.pucp.proyecto.cuentabank.bl.CuentaBancariaBL;
import pe.edu.pe.pucp.proyecto.cuentabank.bl.MovimientoCuentaBL;
import pe.edu.pe.pucp.proyecto.cuentabank.implbl.CuentaBancariaBLImpl;
import pe.edu.pe.pucp.proyecto.cuentabank.implbl.MovimientoCuentaBLImpl;
import pe.edu.pe.pucp.proyecto.economy.MovimientoCuenta;
import pe.edu.pe.pucp.proyecto.web.dto.CuentaBancariaDTO;
import pe.edu.pe.pucp.proyecto.web.dto.MovimientoCuentaDTO;
import pe.edu.pe.pucp.proyecto.web.mapper.CuentaBancariaMapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Recurso REST de Cuentas Bancarias del anfitrión (RF15). Expone DTOs planos y delega
 * en la BL existente (CuentaBancariaBL). Una cuenta nueva nace NO verificada hasta que
 * un admin/proceso la valide.
 *
 * Base de despliegue:
 *   http://localhost:8080/BunkiBackend/webresources/CuentaBancariaRS
 */
@Path("CuentaBancariaRS")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CuentaBancariaRS {

    private final CuentaBancariaBL cuentaBL = new CuentaBancariaBLImpl();
    private final MovimientoCuentaBL movimientoBL = new MovimientoCuentaBLImpl();

    /**
     * GET CuentaBancariaRS/usuario/{idUsuario} -> cuentas de ese anfitrión.
     * Se filtra por id_usuario porque cada anfitrión solo ve/gestiona SUS cuentas.
     */
    @GET
    @Path("usuario/{idUsuario}")
    public List<CuentaBancariaDTO> listarPorUsuario(@PathParam("idUsuario") int idUsuario) {
        List<CuentaBancariaDTO> salida = new ArrayList<>();
        List<CuentaBancaria> lista = cuentaBL.listarPorUsuario(idUsuario);
        if (lista != null) {
            for (CuentaBancaria c : lista) {
                salida.add(CuentaBancariaMapper.toDTO(c));
            }
        }
        return salida;
    }

    /** POST CuentaBancariaRS -> registra una cuenta; 201 con el DTO creado, 400 ante error de negocio. */
    @POST
    public Response crear(CuentaBancariaDTO dto) {
        try {
            CuentaBancaria c = CuentaBancariaMapper.toEntity(dto);
            int id = cuentaBL.insertar(c);   // la BL valida CCI(20), saldo>=0, titular
            c.setIdCuenta(id);
            return Response.status(Response.Status.CREATED)
                           .entity(CuentaBancariaMapper.toDTO(c)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    /**
     * POST CuentaBancariaRS/{id}/deposito -> acredita saldo (RF15). Body { monto, descripcion }.
     * Devuelve la cuenta con su nuevo saldo, o 400 si el monto es inválido.
     */
    @POST
    @Path("{id}/deposito")
    public Response depositar(@PathParam("id") int id, OperacionRequest req) {
        return operar(id, req, true);
    }

    /**
     * POST CuentaBancariaRS/{id}/retiro -> debita saldo (RF15). Body { monto, descripcion }.
     * Devuelve la cuenta con su nuevo saldo, o 400 si el monto es inválido o el saldo es insuficiente.
     */
    @POST
    @Path("{id}/retiro")
    public Response retirar(@PathParam("id") int id, OperacionRequest req) {
        return operar(id, req, false);
    }

    /**
     * PUT CuentaBancariaRS/{id}/principal -> marca esta cuenta como la de COBRO principal del
     * anfitrión (RF15) y desmarca las demás. El abono automático de reservas irá a esta cuenta.
     */
    @PUT
    @Path("{id}/principal")
    public Response marcarPrincipal(@PathParam("id") int id) {
        try {
            CuentaBancaria c = cuentaBL.obtenerPorId(id);
            if (c == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Cuenta no encontrada\"}").build();
            }
            cuentaBL.marcarPrincipal(id, c.getIdUsuario());
            return Response.ok(CuentaBancariaMapper.toDTO(cuentaBL.obtenerPorId(id))).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    /** GET CuentaBancariaRS/{id}/movimientos -> estado de cuenta (historial) de la cuenta. */
    @GET
    @Path("{id}/movimientos")
    public List<MovimientoCuentaDTO> listarMovimientos(@PathParam("id") int id) {
        List<MovimientoCuentaDTO> salida = new ArrayList<>();
        for (MovimientoCuenta m : movimientoBL.listarPorCuenta(id)) {
            salida.add(toDTO(m));
        }
        return salida;
    }

    // ------------------------------------------------------------------

    private Response operar(int id, OperacionRequest req, boolean esDeposito) {
        if (req == null || req.getMonto() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El monto debe ser mayor a 0.\"}").build();
        }
        try {
            if (esDeposito) {
                movimientoBL.depositar(id, req.getMonto(), req.getDescripcion());
            } else {
                movimientoBL.retirar(id, req.getMonto(), req.getDescripcion());
            }
            // Devolvemos la cuenta con el saldo ya actualizado, para que el frontend lo refleje.
            CuentaBancaria actualizada = cuentaBL.obtenerPorId(id);
            return Response.ok(CuentaBancariaMapper.toDTO(actualizada)).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    private MovimientoCuentaDTO toDTO(MovimientoCuenta m) {
        MovimientoCuentaDTO dto = new MovimientoCuentaDTO();
        dto.setId(m.getIdMovimiento());
        dto.setIdCuenta(m.getIdCuenta());
        dto.setTipo(m.getTipo());
        dto.setMonto(m.getMonto());
        dto.setDescripcion(m.getDescripcion());
        dto.setSaldoResultante(m.getSaldoResultante());
        dto.setFecha(formatearFecha(m.getFecha()));
        return dto;
    }

    private static String formatearFecha(Date fecha) {
        if (fecha == null) return null;
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(fecha);
    }

    /** Body de POST {id}/deposito y {id}/retiro: { "monto": 100.0, "descripcion": "..." }. */
    public static class OperacionRequest {
        private double monto;
        private String descripcion;
        public double getMonto() { return monto; }
        public void setMonto(double monto) { this.monto = monto; }
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    }

    /** DELETE CuentaBancariaRS/{id} -> elimina una cuenta (la BL impide borrar con fondos). */
    @DELETE
    @Path("{id}")
    public Response eliminar(@PathParam("id") int id) {
        try {
            CuentaBancaria c = cuentaBL.obtenerPorId(id);
            if (c == null) {
                return Response.status(Response.Status.NOT_FOUND)
                               .entity("{\"error\":\"Cuenta no encontrada\"}").build();
            }
            cuentaBL.eliminar(c);
            return Response.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
