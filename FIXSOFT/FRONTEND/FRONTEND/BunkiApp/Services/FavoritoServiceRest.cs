using System.Net.Http.Json;
using System.Net.Http.Headers;

namespace BunkiApp.Services
{
    // Cliente REST de Favoritos (RF27) contra el backend Java.
    public class FavoritoServiceRest
    {
        private readonly HttpClient _http;
        private const string Endpoint = "FavoritoRS";

        public FavoritoServiceRest(HttpClient http)
        {
            _http = http;
            _http.DefaultRequestHeaders.Accept.Clear();
            _http.DefaultRequestHeaders.Accept.Add(
                new MediaTypeWithQualityHeaderValue("application/json"));
        }

        // Ids de alojamientos favoritos del usuario.
        public async Task<List<int>> ListarAsync(int idUsuario)
            => await _http.GetFromJsonAsync<List<int>>($"{Endpoint}/usuario/{idUsuario}") ?? new();

        // Marca un favorito.
        public async Task AgregarAsync(int idUsuario, int idAlojamiento)
            => (await _http.PostAsJsonAsync(Endpoint, new { idUsuario, idAlojamiento })).EnsureSuccessStatusCode();

        // Quita un favorito.
        public async Task QuitarAsync(int idUsuario, int idAlojamiento)
            => (await _http.DeleteAsync($"{Endpoint}/usuario/{idUsuario}/alojamiento/{idAlojamiento}")).EnsureSuccessStatusCode();
    }
}
