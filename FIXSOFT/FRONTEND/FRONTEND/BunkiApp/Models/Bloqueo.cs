using System.Text.Json.Serialization;

namespace BunkiApp.Models;

/// <summary>
/// Bloqueo de fechas de un alojamiento por el anfitrión (RF30), tal como lo expone
/// BloqueoFechaRS. El anfitrión marca rangos que no admiten reservas; el backend los
/// devuelve además como "ocupados" para el calendario del huésped.
/// Fechas en ISO "yyyy-MM-dd" (string), el formato que consume flatpickr.
/// </summary>
public class Bloqueo
{
    [JsonPropertyName("id")]
    public int Id { get; set; }

    [JsonPropertyName("idAlojamiento")]
    public int IdAlojamiento { get; set; }

    [JsonPropertyName("fechaInicio")]
    public string FechaInicio { get; set; } = "";

    [JsonPropertyName("fechaFin")]
    public string FechaFin { get; set; } = "";

    [JsonPropertyName("motivo")]
    public string? Motivo { get; set; }

    /// <summary>Rango legible "dd/MM/yyyy - dd/MM/yyyy" para la UI.</summary>
    [JsonIgnore]
    public string RangoTexto
    {
        get
        {
            var i = FormatearVisible(FechaInicio);
            var f = FormatearVisible(FechaFin);
            return i == f ? i : $"{i} - {f}";
        }
    }

    private static string FormatearVisible(string iso)
    {
        return DateTime.TryParse(iso, out var d) ? d.ToString("dd/MM/yyyy") : iso;
    }
}
