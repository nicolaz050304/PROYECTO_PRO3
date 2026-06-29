using System.Net.Http.Json;
using System.Net.Http.Headers;
using BunkiApp.Models;

namespace BunkiApp.Services
{
    // Cliente REST de bloqueos de fecha del anfitrión (RF30) contra el backend Java.
    public class BloqueoServiceRest
    {
        private readonly HttpClient _http;
        private const string Endpoint = "BloqueoFechaRS";

        public BloqueoServiceRest(HttpClient http)
        {
            _http = http;
            _http.DefaultRequestHeaders.Accept.Clear();
            _http.DefaultRequestHeaders.Accept.Add(
                new MediaTypeWithQualityHeaderValue("application/json"));
        }

        // Bloqueos de un alojamiento.
        public async Task<List<Bloqueo>> ListarAsync(int idAlojamiento)
            => await _http.GetFromJsonAsync<List<Bloqueo>>($"{Endpoint}/alojamiento/{idAlojamiento}") ?? new();

        // Crea un bloqueo (fechas en ISO "yyyy-MM-dd"); devuelve el bloqueo creado con su id.
        public async Task<Bloqueo?> AgregarAsync(int idAlojamiento, string fechaInicio, string fechaFin, string? motivo)
        {
            var body = new { idAlojamiento, fechaInicio, fechaFin, motivo };
            var resp = await _http.PostAsJsonAsync(Endpoint, body);
            resp.EnsureSuccessStatusCode();
            return await resp.Content.ReadFromJsonAsync<Bloqueo>();
        }

        // Elimina un bloqueo por id.
        public async Task EliminarAsync(int idBloqueo)
            => (await _http.DeleteAsync($"{Endpoint}/{idBloqueo}")).EnsureSuccessStatusCode();
    }
}
