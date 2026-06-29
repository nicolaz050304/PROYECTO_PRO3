package pe.edu.pe.pucp.proyecto.web.mapper;

import pe.edu.pe.pucp.proyecto.cuentaBank.CuentaBancaria;
import pe.edu.pe.pucp.proyecto.cuentaBank.CuentaBancaria.TipoCuenta;
import pe.edu.pe.pucp.proyecto.economy.TipoMoneda;
import pe.edu.pe.pucp.proyecto.web.dto.CuentaBancariaDTO;

/**
 * Conversión CuentaBancaria (entidad) <-> CuentaBancariaDTO (contrato plano).
 * Los enums viajan como String; al volver a entidad se parsean con valueOf (tolerando
 * mayúsculas/minúsculas). Una cuenta nueva nace SIN verificar (verificada=false) hasta
 * que un admin/proceso la valide.
 */
public final class CuentaBancariaMapper {

    private CuentaBancariaMapper() {
    }

    public static CuentaBancariaDTO toDTO(CuentaBancaria c) {
        if (c == null) {
            return null;
        }
        CuentaBancariaDTO dto = new CuentaBancariaDTO();
        dto.setIdCuenta(c.getIdCuenta());
        dto.setIdUsuario(c.getIdUsuario());
        dto.setNumeroCuenta(c.getNumeroCuenta());
        dto.setTipoMoneda(c.getTipoMoneda() != null ? c.getTipoMoneda().name() : null);
        dto.setCci(c.getCCI());
        dto.setNroBanco(c.getNroBanco());
        dto.setTipoCuenta(c.getTipoCuenta() != null ? c.getTipoCuenta().name() : null);
        dto.setTitular(c.getTitular());
        dto.setSaldo(c.getSaldo());
        dto.setVerificada(c.isVerificada());
        dto.setPrincipal(c.isPrincipal());
        return dto;
    }

    public static CuentaBancaria toEntity(CuentaBancariaDTO dto) {
        if (dto == null) {
            return null;
        }
        CuentaBancaria c = new CuentaBancaria();
        c.setIdUsuario(dto.getIdUsuario());
        c.setNumeroCuenta(dto.getNumeroCuenta());
        c.setCCI(dto.getCci());
        c.setNroBanco(dto.getNroBanco());
        c.setTitular(dto.getTitular());
        c.setSaldo(dto.getSaldo()); // 0 si no vino (default de double)
        // La cuenta nace NO verificada; no confiamos en el flag que mande el cliente.
        c.setVerificada(false);
        c.setTipoMoneda(parsearMoneda(dto.getTipoMoneda()));
        c.setTipoCuenta(parsearTipoCuenta(dto.getTipoCuenta()));
        return c;
    }

    /** Parsea el enum TipoMoneda (PEN/USD/EUR) desde String; inválido/nulo -> error claro. */
    private static TipoMoneda parsearMoneda(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("tipoMoneda es obligatorio (PEN, USD o EUR).");
        }
        try {
            return TipoMoneda.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("tipoMoneda inválido: '" + valor + "'. Use PEN, USD o EUR.");
        }
    }

    /** Parsea el enum TipoCuenta (AHORROS/CORRIENTE/SUELDO/INTERBANCARIA); inválido/nulo -> error claro. */
    private static TipoCuenta parsearTipoCuenta(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("tipoCuenta es obligatorio (AHORROS, CORRIENTE, SUELDO o INTERBANCARIA).");
        }
        try {
            return TipoCuenta.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("tipoCuenta inválido: '" + valor
                    + "'. Use AHORROS, CORRIENTE, SUELDO o INTERBANCARIA.");
        }
    }
}
