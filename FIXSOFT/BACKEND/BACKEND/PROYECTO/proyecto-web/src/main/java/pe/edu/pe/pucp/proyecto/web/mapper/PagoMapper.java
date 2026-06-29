package pe.edu.pe.pucp.proyecto.web.mapper;

import pe.edu.pe.pucp.proyecto.economy.Pago;
import pe.edu.pe.pucp.proyecto.web.dto.PagoDTO;

/**
 * Convierte la entidad Pago a su DTO plano (evita exponer Pago->Reserva con ciclos).
 * Tolera nulls en moneda y reserva.
 */
public final class PagoMapper {

    private PagoMapper() {
    }

    public static PagoDTO toDTO(Pago p) {
        if (p == null) {
            return null;
        }
        PagoDTO dto = new PagoDTO();
        dto.setIdPago(p.getIdPago());
        dto.setMontoNeto(p.getMontoNeto());
        dto.setMontoBruto(p.getMontoBruto());
        dto.setPorcenComision(p.getPorcenComision());
        // moneda es un enum: lo pasamos como su nombre (PEN/USD/EUR) o null si no hay.
        dto.setMoneda(p.getMoneda() != null ? p.getMoneda().name() : null);
        dto.setEstadoTransaccion(p.getEstadoTransaccion());
        // Solo el id de la reserva, no el objeto (evita el ciclo).
        dto.setIdReserva(p.getReserva() != null ? p.getReserva().getIdReserva() : 0);
        return dto;
    }
}
