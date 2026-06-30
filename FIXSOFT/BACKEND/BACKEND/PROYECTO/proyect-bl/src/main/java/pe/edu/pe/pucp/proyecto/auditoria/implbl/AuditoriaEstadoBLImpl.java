package pe.edu.pe.pucp.proyecto.auditoria.implbl;

import pe.edu.pe.pucp.proyecto.auditoria.AuditoriaEstado;
import pe.edu.pe.pucp.proyecto.auditoria.bl.AuditoriaEstadoBL;
import pe.edu.pe.pucp.proyecto.auditoria.dao.AuditoriaEstadoDAO;
import pe.edu.pe.pucp.proyecto.auditoria.impl.AuditoriaEstadoImpl;

import java.util.List;

public class AuditoriaEstadoBLImpl implements AuditoriaEstadoBL {

    private final AuditoriaEstadoDAO dao = new AuditoriaEstadoImpl();

    @Override
    public void registrar(String entidad, int idEntidad, String campo,
                          String estadoAnterior, String estadoNuevo, String detalle) {
        // La auditoría nunca debe romper la operación que la origina: validamos lo mínimo y
        // capturamos cualquier error (BD caída, etc.) dejándolo solo en el log.
        if (entidad == null || campo == null || estadoNuevo == null || idEntidad <= 0) {
            return;
        }
        try {
            AuditoriaEstado r = new AuditoriaEstado();
            r.setEntidad(entidad);
            r.setIdEntidad(idEntidad);
            r.setCampo(campo);
            r.setEstadoAnterior(estadoAnterior);
            r.setEstadoNuevo(estadoNuevo);
            r.setDetalle(detalle);
            dao.agregar(r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<AuditoriaEstado> listarPorEntidad(String entidad, int idEntidad) {
        return dao.listarPorEntidad(entidad, idEntidad);
    }

    @Override
    public List<AuditoriaEstado> listarRecientes(int limite) {
        return dao.listarRecientes(limite);
    }
}
