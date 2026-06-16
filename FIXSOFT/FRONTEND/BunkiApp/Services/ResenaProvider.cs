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

        // Moderación admin: lista TODAS (incl. ocultas). Sin fallback a mock; si falla, lista vacía.
        public async Task<List<Resena>> ListarTodasAsync()
        {
            try { return await _rest.ListarTodasAsync(); }
            catch (Exception ex) { _log.LogWarning(ex, "REST no disponible (listar todas resenas)"); return new(); }
        }

        // Moderación admin (escritura): ocultar (DELETE -> activo=0) y reactivar (activo=1).
        // Sin fallback: si el REST falla, propaga para que la UI avise.
        public async Task OcultarAsync(int id) => await _rest.EliminarAsync(id);
        public async Task ReactivarAsync(int id) => await _rest.ReactivarAsync(id);

        // Registro (escritura) vía REST: POST ResenaRS. A diferencia de las lecturas, aquí
        // NO hay fallback silencioso: si el REST falla, propagamos la excepción para que la
        // UI muestre el error (no tiene sentido "guardar" en un mock que no persiste).
        public async Task RegistrarAsync(Resena resena)
        {
            try
            {
                await _rest.RegistrarAsync(resena);
            }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "No se pudo registrar la reseña (reserva {Id})", resena.ReservaId);
                throw;
            }
        }
    }
}
