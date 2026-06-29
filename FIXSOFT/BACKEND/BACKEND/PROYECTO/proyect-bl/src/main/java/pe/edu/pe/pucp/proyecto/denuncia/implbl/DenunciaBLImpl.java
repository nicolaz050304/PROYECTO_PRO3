package pe.edu.pe.pucp.proyecto.denuncia.implbl;

import pe.edu.pe.pucp.proyecto.denuncia.Denuncia;
import pe.edu.pe.pucp.proyecto.denuncia.bl.DenunciaBL;
import pe.edu.pe.pucp.proyecto.denuncia.dao.DenunciaDAO;
import pe.edu.pe.pucp.proyecto.denuncia.impl.DenunciaImpl;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class DenunciaBLImpl implements DenunciaBL {

    private final DenunciaDAO dao = new DenunciaImpl();

    private static final Set<String> ESTADOS_VALIDOS =
            Set.of(Denuncia.EN_REVISION, Denuncia.RESUELTO, Denuncia.CERRADO);

    @Override
    public List<Denuncia> listarTodas() {
        return dao.listarTodas();
    }

    @Override
    public int agregar(Denuncia d) {
        // --- VALIDACIONES DE NEGOCIO ---
        if (d == null) {
            throw new RuntimeException("Error: la denuncia no puede ser nula.");
        }
        if (d.getIdDenunciante() <= 0) {
            throw new RuntimeException("Error: el denunciante es obligatorio.");
        }
        if (d.getIdAlojamiento() <= 0) {
            throw new RuntimeException("Error: el alojamiento denunciado es obligatorio.");
        }
        if (d.getMotivo() == null || d.getMotivo().trim().isEmpty()) {
            throw new RuntimeException("Error: el motivo es obligatorio.");
        }
        // Toda denuncia nace EN_REVISION y con fecha de hoy (no se confía en el cliente).
        d.setEstado(Denuncia.EN_REVISION);
        if (d.getFecha() == null) {
            d.setFecha(new Date());
        }
        return dao.agregar(d);
    }

    @Override
    public void cambiarEstado(int idDenuncia, String estado) {
        if (idDenuncia <= 0) {
            throw new RuntimeException("Error: id de denuncia inválido.");
        }
        if (estado == null || !ESTADOS_VALIDOS.contains(estado)) {
            throw new RuntimeException("Error: estado inválido. Use EN_REVISION, RESUELTO o CERRADO.");
        }
        dao.actualizarEstado(idDenuncia, estado);
    }
}
