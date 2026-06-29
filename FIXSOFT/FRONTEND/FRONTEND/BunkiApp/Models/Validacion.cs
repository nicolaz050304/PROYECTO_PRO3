using System.Text.Json.Serialization;

namespace BunkiApp.Models;

/// <summary>
/// Documento de validación de identidad (RF02). Lo sube el usuario y lo revisa el admin.
/// Estado: PENDIENTE / APROBADO / RECHAZADO. archivoBase64 es el data URL del DNI/pasaporte.
/// </summary>
public class DocumentoValidacion
{
    [JsonPropertyName("id")] public int Id { get; set; }
    [JsonPropertyName("usuarioId")] public int UsuarioId { get; set; }
    [JsonPropertyName("usuarioNombre")] public string? UsuarioNombre { get; set; }
    [JsonPropertyName("usuarioCorreo")] public string? UsuarioCorreo { get; set; }
    [JsonPropertyName("tipoDocumento")] public string TipoDocumento { get; set; } = "";
    [JsonPropertyName("numeroDocumento")] public string NumeroDocumento { get; set; } = "";
    [JsonPropertyName("archivoBase64")] public string? ArchivoBase64 { get; set; }
    [JsonPropertyName("estado")] public string Estado { get; set; } = "PENDIENTE";
    [JsonPropertyName("motivoRechazo")] public string? MotivoRechazo { get; set; }
    [JsonPropertyName("fechaSubida")] public string? FechaSubida { get; set; }
    [JsonPropertyName("fechaRevision")] public string? FechaRevision { get; set; }

    [JsonIgnore]
    public string EstadoTexto => Estado switch
    {
        "APROBADO" => "Aprobado",
        "RECHAZADO" => "Rechazado",
        _ => "Pendiente"
    };
}
