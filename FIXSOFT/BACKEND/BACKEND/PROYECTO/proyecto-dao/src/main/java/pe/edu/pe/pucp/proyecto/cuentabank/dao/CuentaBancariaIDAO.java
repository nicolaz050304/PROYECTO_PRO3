package pe.edu.pe.pucp.proyecto.cuentabank.dao;

import pe.edu.pe.pucp.proyecto.cuentaBank.CuentaBancaria;
import pe.edu.pe.pucp.proyecto.dao.IDAO;

import java.util.List;

public interface CuentaBancariaIDAO extends IDAO <CuentaBancaria,Integer> {
    List<CuentaBancaria>listarPorVerificacion();
    List<CuentaBancaria>listarTodos();
    // Cuentas de UN usuario (anfitrión): cada anfitrión gestiona solo SUS cuentas.
    List<CuentaBancaria> listarPorUsuario(int idUsuario);

    // RF15: marca una cuenta como PRINCIPAL (de cobro) y desmarca el resto del mismo usuario.
    void marcarPrincipal(int idCuenta, int idUsuario);
}

