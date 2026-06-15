using System.Net.Http.Json;
using System.Net.Http.Headers;
using BunkiApp.Models;

namespace BunkiApp.Services
{
    // Cliente REST de Pago contra el backend Java (JAX-RS).
    // NOTA: la TRANSACCIÓN de pago (RegistrarAsync) NO está cableada a la página Pagos;
    // se deja lista para cuando exista el backend. No hay entidad "Pago" en el dominio,
    // se usan ResultadoPago (lectura) y PagoForm (alta) como DTOs hasta validar el contrato.
    public class PagoServiceRest
    {
        private readonly HttpClient _http;
        private const string Endpoint = "PagoRS";

        public PagoServiceRest(HttpClient http)
        {
            _http = http;
            _http.DefaultRequestHeaders.Accept.Clear();
            _http.DefaultRequestHeaders.Accept.Add(
                new MediaTypeWithQualityHeaderValue("application/json"));
        }

        public async Task<List<ResultadoPago>> ListarAsync()
            => await _http.GetFromJsonAsync<List<ResultadoPago>>(Endpoint) ?? new();

        public async Task<ResultadoPago?> ObtenerPorIdAsync(int id)
            => await _http.GetFromJsonAsync<ResultadoPago>($"{Endpoint}/{id}");

        // Escritura (transacción): preparada, NO cableada a la página.
        public async Task<ResultadoPago?> RegistrarAsync(PagoForm form)
        {
            var resp = await _http.PostAsJsonAsync(Endpoint, form);
            resp.EnsureSuccessStatusCode();
            return await resp.Content.ReadFromJsonAsync<ResultadoPago>();
        }

        public async Task EliminarAsync(int id)
            => (await _http.DeleteAsync($"{Endpoint}/{id}")).EnsureSuccessStatusCode();
    }
}
