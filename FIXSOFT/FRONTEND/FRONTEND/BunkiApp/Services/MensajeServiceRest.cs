using System.Net.Http.Json;
using System.Net.Http.Headers;
using BunkiApp.Models;

namespace BunkiApp.Services
{
    // Cliente REST de Mensajería (RF17) contra el backend Java (JAX-RS).
    // El chat se organiza POR RESERVA: los mensajes se piden/envían con el id de la reserva.
    public class MensajeServiceRest
    {
        private readonly HttpClient _http;
        private const string Endpoint = "MensajeRS";

        public MensajeServiceRest(HttpClient http)
        {
            _http = http;
            _http.DefaultRequestHeaders.Accept.Clear();
            _http.DefaultRequestHeaders.Accept.Add(
                new MediaTypeWithQualityHeaderValue("application/json"));
        }

        // Mensajes del chat de una reserva (orden cronológico lo da el backend).
        public async Task<List<MensajeDto>> ListarPorReservaAsync(int idReserva)
            => await _http.GetFromJsonAsync<List<MensajeDto>>($"{Endpoint}/reserva/{idReserva}") ?? new();

        // Envía un mensaje al chat de una reserva. El backend pone idMensaje y fechaEnvio.
        // Devuelve true solo si respondió 2xx (la BL valida no vacío / máx 500 chars).
        public async Task<bool> EnviarAsync(int idReserva, int idEmisor, string texto)
        {
            var dto = new { texto, idEmisor, idReserva };
            var resp = await _http.PostAsJsonAsync(Endpoint, dto);
            return resp.IsSuccessStatusCode;
        }

        // Cuenta los mensajes no leídos del usuario en una reserva (los que le enviaron y no abrió):
        // alimenta el badge tipo WhatsApp. Ante fallo, 0 para no romper la lista de conversaciones.
        public async Task<int> ContarNoLeidosAsync(int idReserva, int idUsuario)
        {
            try
            {
                var r = await _http.GetFromJsonAsync<NoLeidosDto>(
                    $"{Endpoint}/reserva/{idReserva}/noleidos/{idUsuario}");
                return r?.NoLeidos ?? 0;
            }
            catch { return 0; }
        }

        // Marca como leídos los mensajes que le enviaron al usuario en esa reserva (al abrir el chat).
        // El estado persiste en BD; ante fallo no propaga para no romper la apertura del chat.
        public async Task MarcarLeidosAsync(int idReserva, int idUsuario)
        {
            try { await _http.PutAsync($"{Endpoint}/reserva/{idReserva}/leidos/{idUsuario}", null); }
            catch { }
        }
    }
}
