using System.Net;
using System.Net.Mail;

namespace BunkiApp.Services
{
    /// <summary>
    /// Envío de correos vía SMTP de Gmail (con "contraseña de aplicación"). Se eligió SMTP de Gmail
    /// porque entrega a CUALQUIER destinatario sin verificar dominio (a diferencia del dominio de
    /// prueba de MailerSend/SendGrid, que solo entregan al correo propio verificado).
    ///
    /// Credenciales por VARIABLE DE ENTORNO (nunca quemadas en el repo):
    ///   - BUNKI_GMAIL_USER          -> el correo Gmail remitente (p. ej. tucuenta@gmail.com)
    ///   - BUNKI_GMAIL_APP_PASSWORD  -> la clave de aplicación de 16 letras (Google → Contraseñas de app)
    ///   - BUNKI_MAIL_FROM_NAME      -> (opcional) nombre visible del remitente; default "Bunki"
    ///
    /// Si NO hay credenciales configuradas, el servicio queda DESACTIVADO: no envía y no rompe nada
    /// (la app corre igual en desarrollo sin secretos). Ningún fallo de correo propaga excepción a la
    /// operación de negocio (registrarse, reservar, etc.).
    /// </summary>
    public class EmailService
    {
        private readonly ILogger<EmailService> _log;
        private readonly string? _user;
        private readonly string? _appPassword;
        private readonly string _fromName;

        public bool Habilitado => !string.IsNullOrWhiteSpace(_user) && !string.IsNullOrWhiteSpace(_appPassword);

        public EmailService(IConfiguration cfg, ILogger<EmailService> log)
        {
            _log = log;
            _user = Environment.GetEnvironmentVariable("BUNKI_GMAIL_USER") ?? cfg["Mail:GmailUser"];
            _appPassword = Environment.GetEnvironmentVariable("BUNKI_GMAIL_APP_PASSWORD") ?? cfg["Mail:GmailAppPassword"];
            _fromName = Environment.GetEnvironmentVariable("BUNKI_MAIL_FROM_NAME") ?? cfg["Mail:FromName"] ?? "Bunki";
            // La clave de app de Google se muestra con espacios ("abcd efgh ijkl mnop") pero el SMTP
            // los rechaza: se quitan por si la pegaron tal cual.
            _appPassword = _appPassword?.Replace(" ", "");
        }

        /// <summary>
        /// Envía un correo HTML. Devuelve true si se envió, false si está desactivado o falló (nunca
        /// lanza excepción: el correo es secundario y no debe romper la operación que lo dispara).
        /// </summary>
        public async Task<bool> EnviarAsync(string destino, string asunto, string cuerpoHtml)
        {
            if (!Habilitado)
            {
                _log.LogInformation("EmailService desactivado (sin BUNKI_GMAIL_USER/APP_PASSWORD); no se envía a {Destino}.", destino);
                return false;
            }
            if (string.IsNullOrWhiteSpace(destino))
                return false;

            try
            {
                using var msg = new MailMessage
                {
                    From = new MailAddress(_user!, _fromName),
                    Subject = asunto,
                    Body = cuerpoHtml,
                    IsBodyHtml = true
                };
                msg.To.Add(destino);

                using var smtp = new SmtpClient("smtp.gmail.com", 587)
                {
                    EnableSsl = true,                       // STARTTLS
                    DeliveryMethod = SmtpDeliveryMethod.Network,
                    Credentials = new NetworkCredential(_user, _appPassword),
                    Timeout = 12000                         // no colgar la operación si SMTP tarda
                };

                await smtp.SendMailAsync(msg);
                _log.LogInformation("Correo enviado a {Destino} ({Asunto}).", destino, asunto);
                return true;
            }
            catch (Exception ex)
            {
                _log.LogWarning(ex, "No se pudo enviar el correo a {Destino} ({Asunto}).", destino, asunto);
                return false;
            }
        }

        /// <summary>Correo de bienvenida al registrarse.</summary>
        public Task<bool> EnviarBienvenidaAsync(string destino, string nombre)
        {
            var saludo = string.IsNullOrWhiteSpace(nombre) ? "¡Hola!" : $"¡Hola, {nombre}!";
            var cuerpo = Plantilla(
                titulo: "Bienvenido a Bunki",
                saludo: saludo,
                parrafos: new[]
                {
                    "Tu cuenta fue creada con éxito. Ya puedes explorar alojamientos, reservar tu próxima estadía y, cuando quieras, publicar el tuyo.",
                    "Gracias por unirte a Bunki, la plataforma de alojamientos del Perú."
                },
                ctaTexto: "Explorar alojamientos",
                ctaUrl: "#");
            return EnviarAsync(destino, "Bienvenido a Bunki 🌿", cuerpo);
        }

