package pe.edu.pe.pucp.proyecto.economy.bl;

import pe.edu.pe.pucp.proyecto.bl.IBL;
import pe.edu.pe.pucp.proyecto.economy.Pago;
import pe.edu.pe.pucp.proyecto.reservation.Reserva;

public interface PagoBL extends IBL<Pago, Integer> {
    // Método de negocio extra que no está en el CRUD básico
    int registrarPagoDeReserva(Reserva reserva);
    void procesarEnvioAnfitrion(Pago pago, int idCuentaDestino);

    // Pagos recibidos por un anfitrión (alimenta su panel de ganancias).
    java.util.List<Pago> listarPorAnfitrion(int idAnfitrion);

    // El pago de una reserva (o null). Lo usa el comprobante por id de reserva.
    Pago buscarPorReserva(int idReserva);
}
