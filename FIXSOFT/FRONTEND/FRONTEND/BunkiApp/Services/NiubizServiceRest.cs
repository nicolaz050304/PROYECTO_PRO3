using System.Net.Http.Json;
using System.Net.Http.Headers;
using System.Text.Json.Serialization;

namespace BunkiApp.Services
{
    // Cliente REST del pago con Niubiz (sandbox) contra el backend Java.
    public class NiubizServiceRest
    {
        private readonly HttpClient _http;
        private const string Endpoint = "NiubizRS";

        public NiubizServiceRest(HttpClient http)
        {
            _http = http;
            _http.DefaultRequestHeaders.Accept.Clear();
            _http.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));
        }

        // Paso 1-2: crea la sesión de pago (devuelve lo que necesita el lightbox).
        public async Task<NiubizSesion?> CrearSesionAsync(decimal amount, string? clientIp = null)
        {
            var resp = await _http.PostAsJsonAsync($"{Endpoint}/sesion", new { amount, clientIp });
            if (!resp.IsSuccessStatusCode) return null;
            return await resp.Content.ReadFromJsonAsync<NiubizSesion>();
        }

        // Paso 4: autoriza la transacción con el token devuelto por el lightbox.
        // Devuelve el resultado completo (aprobado + detalle) para poder mostrar el motivo del rechazo.
        public async Task<NiubizAutorizacion> AutorizarAsync(string transactionToken, string purchaseNumber, decimal amount)
        {
            var resp = await _http.PostAsJsonAsync($"{Endpoint}/autorizar",
                new { transactionToken, purchaseNumber, amount });
            if (!resp.IsSuccessStatusCode)
                return new NiubizAutorizacion { Aprobado = false, Detalle = "Error de comunicación con la pasarela." };
            return await resp.Content.ReadFromJsonAsync<NiubizAutorizacion>()
                   ?? new NiubizAutorizacion { Aprobado = false };
        }
    }

    public class NiubizSesion
    {
        [JsonPropertyName("merchantId")] public string MerchantId { get; set; } = "";
        [JsonPropertyName("sessionKey")] public string SessionKey { get; set; } = "";
        [JsonPropertyName("purchaseNumber")] public string PurchaseNumber { get; set; } = "";
        [JsonPropertyName("amount")] public decimal Amount { get; set; }
    }

    public class NiubizAutorizacion
    {
        [JsonPropertyName("aprobado")] public bool Aprobado { get; set; }
        [JsonPropertyName("detalle")] public string? Detalle { get; set; }
    }
}
