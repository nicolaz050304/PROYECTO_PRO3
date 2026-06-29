using System.Globalization;
using BunkiApp.Services;

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
    // MONEDA. El cobro real siempre es en S/ (PEN). USD/EUR son conversión
    // REFERENCIAL para presentación. La fuente de verdad de las tasas es ahora
    // el backend (GET TipoCambioRS), pero se cargan UNA sola vez al inicio
    // (ver CargarTasasAsync) y se guardan en memoria, para que los métodos de
    // conversión sigan siendo SÍNCRONOS y no haya que volver firmas async las
    // páginas que ya los usan (Pagos.razor, etc.).
    // =====================================================================
    private static readonly CultureInfo CulturaPe = CultureInfo.GetCultureInfo("es-PE");

    /// <summary>
    /// Monedas soportadas (símbolo + tasa = unidades de esa moneda por 1 PEN).
    /// Lista de INSTANCIA y mutable: se inicializa con tasas hardcodeadas que sirven
    /// de RESPALDO si el backend está caído, y se reemplaza por las del backend cuando
    /// CargarTasasAsync responde. Así la app siempre puede convertir aunque el REST falle.
    /// </summary>
    private List<(string Codigo, string Simbolo, decimal Tasa)> _monedas = new()
    {
        ("PEN", "S/", 1.00m),
        ("USD", "$",  0.27m), // respaldo referencial
        ("EUR", "€",  0.25m), // respaldo referencial
    };

    /// <summary>Vista de solo lectura de las monedas, para que la UI (NavMenu) las itere.</summary>
    public IReadOnlyList<(string Codigo, string Simbolo, decimal Tasa)> Monedas => _monedas;

    // Evita recargar las tasas si CargarTasasAsync se invoca más de una vez
    // (NavMenu puede inicializarse varias veces: prerender + render interactivo).
    private bool _tasasCargadas = false;

    public string MonedaSeleccionada { get; private set; } = "PEN";

    /// <summary>Se dispara cuando cambia un estado de UI compartido (p. ej. la moneda).</summary>
    public event Action? OnChange;

    private (string Codigo, string Simbolo, decimal Tasa) MonedaActual =>
        _monedas.FirstOrDefault(m => m.Codigo == MonedaSeleccionada, _monedas[0]);

    public string SimboloMoneda => MonedaActual.Simbolo;

    /// <summary>True cuando se muestra una moneda distinta de PEN (cobro sigue en S/).</summary>
    public bool MonedaEsReferencial => MonedaSeleccionada != "PEN";

    public void SetMoneda(string codigo)
    {
        if (codigo == MonedaSeleccionada) return;
        if (!_monedas.Any(m => m.Codigo == codigo)) return;

        MonedaSeleccionada = codigo;
        OnChange?.Invoke();
    }

    /// <summary>
    /// Carga las tasas desde el backend (GET TipoCambioRS) UNA sola vez y las deja en memoria.
    /// Es asíncrona porque hace una llamada HTTP, pero los métodos de conversión
    /// (ConvertirPrecio/FormatearPrecio) siguen siendo síncronos: leen la lista ya cargada.
    /// Si el backend no responde o devuelve vacío, se conservan las tasas de respaldo.
    /// </summary>
    public async Task CargarTasasAsync(TipoCambioServiceRest rest)
    {
        if (_tasasCargadas) return; // ya se cargaron en este scope/usuario

        try
        {
            var lista = await rest.ListarAsync();
            if (lista is not null && lista.Count > 0)
            {
                // El backend da TasaAPen = "cuántos PEN vale 1 unidad de esa moneda" (USD=3.75),
                // pero el frontend necesita "unidades de esa moneda por 1 PEN" para multiplicar
                // un monto en PEN. Por eso se INVIERTE (1 / TasaAPen). PEN se fuerza a 1.00 para
                // evitar redondeos. Ej.: 375 PEN * (1/3.75) = 100 USD.
                _monedas = lista.Select(t => (
                    t.Codigo,
                    t.Simbolo,
                    t.Codigo == "PEN" ? 1.00m : Math.Round(1m / t.TasaAPen, 6)
                )).ToList();

                _tasasCargadas = true;
                OnChange?.Invoke(); // refresca la UI con las tasas reales
            }
            // Si viene vacío o null, se quedan las hardcodeadas (respaldo): no marcamos cargado
            // para permitir reintentar en un siguiente arranque.
        }
        catch
        {
            // Backend caído -> se mantienen las tasas de respaldo. No relanzamos para no
            // romper el arranque de la app ni la navegación.
        }
    }

    /// <summary>Convierte un monto en PEN a la moneda seleccionada (referencial).</summary>
    public decimal ConvertirPrecio(decimal montoPen) => montoPen * MonedaActual.Tasa;

    /// <summary>Formatea un monto (recibido en PEN) en la moneda seleccionada: "S/ 1,200".</summary>
    public string FormatearPrecio(decimal montoPen) =>
        $"{MonedaActual.Simbolo} {ConvertirPrecio(montoPen).ToString("N0", CulturaPe)}";

    // EsAdmin ahora mira el ROL real (tipoUsuario), no el dominio del correo:
    // todos los correos son @bunki.pe, así que el dominio no discrimina nada.
    public bool EsAdmin() =>
        UsuarioActual?.TipoUsuario?.Equals("ADMINISTRADOR", StringComparison.OrdinalIgnoreCase) ?? false;

    public bool EsAnfitrion() =>
        UsuarioActual?.TipoUsuario?.Equals("ANFITRION", StringComparison.OrdinalIgnoreCase) ?? false;

    // Login real: guarda el usuario ya autenticado por el backend.
    public void LoginConUsuario(Usuario u)
    {
        UsuarioActual = u;
        OnChange?.Invoke();
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
        FavoritosIds.Clear(); // Limpiamos favoritos al salir
    }

    // =====================================================================
    // FAVORITOS (RF27 · persistentes en backend)
    // El estado local sigue siendo síncrono (los botones de corazón no cambian); la persistencia
    // se hace en segundo plano. Al iniciar sesión se cargan desde el backend (CargarFavoritosAsync).
    // =====================================================================
    public List<int> FavoritosIds { get; private set; } = new();

    // Referencia al servicio REST, guardada tras CargarFavoritosAsync, para poder persistir en ToggleFavorito.
    private FavoritoServiceRest? _favRest;

    /// <summary>Carga los favoritos del usuario actual desde el backend (llamar tras login).</summary>
    public async Task CargarFavoritosAsync(FavoritoServiceRest rest)
    {
        _favRest = rest;
        if (UsuarioActual is null || UsuarioActual.Id <= 0) return;
        try
        {
            FavoritosIds = await rest.ListarAsync(UsuarioActual.Id);
            OnChange?.Invoke();
        }
        catch { /* backend caído: se queda con lo que haya en memoria */ }
    }

    public void ToggleFavorito(int alojamientoId)
    {
        bool agregar = !FavoritosIds.Contains(alojamientoId);
        if (agregar) FavoritosIds.Add(alojamientoId);
        else FavoritosIds.Remove(alojamientoId);
        OnChange?.Invoke(); // UI instantánea

        // Persistencia en segundo plano (no bloquea la UI). Si falla, el estado local ya cambió
        // y se reconciliará en el próximo login (CargarFavoritosAsync).
        var rest = _favRest;
        int uid = UsuarioActual?.Id ?? 0;
        if (rest is not null && uid > 0)
        {
            _ = agregar ? rest.AgregarAsync(uid, alojamientoId) : rest.QuitarAsync(uid, alojamientoId);
        }
    }

    public bool EsFavorito(int alojamientoId) => FavoritosIds.Contains(alojamientoId);
}