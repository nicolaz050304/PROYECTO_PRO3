package pe.edu.pe.pucp.proyecto.web.mapper;

import pe.edu.pe.pucp.proyecto.economy.TipoCambio;
import pe.edu.pe.pucp.proyecto.web.dto.TipoCambioDTO;

/**
 * Convierte entre la entidad TipoCambio y su DTO plano.
 */
public class TipoCambioMapper {

    public static TipoCambioDTO toDTO(TipoCambio t) {
        if (t == null) return null;
        TipoCambioDTO dto = new TipoCambioDTO();
        dto.setCodigo(t.getCodigo());
        dto.setSimbolo(t.getSimbolo());
        dto.setTasaAPen(t.getTasaAPen());
        return dto;
    }

    // Para el update futuro (fase admin).
    public static TipoCambio toEntity(TipoCambioDTO dto) {
        if (dto == null) return null;
        TipoCambio t = new TipoCambio();
        t.setCodigo(dto.getCodigo());
        t.setSimbolo(dto.getSimbolo());
        t.setTasaAPen(dto.getTasaAPen());
        return t;
    }
}
