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
            return Results.LocalRedirect("/");
        }).DisableAntiforgery();

        endpoints.MapGet("/auth/logout", async (HttpContext context) =>
        {
            await context.SignOutAsync(CookieAuthenticationDefaults.AuthenticationScheme);
            return Results.LocalRedirect("/");
        });
        return endpoints;
    }
}
