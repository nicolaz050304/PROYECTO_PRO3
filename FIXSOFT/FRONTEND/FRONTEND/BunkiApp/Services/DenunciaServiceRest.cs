using System.Net.Http.Json;
using System.Net.Http.Headers;
using BunkiApp.Models;

namespace BunkiApp.Services
{
    // Cliente REST de denuncias (RF31) contra el backend Java.
    public class DenunciaServiceRest
    {
        private readonly HttpClient _http;
        private const string Endpoint = "DenunciaRS";

        public DenunciaServiceRest(HttpClient http)
        {
            _http = http;
            _http.DefaultRequestHeaders.Accept.Clear();
            _http.DefaultRequestHeaders.Accept.Add(
                new MediaTypeWithQualityHeaderValue("application/json"));
        }

        // Todas las denuncias (panel del admin).
        public async Task<List<Denuncia>> ListarAsync()
            => await _http.GetFromJsonAsync<List<Denuncia>>(Endpoint) ?? new();

        // El huésped crea una denuncia sobre un alojamiento.
        public async Task AgregarAsync(int denuncianteId, int alojamientoId, string motivo, string? descripcion)
        {
            var body = new { denuncianteId, alojamientoId, motivo, descripcion };
            (await _http.PostAsJsonAsync(Endpoint, body)).EnsureSuccessStatusCode();
        }

        // El admin cambia el estado (EN_REVISION / RESUELTO / CERRADO).
        public async Task CambiarEstadoAsync(int idDenuncia, string estado)
            => (await _http.PutAsJsonAsync($"{Endpoint}/{idDenuncia}/estado", new { estado })).EnsureSuccessStatusCode();
    }
}
