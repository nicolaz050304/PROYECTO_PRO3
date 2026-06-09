# 🏠 Bunki - Blazor Web App (.NET 10)

Conversión completa de los prototipos HTML a una aplicación Blazor Web App profesional.

---

## ⚙️ REQUISITOS

- .NET 10 SDK
- Visual Studio Code (con extensión C# / .NET)
- O Visual Studio 2022 v17.12+

---

## 🚀 CÓMO EJECUTAR

```bash
cd BunkiApp
dotnet run
```

Luego abrir: `http://localhost:5182`

---

## 🗂 ESTRUCTURA FINAL DEL PROYECTO

```
BunkiApp/
│
├── BunkiApp.csproj
├── Program.cs
├── appsettings.json
│
├── Properties/
│   └── launchSettings.json
│
├── Models/
│   └── Models.cs                   ← Todos los modelos y form models
│
├── Services/
│   ├── DataService.cs              ← Datos mock + lógica de negocio
│   └── AuthService.cs              ← Manejo de sesión/autenticación
│
├── Components/
│   │
│   ├── App.razor                   ← Raíz HTML de la app
│   ├── Routes.razor                ← Router con 404 personalizado
│   ├── _Imports.razor              ← Usings globales
│   │
│   ├── Layout/
│   │   ├── MainLayout.razor        ← Layout con NavMenu + Footer
│   │   ├── EmptyLayout.razor       ← Layout sin nav (Login/Registro)
│   │   └── NavMenu.razor           ← Header + navegación principal
│   │
│   ├── Shared/
│   │   ├── AlojamientoCard.razor   ← Card reutilizable de alojamiento
│   │   ├── ReservaCard.razor       ← Card reutilizable de reserva
│   │   ├── StarRating.razor        ← Estrellas de calificación
│   │   ├── StatBox.razor           ← Caja de estadísticas
│   │   └── AlertMessage.razor      ← Mensajes de alerta/éxito/error
│   │
│   └── Pages/
│       ├── Home.razor              ← / (Dashboard)
│       ├── Login.razor             ← /login
│       ├── Registro.razor          ← /registro
│       ├── RecuperarPassword.razor ← /recuperar-password
│       ├── Explorar.razor          ← /explorar + /buscar
│       ├── DetalleAlojamiento.razor← /alojamiento/{id}
│       ├── MisReservas.razor       ← /mis-reservas
│       ├── Perfil.razor            ← /perfil
│       ├── EditarPerfil.razor      ← /perfil/editar
│       ├── CambiarPassword.razor   ← /perfil/cambiar-password
│       ├── Mensajes.razor          ← /mensajes
│       ├── Notificaciones.razor    ← /notificaciones
│       ├── Pagos.razor             ← /pagos
│       ├── PanelAnfitrion.razor    ← /panel-anfitrion
│       ├── CrearAlojamiento.razor  ← /crear-alojamiento
│       └── Admin.razor             ← /admin
│
└── wwwroot/
    └── css/
        └── styles.css              ← Design system completo
```

---

## 🗺 RUTAS DE LA APLICACIÓN

| Ruta                         | Página                   | Descripción                       |
|------------------------------|--------------------------|-----------------------------------|
| `/`                          | Home.razor               | Dashboard con destacados          |
| `/login`                     | Login.razor              | Inicio de sesión                  |
| `/registro`                  | Registro.razor           | Crear cuenta                      |
| `/recuperar-password`        | RecuperarPassword.razor  | Reseteo de contraseña             |
| `/explorar` o `/buscar`      | Explorar.razor           | Búsqueda con filtros              |
| `/alojamiento/{id}`          | DetalleAlojamiento.razor | Detalle + reserva                 |
| `/mis-reservas`              | MisReservas.razor        | Próximas / Pasadas / Canceladas   |
| `/perfil`                    | Perfil.razor             | Mi perfil y stats                 |
| `/perfil/editar`             | EditarPerfil.razor       | Editar datos personales           |
| `/perfil/cambiar-password`   | CambiarPassword.razor    | Cambiar contraseña                |
| `/mensajes`                  | Mensajes.razor           | Chat en tiempo real               |
| `/notificaciones`            | Notificaciones.razor     | Centro de alertas                 |
| `/pagos`                     | Pagos.razor              | Checkout con tarjeta/Yape         |
| `/panel-anfitrion`           | PanelAnfitrion.razor     | Dashboard del anfitrión           |
| `/crear-alojamiento`         | CrearAlojamiento.razor   | Formulario multi-sección          |
| `/admin`                     | Admin.razor              | Panel de administración           |

---

## 🎯 CARACTERÍSTICAS DE BLAZOR UTILIZADAS

### Directivas
- `@page` para rutas
- `@rendermode InteractiveServer` en todas las páginas interactivas
- `@inject` para inyección de dependencias (DataService, AuthService, NavigationManager)
- `@code { }` con lógica C# completa

### Componentes Razor
- `EditForm` + `DataAnnotationsValidator` + `ValidationMessage`
- `InputText`, `InputNumber`, `InputSelect`, `InputDate`, `InputCheckbox`, `InputTextArea`
- `@bind`, `@onclick`, `@onchange`, `@onkeypress`, `@oninput`
- `@if`, `@else`, `@foreach`, `@for`, `@switch`
- `[Parameter]`, `EventCallback`

### Routing
- `NavigationManager.NavigateTo()` para navegación programática
- Rutas con parámetros: `/alojamiento/{Id:int}`
- Rutas múltiples en una página: `/explorar` y `/buscar`

### Layouts
- `MainLayout` (con NavMenu + Footer)
- `EmptyLayout` (para páginas de auth sin navegación)
- Detección automática de páginas que no deben mostrar nav

### Servicios
- `DataService` (Singleton) - datos y lógica de negocio
- `AuthService` (Scoped) - manejo de sesión

---

## 🔄 CÓMO INTEGRAR CON BACKEND JAVA

### 1. Reemplazar DataService

```csharp
// En lugar de datos mock, usar HttpClient:
public class DataService
{
    private readonly HttpClient _http;

    public DataService(HttpClient http)
    {
        _http = http;
    }

    public async Task<List<Alojamiento>> ObtenerAlojamientosAsync()
    {
        return await _http.GetFromJsonAsync<List<Alojamiento>>("api/alojamientos") ?? new();
    }
}
```

### 2. Registrar HttpClient en Program.cs

```csharp
builder.Services.AddHttpClient<DataService>(client =>
{
    client.BaseAddress = new Uri("https://tu-api-java.com/");
});
```

### 3. Hacer métodos async en las páginas

```csharp
protected override async Task OnInitializedAsync()
{
    Alojamientos = await Data.ObtenerAlojamientosAsync();
}
```

---

## 🎨 DESIGN SYSTEM (CSS)

El archivo `wwwroot/css/styles.css` incluye:

- **Variables CSS**: colores, sombras, radios
- **Layout**: container, grid-2/3/4, two-column, sidebar
- **Componentes**: cards, buttons, forms, badges, alerts, tabs, modals
- **Utilidades**: flex, spacing, typography
- **Responsive**: breakpoints 900px y 600px

Para cambiar el color primario, editar:
```css
:root {
    --primary: #FF6B6B; /* ← Cambiar aquí */
}
```

---

## ✅ CHECKLIST ANTES DE PRODUCCIÓN

- [ ] Reemplazar `DataService` mock con llamadas HTTP reales
- [ ] Implementar autenticación real (ASP.NET Identity o JWT)
- [ ] Agregar manejo de archivos/imágenes real
- [ ] Configurar HTTPS y certificados
- [ ] Agregar logging y manejo de errores global
- [ ] Implementar paginación server-side
- [ ] Agregar pruebas unitarias (bUnit)
