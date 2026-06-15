using System.Net.Http.Json;
using System.Net.Http.Headers;
using BunkiApp.Models;

namespace BunkiApp.Services
{
    // Cliente REST de Reserva contra el backend Java (JAX-RS).
    public class ReservaServiceRest
    {
        private readonly HttpClient _http;
        private const string Endpoint = "ReservaRS";

        public ReservaServiceRest(HttpClient http)
        {
            _http = http;
            _http.DefaultRequestHeaders.Accept.Clear();
            _http.DefaultRequestHeaders.Accept.Add(
                new MediaTypeWithQualityHeaderValue("application/json"));
        }

        public async Task<List<Reserva>> ListarAsync()
            => await _http.GetFromJsonAsync<List<Reserva>>(Endpoint) ?? new();

        public async Task<Reserva?> ObtenerPorIdAsync(int id)
            => await _http.GetFromJsonAsync<Reserva>($"{Endpoint}/{id}");

        public async Task<List<Reserva>> ListarPorUsuarioAsync(int usuarioId)
            => await _http.GetFromJsonAsync<List<Reserva>>($"{Endpoint}/usuario/{usuarioId}") ?? new();

        public async Task<List<Reserva>> ListarPorAnfitrionAsync(int anfitrionId)
            => await _http.GetFromJsonAsync<List<Reserva>>($"{Endpoint}/anfitrion/{anfitrionId}") ?? new();

        public async Task RegistrarAsync(Reserva r)
            => (await _http.PostAsJsonAsync(Endpoint, r)).EnsureSuccessStatusCode();

        public async Task ActualizarAsync(Reserva r)
            => (await _http.PutAsJsonAsync($"{Endpoint}/{r.Id}", r)).EnsureSuccessStatusCode();

        public async Task EliminarAsync(int id)
            => (await _http.DeleteAsync($"{Endpoint}/{id}")).EnsureSuccessStatusCode();
    }
}
