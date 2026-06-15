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
    }
}