        /// <summary>Correo al HUÉSPED cuando el anfitrión confirma su reserva.</summary>
        public Task<bool> EnviarConfirmacionReservaAsync(string destino, string nombreHuesped,
            string alojamiento, string fechas, string total)
        {
            var saludo = string.IsNullOrWhiteSpace(nombreHuesped) ? "¡Hola!" : $"¡Hola, {nombreHuesped}!";
            // Líneas etiqueta/valor con <br> (seguras dentro de <p>, a diferencia de una <table>).
            var detalle =
                Linea("Alojamiento", alojamiento) + "<br>" +
                Linea("Fechas", fechas) + "<br>" +
                Linea("Total", total);
            var cuerpo = Plantilla(
                titulo: "Tu reserva fue confirmada",
                saludo: saludo,
                parrafos: new[]
                {
                    "¡Buenas noticias! El anfitrión confirmó tu reserva. Aquí están los detalles:",
                    detalle,
                    "Puedes ver tu reserva y descargar el comprobante desde \"Mis reservas\" en Bunki."
                },
                ctaTexto: "Ver mis reservas",
                ctaUrl: "#");
            return EnviarAsync(destino, "Tu reserva en Bunki fue confirmada ✅", cuerpo);
        }

        // Línea "Etiqueta: valor" para el detalle de la reserva (la etiqueta tenue, el valor resaltado).
        private static string Linea(string etiqueta, string valor) =>
            $"<span style=\"color:#7E7869;\">{etiqueta}:</span> <strong style=\"color:#1C1A17;\">{valor}</strong>";

        // Plantilla HTML simple con la identidad "Porcelana" (verde petróleo #1F6B5E sobre fondo claro).
        // Estilos inline porque los clientes de correo no respetan <style>/clases externas.
        private string Plantilla(string titulo, string saludo, string[] parrafos, string ctaTexto, string ctaUrl)
        {
            var cuerpoParrafos = string.Join("", parrafos.Select(p =>
                $"<p style=\"margin:0 0 16px;color:#1C1A17;font-size:15px;line-height:1.6;\">{p}</p>"));

            var cta = string.IsNullOrWhiteSpace(ctaUrl) || ctaUrl == "#" ? "" :
                $"<a href=\"{ctaUrl}\" style=\"display:inline-block;margin-top:8px;background:#1F6B5E;color:#F5F2EA;" +
                "text-decoration:none;padding:12px 22px;border-radius:999px;font-size:14px;font-weight:600;\">" +
                $"{ctaTexto}</a>";

            return $@"<!DOCTYPE html><html><body style=""margin:0;padding:0;background:#F3F0EA;"">
  <table role=""presentation"" width=""100%"" cellpadding=""0"" cellspacing=""0"" style=""background:#F3F0EA;padding:32px 0;"">
    <tr><td align=""center"">
      <table role=""presentation"" width=""520"" cellpadding=""0"" cellspacing=""0""
             style=""background:#FFFFFF;border:1px solid #E3DFD5;border-radius:16px;overflow:hidden;"">
        <tr><td style=""padding:28px 32px 0;"">
          <div style=""font-size:22px;font-weight:800;color:#1F6B5E;letter-spacing:-0.5px;"">bunki</div>
          <div style=""height:3px;width:48px;background:#1F6B5E;border-radius:2px;margin-top:10px;""></div>
        </td></tr>
        <tr><td style=""padding:22px 32px 32px;"">
          <h1 style=""margin:0 0 6px;color:#1C1A17;font-size:20px;"">{titulo}</h1>
          <p style=""margin:0 0 18px;color:#1F6B5E;font-size:15px;font-weight:600;"">{saludo}</p>
          {cuerpoParrafos}
          {cta}
        </td></tr>
        <tr><td style=""padding:18px 32px;border-top:1px solid #E3DFD5;color:#7E7869;font-size:12px;"">
          Bunki · Plataforma de alojamientos · Perú
        </td></tr>
      </table>
    </td></tr>
  </table>
</body></html>";
        }
    }
}
