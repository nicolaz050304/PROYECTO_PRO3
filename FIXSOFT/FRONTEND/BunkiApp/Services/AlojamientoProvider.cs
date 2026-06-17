using BunkiApp.Models;
using Microsoft.Extensions.Logging;

namespace BunkiApp.Services
{
    // Decide la fuente de datos de Alojamiento: intenta REST y, si falla, usa el mock (DataService).
    public class AlojamientoProvider
    {
        private readonly AlojamientoServiceRest _rest;
        private readonly DataService _mock;
        private readonly ILogger<AlojamientoProvider> _log;

        public AlojamientoProvider(AlojamientoServiceRest rest, DataService mock,
                                   ILogger<AlojamientoProvider> log)
        {
            _rest = rest;
            _mock = mock;
            _log = log;
        }

        public async Task<List<Alojamiento>> ListarAsync()
        {
            try
            {
                var lista = await _rest.ListarAsync();
                if (lista is null || lista.Count == 0)
                    return _mock.ObtenerAlojamientos();          // MÉTODO REAL de DataService
                foreach (var a in lista) AplicarDefaultsVisuales(a);
                return lista;
            }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "REST no disponible al listar; usando mock");
                return _mock.ObtenerAlojamientos();              // MÉTODO REAL de DataService
            }
        }

        public async Task<Alojamiento?> ObtenerPorIdAsync(int id)
        {
            try
            {
                var a = await _rest.ObtenerPorIdAsync(id);
                if (a is null)
                    return _mock.ObtenerAlojamiento(id);         // MÉTODO REAL de DataService
                AplicarDefaultsVisuales(a);
                return a;
            }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "REST no disponible al obtener {Id}; usando mock", id);
                return _mock.ObtenerAlojamiento(id);             // MÉTODO REAL de DataService
            }
        }

        public async Task<List<Alojamiento>> ListarPorAnfitrionAsync(int anfitrionId)
        {
            try
            {
                var lista = await _rest.ListarPorAnfitrionAsync(anfitrionId);
                if (lista is null || lista.Count == 0)
                    return _mock.ObtenerAlojamientosAnfitrion();   // MÉTODO REAL de DataService
                foreach (var a in lista) AplicarDefaultsVisuales(a);
                return lista;
            }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "REST no disponible (alojamientos del anfitrión {Id}); usando mock", anfitrionId);
                return _mock.ObtenerAlojamientosAnfitrion();       // MÉTODO REAL de DataService
            }
        }

        // Creación (escritura) vía REST: POST AlojamientoRS. A diferencia de las lecturas,
        // aquí NO hay fallback a mock: si el REST falla, propagamos la excepción para que la
        // UI avise que no se guardó (igual que ResenaProvider.RegistrarAsync).
        public async Task RegistrarAsync(Alojamiento a) => await _rest.RegistrarAsync(a);

        // Eliminación (escritura) vía REST: DELETE AlojamientoRS/{id} -> baja lógica
        // (disponibilidad 0). Sin fallback a mock: si el REST falla, propagamos para
        // que la UI avise y NO quite la card.
        public async Task EliminarAsync(int alojamientoId) => await _rest.EliminarAsync(alojamientoId);

        // El backend no envía datos visuales; los rellenamos para no romper el theme dark luxury.
        private static void AplicarDefaultsVisuales(Alojamiento a)
        {
            a.GradienteColor = Gradientes[Math.Abs(a.Id) % Gradientes.Length];

            if (string.IsNullOrWhiteSpace(a.AnfitrionInicial) &&
                !string.IsNullOrWhiteSpace(a.AnfitrionNombre))
                a.AnfitrionInicial = char.ToUpper(a.AnfitrionNombre[0]).ToString();
        }

        private static readonly string[] Gradientes =
        {
            "linear-gradient(135deg, #2b2b2e, #1a1a1d)",
            "linear-gradient(135deg, #3a2f2a, #1f1a17)",
            "linear-gradient(135deg, #2a2f3a, #16191f)"
        };
    }
}
