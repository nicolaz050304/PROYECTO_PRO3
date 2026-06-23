using System.Text.Json.Serialization;

namespace BunkiApp.Models;

/// <summary>
/// Rango de fechas OCUPADO de un alojamiento, tal como lo devuelve
/// GET ReservaRS/ocupadas/{idAlojamiento} (reservas CONFIRMADA + PENDIENTE).
/// Solo trae las fechas (sin datos del huésped): es lo mínimo para bloquear el calendario.
/// Fechas en ISO "yyyy-MM-dd" (string), el formato que consume flatpickr.
/// </summary>
public class RangoFecha
{
    [JsonPropertyName("fechaInicio")]
    public string FechaInicio { get; set; } = "";

    [JsonPropertyName("fechaFin")]
    public string FechaFin { get; set; } = "";
}
