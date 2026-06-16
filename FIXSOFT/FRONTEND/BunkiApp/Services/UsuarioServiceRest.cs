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
        private const string Endpoint = "UsuarioRS";

        public UsuarioServiceRest(HttpClient http)
        {
            _http = http;
            _http.DefaultRequestHeaders.Accept.Clear();
            _http.DefaultRequestHeaders.Accept.Add(
                new MediaTypeWithQualityHeaderValue("application/json"));
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

        public async Task<List<Usuario>> ListarAsync()
            => await _http.GetFromJsonAsync<List<Usuario>>(Endpoint) ?? new();

        public async Task<Usuario?> ObtenerPorIdAsync(int id)
            => await _http.GetFromJsonAsync<Usuario>($"{Endpoint}/{id}");

        public async Task RegistrarAsync(Usuario u)
            => (await _http.PostAsJsonAsync(Endpoint, u)).EnsureSuccessStatusCode();

        public async Task ActualizarAsync(Usuario u)
            => (await _http.PutAsJsonAsync($"{Endpoint}/{u.Id}", u)).EnsureSuccessStatusCode();

        public async Task EliminarAsync(int id)
            => (await _http.DeleteAsync($"{Endpoint}/{id}")).EnsureSuccessStatusCode();
    }
}
