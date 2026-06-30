namespace BunkiApp.Services
{
    /// <summary>
    /// URL PÚBLICA del backend para enlaces que abre el NAVEGADOR del usuario (descargas de
    /// comprobante, voucher y reportes PDF/CSV). A diferencia de las llamadas server-side
    /// (Program.cs, que usan localhost porque corren EN el servidor), estos enlaces los abre el
    /// navegador del usuario; si apuntan a localhost, en un despliegue remoto apuntarían a la
    /// máquina del usuario y fallarían.
    ///
    /// El valor NO está hardcodeado: se lee de configuración/entorno, con localhost por defecto.
    ///   - En desarrollo local: no se setea nada -> usa localhost:8080 (todo igual que antes).
    ///   - En el servidor desplegado: setear la variable de entorno BUNKI_BACKEND_PUBLIC_URL
    ///     (o la clave Backend:PublicUrl en appsettings) con la URL pública, p. ej.
    ///     http://TU_IP_ELASTICA:8080/BunkiBackend/webresources/
    /// </summary>
    public class BackendUrls
    {
        public string PublicBase { get; }

        public BackendUrls(IConfiguration cfg)
        {
            // Misma variable que usan las llamadas server-side en Program.cs (BUNKI_BACKEND_URL):
            // así una sola configuración controla TODO. Se conservan los nombres antiguos como
            // respaldo, y localhost como default de desarrollo.
            var url = Environment.GetEnvironmentVariable("BUNKI_BACKEND_URL")
                      ?? Environment.GetEnvironmentVariable("BUNKI_BACKEND_PUBLIC_URL")
                      ?? cfg["Backend:Url"]
                      ?? cfg["Backend:PublicUrl"]
                      ?? "http://localhost:8080/BunkiBackend/webresources/";
            if (!url.EndsWith("/")) url += "/";
            PublicBase = url;
        }

        /// <summary>
        /// Devuelve la ruta LOCAL del proxy de descargas del propio frontend (no la URL del backend).
        /// El navegador pide el archivo al frontend (mismo origen) y este lo baja del backend por la
        /// red interna y lo reenvía (ver "/descargas" en Program.cs). Así el backend NO necesita estar
        /// expuesto a internet. El recurso viaja URL-encoded como UN solo parámetro, incluyendo su
        /// propio query string (p. ej. "ReporteRS/voucher/pdf?reserva=5").
        /// </summary>
        public string Para(string recursoRelativo)
            => "/descargas?recurso=" + Uri.EscapeDataString(recursoRelativo.TrimStart('/'));
    }
}
