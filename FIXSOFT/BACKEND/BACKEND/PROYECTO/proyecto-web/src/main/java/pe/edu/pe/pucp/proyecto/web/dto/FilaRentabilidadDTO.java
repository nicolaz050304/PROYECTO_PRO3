package pe.edu.pe.pucp.proyecto.web.dto;

/**
 * Fila del reporte de Rentabilidad (RF20) que alimenta el datasource de JasperReports.
 * Cada fila = un Pago real cruzado con su Reserva (fecha + alojamiento). Los montos son
 * los REALES almacenados en el Pago (bruto / comisión / neto), no estimaciones.
 */
public class FilaRentabilidadDTO {

    private String fecha;       // fecha de la reserva (ancla temporal), "dd/MM/yyyy"
    private String concepto;    // alojamiento + nº de reserva
    private double montoBruto;
    private double comision;
    private double montoNeto;

    public FilaRentabilidadDTO() { }

    public FilaRentabilidadDTO(String fecha, String concepto, double montoBruto, double comision, double montoNeto) {
        this.fecha = fecha;
        this.concepto = concepto;
        this.montoBruto = montoBruto;
        this.comision = comision;
        this.montoNeto = montoNeto;
    }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }

    public double getMontoBruto() { return montoBruto; }
    public void setMontoBruto(double montoBruto) { this.montoBruto = montoBruto; }

    public double getComision() { return comision; }
    public void setComision(double comision) { this.comision = comision; }

    public double getMontoNeto() { return montoNeto; }
    public void setMontoNeto(double montoNeto) { this.montoNeto = montoNeto; }
}
