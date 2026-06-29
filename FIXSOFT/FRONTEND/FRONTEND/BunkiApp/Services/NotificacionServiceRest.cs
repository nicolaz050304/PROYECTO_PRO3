using System.Net.Http.Json;
using System.Net.Http.Headers;
using BunkiApp.Models;

namespace BunkiApp.Services
{
    // Cliente REST de Notificaciones (RF18) contra el backend Java (JAX-RS).
    // Las notificaciones son POR USUARIO: se piden/marcan con el id del usuario o de la notificación.
    public class NotificacionServiceRest
    {
        private readonly HttpClient _http;
        private const string Endpoint = "NotificacionRS";

        public NotificacionServiceRest(HttpClient http)
        {
            _http = http;
            _http.DefaultRequestHeaders.Accept.Clear();
            _http.DefaultRequestHeaders.Accept.Add(
                new MediaTypeWithQualityHeaderValue("application/json"));
        }

        // Notificaciones del usuario (recientes primero, orden del backend).
        public async Task<List<NotificacionDto>> ListarPorUsuarioAsync(int idUsuario)
            => await _http.GetFromJsonAsync<List<NotificacionDto>>($"{Endpoint}/usuario/{idUsuario}") ?? new();

        // Nº de notificaciones no leídas del usuario (alimenta el badge de la campanita).
        // Ante fallo, 0 para no romper la barra de navegación.
        public async Task<int> ContarNoLeidasAsync(int idUsuario)
        {
            try
            {
                var r = await _http.GetFromJsonAsync<NoLeidasDto>($"{Endpoint}/usuario/{idUsuario}/noleidas");
                return r?.NoLeidas ?? 0;
            }
            catch { return 0; }
        }

        // Marca una notificación como leída en BD (al abrirla). Devuelve true solo si respondió 2xx,
        // para que la UI sepa si realmente persistió (no oculta el fallo en silencio).
        public async Task<bool> MarcarLeidaAsync(int idNotificacion)
        {
            try
            {
                var resp = await _http.PutAsync($"{Endpoint}/{idNotificacion}/leida", null);
                return resp.IsSuccessStatusCode;
            }
            catch { return false; }
        }
    }
}
