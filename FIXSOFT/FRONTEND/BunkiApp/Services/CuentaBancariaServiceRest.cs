using System.Net.Http.Json;
using System.Net.Http.Headers;
using BunkiApp.Models;

namespace BunkiApp.Services
{
    // Cliente REST de Cuentas Bancarias del anfitrión (RF15) contra el backend Java (JAX-RS).
    // Clona el patrón de UsuarioServiceRest: HttpClient inyectado, Endpoint const, JSON.
    public class CuentaBancariaServiceRest
    {
        private readonly HttpClient _http;
        private const string Endpoint = "CuentaBancariaRS";

        public CuentaBancariaServiceRest(HttpClient http)
        {
            _http = http;
            _http.DefaultRequestHeaders.Accept.Clear();
            _http.DefaultRequestHeaders.Accept.Add(
                new MediaTypeWithQualityHeaderValue("application/json"));
        }

        // Cuentas del usuario logueado: cada anfitrión ve SOLO las suyas (filtrado por id en el backend).
        public async Task<List<CuentaBancaria>> ListarPorUsuarioAsync(int idUsuario)
            => await _http.GetFromJsonAsync<List<CuentaBancaria>>($"{Endpoint}/usuario/{idUsuario}") ?? new();

        // Registra una cuenta nueva. El backend pone saldo=0 y verificada=false por defecto.
        // Devuelve true solo si respondió 2xx, para que la UI distinga éxito de fallo (400 con error).
        public async Task<bool> RegistrarAsync(CuentaBancaria c)
        {
            var resp = await _http.PostAsJsonAsync(Endpoint, c);
            return resp.IsSuccessStatusCode;
        }
    }
}
