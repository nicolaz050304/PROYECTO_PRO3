package pe.edu.pe.pucp.proyecto.economy.implbl;

import pe.edu.pe.pucp.proyecto.economy.Pago;
import pe.edu.pe.pucp.proyecto.economy.bl.PagoBL;
import pe.edu.pe.pucp.proyecto.economy.dao.PagoIDAO;
import pe.edu.pe.pucp.proyecto.economy.impl.PagoImpl;
import pe.edu.pe.pucp.proyecto.reservation.Reserva;

import java.util.List;

public class PagoBLImpl implements PagoBL {

    // El BL siempre llama al DAO para persistir datos
    private PagoIDAO daoPago = new PagoImpl();

    @Override
    public int registrarPagoDeReserva(Reserva reserva) {
        Pago nuevoPago = new Pago();

        // --- LÓGICA DE NEGOCIO ---
        double bruto = reserva.getMontoTotal();
        double comision = bruto * 0.10; // Regla del 10%

        nuevoPago.setReserva(reserva);
        nuevoPago.setMontoBruto(bruto);
        nuevoPago.setMontoNeto(bruto - comision);
        nuevoPago.setPorcenComision(0.10);
        nuevoPago.setMoneda(reserva.getMoneda());
        nuevoPago.setTipoCambio(3.75);
        nuevoPago.setEstadoTransaccion("COBRADO_AL_INVITADO");

        // Delegamos al DAO el guardado
        daoPago.save(nuevoPago);
        return nuevoPago.getIdPago();
    }

    @Override
    public void procesarEnvioAnfitrion(Pago pago, int idCuentaDestino) {
        if (pago.getEstadoTransaccion().equals("COBRADO_AL_INVITADO")) {
            pago.registrarEnvioAnfitrion(idCuentaDestino);
            daoPago.update(pago);
        }
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