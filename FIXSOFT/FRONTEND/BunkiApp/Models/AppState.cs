namespace BunkiApp.Models;

/// <summary>
/// Estado global de la aplicación (POR USUARIO).
/// </summary>
public class AppState
{
    // Propiedades del usuario autenticado
    public Usuario? UsuarioActual { get; private set; } = null;

    public bool EstaAutenticado => UsuarioActual != null;

    // 👇 AQUÍ ESTÁ EL PUENTE: Esta variable guardará la reserva temporalmente 👇
    public Reserva? ReservaAPagar { get; set; }

    public bool EsAdmin()
    {
        // (Pequeña mejora: le agregué un '?' después de UsuarioActual para evitar errores si está nulo)
        return UsuarioActual?.Email?.EndsWith("@bunki.pe", StringComparison.OrdinalIgnoreCase) ?? false;
    }

    public bool Login(string nombre, string apellido, string email, string tipoUsuario = "Usuario")
    {
        if (string.IsNullOrWhiteSpace(email)) return false;

        UsuarioActual = new Usuario
        {
            Id = 1,
            Nombre = nombre,
            Apellido = apellido,
            Email = email,
            TipoUsuario = tipoUsuario,
            Verificado = true,
            MiembroDesde = DateTime.Now.ToString("MMMM yyyy"),
            Rating = 4.9,
            TotalResenas = 0,
            TotalReservas = 0,
            TotalNoches = 0,
            GastoTotal = 0
        };
        return true;
    }

    public void Logout()
    {
        UsuarioActual = null;
        ReservaAPagar = null; // También limpiamos la reserva al cerrar sesión por seguridad
    }
}