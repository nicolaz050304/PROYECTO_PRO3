package pe.edu.pe.pucp.proyecto.economy.implbl;

import pe.edu.pe.pucp.proyecto.economy.Pago;
import pe.edu.pe.pucp.proyecto.economy.TipoCambio;
import pe.edu.pe.pucp.proyecto.economy.TipoMoneda;
import pe.edu.pe.pucp.proyecto.economy.bl.PagoBL;
import pe.edu.pe.pucp.proyecto.economy.bl.TipoCambioBL;
import pe.edu.pe.pucp.proyecto.economy.implbl.TipoCambioBLImpl;
import pe.edu.pe.pucp.proyecto.economy.dao.PagoIDAO;
import pe.edu.pe.pucp.proyecto.economy.impl.PagoImpl;
import pe.edu.pe.pucp.proyecto.reservation.Reserva;
import pe.edu.pe.pucp.proyecto.accomodations.Alojamiento;
import pe.edu.pe.pucp.proyecto.accomodations.implbl.AlojamientoBLImpl;
import pe.edu.pe.pucp.proyecto.notif.implbl.NotificacionBLImpl;

import java.util.List;

public class PagoBLImpl implements PagoBL {

    // El BL siempre llama al DAO para persistir datos
    private PagoIDAO daoPago = new PagoImpl();

    @Override
    public int registrarPagoDeReserva(Reserva reserva) {
        // Evitar DOBLE PAGO (integridad): si la reserva ya tiene un pago registrado, no creamos otro.
        // Devolvemos el id del pago existente (idempotente) en vez de duplicar; así no se rompe el flujo
        // del frontend, que solo espera un id válido.
        Pago existente = daoPago.buscarPorReserva(reserva.getIdReserva());
        if (existente != null) {
            return existente.getIdPago();
        }

        Pago nuevoPago = new Pago();

        // --- LÓGICA DE NEGOCIO ---
        double bruto = reserva.getMontoTotal();
        double comision = bruto * 0.10; // Regla del 10%

        nuevoPago.setReserva(reserva);
        nuevoPago.setMontoBruto(bruto);
        nuevoPago.setMontoNeto(bruto - comision);
        nuevoPago.setPorcenComision(0.10);
        nuevoPago.setMoneda(reserva.getMoneda());
        // Tipo de cambio REAL desde la tabla tipo_cambio (antes estaba quemado a 3.75).
        nuevoPago.setTipoCambio(obtenerTasaAPen(reserva.getMoneda()));
        nuevoPago.setEstadoTransaccion("COBRADO_AL_INVITADO");

        // Delegamos al DAO el guardado
        daoPago.save(nuevoPago);

        // EVENTO 2 (RF18): tras registrar el pago, notificamos al ANFITRIÓN (dueño del alojamiento).
        // La reserva llega con el alojamiento como proxy solo-id (getDuenho() == null), así que cargamos
        // el alojamiento por su id para obtener el id real del dueño. Va en try/catch: la notificación es
        // secundaria y NUNCA debe romper el registro del pago.
        try {
            if (reserva.getAlojamiento() != null) {
                Alojamiento alo = new AlojamientoBLImpl().obtenerPorId(reserva.getAlojamiento().getIdAlojamiento());
                if (alo != null && alo.getDuenho() != null) {
                    int idAnfitrion = alo.getDuenho().getIdUsuario();
                    if (idAnfitrion > 0) {
                        new NotificacionBLImpl().crear("Pago recibido",
                                "Recibiste S/ " + String.format(java.util.Locale.US, "%.2f", nuevoPago.getMontoNeto())
                                        + " por una de tus reservas.", idAnfitrion, "PAGO");
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return nuevoPago.getIdPago();
    }

    /**
     * Tasa de conversión a PEN para la moneda dada, leída de la tabla tipo_cambio.
     * PEN -> 1.0. Si no hay tasa registrada o falla la consulta, devuelve 1.0 (no convertir)
     * en vez de un número inventado.
     */
    private double obtenerTasaAPen(TipoMoneda moneda) {
        if (moneda == null || moneda == TipoMoneda.PEN) {
            return 1.0;
        }
        try {
            TipoCambioBL tipoCambioBL = new TipoCambioBLImpl();
            for (TipoCambio tc : tipoCambioBL.listarTodas()) {
                if (tc.getCodigo() != null && tc.getCodigo().equalsIgnoreCase(moneda.name())) {
                    return tc.getTasaAPen() > 0 ? tc.getTasaAPen() : 1.0;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 1.0;
    }

    @Override
    public void procesarEnvioAnfitrion(Pago pago, int idCuentaDestino) {
        if (pago.getEstadoTransaccion().equals("COBRADO_AL_INVITADO")) {
            pago.registrarEnvioAnfitrion(idCuentaDestino);
            daoPago.update(pago);
        }
    }

    @Override
    public List<Pago> listarPorAnfitrion(int idAnfitrion) {
        return daoPago.listarPorAnfitrion(idAnfitrion);
    }

    // El comprobante se pide por id de reserva (el frontend no conoce el id del pago): delegamos al DAO.
    @Override
    public Pago buscarPorReserva(int idReserva) {
        return daoPago.buscarPorReserva(idReserva);
    }

    // Implementación de métodos genéricos de IBL
    @Override public List<Pago> listarTodos() { return daoPago.listAll(); }
    @Override public Pago obtenerPorId(Integer id) { return daoPago.load(id); }
    @Override
    public int insertar(Pago pago) {
        if(pago.getMontoBruto()<0 ||pago.getMontoNeto()<0)throw new RuntimeException("Error: No puedes pagar un monto negativo");
        return daoPago.save(pago).getIdPago();
    }

    @Override
    public int modificar(Pago pago) {
        return daoPago.update(pago).getIdPago();
    }
    @Override public int eliminar(Pago pago) { daoPago.remove(pago); return 1; }
}