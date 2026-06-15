using BunkiApp.Models;
using Microsoft.Extensions.Logging;

namespace BunkiApp.Services
{
    // Decide la fuente de datos de Usuario: intenta REST y, si falla, usa el mock.
    // LIMITACIÓN: DataService no modela una COLECCIÓN de usuarios (solo ObtenerUsuarioActual).
    // Por eso ListarAsync cae a una lista vacía cuando el backend no responde; la página que
    // la consume conserva su propia lista demo en ese caso (ver AdminUsuarios).
    public class UsuarioProvider
    {
        private readonly UsuarioServiceRest _rest;
        private readonly DataService _mock;
        private readonly ILogger<UsuarioProvider> _log;

        public UsuarioProvider(UsuarioServiceRest rest, DataService mock,
                               ILogger<UsuarioProvider> log)
        {
            _rest = rest;
            _mock = mock;
            _log = log;
        }

        public async Task<List<Usuario>> ListarAsync()
        {
            try
            {
                return await _rest.ListarAsync();   // vacío si el backend no tiene datos
            }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "REST no disponible al listar usuarios; sin colección mock");
                return new();                        // no hay lista mock en DataService
            }
        }

        public async Task<Usuario?> ObtenerPorIdAsync(int id)
        {
            try
            {
                var u = await _rest.ObtenerPorIdAsync(id);
                if (u is not null) return u;
            }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "REST no disponible al obtener usuario {Id}; usando mock", id);
            }

            var actual = _mock.ObtenerUsuarioActual();    // MÉTODO REAL de DataService
            return actual.Id == id ? actual : null;
        }

        // Usuario de la sesión: REST por id y, ante fallo, el mock real.
        public async Task<Usuario> ObtenerActualAsync(int id)
        {
            try
            {
                return await _rest.ObtenerPorIdAsync(id) ?? _mock.ObtenerUsuarioActual();
            }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "REST no disponible (usuario actual {Id}); usando mock", id);
                return _mock.ObtenerUsuarioActual();      // MÉTODO REAL de DataService
            }
        }
    }
}
