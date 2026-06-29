package pe.edu.pe.pucp.proyecto.cuentabank.dao;

import pe.edu.pe.pucp.proyecto.economy.MovimientoCuenta;

import java.util.List;

/** DAO del estado de cuenta (movimientos) de una cuenta bancaria (RF15). */
public interface MovimientoCuentaDAO {

    /** Inserta un movimiento y devuelve su id generado (0 si no se pudo). */
    int agregar(MovimientoCuenta movimiento);

    /** Movimientos de una cuenta, del más reciente al más antiguo. */
    List<MovimientoCuenta> listarPorCuenta(int idCuenta);

    /**
     * Primer movimiento de un tipo (ABONO/REEMBOLSO) para una reserva, o null si no existe.
     * Sirve para la idempotencia: no abonar dos veces ni reembolsar dos veces la misma reserva.
     */
    MovimientoCuenta buscarPorReservaYTipo(int idReserva, String tipo);
}
