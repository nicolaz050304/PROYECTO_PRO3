package pe.edu.pe.pucp.proyecto.web.dto;

/**
 * DTO plano de Pago. Se expone esto y NO la entidad Pago, porque Pago->Reserva
 * arrastra ciclos de serialización (igual criterio que ReservaRS/ResenaRS).
 * Aquí solo viaja el id de la reserva, no el objeto completo.
 *
 * moneda y estadoTransaccion viajan como String; idReserva como int.
 */
public class PagoDTO {

    private int idPago;
    private double montoNeto;
    private double montoBruto;
    private double porcenComision;
    private String moneda;
    private String estadoTransaccion;
    private int idReserva;

    public PagoDTO() {
    }

    public int getIdPago() { return idPago; }
    public void setIdPago(int idPago) { this.idPago = idPago; }

    public double getMontoNeto() { return montoNeto; }
    public void setMontoNeto(double montoNeto) { this.montoNeto = montoNeto; }

    public double getMontoBruto() { return montoBruto; }
    public void setMontoBruto(double montoBruto) { this.montoBruto = montoBruto; }

    public double getPorcenComision() { return porcenComision; }
    public void setPorcenComision(double porcenComision) { this.porcenComision = porcenComision; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    public String getEstadoTransaccion() { return estadoTransaccion; }
    public void setEstadoTransaccion(String estadoTransaccion) { this.estadoTransaccion = estadoTransaccion; }

    public int getIdReserva() { return idReserva; }
    public void setIdReserva(int idReserva) { this.idReserva = idReserva; }
}
