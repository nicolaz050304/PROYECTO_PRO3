package pe.edu.pe.pucp.proyecto.economy;

import pe.edu.pe.pucp.proyecto.reservation.Reserva;
import java.util.Date;

public class Pago {
    private int idPago;
    private double montoNeto;
    private double montoBruto;
    private double porcenComision;
    private TipoMoneda moneda;
    private Reserva reserva;

    // ============================================================
    // NUEVOS CAMPOS:
    // ============================================================
    private double tipoCambio;           // "Almacenar historial monetario"
    private String estadoTransaccion;    // "Manejo de envíos de dinero"
    private Date fechaEnvioAnfitrion;    // Trazabilidad del envío al dueño
    private int idCuentaDestino;         // FK a la cuenta del anfitrión

    public Pago() {
        // Inicializamos valores por defecto coherentes
        this.porcenComision = 0.10;
        this.tipoCambio = 1.0;
        this.estadoTransaccion = "COBRADO_AL_INVITADO";
    }

    public Pago(int idPago, double montoBruto, double montoNeto, TipoMoneda moneda, Reserva reserva) {
        this.idPago = idPago;
        this.montoNeto = montoNeto;
        this.montoBruto = montoBruto;
        this.moneda = moneda;
        this.reserva = reserva;
        this.porcenComision = 0.10;
        // Valores por defecto para nuevos campos
        this.tipoCambio = 1.0;
        this.estadoTransaccion = "COBRADO_AL_INVITADO";
    }

    // --- GETTERS Y SETTERS NUEVOS ---
    public double getTipoCambio() { return tipoCambio; }
    public void setTipoCambio(double tipoCambio) { this.tipoCambio = tipoCambio; }

    public String getEstadoTransaccion() { return estadoTransaccion; }
    public void setEstadoTransaccion(String estadoTransaccion) { this.estadoTransaccion = estadoTransaccion; }

    public Date getFechaEnvioAnfitrion() { return fechaEnvioAnfitrion; }
    public void setFechaEnvioAnfitrion(Date fechaEnvioAnfitrion) { this.fechaEnvioAnfitrion = fechaEnvioAnfitrion; }

    public int getIdCuentaDestino() { return idCuentaDestino; }
    public void setIdCuentaDestino(int idCuentaDestino) { this.idCuentaDestino = idCuentaDestino; }

    // --- TUS GETTERS Y SETTERS ORIGINALES ---
    public int getIdPago() { return idPago; }
    public void setIdPago(int idPago) { this.idPago = idPago; }

    public double getMontoNeto() { return montoNeto; }
    public void setMontoNeto(double montoNeto) { this.montoNeto = montoNeto; }

    public double getMontoBruto() { return montoBruto; }
    public void setMontoBruto(double montoBruto) { this.montoBruto = montoBruto; }

    public double getPorcenComision() { return porcenComision; }
    public void setPorcenComision(double porcenComision) { this.porcenComision = porcenComision; }

    public TipoMoneda getMoneda() { return moneda; }
    public void setMoneda(TipoMoneda moneda) { this.moneda = moneda; }

    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }

    // ============================================================
    // MÉTODOS DE NEGOCIO ENRIQUECIDOS
    // ============================================================

    public boolean procesarPago() {
        if (this.montoBruto > 0) {
            System.out.println("Pago recibido del huésped: " + this.montoBruto + " " + this.moneda);
            System.out.println("Tipo de cambio registrado: " + this.tipoCambio);
            return true;
        }
        return false;
    }

    // Nuevo método para demostrar el flujo hacia el anfitrión
    public void registrarEnvioAnfitrion(int idCuentaAnfitrion) {
        this.idCuentaDestino = idCuentaAnfitrion;
        this.fechaEnvioAnfitrion = new Date();
        this.estadoTransaccion = "ENVIADO_AL_ANFITRION";
        System.out.println("Auditando envío: Monto neto " + montoNeto + " transferido a cuenta ID: " + idCuentaAnfitrion);
    }

    public void calcularMontoBruto() {
        double montoComision = montoNeto * porcenComision;
        this.montoBruto = montoNeto + montoComision;
    }

    public String emitirComprobante() {
        return "COMPROBANTE DE PAGO #" + idPago +
                "\nEstado: " + estadoTransaccion +
                "\nMonto Neto (Anfitrión): " + montoNeto +
                "\nTotal Pagado (Huésped): " + montoBruto + " " + moneda +
                "\nTipo de Cambio: " + tipoCambio;
    }
}