package pe.edu.pe.pucp.proyecto.cuentabank.bl;

import pe.edu.pe.pucp.proyecto.economy.MovimientoCuenta;

import java.util.List;

/** Lógica de negocio del estado de cuenta: depósitos, retiros e historial (RF15). */
public interface MovimientoCuentaBL {

    /** Acredita {@code monto} en la cuenta, persiste el nuevo saldo y registra el movimiento. */
    MovimientoCuenta depositar(int idCuenta, double monto, String descripcion);

    /** Debita {@code monto} (falla si no hay saldo suficiente), persiste y registra el movimiento. */
    MovimientoCuenta retirar(int idCuenta, double monto, String descripcion);

    /**
     * Acredita la ganancia de una reserva (tipo ABONO): suma al saldo y registra el movimiento.
     * IDEMPOTENTE por reserva: si ya existe un ABONO para esa reserva, no vuelve a abonar.
     */
    MovimientoCuenta abonar(int idCuenta, double monto, String descripcion, int idReserva);

    /**
     * Reversa el abono de una reserva cancelada (tipo REEMBOLSO): debita del saldo de la cuenta
     * que recibió el abono el mismo monto. Idempotente y no-op si la reserva nunca tuvo abono.
     * Devuelve el movimiento de reembolso, o null si no había nada que reembolsar.
     */
    MovimientoCuenta reembolsarPorReserva(int idReserva);

    /** Movimientos de una cuenta, del más reciente al más antiguo. */
    List<MovimientoCuenta> listarPorCuenta(int idCuenta);
}
