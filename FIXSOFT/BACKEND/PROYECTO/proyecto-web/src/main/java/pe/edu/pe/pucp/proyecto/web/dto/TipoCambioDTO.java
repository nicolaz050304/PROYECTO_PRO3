package pe.edu.pe.pucp.proyecto.web.dto;

/**
 * DTO plano de Tipo de Cambio. Único objeto expuesto por TipoCambioRS.
 * Campos camelCase; el System.Text.Json del frontend C# mapea camelCase ->
 * PascalCase case-insensitive.
 */
public class TipoCambioDTO {

    private String codigo;
    private String simbolo;
    private double tasaAPen;

    public TipoCambioDTO() {
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getSimbolo() { return simbolo; }
    public void setSimbolo(String simbolo) { this.simbolo = simbolo; }

    public double getTasaAPen() { return tasaAPen; }
    public void setTasaAPen(double tasaAPen) { this.tasaAPen = tasaAPen; }
}
