using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

namespace BunkiApp.Models;

public class Alojamiento
{
    public int Id { get; set; }
    public string Nombre { get; set; } = "";
    public string Tipo { get; set; } = "";
    public string Descripcion { get; set; } = "";
    public string Ubicacion { get; set; } = "";
    public string Distrito { get; set; } = "";
    public string Provincia { get; set; } = "Lima";
    public string Pais { get; set; } = "Perú";
    public decimal PrecioNoche { get; set; }
    public decimal TarifaLimpieza { get; set; }
    public int MaxHuespedes { get; set; }
    public int Habitaciones { get; set; }
    public double Rating { get; set; }
    public int TotalResenas { get; set; }
    public string ImagenUrl { get; set; } = string.Empty;
    public string GradienteColor { get; set; } = "linear-gradient(135deg, #FF6B6B, #FFB3B3)";
    public string EstadoPublicacion { get; set; } = "Activo";
    public List<string> Servicios { get; set; } = new();
    public List<Resena> Resenas { get; set; } = new();
    public string AnfitrionNombre { get; set; } = "";
    public string AnfitrionInicial { get; set; } = "";
    public string AnfitrionDesde { get; set; } = "";
    public string PoliticaCancelacion { get; set; } = "Flexible hasta 48 horas antes";
    public string Reglas { get; set; } = "Sin mascotas, sin fiestas";
    public int AnfitrionId { get; set; } = 0;
    public double Latitud { get; set; }
    public double Longitud { get; set; }

    [JsonIgnore]
    public decimal ComisionServicio => PrecioNoche * 0.10m;
}

public class Resena
{
    public int Id { get; set; }
    public string AutorNombre { get; set; } = "";
    public int Estrellas { get; set; }
    public string Comentario { get; set; } = "";
    public DateTime? FechaPublicacion { get; set; }
    public int AlojamientoId { get; set; }
    public string? AlojamientoNombre { get; set; }
    public int ReservaId { get; set; }
    public string TipoAutor { get; set; } = "INVITADO";
    public bool Activo { get; set; } = true;
}

public class Reserva
{
    public int Id { get; set; }
    public string AlojamientoNombre { get; set; } = "";
    public string Ubicacion { get; set; } = "";
    public DateTime FechaEntrada { get; set; }
    public DateTime FechaSalida { get; set; }
    public int NumHuespedes { get; set; }
    public decimal Total { get; set; }
    public string Estado { get; set; } = "Pendiente";
    public string AnfitrionNombre { get; set; } = "";
    public int HuespedId { get; set; } = 0;
    public string HuespedNombre { get; set; } = "";
    public int AlojamientoId { get; set; } = 0;

    [JsonIgnore]
    public int Noches => (FechaSalida - FechaEntrada).Days;
    [JsonIgnore]
    public string FechasTexto => $"{FechaEntrada:dd MMM} - {FechaSalida:dd MMM yyyy}";

    [JsonIgnore]
    public string EstadoBadgeClass => Estado switch
    {
        "Confirmada" => "badge-success",
        "Pendiente"  => "badge-warning",
        "Cancelada"  => "badge-danger",
        "Completada" => "badge-success",
        _            => "badge-primary"
    };
}

