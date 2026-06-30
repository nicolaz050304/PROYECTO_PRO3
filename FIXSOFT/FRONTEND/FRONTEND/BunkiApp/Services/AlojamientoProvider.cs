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
                var lista = await _rest.ListarAsync() ?? new();
                // Vacío = catálogo realmente sin alojamientos (resultado válido): NO mostramos los
                // de demostración. El mock es solo para cuando el REST CAE (catch), no para "0 resultados".
                foreach (var a in lista) AplicarDefaultsVisuales(a);
                return lista;
            }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "REST no disponible al listar; usando mock");
                return _mock.ObtenerAlojamientos();              // MÉTODO REAL de DataService
            }
        }

        // Disponibles por fecha: el backend excluye los alojamientos con reserva activa solapada.
        // OJO: aquí una lista VACÍA es un resultado VÁLIDO (no hay nada libre en ese rango), así que
        // NO caemos al mock por vacío —solo ante caída del REST— para no mostrar disponibles falsos.
        public async Task<List<Alojamiento>> ListarDisponiblesAsync(DateTime entrada, DateTime salida)
        {
            try
            {
                var lista = await _rest.ListarDisponiblesAsync(entrada, salida);
                foreach (var a in lista) AplicarDefaultsVisuales(a);
                return lista;
            }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "REST no disponible al listar disponibles; usando mock");
                return _mock.ObtenerAlojamientos();
            }
        }

        // Top de alojamientos (RF22): el ranking lo calcula el backend (lógica de negocio allá).
        // Ante caída del REST devolvemos lista vacía (no hay equivalente en el mock para el ranking).
        public async Task<List<Alojamiento>> TopAsync(string criterio, int limite = 5)
        {
            try
            {
                var lista = await _rest.TopAsync(criterio, limite);
                foreach (var a in lista) AplicarDefaultsVisuales(a);
                return lista;
            }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "Error al obtener el top de alojamientos; lista vacía");
                return new();
            }
        }

        public async Task<Alojamiento?> ObtenerPorIdAsync(int id)
        {
            try
            {
                var a = await _rest.ObtenerPorIdAsync(id);
                // null del REST = no existe (resultado válido) -> null, no un alojamiento demo.
                if (a is null) return null;
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
                var lista = await _rest.ListarPorAnfitrionAsync(anfitrionId) ?? new();
                // Una lista VACÍA es un resultado VÁLIDO: el anfitrión simplemente no tiene
                // alojamientos. NO caemos al mock por vacío (antes lo hacía y mostraba los
                // alojamientos DEMO hardcodeados como si fueran suyos -> "cuenta nueva ya tiene
                // alojamientos"). El mock NO distingue por dueño, así que nunca debe representar
                // "los alojamientos de ESTE anfitrión".
                foreach (var a in lista) AplicarDefaultsVisuales(a);
                return lista;
            }
            catch (Exception ex)
            {
                // REST caído: devolvemos vacío, no mock. Mostrar alojamientos ajenos/demo como
                // "los tuyos" sería incorrecto y confuso (mismo criterio que las reservas del usuario).
                _log.LogWarning(ex, "REST no disponible (alojamientos del anfitrión {Id}); lista vacía", anfitrionId);
                return new();
            }
        }

        // Moderación (admin): lista de alojamientos PENDIENTE de aprobación. Ante caída del REST
        // devolvemos lista vacía (no hay equivalente real en el mock para la cola de moderación).
        public async Task<List<Alojamiento>> ListarPendientesAsync()
        {
            try
            {
                var lista = await _rest.ListarPendientesAsync();
                foreach (var a in lista) AplicarDefaultsVisuales(a);
                return lista;
            }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "REST no disponible al listar pendientes de moderación; lista vacía");
                return new();
            }
        }

        // Moderación (admin): aprobar/rechazar. Escritura sin fallback a mock: si el REST falla,
        // propagamos para que la UI avise y NO quite la fila de la cola.
        public async Task ValidarAsync(int id, string estado) => await _rest.ValidarAsync(id, estado);

        // Creación (escritura) vía REST: POST AlojamientoRS. A diferencia de las lecturas,
        // aquí NO hay fallback a mock: si el REST falla, propagamos la excepción para que la
        // UI avise que no se guardó (igual que ResenaProvider.RegistrarAsync).
        public async Task RegistrarAsync(Alojamiento a) => await _rest.RegistrarAsync(a);

        // Eliminación (escritura) vía REST: DELETE AlojamientoRS/{id} -> baja lógica
        // (disponibilidad 0). Sin fallback a mock: si el REST falla, propagamos para
        // que la UI avise y NO quite la card.
        public async Task EliminarAsync(int alojamientoId) => await _rest.EliminarAsync(alojamientoId);

        // Actualización (escritura) vía REST: PUT AlojamientoRS/{id}. Sin fallback a mock:
        // si el REST falla, propagamos para que la UI revierta y avise (p.ej. pausar/activar).
        public async Task ActualizarAsync(Alojamiento a) => await _rest.ActualizarAsync(a);

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
