using System.Text.Json.Serialization;

namespace BunkiApp.Models;

/// <summary>
/// Denuncia de un huésped contra un alojamiento (RF31), tal como la expone DenunciaRS.
/// El admin la gestiona cambiando su estado.
/// </summary>
public class Denuncia
{
    [JsonPropertyName("id")] public int Id { get; set; }
    [JsonPropertyName("denuncianteId")] public int DenuncianteId { get; set; }
    [JsonPropertyName("denuncianteNombre")] public string? DenuncianteNombre { get; set; }
    [JsonPropertyName("alojamientoId")] public int AlojamientoId { get; set; }
    [JsonPropertyName("alojamientoNombre")] public string? AlojamientoNombre { get; set; }
    [JsonPropertyName("anfitrionId")] public int AnfitrionId { get; set; }
    [JsonPropertyName("anfitrionNombre")] public string? AnfitrionNombre { get; set; }
    [JsonPropertyName("motivo")] public string Motivo { get; set; } = "";
    [JsonPropertyName("descripcion")] public string? Descripcion { get; set; }
    [JsonPropertyName("estado")] public string Estado { get; set; } = "EN_REVISION";
    [JsonPropertyName("fecha")] public string? Fecha { get; set; }

    /// <summary>Estado en texto legible para la UI.</summary>
    [JsonIgnore]
    public string EstadoTexto => Estado switch
    {
        "EN_REVISION" => "En revisión",
        "RESUELTO" => "Resuelto",
        "CERRADO" => "Cerrado",
        _ => Estado
    };
}
