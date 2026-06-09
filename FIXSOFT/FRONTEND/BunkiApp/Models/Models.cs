using System.ComponentModel.DataAnnotations;

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

    public decimal ComisionServicio => PrecioNoche * 0.10m;
}

public class Resena
{
    public int Id { get; set; }
    public string AutorNombre { get; set; } = "";
    public int Estrellas { get; set; }
    public string Comentario { get; set; } = "";
    public string FechaTexto { get; set; } = "";
}

public class Reserva
{
    public int Id { get; set; }
    public string AlojamientoNombre { get; set; } = "";
    public string Ubicacion { get; set; } = "";

    public string ImagenAlojamiento { get; set; } = "";

    public DateTime FechaEntrada { get; set; }
    public DateTime FechaSalida { get; set; }
    public int NumHuespedes { get; set; }
    public decimal Total { get; set; }
    public string Estado { get; set; } = "Confirmada";
    public string AnfitrionNombre { get; set; } = "";
    public int HuespedId { get; set; } = 0;
    public int AlojamientoId { get; set; } = 0;

    public int Noches => (FechaSalida - FechaEntrada).Days;
    public string FechasTexto => $"{FechaEntrada:dd MMM} - {FechaSalida:dd MMM yyyy}";

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
    public string Apellido { get; set; } = "";
    public string Email { get; set; } = "";
    public string Telefono { get; set; } = "";
    public string Ciudad { get; set; } = "";
    public string Bio { get; set; } = "";
    // FIX: solo "Usuario" o "Admin" — no existe rol anfitrion separado
    public string TipoUsuario { get; set; } = "Usuario";
    public bool Verificado { get; set; } = true;
    public string MiembroDesde { get; set; } = "Enero 2024";
    public double Rating { get; set; } = 4.9;
    public int TotalResenas { get; set; } = 48;
    public int TotalReservas { get; set; } = 15;
    public int TotalNoches { get; set; } = 62;
    public decimal GastoTotal { get; set; } = 8500;

    public string NombreCompleto => $"{Nombre} {Apellido}".Trim();

    // FIX: genera iniciales correctamente incluso con nombre vacío
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
    public Reserva? ReservaAsociada { get; set; }
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
    [Required(ErrorMessage = "El nombre es obligatorio")]
    public string NombreCompleto { get; set; } = "";

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