public class Usuario
{
    public int Id { get; set; }
    public string Nombre { get; set; } = "";
    // El backend manda "apellidoPaterno"; lo mapeamos al Apellido existente.
    [JsonPropertyName("apellidoPaterno")]
    public string Apellido { get; set; } = "";
    // El backend manda "apellidoMaterno" (lo capturamos por completitud).
    [JsonPropertyName("apellidoMaterno")]
    public string ApellidoMaterno { get; set; } = "";
    // El backend usa "correo"; el resto del front sigue usando .Email sin cambios.
    [JsonPropertyName("correo")]
    public string Email { get; set; } = "";
    // SOLO de salida->entrada en el REGISTRO: la contraseña en claro que se envía al
    // crear la cuenta (el backend la hashea). En las respuestas viaja null y no se usa.
    [JsonPropertyName("password")]
    public string Password { get; set; } = "";
    public string Telefono { get; set; } = "";
    public string Ciudad { get; set; } = "";
    // El backend manda "pais" (la BD lo guarda como país, no ciudad).
    [JsonPropertyName("pais")]
    public string Pais { get; set; } = "";
    public string Bio { get; set; } = "";
    // Rol real del backend: "ADMINISTRADOR" / "ANFITRION" / "INVITADO".
    // (calza por case-insensitive con "tipoUsuario" del JSON)
    public string TipoUsuario { get; set; } = "Usuario";
    // Lista completa de roles que envía el backend.
    public List<string> Roles { get; set; } = new();
    public bool Verificado { get; set; } = true;
    public string MiembroDesde { get; set; } = "Enero 2024";
    public double Rating { get; set; } = 4.9;
    public int TotalResenas { get; set; } = 48;
    public int TotalReservas { get; set; } = 15;
    public int TotalNoches { get; set; } = 62;
    public decimal GastoTotal { get; set; } = 8500;

    [JsonIgnore]
    public string NombreCompleto => $"{Nombre} {Apellido}".Trim();

    // FIX: genera iniciales correctamente incluso con nombre vacío
    [JsonIgnore]
    public string Iniciales
    {
        get
        {
            var n = Nombre.Length > 0 ? Nombre[0].ToString() : "";
            var a = Apellido.Length > 0 ? Apellido[0].ToString() : "";
            return (n + a).ToUpper();
        }
    }
}

public class Mensaje
{
    public int Id { get; set; }
    public string AutorNombre { get; set; } = "";
    public string AutorInicial { get; set; } = "";
    public string Texto { get; set; } = "";
    public string Hora { get; set; } = "";
    public bool EsMio { get; set; } = false;
}

public class Conversacion
{
    public int Id { get; set; }
    public string ContactoNombre { get; set; } = "";
    public string ContactoInicial { get; set; } = "";
    public string UltimoMensaje { get; set; } = "";
    public int MensajesNoLeidos { get; set; } = 0;
    public bool EstaEnLinea { get; set; } = false;
    public string Rol { get; set; } = "Usuario";
    public List<Mensaje> Mensajes { get; set; } = new();
}

// DTO de transporte del chat real (RF17), separado del Mensaje de UI para no chocar con él.
// Refleja el JSON del backend (MensajeRS): el chat va POR RESERVA, por eso lleva IdReserva.
public class MensajeDto
{
    public int IdMensaje { get; set; }
    public string Texto { get; set; } = "";
    public string FechaEnvio { get; set; } = "";   // ya viene formateada "yyyy-MM-dd HH:mm"
    public int IdEmisor { get; set; }
    public string? NombreEmisor { get; set; }       // puede venir null si el emisor es proxy solo-id
    public int IdReserva { get; set; }
}

// Respuesta del conteo de no leídos (RF17): el backend envía {"noLeidos": N}.
// System.Text.Json mapea "noLeidos" -> NoLeidos por defecto (case-insensitive).
public class NoLeidosDto
{
    public int NoLeidos { get; set; }
}

// DTO de transporte de Notificación real (RF18), separado del modelo de UI Notificacion.
// Refleja el JSON del backend (NotificacionRS): { idNotificacion, titulo, mensaje, leido, idUsuario }.
public class NotificacionDto
{
    public int IdNotificacion { get; set; }
    public string Titulo { get; set; } = "";
    public string Mensaje { get; set; } = "";
    public bool Leido { get; set; }
    public int IdUsuario { get; set; }
}

// Respuesta del conteo de notificaciones no leídas (RF18): el backend envía {"noLeidas": N}.
public class NoLeidasDto
{
    public int NoLeidas { get; set; }
}

public class Notificacion
{
    public int Id { get; set; }
    public string Titulo { get; set; } = "";
    public string Descripcion { get; set; } = "";
    public string Tiempo { get; set; } = "";
    public string Categoria { get; set; } = "General";
    public bool Leida { get; set; } = false;
    public string AccionTexto { get; set; } = "Ver";
    public string AccionUrl { get; set; } = "/";

