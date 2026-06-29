package pe.edu.pe.pucp.proyecto.economy;

/**
 * Tasa de cambio de una moneda relativa al PEN (fila de la tabla tipo_cambio).
 * tasaAPen = cuántos soles (PEN) vale 1 unidad de esta moneda.
 * Ej.: PEN=1.0, USD=3.75, EUR=4.05. Reemplaza las tasas hardcodeadas de
 * {@link ConversorMonetario} como fuente de verdad.
 */
public class TipoCambio {
    private String codigo;   // PEN, USD, EUR (PRIMARY KEY)
    private String simbolo;  // S/, $, €
    private double tasaAPen;

    public TipoCambio() {
    }

    public TipoCambio(String codigo, String simbolo, double tasaAPen) {
        this.codigo = codigo;
        this.simbolo = simbolo;
        this.tasaAPen = tasaAPen;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getSimbolo() { return simbolo; }
    public void setSimbolo(String simbolo) { this.simbolo = simbolo; }

    public double getTasaAPen() { return tasaAPen; }
    public void setTasaAPen(double tasaAPen) { this.tasaAPen = tasaAPen; }
}
