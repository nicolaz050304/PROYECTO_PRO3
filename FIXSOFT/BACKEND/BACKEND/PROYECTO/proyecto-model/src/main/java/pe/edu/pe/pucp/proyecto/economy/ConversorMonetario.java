package pe.edu.pe.pucp.proyecto.economy;

public class ConversorMonetario {
    private double monto;
    private TipoMoneda monedaOrigen;
    private TipoMoneda monedaDestino;

    public ConversorMonetario(double monto, TipoMoneda monedaOrigen, TipoMoneda monedaDestino) {
        this.monto = monto;
        this.monedaOrigen = monedaOrigen;
        this.monedaDestino = monedaDestino;
    }



    public double calcularConversion(double monto, TipoMoneda origen, TipoMoneda destino) {
        if (origen == destino) return monto;
        final double USD_TO_PEN = 3.75;
        final double EUR_TO_PEN = 4.05;

        double resultado = 0;

        // Conversiones de cada uno
        if (origen == TipoMoneda.USD && destino == TipoMoneda.PEN) {
            resultado = monto * USD_TO_PEN;
        } else if (origen == TipoMoneda.PEN && destino == TipoMoneda.USD) {
            resultado = monto / USD_TO_PEN;
        } else if (origen == TipoMoneda.EUR && destino == TipoMoneda.PEN) {
            resultado = monto * EUR_TO_PEN;
        } else if (origen == TipoMoneda.PEN && destino == TipoMoneda.EUR) {
            resultado = monto / EUR_TO_PEN;
        }

        this.monto = resultado;
        return resultado;

    }

}
