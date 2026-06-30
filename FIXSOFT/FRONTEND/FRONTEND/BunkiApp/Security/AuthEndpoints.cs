using System.Security.Claims;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authentication.Cookies;
using BunkiApp.Models;
using BunkiApp.Services;

namespace BunkiApp.Security;

public static class AuthEndpoints
{
    public static IEndpointRouteBuilder MapAuthEndpoints(this IEndpointRouteBuilder endpoints)
    {
        endpoints.MapPost("/auth/login", async (HttpContext context, UsuarioProvider provider) =>
        {
            var form = await context.Request.ReadFormAsync();
            var correo = form["correo"].ToString();
            var contrasena = form["contrasena"].ToString();
            if (string.IsNullOrWhiteSpace(correo) || string.IsNullOrWhiteSpace(contrasena))
                return Results.LocalRedirect("/login?error=1");

            Usuario? usuario;
            try { usuario = await provider.LoginAsync(correo, contrasena); }
            catch { return Results.LocalRedirect("/login?error=2"); } // backend caído

            if (usuario is null) return Results.LocalRedirect("/login?error=1");

            await FirmarSesionAsync(context, usuario);
            return Results.LocalRedirect("/");
        }).DisableAntiforgery();

        // Registro: MISMO patrón que /auth/login. Es un endpoint (no el circuito Blazor) porque solo
        // aquí, antes de comprometer la respuesta, se puede escribir la cookie con SignInAsync. Si se
        // hiciera desde el componente interactivo, el usuario quedaría logueado solo en memoria y se
        // desconectaría al recargar (bug que esto corrige).
        endpoints.MapPost("/auth/register", async (HttpContext context, UsuarioProvider provider, EmailService email) =>
        {
            var form = await context.Request.ReadFormAsync();
            var nombre    = form["nombre"].ToString().Trim();
            var apellido  = form["apellido"].ToString().Trim();
            var correo    = form["correo"].ToString().Trim();
            var password  = form["password"].ToString();
            var confirmar = form["confirmar"].ToString();
            var terminos  = form["terminos"].ToString();

            if (string.IsNullOrWhiteSpace(nombre) || string.IsNullOrWhiteSpace(apellido)
                || string.IsNullOrWhiteSpace(correo) || string.IsNullOrWhiteSpace(password))
                return Results.LocalRedirect("/registro?error=campos");
            if (terminos != "on" && terminos != "true")
                return Results.LocalRedirect("/registro?error=terminos");
            if (password != confirmar)
                return Results.LocalRedirect("/registro?error=password");

            // Crea el usuario REAL en el backend. Modelo de negocio: toda cuenta nace ANFITRION
            // (puede publicar y, como Anfitrion extiende Cliente, también reservar).
            try
            {
                var nuevo = new Usuario
                {
                    Nombre      = nombre,
                    Apellido    = apellido,   // serializa a "apellidoPaterno"
                    Email       = correo,     // serializa a "correo"
                    Password    = password,   // el backend lo hashea
                    TipoUsuario = "ANFITRION"
                };
                await provider.RegistrarAsync(nuevo);
            }
            catch { return Results.LocalRedirect("/registro?error=duplicado"); }

            // Login real + cookie (igual que /auth/login): así la sesión PERSISTE tras recargar.
            Usuario? usuario;
            try { usuario = await provider.LoginAsync(correo, password); }
            catch { return Results.LocalRedirect("/login?error=2"); }
            if (usuario is null) return Results.LocalRedirect("/login"); // creada, pero que entre manual

            await FirmarSesionAsync(context, usuario);

            // Correo de bienvenida (secundario): no rompe el registro si el envío falla o está
            // desactivado (EmailService devuelve false y traga la excepción internamente).
            await email.EnviarBienvenidaAsync(correo, nombre);

            return Results.LocalRedirect("/");
        }).DisableAntiforgery();

        endpoints.MapGet("/auth/logout", async (HttpContext context) =>
        {
            await context.SignOutAsync(CookieAuthenticationDefaults.AuthenticationScheme);
            return Results.LocalRedirect("/");
        });
        return endpoints;
    }

    // Escribe la cookie de sesión (ClaimsPrincipal). Compartido por login y registro para que ambos
    // produzcan EXACTAMENTE la misma sesión persistente.
    private static async Task FirmarSesionAsync(HttpContext context, Usuario usuario)
    {
        var claims = new List<Claim>
        {
            new(ClaimTypes.NameIdentifier, usuario.Id.ToString()),
            new(ClaimTypes.Name, usuario.Email ?? ""),
            new(ClaimTypes.Role, usuario.TipoUsuario ?? "INVITADO"),
            new("Nombre", usuario.Nombre ?? ""),
            new("Apellido", usuario.Apellido ?? ""),
            new("Email", usuario.Email ?? "")
        };
        var identity = new ClaimsIdentity(claims, CookieAuthenticationDefaults.AuthenticationScheme);
        await context.SignInAsync(
            CookieAuthenticationDefaults.AuthenticationScheme,
            new ClaimsPrincipal(identity),
            new AuthenticationProperties { IsPersistent = true, ExpiresUtc = DateTimeOffset.UtcNow.AddHours(8) });
    }
}
