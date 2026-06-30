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

        // Login real: sin fallback a mock. Un 401 devuelve null (credenciales inválidas);
        // un fallo de red propaga la excepción para que la UI avise.
        public async Task<Usuario?> LoginAsync(string correo, string password)
            => await _rest.LoginAsync(correo, password);

        // Registro (ESCRITURA): POST UsuarioRS. Sin fallback a mock: si el REST falla
        // (p.ej. correo duplicado, 400/red), propaga para que la UI avise.
        public async Task RegistrarAsync(Usuario u) => await _rest.RegistrarAsync(u);

        // Actualización de perfil (ESCRITURA): PUT UsuarioRS/{id}. Devuelve bool en vez de propagar
        // para que la página de editar perfil distinga éxito de fallo y muestre el mensaje adecuado.
        public async Task<bool> ActualizarAsync(Usuario u)
        {
            try { await _rest.ActualizarAsync(u); return true; }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "No se pudo actualizar el perfil del usuario {Id}", u.Id);
                return false;
            }
        }

        // Cambio de estado (ESCRITURA) para el panel admin: bloquear (SUSPENDIDO) / desbloquear (DISPONIBLE).
        // Devuelve bool para que la UI distinga éxito de fallo y muestre el mensaje adecuado.
        public async Task<bool> CambiarEstadoAsync(int id, string estado)
            => await _rest.CambiarEstadoAsync(id, estado);

        // Recuperar contraseña (RF04): valida correo + documento y fija la nueva clave en backend.
        // Devuelve (ok, error): si el REST cae, lo reporta como mensaje para la UI (sin propagar).
        public async Task<(bool ok, string? error)> RecuperarAsync(string correo, string documento, string nuevaPassword)
        {
            try { return await _rest.RecuperarAsync(correo, documento, nuevaPassword); }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "Recuperación de contraseña: REST no disponible");
                return (false, "No se pudo conectar con el servidor. Intenta más tarde.");
            }
        }

        // Cambiar contraseña (logueado): verifica la actual y fija la nueva en backend.
        public async Task<(bool ok, string? error)> CambiarPasswordAsync(int id, string passwordActual, string nuevaPassword)
        {
            try { return await _rest.CambiarPasswordAsync(id, passwordActual, nuevaPassword); }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "Cambio de contraseña: REST no disponible");
                return (false, "No se pudo conectar con el servidor. Intenta más tarde.");
            }
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
                // null del REST = el usuario no existe (resultado válido) -> null, no un mock.
                return await _rest.ObtenerPorIdAsync(id);
            }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "REST no disponible al obtener usuario {Id}; usando mock", id);
                // Solo ante CAÍDA del REST usamos el mock, y solo si su id coincide con el pedido.
                var actual = _mock.ObtenerUsuarioActual();
                return actual.Id == id ? actual : null;
            }
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