    public string AccionBtnClass => Categoria switch
    {
        "Reserva" => "btn-primary",
        "Mensaje" => "btn-secondary",
        "Pago"    => "btn-warning",
        _         => "btn-outline"
    };
}

public class ResultadoPago
{
    public bool Exitoso { get; set; }
    public string Mensaje { get; set; } = "";
    public string NumeroTransaccion { get; set; } = "";

    // Campos del pago real que devuelve el backend (GET PagoRS y PagoRS/anfitrion/{id}).
    // System.Text.Json (modo web) mapea el camelCase del JSON -> estas propiedades.
    // MontoNeto = lo que le queda al anfitrión tras la comisión del 10%.
    public int IdPago { get; set; }
    public int IdReserva { get; set; }
    public double MontoBruto { get; set; }
    public double MontoNeto { get; set; }
    public double PorcenComision { get; set; }
    public string Moneda { get; set; } = "PEN";
    public string EstadoTransaccion { get; set; } = "";
}

// =============================================
// FORM MODELS
// =============================================

public class LoginForm
{
    [Required(ErrorMessage = "El correo es obligatorio")]
    [EmailAddress(ErrorMessage = "Correo inválido")]
    public string Email { get; set; } = "";

    [Required(ErrorMessage = "La contraseña es obligatoria")]
    public string Password { get; set; } = "";
}

// FIX: sin TipoUsuario — todo usuario puede hacer todo
public class RegistroForm
{
    [Required(ErrorMessage = "El nombre es obligatorio")]
    public string Nombre { get; set; } = "";

    [Required(ErrorMessage = "El apellido es obligatorio")]
    public string Apellido { get; set; } = "";

    [Required(ErrorMessage = "El correo es obligatorio")]
    [EmailAddress(ErrorMessage = "Correo inválido")]
    public string Email { get; set; } = "";

    [Required(ErrorMessage = "La contraseña es obligatoria")]
    [MinLength(8, ErrorMessage = "Mínimo 8 caracteres")]
    public string Password { get; set; } = "";

    [Required(ErrorMessage = "Confirma tu contraseña")]
    [Compare("Password", ErrorMessage = "Las contraseñas no coinciden")]
    public string ConfirmarPassword { get; set; } = "";

    public bool AceptaTerminos { get; set; } = false;
}

public class RecuperarPasswordForm
{
    [Required(ErrorMessage = "El correo es obligatorio")]
    [EmailAddress(ErrorMessage = "Correo inválido")]
    public string Email { get; set; } = "";
}

public class CambiarPasswordForm
{
    [Required(ErrorMessage = "Ingresa tu contraseña actual")]
    public string PasswordActual { get; set; } = "";

    [Required(ErrorMessage = "Ingresa la nueva contraseña")]
    [MinLength(8, ErrorMessage = "Mínimo 8 caracteres")]
    public string NuevaPassword { get; set; } = "";

    [Required(ErrorMessage = "Confirma la nueva contraseña")]
    [Compare("NuevaPassword", ErrorMessage = "Las contraseñas no coinciden")]
    public string ConfirmarPassword { get; set; } = "";
}

public class EditarPerfilForm
{
    // Nombre y apellidos SEPARADOS: antes había un solo "Nombre Completo" que al guardar se volcaba
    // entero en Usuario.Nombre y duplicaba el apellido (NombreCompleto = Nombre + Apellido). Con campos
    // separados, cada uno va a su propiedad del backend (nombre / apellidoPaterno / apellidoMaterno).
    [Required(ErrorMessage = "El nombre es obligatorio")]
    public string Nombre { get; set; } = "";
    public string Apellido { get; set; } = "";          // apellido paterno
    public string ApellidoMaterno { get; set; } = "";

    [Required(ErrorMessage = "El correo es obligatorio")]
    [EmailAddress(ErrorMessage = "Correo inválido")]
    public string Email { get; set; } = "";

