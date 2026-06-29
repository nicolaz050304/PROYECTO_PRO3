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
                // Una lista vacía del REST es válida (ese alojamiento no tiene reseñas):
                // se retorna tal cual. Solo se cae al mock ante EXCEPCIÓN (REST caído).
                return await _rest.ListarPorAlojamientoAsync(alojamientoId) ?? new();
            }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "REST no disponible (reseñas alojamiento {Id}); usando mock", alojamientoId);
                // Fallback real: las reseñas embebidas del alojamiento en DataService.
                return _mock.ObtenerAlojamiento(alojamientoId)?.Resenas ?? new();
            }
        }

        // Reseñas que un usuario recibió como cliente (perfil público). Lectura: si falla, lista vacía.
        public async Task<List<Resena>> ListarRecibidasPorClienteAsync(int usuarioId)
        {
            try { return await _rest.ListarRecibidasPorClienteAsync(usuarioId); }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "REST no disponible (reseñas recibidas usuario {Id})", usuarioId);
                return new();
            }
        }

        // RF19: reseñas que los huéspedes dejaron AL ANFITRIÓN (perfil público). Lectura: si falla, lista vacía.
        public async Task<List<Resena>> ListarDeAnfitrionAsync(int anfitrionId)
        {
            try { return await _rest.ListarDeAnfitrionAsync(anfitrionId); }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "REST no disponible (reseñas del anfitrión {Id})", anfitrionId);
                return new();
            }
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

        // El AUTOR edita/elimina su propia reseña (PUT/DELETE). Sin fallback: propaga el error a la UI.
        public async Task ActualizarAsync(Resena resena) => await _rest.ActualizarAsync(resena);
        public async Task EliminarAsync(int id) => await _rest.EliminarAsync(id);

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
