namespace BunkiApp.Models;

/// <summary>
/// Estado global de la aplicación (POR USUARIO).
/// </summary>
public class AppState // <-- LE QUITAMOS EL STATIC AQUÍ
{
    // Cambiamos las propiedades para que pertenezcan a la instancia de la clase
    public Usuario? UsuarioActual { get; private set; } = null;

    public bool EstaAutenticado => UsuarioActual != null;

    public bool EsAdmin()
    {
        return UsuarioActual.Email?.EndsWith("@bunki.pe", StringComparison.OrdinalIgnoreCase) ?? false;
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
    }
}