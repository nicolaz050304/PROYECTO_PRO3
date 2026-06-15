using System.Net.Http.Json;
using System.Net.Http.Headers;
using BunkiApp.Models;

namespace BunkiApp.Services
{
    // Cliente REST de Alojamiento contra el backend Java (JAX-RS).
    public class AlojamientoServiceRest
    {
        private readonly HttpClient _http;
        private const string Endpoint = "AlojamientoRS";

        public AlojamientoServiceRest(HttpClient http)
        {
            _http = http;
            _http.DefaultRequestHeaders.Accept.Clear();
            _http.DefaultRequestHeaders.Accept.Add(
                new MediaTypeWithQualityHeaderValue("application/json"));
        }

        public async Task<List<Alojamiento>> ListarAsync()
            => await _http.GetFromJsonAsync<List<Alojamiento>>(Endpoint) ?? new();

        public async Task<Alojamiento?> ObtenerPorIdAsync(int id)
            => await _http.GetFromJsonAsync<Alojamiento>($"{Endpoint}/{id}");

        public async Task<List<Alojamiento>> ListarPorAnfitrionAsync(int anfitrionId)
            => await _http.GetFromJsonAsync<List<Alojamiento>>($"{Endpoint}/anfitrion/{anfitrionId}") ?? new();

        public async Task RegistrarAsync(Alojamiento a)
            => (await _http.PostAsJsonAsync(Endpoint, a)).EnsureSuccessStatusCode();

        public async Task ActualizarAsync(Alojamiento a)
            => (await _http.PutAsJsonAsync($"{Endpoint}/{a.Id}", a)).EnsureSuccessStatusCode();

        public async Task EliminarAsync(int id)
            => (await _http.DeleteAsync($"{Endpoint}/{id}")).EnsureSuccessStatusCode();
    }
}
