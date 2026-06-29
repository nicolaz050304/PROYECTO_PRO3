package pe.edu.pe.pucp.proyecto.web.rs;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import pe.edu.pe.pucp.proyecto.economy.TipoCambio;
import pe.edu.pe.pucp.proyecto.economy.bl.TipoCambioBL;
import pe.edu.pe.pucp.proyecto.economy.implbl.TipoCambioBLImpl;
import pe.edu.pe.pucp.proyecto.web.dto.TipoCambioDTO;
import pe.edu.pe.pucp.proyecto.web.mapper.TipoCambioMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Recurso REST de Tipo de Cambio. Expone las tasas de la tabla tipo_cambio
 * (fuente de verdad) para que el frontend las consuma. Delega en TipoCambioBL.
 *
 * Base de despliegue:
 *   http://localhost:8080/BunkiBackend/webresources/TipoCambioRS
 */
@Path("TipoCambioRS")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TipoCambioRS {

    private final TipoCambioBL tipoCambioBL = new TipoCambioBLImpl();

    /** GET TipoCambioRS -> todas las tasas. */
    @GET
    public List<TipoCambioDTO> listar() {
        List<TipoCambioDTO> salida = new ArrayList<>();
        List<TipoCambio> lista = tipoCambioBL.listarTodas();
        if (lista != null) {
            for (TipoCambio t : lista) {
                salida.add(TipoCambioMapper.toDTO(t));
            }
        }
        return salida;
    }

    /** PUT TipoCambioRS/{codigo} -> actualiza simbolo y tasa (fase admin). */
    @PUT
    @Path("{codigo}")
    public Response actualizar(@PathParam("codigo") String codigo, TipoCambioDTO dto) {
        TipoCambio entidad = TipoCambioMapper.toEntity(dto);
        entidad.setCodigo(codigo);
        tipoCambioBL.actualizar(entidad);
        return Response.ok(TipoCambioMapper.toDTO(entidad)).build();
    }
}
