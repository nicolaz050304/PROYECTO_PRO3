using System.Net.Http.Json;
using System.Net.Http.Headers;
using BunkiApp.Models;

namespace BunkiApp.Services
{
    // Cliente REST de Tipo de Cambio contra el backend Java (JAX-RS).
    // Clona el patrón de AlojamientoServiceRest: HttpClient inyectado, Accept JSON, GetFromJsonAsync.
    public class TipoCambioServiceRest
    {
        private readonly HttpClient _http;
        private const string Endpoint = "TipoCambioRS";

        public TipoCambioServiceRest(HttpClient http)
        {
            _http = http;
            _http.DefaultRequestHeaders.Accept.Clear();
            _http.DefaultRequestHeaders.Accept.Add(
                new MediaTypeWithQualityHeaderValue("application/json"));
        }

        // Devuelve las tasas tal cual las da el backend (TasaAPen sin invertir).
        // La inversión a "unidades por PEN" la hace AppState al consumir esta lista.
        public async Task<List<TipoCambioDto>> ListarAsync()
            => await _http.GetFromJsonAsync<List<TipoCambioDto>>(Endpoint) ?? new();

        // Actualiza la tasa/símbolo de una moneda. El backend espera el código en la URL
        // (PUT TipoCambioRS/{codigo}) y el DTO en el body. Devuelve true solo si el PUT
        // respondió 2xx, para que la UI pueda distinguir éxito de fallo sin afirmar de más.
        public async Task<bool> ActualizarAsync(TipoCambioDto dto)
        {
            var resp = await _http.PutAsJsonAsync($"{Endpoint}/{dto.Codigo}", dto);
            return resp.IsSuccessStatusCode;
        }
    }
}
