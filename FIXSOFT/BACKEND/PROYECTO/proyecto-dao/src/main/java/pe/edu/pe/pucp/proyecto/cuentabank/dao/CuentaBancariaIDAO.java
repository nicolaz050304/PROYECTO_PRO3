package pe.edu.pe.pucp.proyecto.cuentabank.dao;

import pe.edu.pe.pucp.proyecto.cuentaBank.CuentaBancaria;
import pe.edu.pe.pucp.proyecto.dao.IDAO;

import java.util.List;

public interface CuentaBancariaIDAO extends IDAO <CuentaBancaria,Integer> {
    List<CuentaBancaria>listarPorVerificacion();
    List<CuentaBancaria>listarTodos();
}

