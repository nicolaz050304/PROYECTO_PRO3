using System.Globalization;

namespace BunkiApp.Models;

/// <summary>
/// Estado global de la aplicación (POR USUARIO).
/// </summary>
public class AppState // <-- LE QUITAMOS EL STATIC AQUÍ
{
    // Cambiamos las propiedades para que pertenezcan a la instancia de la clase
    public Usuario? UsuarioActual { get; private set; } = null;

    public bool EstaAutenticado => UsuarioActual != null;

    // =====================================================================
    // MONEDA (frontend mock — SIN API de tipo de cambio).
    // El cobro real siempre es en S/ (PEN). USD/EUR son conversión
    // REFERENCIAL para presentación; las tasas son temporales y se
    // reemplazarán por el backend REST cuando exista el endpoint.
    // =====================================================================
    private static readonly CultureInfo CulturaPe = CultureInfo.GetCultureInfo("es-PE");

    /// <summary>Monedas soportadas: símbolo + tasa referencial relativa a PEN.</summary>
    public static readonly IReadOnlyList<(string Codigo, string Simbolo, decimal Tasa)> Monedas = new[]
    {
        ("PEN", "S/", 1.00m),
        ("USD", "$",  0.27m), // referencial
        ("EUR", "€",  0.25m), // referencial
    };

    public string MonedaSeleccionada { get; private set; } = "PEN";

    /// <summary>Se dispara cuando cambia un estado de UI compartido (p. ej. la moneda).</summary>
    public event Action? OnChange;

    private (string Codigo, string Simbolo, decimal Tasa) MonedaActual =>
        Monedas.FirstOrDefault(m => m.Codigo == MonedaSeleccionada, Monedas[0]);

    public string SimboloMoneda => MonedaActual.Simbolo;

    /// <summary>True cuando se muestra una moneda distinta de PEN (cobro sigue en S/).</summary>
    public bool MonedaEsReferencial => MonedaSeleccionada != "PEN";

    public void SetMoneda(string codigo)
    {
        if (codigo == MonedaSeleccionada) return;
        if (!Monedas.Any(m => m.Codigo == codigo)) return;

        MonedaSeleccionada = codigo;
        OnChange?.Invoke();
    }

    /// <summary>Convierte un monto en PEN a la moneda seleccionada (referencial).</summary>
    public decimal ConvertirPrecio(decimal montoPen) => montoPen * MonedaActual.Tasa;

    /// <summary>Formatea un monto (recibido en PEN) en la moneda seleccionada: "S/ 1,200".</summary>
    public string FormatearPrecio(decimal montoPen) =>
        $"{MonedaActual.Simbolo} {ConvertirPrecio(montoPen).ToString("N0", CulturaPe)}";

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