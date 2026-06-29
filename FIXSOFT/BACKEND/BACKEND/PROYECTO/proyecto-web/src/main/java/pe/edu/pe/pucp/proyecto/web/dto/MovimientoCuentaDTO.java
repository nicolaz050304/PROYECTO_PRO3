package pe.edu.pe.pucp.proyecto.web.dto;

/**
 * Contrato plano de un movimiento del estado de cuenta (RF15).
 * - Operación (POST deposito/retiro): el cliente envía monto y descripcion.
 * - Lectura (GET movimientos): viaja completo. Fecha como String ISO "yyyy-MM-dd HH:mm".
 */
public class MovimientoCuentaDTO {

    private int id;
    private int idCuenta;
    private String tipo;
    private double monto;
    private String descripcion;
    private double saldoResultante;
    private String fecha;

    public MovimientoCuentaDTO() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

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

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}
