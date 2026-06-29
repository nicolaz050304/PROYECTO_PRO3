package pe.edu.pe.pucp.proyecto.cuentabank.bl;

import pe.edu.pe.pucp.proyecto.bl.IBL;
import pe.edu.pe.pucp.proyecto.cuentaBank.CuentaBancaria;

import java.util.List;

public interface CuentaBancariaBL extends IBL<CuentaBancaria, Integer> {
    // Si más adelante necesitas un método como "verificarCuenta", lo pones aquí

    // Cuentas de UN usuario (anfitrión): cada anfitrión gestiona solo SUS cuentas.
    List<CuentaBancaria> listarPorUsuario(int idUsuario);

    // RF15: marca la cuenta de cobro PRINCIPAL del anfitrión (desmarca las demás).
    void marcarPrincipal(int idCuenta, int idUsuario);
}