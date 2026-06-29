using BunkiApp.Models;
using Microsoft.Extensions.Logging;

namespace BunkiApp.Services
{
    // Lecturas de Pago con fallback. LIMITACIÓN: DataService no expone historial de pagos,
    // así que el fallback es vacío/null. Hoy ninguna página lee pagos (Pagos calcula su
    // resumen localmente), por eso este provider no se cablea todavía: queda listo para un
    // futuro historial/reporte de pagos.
    public class PagoProvider
    {
        private readonly PagoServiceRest _rest;
        private readonly DataService _mock;
        private readonly ILogger<PagoProvider> _log;

        public PagoProvider(PagoServiceRest rest, DataService mock,
                            ILogger<PagoProvider> log)
        {
            _rest = rest;
            _mock = mock;
            _log = log;
        }

        public async Task<List<ResultadoPago>> ListarAsync()
        {
            try
            {
                return await _rest.ListarAsync();
            }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "REST no disponible al listar pagos; sin historial mock");
                return new();   // DataService no modela historial de pagos
            }
        }

        public async Task<ResultadoPago?> ObtenerPorIdAsync(int id)
        {
            try
            {
                return await _rest.ObtenerPorIdAsync(id);
            }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "REST no disponible al obtener pago {Id}", id);
                return null;
            }
        }

        // Pagos recibidos por un anfitrión (para su panel de ganancias). Lectura: ante fallo, lista vacía.
        public async Task<List<ResultadoPago>> ListarPorAnfitrionAsync(int idAnfitrion)
        {
            try { return await _rest.ListarPorAnfitrionAsync(idAnfitrion); }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "REST no disponible al listar pagos del anfitrión {Id}", idAnfitrion);
                return new();
            }
        }

        // Registra el pago de una reserva ya creada (RF13). Devuelve bool y NO propaga:
        // si el pago falla, la reserva ya quedó creada, así que no debe tumbar el flujo de la UI.
        public async Task<bool> RegistrarPagoDeReservaAsync(int idReserva)
        {
            try { return await _rest.RegistrarPagoDeReservaAsync(idReserva); }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "Error al registrar pago de reserva {Id}", idReserva);
                return false;
            }
        }
    }
}
