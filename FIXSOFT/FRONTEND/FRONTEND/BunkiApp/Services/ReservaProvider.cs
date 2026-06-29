using BunkiApp.Models;
using Microsoft.Extensions.Logging;

namespace BunkiApp.Services
{
    // Decide la fuente de datos de Reserva: intenta REST y, si falla o viene vacío, usa el mock.
    // El backend expone una sola colección; las vistas (próximas/pasadas/canceladas) se derivan
    // por Estado, igual que las separa hoy DataService.
    public class ReservaProvider
    {
        private readonly ReservaServiceRest _rest;
        private readonly DataService _mock;
        private readonly ILogger<ReservaProvider> _log;

        public ReservaProvider(ReservaServiceRest rest, DataService mock,
                               ILogger<ReservaProvider> log)
        {
            _rest = rest;
            _mock = mock;
            _log = log;
        }

        public async Task<List<Reserva>> ListarTodasAsync()
        {
            try
            {
                var lista = await _rest.ListarAsync();
                return (lista is null || lista.Count == 0)
                    ? _mock.ObtenerTodasLasReservas()             // MÉTODO REAL de DataService
                    : lista;
            }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "REST no disponible al listar reservas; usando mock");
                return _mock.ObtenerTodasLasReservas();
            }
        }

        public async Task<List<Reserva>> ListarProximasAsync()
        {
            try
            {
                var lista = await _rest.ListarAsync();
                if (lista is null || lista.Count == 0)
                    return _mock.ObtenerReservasProximas();       // MÉTODO REAL
                return lista.Where(r => r.Estado is "Confirmada" or "Pendiente").ToList();
            }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "REST no disponible (próximas); usando mock");
                return _mock.ObtenerReservasProximas();
            }
        }

        public async Task<List<Reserva>> ListarPasadasAsync()
        {
            try
            {
                var lista = await _rest.ListarAsync();
                if (lista is null || lista.Count == 0)
                    return _mock.ObtenerReservasPasadas();        // MÉTODO REAL
                return lista.Where(r => r.Estado == "Completada").ToList();
            }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "REST no disponible (pasadas); usando mock");
                return _mock.ObtenerReservasPasadas();
            }
        }

        public async Task<List<Reserva>> ListarCanceladasAsync()
        {
            try
            {
                var lista = await _rest.ListarAsync();
                if (lista is null || lista.Count == 0)
                    return _mock.ObtenerReservasCanceladas();     // MÉTODO REAL
                return lista.Where(r => r.Estado == "Cancelada").ToList();
            }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "REST no disponible (canceladas); usando mock");
                return _mock.ObtenerReservasCanceladas();
            }
        }

        public async Task<Reserva?> ObtenerPorIdAsync(int id)
        {
            try
            {
                return await _rest.ObtenerPorIdAsync(id)
                       ?? _mock.ObtenerTodasLasReservas().FirstOrDefault(r => r.Id == id);
            }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "REST no disponible al obtener reserva {Id}; usando mock", id);
                return _mock.ObtenerTodasLasReservas().FirstOrDefault(r => r.Id == id);
            }
        }

        // Reservas del INVITADO logueado (el backend filtra por HuespedId en ReservaRS/usuario/{id}).
        // OJO: una lista vacía es un resultado VÁLIDO (ese invitado no tiene reservas), así que
        // NO caemos al mock por vacío —solo ante caída del REST—.
        public async Task<List<Reserva>> ListarPorUsuarioAsync(int usuarioId)
        {
            try
            {
                return await _rest.ListarPorUsuarioAsync(usuarioId) ?? new();
            }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "REST no disponible (reservas del usuario {Id}); devolviendo lista vacía", usuarioId);
                // SEGURIDAD/PRIVACIDAD: NO caer a "todas las reservas" (el mock no distingue por usuario y
                // expondría reservas ajenas). Mejor lista vacía: el usuario ve "sin reservas" en vez de datos de otros.
                return new();
            }
        }

        // Reservas recibidas por el ANFITRIÓN logueado (ReservaRS/anfitrion/{id}). Mismo criterio.
        public async Task<List<Reserva>> ListarPorAnfitrionAsync(int anfitrionId)
        {
            try
            {
                return await _rest.ListarPorAnfitrionAsync(anfitrionId) ?? new();
            }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "REST no disponible (reservas del anfitrión {Id}); devolviendo lista vacía", anfitrionId);
                // SEGURIDAD/PRIVACIDAD: mismo motivo — no exponer reservas de otros anfitriones ante fallo del REST.
                return new();
            }
        }

        // Fechas OCUPADAS (CONFIRMADA + PENDIENTE) de un alojamiento, para bloquearlas en el
        // calendario y que el huésped solo elija días libres. Si el REST falla, devolvemos lista
        // vacía: el calendario simplemente no bloquea nada y la reserva NO se rompe.
        public async Task<List<RangoFecha>> ObtenerFechasOcupadasAsync(int idAlojamiento)
        {
            try
            {
                return await _rest.ListarOcupadasPorAlojamientoAsync(idAlojamiento) ?? new();
            }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "REST no disponible (fechas ocupadas del alojamiento {Id}); sin bloqueo de fechas", idAlojamiento);
                return new();
            }
        }

        // Registro (ESCRITURA): POST ReservaRS. Sin fallback: propaga para que la UI avise.
        // Devuelve el id de la reserva creada (necesario para registrar luego su pago).
        public async Task<int> RegistrarAsync(Reserva reserva)
        {
            try { return await _rest.RegistrarAsync(reserva); }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "No se pudo registrar la reserva (alojamiento {Id})", reserva.AlojamientoId);
                throw;
            }
        }

        // Actualización (ESCRITURA): PUT ReservaRS/{id}. Sin fallback: propaga.
        public async Task ActualizarAsync(Reserva reserva)
        {
            try { await _rest.ActualizarAsync(reserva); }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "No se pudo actualizar la reserva {Id}", reserva.Id);
                throw;
            }
        }

        // Cancelación (ESCRITURA): DELETE ReservaRS/{id} -> baja lógica (estado CANCELADA).
        // Sin fallback a mock: propaga para que la UI avise si no se pudo cancelar.
        public async Task CancelarAsync(int reservaId)
        {
            try { await _rest.EliminarAsync(reservaId); }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "No se pudo cancelar la reserva {Id}", reservaId);
                throw;
            }
        }
    }
}
