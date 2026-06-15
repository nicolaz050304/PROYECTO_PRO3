using BunkiApp.Models;
using Microsoft.Extensions.Logging;

namespace BunkiApp.Services
{
    // Lecturas de Reseña con fallback. Hoy las reseñas se leen embebidas en Alojamiento.Resenas
    // (DetalleAlojamiento ya las recibe vía AlojamientoProvider), por eso este provider NO se
    // cablea aún: queda listo por si se necesita listar reseñas por separado. El fallback usa
    // las reseñas del alojamiento en el mock real.
    public class ResenaProvider
    {
        private readonly ResenaServiceRest _rest;
        private readonly DataService _mock;
        private readonly ILogger<ResenaProvider> _log;

        public ResenaProvider(ResenaServiceRest rest, DataService mock,
                             ILogger<ResenaProvider> log)
        {
            _rest = rest;
            _mock = mock;
            _log = log;
        }

        public async Task<List<Resena>> ListarPorAlojamientoAsync(int alojamientoId)
        {
            try
            {
                var lista = await _rest.ListarPorAlojamientoAsync(alojamientoId);
                if (lista is not null && lista.Count > 0) return lista;
            }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "REST no disponible (reseñas alojamiento {Id}); usando mock", alojamientoId);
            }

            // Fallback real: las reseñas embebidas del alojamiento en DataService.
            return _mock.ObtenerAlojamiento(alojamientoId)?.Resenas ?? new();
        }
    }
}
