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

        // RF15: depósito/retiro. Devuelve (ok, error). El backend valida monto>0 y saldo suficiente.
        public Task<(bool ok, string? error)> DepositarAsync(int idCuenta, double monto, string? descripcion)
            => OperarAsync($"{Endpoint}/{idCuenta}/deposito", monto, descripcion);

        public Task<(bool ok, string? error)> RetirarAsync(int idCuenta, double monto, string? descripcion)
            => OperarAsync($"{Endpoint}/{idCuenta}/retiro", monto, descripcion);

        // RF15: estado de cuenta (historial de movimientos).
        public async Task<List<MovimientoCuenta>> ListarMovimientosAsync(int idCuenta)
            => await _http.GetFromJsonAsync<List<MovimientoCuenta>>($"{Endpoint}/{idCuenta}/movimientos") ?? new();

        // RF15: marca una cuenta como la de cobro principal (el abono automático irá a ella).
        public async Task<bool> HacerPrincipalAsync(int idCuenta)
        {
            try
            {
                var resp = await _http.PutAsync($"{Endpoint}/{idCuenta}/principal", null);
                return resp.IsSuccessStatusCode;
            }
            catch { return false; }
        }

        private async Task<(bool ok, string? error)> OperarAsync(string url, double monto, string? descripcion)
        {
            var resp = await _http.PostAsJsonAsync(url, new { monto, descripcion });
            if (resp.IsSuccessStatusCode) return (true, null);
            string? msg = null;
            try
            {
                var doc = await resp.Content.ReadFromJsonAsync<Dictionary<string, string>>();
                if (doc != null && doc.TryGetValue("error", out var e)) msg = e;
            }
            catch { /* respuesta sin JSON de error: la UI usa un mensaje genérico */ }
            return (false, msg);
        }
    }
}
