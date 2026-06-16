using System.Net.Http.Json;
using System.Net.Http.Headers;
using BunkiApp.Models;

namespace BunkiApp.Services
{
    // Cliente REST de Reseña contra el backend Java (JAX-RS).
    // NOTA: la creación de reseñas (RegistrarAsync) es escritura y NO está cableada.
    public class ResenaServiceRest
    {
        private readonly HttpClient _http;
        private const string Endpoint = "ResenaRS";

        public ResenaServiceRest(HttpClient http)
        {
            _http = http;
            _http.DefaultRequestHeaders.Accept.Clear();
            _http.DefaultRequestHeaders.Accept.Add(
                new MediaTypeWithQualityHeaderValue("application/json"));
        }

        public async Task<List<Resena>> ListarAsync()
            => await _http.GetFromJsonAsync<List<Resena>>(Endpoint) ?? new();

        // Moderación admin: TODAS las reseñas, incluidas las ocultas (campo Activo).
        public async Task<List<Resena>> ListarTodasAsync()
            => await _http.GetFromJsonAsync<List<Resena>>($"{Endpoint}/todas") ?? new();

        // Moderación admin: reactiva una reseña oculta (activo=1).
        public async Task ReactivarAsync(int id)
            => (await _http.PutAsync($"{Endpoint}/{id}/reactivar", null)).EnsureSuccessStatusCode();

        public async Task<Resena?> ObtenerPorIdAsync(int id)
            => await _http.GetFromJsonAsync<Resena>($"{Endpoint}/{id}");

        public async Task<List<Resena>> ListarPorAlojamientoAsync(int alojamientoId)
            => await _http.GetFromJsonAsync<List<Resena>>($"{Endpoint}/alojamiento/{alojamientoId}") ?? new();

        public async Task RegistrarAsync(Resena r)
            => (await _http.PostAsJsonAsync(Endpoint, r)).EnsureSuccessStatusCode();

        public async Task ActualizarAsync(Resena r)
            => (await _http.PutAsJsonAsync($"{Endpoint}/{r.Id}", r)).EnsureSuccessStatusCode();

        public async Task EliminarAsync(int id)
            => (await _http.DeleteAsync($"{Endpoint}/{id}")).EnsureSuccessStatusCode();
    }
}
