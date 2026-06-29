package pe.edu.pe.pucp.proyecto.cuentabank.implbl;

import pe.edu.pe.pucp.proyecto.cuentaBank.CuentaBancaria;
import pe.edu.pe.pucp.proyecto.cuentabank.bl.CuentaBancariaBL;
import pe.edu.pe.pucp.proyecto.cuentabank.bl.MovimientoCuentaBL;
import pe.edu.pe.pucp.proyecto.cuentabank.dao.CuentaBancariaIDAO;
import pe.edu.pe.pucp.proyecto.cuentabank.dao.MovimientoCuentaDAO;
import pe.edu.pe.pucp.proyecto.cuentabank.impl.CuentaBancariaImpl;
import pe.edu.pe.pucp.proyecto.cuentabank.impl.MovimientoCuentaImpl;
import pe.edu.pe.pucp.proyecto.economy.MovimientoCuenta;

import java.util.Date;
import java.util.List;

public class MovimientoCuentaBLImpl implements MovimientoCuentaBL {

    private final MovimientoCuentaDAO dao = new MovimientoCuentaImpl();
    private final CuentaBancariaBL cuentaBL = new CuentaBancariaBLImpl();
    // DAO directo: el reembolso (clawback) puede dejar el saldo NEGATIVO (deuda del anfitrión),
    // cosa que la BL.modificar prohíbe. Por eso ese caso persiste vía DAO, saltándose el guard.
    private final CuentaBancariaIDAO cuentaDao = new CuentaBancariaImpl();

    @Override
    public MovimientoCuenta depositar(int idCuenta, double monto, String descripcion) {
        CuentaBancaria cuenta = cargarValidando(idCuenta, monto);
        // recibirDeposito suma al saldo del modelo; luego persistimos la cuenta.
        cuenta.recibirDeposito(monto);
        cuentaBL.modificar(cuenta);
        return registrar(idCuenta, MovimientoCuenta.DEPOSITO, monto, descripcion, cuenta.getSaldo(), 0);
    }

    @Override
    public MovimientoCuenta retirar(int idCuenta, double monto, String descripcion) {
        CuentaBancaria cuenta = cargarValidando(idCuenta, monto);
        // retirarDinero devuelve false si el saldo es insuficiente (no toca el saldo en ese caso).
        if (!cuenta.retirarDinero(monto)) {
            throw new RuntimeException("Saldo insuficiente para retirar S/ " + monto + ".");
        }
        cuentaBL.modificar(cuenta);
        return registrar(idCuenta, MovimientoCuenta.RETIRO, monto, descripcion, cuenta.getSaldo(), 0);
    }

    @Override
    public MovimientoCuenta abonar(int idCuenta, double monto, String descripcion, int idReserva) {
        // Idempotencia: si esa reserva ya tiene un ABONO, no volvemos a acreditar (evita el doble
        // abono en CONFIRMADA→PENDIENTE→CONFIRMADA). Devolvemos el abono existente.
        if (idReserva > 0) {
            MovimientoCuenta existente = dao.buscarPorReservaYTipo(idReserva, MovimientoCuenta.ABONO);
            if (existente != null) {
                return existente;
            }
        }
        // Igual que un depósito (suma al saldo), pero tipificado como ABONO de reserva para el historial.
        CuentaBancaria cuenta = cargarValidando(idCuenta, monto);
        cuenta.recibirDeposito(monto);
        cuentaBL.modificar(cuenta);
        return registrar(idCuenta, MovimientoCuenta.ABONO, monto, descripcion, cuenta.getSaldo(), idReserva);
    }

    @Override
    public MovimientoCuenta reembolsarPorReserva(int idReserva) {
        if (idReserva <= 0) {
            return null;
        }
        // ¿Hubo abono por esta reserva? Si no, no hay nada que reembolsar.
        MovimientoCuenta abono = dao.buscarPorReservaYTipo(idReserva, MovimientoCuenta.ABONO);
        if (abono == null) {
            return null;
        }
        // Idempotencia: si ya se reembolsó esta reserva, no lo repetimos.
        MovimientoCuenta yaReembolsado = dao.buscarPorReservaYTipo(idReserva, MovimientoCuenta.REEMBOLSO);
        if (yaReembolsado != null) {
            return yaReembolsado;
        }
        // Debitamos el MISMO monto, de la MISMA cuenta que recibió el abono. Puede dejar el saldo
        // negativo (deuda) si el anfitrión ya había retirado: por eso persistimos vía DAO directo.
        CuentaBancaria cuenta = cuentaBL.obtenerPorId(abono.getIdCuenta());
        if (cuenta == null) {
            return null;
        }
        double monto = abono.getMonto();
        cuenta.setSaldo(cuenta.getSaldo() - monto);
        cuentaDao.update(cuenta);
        String desc = "Reembolso por cancelación de reserva #" + idReserva;
        return registrar(abono.getIdCuenta(), MovimientoCuenta.REEMBOLSO, monto, desc, cuenta.getSaldo(), idReserva);
    }

    @Override
    public List<MovimientoCuenta> listarPorCuenta(int idCuenta) {
        if (idCuenta <= 0) {
            throw new RuntimeException("Error: id de cuenta inválido.");
        }
        return dao.listarPorCuenta(idCuenta);
    }

    // ------------------------------------------------------------------

    private CuentaBancaria cargarValidando(int idCuenta, double monto) {
        if (idCuenta <= 0) {
            throw new RuntimeException("Error: id de cuenta inválido.");
        }
        if (monto <= 0) {
            throw new RuntimeException("Error: el monto debe ser mayor a 0.");
        }
        CuentaBancaria cuenta = cuentaBL.obtenerPorId(idCuenta);
        if (cuenta == null) {
            throw new RuntimeException("Error: la cuenta no existe.");
        }
        return cuenta;
    }

    private MovimientoCuenta registrar(int idCuenta, String tipo, double monto,
                                       String descripcion, double saldoResultante, int idReserva) {
        MovimientoCuenta m = new MovimientoCuenta(0, idCuenta, tipo, monto, descripcion, saldoResultante, new Date());
        m.setIdReserva(idReserva);
        int id = dao.agregar(m);
        m.setIdMovimiento(id);
        return m;
    }
}
