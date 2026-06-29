package pe.edu.pe.pucp.proyecto.economy;

import java.util.Date;

/**
 * Movimiento del estado de cuenta de una cuenta bancaria del anfitrión (RF15).
 * Es un asiento del libro mayor: cada depósito/retiro/abono deja una fila con el
 * saldo resultante, para reconstruir el historial sin recalcular.
 *
 * Tipo: "DEPOSITO" (ingreso manual) | "RETIRO" (egreso a su banco) | "ABONO" (ganancia de reserva)
 *      | "REEMBOLSO" (reversa del abono cuando esa reserva se cancela).
 */
public class MovimientoCuenta {

    public static final String DEPOSITO = "DEPOSITO";
    public static final String RETIRO = "RETIRO";
    public static final String ABONO = "ABONO";
    public static final String REEMBOLSO = "REEMBOLSO";

    private int idMovimiento;
    private int idCuenta;
    private String tipo;
    private double monto;
    private String descripcion;
    private double saldoResultante;
    private Date fecha;
    // Reserva que originó el movimiento (ABONO/REEMBOLSO). 0 para depósitos/retiros manuales.
    private int idReserva;

    public MovimientoCuenta() {
    }

    public MovimientoCuenta(int idMovimiento, int idCuenta, String tipo, double monto,
                            String descripcion, double saldoResultante, Date fecha) {
        this.idMovimiento = idMovimiento;
        this.idCuenta = idCuenta;
        this.tipo = tipo;
        this.monto = monto;
        this.descripcion = descripcion;
        this.saldoResultante = saldoResultante;
        this.fecha = fecha;
    }

    public int getIdMovimiento() { return idMovimiento; }
    public void setIdMovimiento(int idMovimiento) { this.idMovimiento = idMovimiento; }

    public int getIdCuenta() { return idCuenta; }
    public void setIdCuenta(int idCuenta) { this.idCuenta = idCuenta; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getSaldoResultante() { return saldoResultante; }
    public void setSaldoResultante(double saldoResultante) { this.saldoResultante = saldoResultante; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public int getIdReserva() { return idReserva; }
    public void setIdReserva(int idReserva) { this.idReserva = idReserva; }
}
