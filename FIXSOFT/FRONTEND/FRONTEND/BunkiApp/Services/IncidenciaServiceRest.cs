using System.Net.Http.Json;
using System.Net.Http.Headers;
using BunkiApp.Models;

namespace BunkiApp.Services
{
    // Cliente REST de incidencias / soporte (RF28) contra el backend Java.
    public class IncidenciaServiceRest
    {
        private readonly HttpClient _http;
        private const string Endpoint = "IncidenciaRS";

        public IncidenciaServiceRest(HttpClient http)
        {
            _http = http;
            _http.DefaultRequestHeaders.Accept.Clear();
            _http.DefaultRequestHeaders.Accept.Add(
                new MediaTypeWithQualityHeaderValue("application/json"));
        }

        // Todas las incidencias (panel del admin).
        public async Task<List<Incidencia>> ListarAsync()
            => await _http.GetFromJsonAsync<List<Incidencia>>(Endpoint) ?? new();

        // Un usuario abre un ticket de soporte.
        public async Task AgregarAsync(int usuarioId, string asunto, string? descripcion, string prioridad)
        {
            var body = new { usuarioId, asunto, descripcion, prioridad };
            (await _http.PostAsJsonAsync(Endpoint, body)).EnsureSuccessStatusCode();
        }

        // El admin cambia el estado (ABIERTO / EN_PROCESO / RESUELTO / CERRADO).
        public async Task CambiarEstadoAsync(int idIncidencia, string estado)
            => (await _http.PutAsJsonAsync($"{Endpoint}/{idIncidencia}/estado", new { estado })).EnsureSuccessStatusCode();
    }
}
