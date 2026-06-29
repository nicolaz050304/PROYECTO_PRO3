package pe.edu.pe.pucp.proyecto.web.dto;

/** Fila del reporte de Satisfacción / Reseñas (RF24) para JasperReports. */
public class FilaSatisfaccionDTO {

    private String alojamiento;
    private String anfitrion;
    private double promedio;
    private int resenas;

    public FilaSatisfaccionDTO() { }

    public FilaSatisfaccionDTO(String alojamiento, String anfitrion, double promedio, int resenas) {
        this.alojamiento = alojamiento;
        this.anfitrion = anfitrion;
        this.promedio = promedio;
        this.resenas = resenas;
    }

    public String getAlojamiento() { return alojamiento; }
    public void setAlojamiento(String alojamiento) { this.alojamiento = alojamiento; }

    public String getAnfitrion() { return anfitrion; }
    public void setAnfitrion(String anfitrion) { this.anfitrion = anfitrion; }

    public double getPromedio() { return promedio; }
    public void setPromedio(double promedio) { this.promedio = promedio; }

    public int getResenas() { return resenas; }
    public void setResenas(int resenas) { this.resenas = resenas; }
}
