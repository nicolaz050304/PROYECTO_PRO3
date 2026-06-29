using System.Net;
using System.Net.Http.Json;
using System.Net.Http.Headers;
using BunkiApp.Models;

namespace BunkiApp.Services
{
    // Cliente REST de la validación documentaria (RF02) contra el backend Java.
    public class ValidacionServiceRest
    {
        private readonly HttpClient _http;
        private readonly AppState _appState;
        private const string Endpoint = "ValidacionRS";

        public ValidacionServiceRest(HttpClient http, AppState appState)
        {
            _http = http;
            _appState = appState;
            _http.DefaultRequestHeaders.Accept.Clear();
            _http.DefaultRequestHeaders.Accept.Add(
                new MediaTypeWithQualityHeaderValue("application/json"));
        }

        // Adjunta la identidad del usuario logueado: el backend (AuthFilter) exige ADMINISTRADOR
        // para aprobar/rechazar identidades. La pone Blazor (el navegador no llama directo al backend).
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

        // Documento/estado del usuario (para "Mis documentos"). 204 -> aún no subió nada -> null.
        public async Task<DocumentoValidacion?> ObtenerDeUsuarioAsync(int usuarioId)
        {
            var resp = await _http.GetAsync($"{Endpoint}/usuario/{usuarioId}");
            if (resp.StatusCode == HttpStatusCode.NoContent || !resp.IsSuccessStatusCode) return null;
            return await resp.Content.ReadFromJsonAsync<DocumentoValidacion>();
        }

        // Cola de revisión del admin: documentos PENDIENTE con datos del usuario.
        public async Task<List<DocumentoValidacion>> ListarPendientesAsync()
            => await _http.GetFromJsonAsync<List<DocumentoValidacion>>($"{Endpoint}/pendientes") ?? new();

        // El usuario sube su documento (data URL). Devuelve (ok, mensaje de error del backend).
        public async Task<(bool ok, string? error)> SubirAsync(
            int usuarioId, string tipoDocumento, string numeroDocumento, string archivoBase64)
        {
            var body = new { usuarioId, tipoDocumento, numeroDocumento, archivoBase64 };
            var resp = await _http.PostAsJsonAsync(Endpoint, body);
            if (resp.IsSuccessStatusCode) return (true, null);
            return (false, await LeerErrorAsync(resp));
        }

        // El admin aprueba o rechaza. estado = "APROBADO" / "RECHAZADO"; motivo solo en rechazo.
        public async Task<(bool ok, string? error)> DecidirAsync(
            int idDocumento, string estado, string? motivoRechazo, int adminId)
        {
            AplicarIdentidad();
            var body = new { estado, motivoRechazo, adminId };
            var resp = await _http.PutAsJsonAsync($"{Endpoint}/{idDocumento}/decision", body);
            if (resp.IsSuccessStatusCode) return (true, null);
            return (false, await LeerErrorAsync(resp));
        }

        private static async Task<string?> LeerErrorAsync(HttpResponseMessage resp)
        {
            try
            {
                var doc = await resp.Content.ReadFromJsonAsync<Dictionary<string, string>>();
                if (doc != null && doc.TryGetValue("error", out var e)) return e;
            }
            catch { /* respuesta sin JSON de error: la UI usa un mensaje genérico */ }
            return null;
        }
    }
}
