using BunkiApp.Models;
using Microsoft.Extensions.Logging;

namespace BunkiApp.Services
{
    // Decide la fuente de datos de Reserva: intenta REST y, si falla o viene vacío, usa el mock.
    // El backend expone una sola colección; las vistas (próximas/pasadas/canceladas) se derivan
    // por Estado, igual que las separa hoy DataService.
    public class ReservaProvider
    {
        private readonly ReservaServiceRest _rest;
        private readonly DataService _mock;
        private readonly ILogger<ReservaProvider> _log;

        public ReservaProvider(ReservaServiceRest rest, DataService mock,
                               ILogger<ReservaProvider> log)
        {
            _rest = rest;
            _mock = mock;
            _log = log;
        }

        public async Task<List<Reserva>> ListarTodasAsync()
        {
            try
            {
                var lista = await _rest.ListarAsync();
                return (lista is null || lista.Count == 0)
                    ? _mock.ObtenerTodasLasReservas()             // MÉTODO REAL de DataService
                    : lista;
            }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "REST no disponible al listar reservas; usando mock");
                return _mock.ObtenerTodasLasReservas();
            }
        }

        public async Task<List<Reserva>> ListarProximasAsync()
        {
            try
            {
                var lista = await _rest.ListarAsync();
                if (lista is null || lista.Count == 0)
                    return _mock.ObtenerReservasProximas();       // MÉTODO REAL
                return lista.Where(r => r.Estado is "Confirmada" or "Pendiente").ToList();
            }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "REST no disponible (próximas); usando mock");
                return _mock.ObtenerReservasProximas();
            }
        }

        public async Task<List<Reserva>> ListarPasadasAsync()
        {
            try
            {
                var lista = await _rest.ListarAsync();
                if (lista is null || lista.Count == 0)
                    return _mock.ObtenerReservasPasadas();        // MÉTODO REAL
                return lista.Where(r => r.Estado == "Completada").ToList();
            }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "REST no disponible (pasadas); usando mock");
                return _mock.ObtenerReservasPasadas();
            }
        }

        public async Task<List<Reserva>> ListarCanceladasAsync()
        {
            try
            {
                var lista = await _rest.ListarAsync();
                if (lista is null || lista.Count == 0)
                    return _mock.ObtenerReservasCanceladas();     // MÉTODO REAL
                return lista.Where(r => r.Estado == "Cancelada").ToList();
            }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "REST no disponible (canceladas); usando mock");
                return _mock.ObtenerReservasCanceladas();
            }
        }

        public async Task<Reserva?> ObtenerPorIdAsync(int id)
        {
            try
            {
                return await _rest.ObtenerPorIdAsync(id)
                       ?? _mock.ObtenerTodasLasReservas().FirstOrDefault(r => r.Id == id);
            }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "REST no disponible al obtener reserva {Id}; usando mock", id);
                return _mock.ObtenerTodasLasReservas().FirstOrDefault(r => r.Id == id);
            }
        }
    }
}
