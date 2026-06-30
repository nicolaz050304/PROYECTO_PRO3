package pe.edu.pe.pucp.proyecto.web.mapper;

import pe.edu.pe.pucp.proyecto.auditoria.AuditoriaEstado;
import pe.edu.pe.pucp.proyecto.web.dto.AuditoriaEstadoDTO;

import java.text.SimpleDateFormat;
import java.util.Date;

/** Mapper de AuditoriaEstado (entidad) a su DTO plano. Formatea la fecha a ISO con hora. */
public final class AuditoriaEstadoMapper {

    private AuditoriaEstadoMapper() {
    }

    public static AuditoriaEstadoDTO toDTO(AuditoriaEstado a) {
        if (a == null) return null;
        AuditoriaEstadoDTO dto = new AuditoriaEstadoDTO();
        dto.setId(a.getIdAuditoria());
        dto.setEntidad(a.getEntidad());
        dto.setIdEntidad(a.getIdEntidad());
        dto.setCampo(a.getCampo());
        dto.setEstadoAnterior(a.getEstadoAnterior());
        dto.setEstadoNuevo(a.getEstadoNuevo());
        dto.setDetalle(a.getDetalle());
        dto.setFecha(formatear(a.getFecha()));
        return dto;
    }

    private static String formatear(Date fecha) {
        if (fecha == null) return null;
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fecha);
    }
}
