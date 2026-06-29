package pe.edu.pe.pucp.proyecto.web.dto;

/**
 * Fila del reporte de Ocupación / Demanda (RF21) para el datasource de JasperReports.
 * Una fila por alojamiento, con sus reservas (no canceladas), noches y ingresos acumulados.
 */
public class FilaOcupacionDTO {

    private String alojamiento;
    private String anfitrion;
    private int reservas;
    private int noches;
    private double ingresos;

    public FilaOcupacionDTO() { }

    public FilaOcupacionDTO(String alojamiento, String anfitrion, int reservas, int noches, double ingresos) {
        this.alojamiento = alojamiento;
        this.anfitrion = anfitrion;
        this.reservas = reservas;
        this.noches = noches;
        this.ingresos = ingresos;
    }

    public String getAlojamiento() { return alojamiento; }
    public void setAlojamiento(String alojamiento) { this.alojamiento = alojamiento; }

    public String getAnfitrion() { return anfitrion; }
    public void setAnfitrion(String anfitrion) { this.anfitrion = anfitrion; }

    public int getReservas() { return reservas; }
    public void setReservas(int reservas) { this.reservas = reservas; }

    public int getNoches() { return noches; }
    public void setNoches(int noches) { this.noches = noches; }

    public double getIngresos() { return ingresos; }
    public void setIngresos(double ingresos) { this.ingresos = ingresos; }
}
