using System.Net.Http.Json;
using System.Net.Http.Headers;
using BunkiApp.Models;

namespace BunkiApp.Services
{
    // Cliente REST de Usuario contra el backend Java (JAX-RS).
    // NOTA: login/registro/cambio de contraseña NO pasan por aquí (van por auth aparte).
    public class UsuarioServiceRest
    {
        private readonly HttpClient _http;
        private readonly AppState _appState;
        private const string Endpoint = "UsuarioRS";

        public UsuarioServiceRest(HttpClient http, AppState appState)
        {
            _http = http;
            _appState = appState;
            _http.DefaultRequestHeaders.Accept.Clear();
            _http.DefaultRequestHeaders.Accept.Add(
                new MediaTypeWithQualityHeaderValue("application/json"));
        }

        // Adjunta la identidad del usuario logueado para que el backend autorice las acciones
        // sensibles (admin). El navegador nunca llama directo al backend, así que la pone Blazor.
        private void AplicarIdentidad()
        {
            _http.DefaultRequestHeaders.Remove("X-Usuario-Id");
            _http.DefaultRequestHeaders.Remove("X-Usuario-Rol");
            var u = _appState.UsuarioActual;
            if (u is not null)
            {
                _http.DefaultRequestHeaders.Add("X-Usuario-Id", u.Id.ToString());
                _http.DefaultRequestHeaders.Add("X-Usuario-Rol", u.TipoUsuario ?? "");
            }
        }

        // Login real: POST UsuarioRS/login con {correo, password}.
        // 200 -> usuario autenticado (sin password); 401 -> credenciales inválidas -> null.
        public async Task<Usuario?> LoginAsync(string correo, string password)
        {
            var payload = new { correo, password };
            var resp = await _http.PostAsJsonAsync($"{Endpoint}/login", payload);
            if (!resp.IsSuccessStatusCode) return null;   // 401 -> credenciales inválidas
            return await resp.Content.ReadFromJsonAsync<Usuario>();
        }

        // Recuperar contraseña (RF04): POST UsuarioRS/recuperar con {correo, documento, nuevaPassword}.
        // 200 -> ok; 400 -> datos no coinciden / política. Devuelve el mensaje de error del backend.
        public async Task<(bool ok, string? error)> RecuperarAsync(string correo, string documento, string nuevaPassword)
        {
            var payload = new { correo, documento, nuevaPassword };
            var resp = await _http.PostAsJsonAsync($"{Endpoint}/recuperar", payload);
            if (resp.IsSuccessStatusCode) return (true, null);
            string? msg = null;
            try
            {
                var doc = await resp.Content.ReadFromJsonAsync<Dictionary<string, string>>();
                if (doc != null && doc.TryGetValue("error", out var e)) msg = e;
            }
            catch { /* respuesta sin JSON de error: usamos mensaje genérico en la UI */ }
            return (false, msg);
        }

        // Cambiar contraseña (logueado): POST UsuarioRS/{id}/cambiar-password con {passwordActual, nuevaPassword}.
        // 200 -> ok; 400 -> actual incorrecta / política. Devuelve el mensaje de error del backend.
        public async Task<(bool ok, string? error)> CambiarPasswordAsync(int id, string passwordActual, string nuevaPassword)
        {
            var payload = new { passwordActual, nuevaPassword };
            var resp = await _http.PostAsJsonAsync($"{Endpoint}/{id}/cambiar-password", payload);
            if (resp.IsSuccessStatusCode) return (true, null);
            string? msg = null;
            try
            {
                var doc = await resp.Content.ReadFromJsonAsync<Dictionary<string, string>>();
                if (doc != null && doc.TryGetValue("error", out var e)) msg = e;
            }
            catch { }
            return (false, msg);
        }

        public async Task<List<Usuario>> ListarAsync()
            => await _http.GetFromJsonAsync<List<Usuario>>(Endpoint) ?? new();

        public async Task<Usuario?> ObtenerPorIdAsync(int id)
            => await _http.GetFromJsonAsync<Usuario>($"{Endpoint}/{id}");

        public async Task RegistrarAsync(Usuario u)
            => (await _http.PostAsJsonAsync(Endpoint, u)).EnsureSuccessStatusCode();

        public async Task ActualizarAsync(Usuario u)
            => (await _http.PutAsJsonAsync($"{Endpoint}/{u.Id}", u)).EnsureSuccessStatusCode();

        // Acción de admin: el backend exige rol ADMINISTRADOR (AuthFilter).
        public async Task EliminarAsync(int id)
        {
            AplicarIdentidad();
            (await _http.DeleteAsync($"{Endpoint}/{id}")).EnsureSuccessStatusCode();
        }

        // Cambia el estado de la cuenta de un usuario (DISPONIBLE / SUSPENDIDO). Lo usa el panel admin
        // para bloquear/desbloquear; PUT UsuarioRS/{id}/estado con {"estado":"..."} -> persiste en BD.
        // Acción de admin: el backend exige rol ADMINISTRADOR (AuthFilter).
        public async Task<bool> CambiarEstadoAsync(int id, string estado)
        {
            try
            {
                AplicarIdentidad();
                var resp = await _http.PutAsJsonAsync($"{Endpoint}/{id}/estado", new { estado });
                return resp.IsSuccessStatusCode;
            }
            catch { return false; }
        }
    }
}