    public string Telefono { get; set; } = "";
    public string Ciudad { get; set; } = "";
    public string Bio { get; set; } = "";
}

public class BusquedaFiltros
{
    public string Ubicacion { get; set; } = "";
    public DateTime? FechaEntrada { get; set; }
    public DateTime? FechaSalida { get; set; }
    public int Huespedes { get; set; } = 1;
    public decimal PrecioMaximo { get; set; } = 500;
    public List<string> TiposSeleccionados { get; set; } = new();
    public string RatingMinimo { get; set; } = "Cualquiera";
}

public class CrearAlojamientoForm
{
    [Required(ErrorMessage = "El nombre es obligatorio")]
    public string Nombre { get; set; } = "";

    [Required(ErrorMessage = "Selecciona el tipo")]
    public string Tipo { get; set; } = "";

    public string Descripcion { get; set; } = "";

    [Range(1, 20, ErrorMessage = "Entre 1 y 20 huéspedes")]
    public int MaxHuespedes { get; set; } = 1;

    [Range(1, 20, ErrorMessage = "Entre 1 y 20 habitaciones")]
    public int Habitaciones { get; set; } = 1;

    [Required(ErrorMessage = "La dirección es obligatoria")]
    public string Direccion { get; set; } = "";

    [Required(ErrorMessage = "Selecciona el distrito")]
    public string Distrito { get; set; } = "";

    public string Provincia { get; set; } = "Lima";

    [Range(1, 10000, ErrorMessage = "Precio inválido")]
    public decimal PrecioNoche { get; set; }

    public decimal TarifaLimpieza { get; set; }
    public decimal TarifaServicio { get; set; }
    public decimal DescuentoSemanal { get; set; }

    public bool TieneWifi { get; set; }
    public bool TieneEstacionamiento { get; set; }
    public bool TieneTV { get; set; }
    public bool TienePiscina { get; set; }
    public bool TieneAireAcondicionado { get; set; }
    public bool TieneCocinaEquipada { get; set; }
}

public class PagoForm
{
    [Required(ErrorMessage = "El número de tarjeta es obligatorio")]
    public string NumeroTarjeta { get; set; } = "";

    [Required(ErrorMessage = "La fecha de vencimiento es obligatoria")]
    public string Vencimiento { get; set; } = "";

    [Required(ErrorMessage = "El CVV es obligatorio")]
    public string Cvv { get; set; } = "";

    [Required(ErrorMessage = "El nombre es obligatorio")]
    public string NombreTarjeta { get; set; } = "";

    public string MetodoPago { get; set; } = "tarjeta";
}

// Validador de dominio corporativo para administradores
public static class AdminEmailValidator
{
    public const string DominioCorporativo = "@bunki.pe";

    public static bool EsValido(string email)
        => !string.IsNullOrEmpty(email)
           && email.TrimEnd().EndsWith(DominioCorporativo, StringComparison.OrdinalIgnoreCase);

    public static string MensajeError
        => $"Los administradores deben usar un correo corporativo (@bunki.pe). Ej: admin@bunki.pe";
}

// Cuenta bancaria del ANFITRIÓN para RECIBIR sus ganancias (distinto de una tarjeta para pagar).
// Mapea el JSON del backend (camelCase). System.Text.Json en modo web es case-insensitive, así que
// "cci"->Cci, "nroBanco"->NroBanco, etc. mapean sin necesidad de [JsonPropertyName].
// Una cuenta nueva nace Verificada=false hasta que un proceso/admin la valide.
public class CuentaBancaria
{
    public int IdCuenta { get; set; }
    public int IdUsuario { get; set; }
    public string NumeroCuenta { get; set; } = "";
    public string TipoMoneda { get; set; } = "PEN";   // PEN | USD | EUR
    public string Cci { get; set; } = "";
    public string NroBanco { get; set; } = "";
    public string TipoCuenta { get; set; } = "AHORROS"; // AHORROS | CORRIENTE
    public string Titular { get; set; } = "";
    public double Saldo { get; set; }
    public bool Verificada { get; set; }
}
