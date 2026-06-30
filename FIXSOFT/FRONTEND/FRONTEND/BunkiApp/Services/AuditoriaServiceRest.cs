using System.Net.Http.Json;
using System.Net.Http.Headers;
using BunkiApp.Models;

namespace BunkiApp.Services
{
    // Cliente REST de la bitácora de auditoría (RNF09). Solo lectura. El backend (AuthFilter) exige
    // rol ADMINISTRADOR, así que cada llamada adjunta la identidad del usuario logueado (igual que
    // ValidacionServiceRest). El navegador nunca llama directo al backend: la cabecera la pone Blazor.
    public class AuditoriaServiceRest
    {
        private readonly HttpClient _http;
        private readonly AppState _appState;
        private const string Endpoint = "AuditoriaRS";

        public AuditoriaServiceRest(HttpClient http, AppState appState)
        {
            _http = http;
            _appState = appState;
            _http.DefaultRequestHeaders.Accept.Clear();
            _http.DefaultRequestHeaders.Accept.Add(
                new MediaTypeWithQualityHeaderValue("application/json"));
        }

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

        // Últimos N registros (vista global). Tolera 401/403 -> lista vacía.
        public async Task<List<AuditoriaRegistro>> ListarRecientesAsync(int limite = 200)
        {
            AplicarIdentidad();
            var resp = await _http.GetAsync($"{Endpoint}?limite={limite}");
            if (!resp.IsSuccessStatusCode) return new();
            return await resp.Content.ReadFromJsonAsync<List<AuditoriaRegistro>>() ?? new();
        }

        // Historial cronológico de una reserva concreta.
        public async Task<List<AuditoriaRegistro>> HistorialReservaAsync(int idReserva)
        {
            AplicarIdentidad();
            var resp = await _http.GetAsync($"{Endpoint}/reserva/{idReserva}");
            if (!resp.IsSuccessStatusCode) return new();
            return await resp.Content.ReadFromJsonAsync<List<AuditoriaRegistro>>() ?? new();
        }

        // Historial cronológico de un perfil de usuario concreto.
        public async Task<List<AuditoriaRegistro>> HistorialUsuarioAsync(int idUsuario)
        {
            AplicarIdentidad();
            var resp = await _http.GetAsync($"{Endpoint}/usuario/{idUsuario}");
            if (!resp.IsSuccessStatusCode) return new();
            return await resp.Content.ReadFromJsonAsync<List<AuditoriaRegistro>>() ?? new();
        }
    }
}
