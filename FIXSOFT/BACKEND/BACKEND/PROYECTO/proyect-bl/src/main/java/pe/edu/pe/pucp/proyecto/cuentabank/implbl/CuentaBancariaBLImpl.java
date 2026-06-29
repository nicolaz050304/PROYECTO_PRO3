package pe.edu.pe.pucp.proyecto.cuentabank.implbl;

import pe.edu.pe.pucp.proyecto.cuentaBank.CuentaBancaria;
import pe.edu.pe.pucp.proyecto.cuentabank.bl.CuentaBancariaBL;
import pe.edu.pe.pucp.proyecto.cuentabank.dao.CuentaBancariaIDAO;
import pe.edu.pe.pucp.proyecto.cuentabank.impl.CuentaBancariaImpl;

import java.util.List;

public class CuentaBancariaBLImpl implements CuentaBancariaBL {

    private CuentaBancariaIDAO daoCuenta = new CuentaBancariaImpl();

    @Override
    public int insertar(CuentaBancaria cuenta) {
        // --- LÓGICA DE NEGOCIO: VALIDACIONES BANCARIAS ---

        // 1. Validar que el CCI tenga 20 dígitos (Estándar peruano)
        if (cuenta.getCCI() == null || cuenta.getCCI().replaceAll("-", "").length() != 20) {
            throw new RuntimeException("Error: El Código de Cuenta Interbancario (CCI) debe tener 20 dígitos.");
        }

        // 2. Validar que el saldo inicial no sea negativo
        if (cuenta.getSaldo() < 0) {
            throw new RuntimeException("Error: Una cuenta bancaria no puede registrarse con saldo negativo.");
        }

        // 3. Validar que tenga un titular asignado
        if (cuenta.getTitular() == null || cuenta.getTitular().trim().isEmpty()) {
            throw new RuntimeException("Error: La cuenta bancaria debe tener un titular.");
        }

        // RF15: ¿es la PRIMERA cuenta del anfitrión? Si lo es, nacerá como principal (de cobro),
        // para que el abono automático tenga un destino sin que el usuario tenga que elegir.
        boolean esPrimera = daoCuenta.listarPorUsuario(cuenta.getIdUsuario()).isEmpty();

        int id = daoCuenta.save(cuenta).getIdCuenta();

        if (esPrimera) {
            daoCuenta.marcarPrincipal(id, cuenta.getIdUsuario());
        }
        return id;
    }

    @Override
    public void marcarPrincipal(int idCuenta, int idUsuario) {
        if (idCuenta <= 0 || idUsuario <= 0) {
            throw new RuntimeException("Error: cuenta o usuario inválido.");
        }
        daoCuenta.marcarPrincipal(idCuenta, idUsuario);
    }

    @Override
    public List<CuentaBancaria> listarTodos() {
        return daoCuenta.listarTodos();
    }

    @Override
    public List<CuentaBancaria> listarPorUsuario(int idUsuario) {
        return daoCuenta.listarPorUsuario(idUsuario);
    }

    @Override
    public CuentaBancaria obtenerPorId(Integer id) {
        return daoCuenta.load(id);
    }

    @Override
    public int modificar(CuentaBancaria cuenta) {
        // Al modificar, evitamos que el saldo se vuelva negativo por algún error
        if (cuenta.getSaldo() < 0) {
            throw new RuntimeException("Error: El saldo de la cuenta no puede ser negativo.");
        }
        return daoCuenta.update(cuenta).getIdCuenta();
    }

    @Override
    public int eliminar(CuentaBancaria cuenta) {
        // Dependiendo de tu lógica, podrías evitar borrar cuentas que tengan saldo
        if (cuenta.getSaldo() > 0) {
            throw new RuntimeException("Error: No se puede eliminar una cuenta que aún tiene fondos.");
        }
        daoCuenta.remove(cuenta);
        return 1;
    }
}