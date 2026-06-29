using System.Text.Json.Serialization;

namespace BunkiApp.Models;

/// <summary>
/// Incidencia / ticket de soporte (RF28) expuesto por IncidenciaRS. Un usuario la abre; el admin
/// la gestiona cambiando su estado.
/// </summary>
public class Incidencia
{
    [JsonPropertyName("id")] public int Id { get; set; }
    [JsonPropertyName("usuarioId")] public int UsuarioId { get; set; }
    [JsonPropertyName("usuarioNombre")] public string? UsuarioNombre { get; set; }
    [JsonPropertyName("usuarioCorreo")] public string? UsuarioCorreo { get; set; }
    [JsonPropertyName("asunto")] public string Asunto { get; set; } = "";
    [JsonPropertyName("descripcion")] public string? Descripcion { get; set; }
    [JsonPropertyName("prioridad")] public string Prioridad { get; set; } = "MEDIA";
    [JsonPropertyName("estado")] public string Estado { get; set; } = "ABIERTO";
    [JsonPropertyName("fecha")] public string? Fecha { get; set; }

    [JsonIgnore]
    public string EstadoTexto => Estado switch
    {
        "ABIERTO" => "Abierto",
        "EN_PROCESO" => "En proceso",
        "RESUELTO" => "Resuelto",
        "CERRADO" => "Cerrado",
        _ => Estado
    };

    [JsonIgnore]
    public string PrioridadTexto => Prioridad switch
    {
        "ALTA" => "Alta",
        "MEDIA" => "Media",
        "BAJA" => "Baja",
        _ => Prioridad
    };

    /// <summary>Identificador legible tipo "INC001".</summary>
    [JsonIgnore]
    public string Codigo => $"INC{Id:D3}";
}
