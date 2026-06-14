using BunkiApp.Models;

namespace BunkiApp.Services;

/// <summary>
/// Servicio central de datos. En producción, reemplazar con llamadas HTTP/EF Core.
/// </summary>
public class DataService
{
    // =============================================
    // ALOJAMIENTOS
    // =============================================

    public List<Alojamiento> ObtenerAlojamientos() => new()
    {   
        new Alojamiento
        {
            Id = 1,
            Nombre = "Departamento en Miraflores",
            Tipo = "Departamento",
            Descripcion = "Hermoso loft moderno ubicado en el corazón de Miraflores, con todas las comodidades para tu estadía. Perfecto para parejas o viajeros solitarios que buscan confort y ubicación céntrica.",
            Ubicacion = "Av. Larco 456, Miraflores",
            Distrito = "Miraflores",
            PrecioNoche = 150,
            TarifaLimpieza = 50,
            MaxHuespedes = 2,
            Habitaciones = 1,
            Rating = 5.0,
            TotalResenas = 48,
            GradienteColor = "linear-gradient(135deg, #FF6B6B, #FFB3B3)",
            EstadoPublicacion = "Activo",
            ImagenUrl = "https://i.postimg.cc/d1kWHVNS/depa-miraflores.jpg",
            Servicios = new() { "Aire acondicionado", "Wifi de alta velocidad", "Cocina equipada", "Baño privado", "TV con cable", "Cama cómoda" },
            AnfitrionNombre = "María Andrades",
            AnfitrionInicial = "MA",
            AnfitrionDesde = "2021",
            PoliticaCancelacion = "Flexible hasta 48 horas antes",
            Reglas = "Sin mascotas, sin fiestas",
            Resenas = new()
            {
                new() { Id = 1, AutorNombre = "Carlos R.", Estrellas = 5, Comentario = "Excelente lugar, muy limpio y cómodo. El anfitrión fue muy atento.", FechaTexto = "Hace 2 semanas" },
                new() { Id = 2, AutorNombre = "Laura M.", Estrellas = 5, Comentario = "Perfecto para una escapada en Lima. Ubicación inmejorable.", FechaTexto = "Hace 1 mes" }
            }
        },
        new Alojamiento
        {
            Id = 2,
            Nombre = "Casa en La Molina",
            Tipo = "Casa",
            Descripcion = "Casa unifamiliar amplia con jardín privado, perfecta para familias o grupos. Ubicada en una zona tranquila y residencial.",
            Ubicacion = "Jr. Los Ficus 234, La Molina",
            Distrito = "La Molina",
            PrecioNoche = 200,
            TarifaLimpieza = 100,
            MaxHuespedes = 6,
            Habitaciones = 3,
            Rating = 4.0,
            TotalResenas = 32,
            GradienteColor = "linear-gradient(135deg, #4ECDC4, #8FE7E0)",
            EstadoPublicacion = "Activo",
             ImagenUrl = "https://i.postimg.cc/13BY4TNb/Casa-molina.jpg",
            Servicios = new() { "Jardín", "Estacionamiento", "Wifi", "Cocina equipada", "TV", "Lavandería" },
            AnfitrionNombre = "Carlos Rodríguez",
            AnfitrionInicial = "CR",
            AnfitrionDesde = "2020",
            PoliticaCancelacion = "Moderada - 5 días antes",
            Reglas = "Sin fiestas, sin fumar",
        },
        new Alojamiento
        {
            Id = 3,
            Nombre = "Habitación en Barranco",
            Tipo = "Habitación",
            Descripcion = "Habitación privada en un hogar familiar bohemio en Barranco. Ambiente cálido y acogedor, cerca de la vida cultural del distrito.",
            Ubicacion = "Jirón Unión 89, Barranco",
            Distrito = "Barranco",
            PrecioNoche = 80,
            TarifaLimpieza = 30,
            MaxHuespedes = 1,
            Habitaciones = 1,
            Rating = 5.0,
            TotalResenas = 15,
            GradienteColor = "linear-gradient(135deg, #FFD43B, #FFE680)",
            EstadoPublicacion = "Revisión",
            ImagenUrl = "https://i.postimg.cc/ZR2Q4dyB/Habitacion-barranco.jpg",
            Servicios = new() { "Wifi", "Baño compartido", "Desayuno incluido", "TV" },
            AnfitrionNombre = "Ana Torres",
            AnfitrionInicial = "AT",
            AnfitrionDesde = "2022",
        },
        new Alojamiento
        {
            Id = 4,
            Nombre = "Estudio en San Isidro",
            Tipo = "Departamento",
            Descripcion = "Estudio moderno y completamente equipado en el distrito financiero de Lima. Ideal para viajeros de negocios.",
            Ubicacion = "Av. Javier Prado 678, San Isidro",
            Distrito = "San Isidro",
            PrecioNoche = 120,
            TarifaLimpieza = 40,
            MaxHuespedes = 2,
            Habitaciones = 1,
            Rating = 5.0,
            TotalResenas = 28,
            GradienteColor = "linear-gradient(135deg, #51CF66, #9BEA8F)",
            EstadoPublicacion = "Activo",
            ImagenUrl = "https://i.postimg.cc/25jMwmf0/Esrudio-sani.jpg",
            Servicios = new() { "Wifi", "Aire acondicionado", "Cocina equipada", "TV", "Escritorio de trabajo" },
            AnfitrionNombre = "Pedro López",
            AnfitrionInicial = "PL",
            AnfitrionDesde = "2021",
        },
        new Alojamiento
        {
            Id = 5,
            Nombre = "Departamento en Pueblo Libre",
            Tipo = "Departamento",
            Descripcion = "Departamento amplio y tranquilo en el acogedor distrito de Pueblo Libre. Fácil acceso al transporte público.",
            Ubicacion = "Av. Sucre 456, Pueblo Libre",
            Distrito = "Pueblo Libre",
            PrecioNoche = 110,
            TarifaLimpieza = 40,
            MaxHuespedes = 4,
            Habitaciones = 2,
            Rating = 4.0,
            TotalResenas = 22,
            GradienteColor = "linear-gradient(135deg, #A78BFA, #D8B4FE)",
            EstadoPublicacion = "Activo",
            ImagenUrl = "https://i.postimg.cc/qvT9NyDK/Depa-peublo.webp",
            Servicios = new() { "Wifi", "Cocina equipada", "TV", "Lavandería", "Balcón" },
            AnfitrionNombre = "Rosa Mejía",
            AnfitrionInicial = "RM",
            AnfitrionDesde = "2022",
        },
        new Alojamiento
        {
            Id = 6,
            Nombre = "Casa en Chaclacayo",
            Tipo = "Cabaña",
            Descripcion = "Cabaña rústica en contacto con la naturaleza, perfecta para desconectarse del ruido de la ciudad.",
            Ubicacion = "Carretera Central Km 32, Chaclacayo",
            Distrito = "Chaclacayo",
            PrecioNoche = 180,
            TarifaLimpieza = 80,
            MaxHuespedes = 5,
            Habitaciones = 2,
            Rating = 5.0,
            TotalResenas = 41,
            GradienteColor = "linear-gradient(135deg, #F43F5E, #FB7185)",
            EstadoPublicacion = "Activo",
            ImagenUrl = "https://i.postimg.cc/Kj35NpjY/casa-chaclacayo.jpg",
            Servicios = new() { "Jardín", "Barbacoa", "Wifi", "Cocina rústica", "Chimenea" },
            AnfitrionNombre = "Luis Vega",
            AnfitrionInicial = "LV",
            AnfitrionDesde = "2019",
        },
        new Alojamiento
        {
            Id = 7,
            Nombre = "Acogedor Minidepa en Los Olivos",
            Tipo = "Departamento",
            Descripcion = "Minidepartamento independiente cerca de la municipalidad de Los Olivos y MegaPlaza. Zona segura y llena de comercios.",
            Ubicacion = "Av. Carlos Izaguirre 1230, Los Olivos",
            Distrito = "Los Olivos",
            PrecioNoche = 85,
            TarifaLimpieza = 25,
            MaxHuespedes = 2,
            Habitaciones = 1,
            Rating = 4.8,
            TotalResenas = 34,
            GradienteColor = "linear-gradient(135deg, #3B82F6, #93C5FD)",
            EstadoPublicacion = "Activo",
            ImagenUrl = "https://i.postimg.cc/jS2kwcQD/DEPA-OLVIOS.jpg",
            Servicios = new() { "Wifi", "TV", "Cocina básica", "Entrada independiente" },
            AnfitrionNombre = "Jorge Mamani",
            AnfitrionInicial = "JM",
            AnfitrionDesde = "2023",
            PoliticaCancelacion = "Flexible - 24 horas antes",
            Reglas = "No hacer ruido excesivo después de las 11 PM",
            Resenas = new()
        },
        new Alojamiento
        {
            Id = 8,
            Nombre = "Casa Familiar en San Juan de Lurigancho",
            Tipo = "Casa",
            Descripcion = "Amplia casa en Zárate, ideal para familias numerosas que visitan la capital. A 5 minutos de la estación del tren eléctrico.",
            Ubicacion = "Av. Gran Chimú 450, San Juan de Lurigancho",
            Distrito = "San Juan de Lurigancho",
            PrecioNoche = 140,
            TarifaLimpieza = 50,
            MaxHuespedes = 6,
            Habitaciones = 3,
            Rating = 4.5,
            TotalResenas = 18,
            GradienteColor = "linear-gradient(135deg, #10B981, #6EE7B7)",
            EstadoPublicacion = "Activo",
            ImagenUrl = "https://i.postimg.cc/7ZhQG9MC/CASA-SJL.webp",
            Servicios = new() { "Wifi", "Cocina amplia", "Patio", "Lavadora" },
            AnfitrionNombre = "Carmen Rojas",
            AnfitrionInicial = "CR",
            AnfitrionDesde = "2021",
            PoliticaCancelacion = "Moderada - 5 días antes",
            Reglas = "Cuidar las instalaciones, se admiten mascotas pequeñas",
            Resenas = new()
        },
        new Alojamiento
        {
            Id = 9,
            Nombre = "Oficina TUPIA SOLUTIONS",
            Tipo = "Departamento",
            Descripcion = "Para consultorias y como habitaciones.",
            Ubicacion = "Av. Túpac Amaru 800, San Martín de Porres",
            Distrito = "San Martín de Porres",
            PrecioNoche = 150,
            TarifaLimpieza = 15,
            MaxHuespedes = 1,
            Habitaciones = 1,
            Rating = 4.2,
            TotalResenas = 52,
            GradienteColor = "linear-gradient(135deg, #F59E0B, #FCD34D)",
            EstadoPublicacion = "Activo",
            ImagenUrl = "https://i.postimg.cc/K8jskfrR/TUPIA-SOLITONS.jpg",
            Servicios = new() { "Wifi alta velocidad", "Escritorio", "Baño compartido" },
            AnfitrionNombre = "Luis Cárdenas",
            AnfitrionInicial = "LC",
            AnfitrionDesde = "2019",
            PoliticaCancelacion = "Estricta",
            Reglas = "Prohibido fumar, visitas solo hasta las 8 PM",
            Resenas = new()
        },
        new Alojamiento
        {
            Id = 10,
            Nombre = "Departamento Moderno en Villa El Salvador",
            Tipo = "Departamento",
            Descripcion = "Bonito y moderno departamento de estreno cerca del Parque Industrial. Excelente ventilación e iluminación natural.",
            Ubicacion = "Sector 2 Grupo 15, Villa El Salvador",
            Distrito = "Villa El Salvador",
            PrecioNoche = 90,
            TarifaLimpieza = 30,
            MaxHuespedes = 4,
            Habitaciones = 2,
            Rating = 4.9,
            TotalResenas = 21,
            GradienteColor = "linear-gradient(135deg, #8B5CF6, #C4B5FD)",
            EstadoPublicacion = "Activo",
            ImagenUrl = "https://i.postimg.cc/zGQ2nh0z/DEPA-VILLASALVADOR.avif",
            Servicios = new() { "Smart TV", "Cocina equipada", "Wifi", "Ventilador" },
            AnfitrionNombre = "Maribel Quispe",
            AnfitrionInicial = "MQ",
            AnfitrionDesde = "2022",
            PoliticaCancelacion = "Flexible - 48 horas antes",
            Reglas = "Prohibido fiestas, mantener la limpieza",
            Resenas = new()
        },
        new Alojamiento
        {
            Id = 11,
            Nombre = "Casa de Campo en Puente Piedra",
            Tipo = "Casa",
            Descripcion = "Escapa del bullicio sin salir de Lima. Casa con áreas verdes y piscina pequeña. Perfecto para parrillas en familia los fines de semana.",
            Ubicacion = "Urb. Las Vegas, Puente Piedra",
            Distrito = "Puente Piedra",
            PrecioNoche = 250,
            TarifaLimpieza = 70,
            MaxHuespedes = 8,
            Habitaciones = 3,
            Rating = 4.7,
            TotalResenas = 40,
            GradienteColor = "linear-gradient(135deg, #14B8A6, #5EEAD4)",
            EstadoPublicacion = "Activo",
            ImagenUrl = "https://i.postimg.cc/mgk6zjNh/Casa-peuentepiedra.jpg",
            Servicios = new() { "Piscina", "Zona de parrilla", "Jardín amplio", "Estacionamiento" },
            AnfitrionNombre = "Roberto Sánchez",
            AnfitrionInicial = "RS",
            AnfitrionDesde = "2018",
            PoliticaCancelacion = "Moderada - 7 días antes",
            Reglas = "Música a volumen moderado, recoger la basura tras la parrilla",
            Resenas = new()
        },
        new Alojamiento
        {
            Id = 12,
            Nombre = "Cuarto Económico en Comas",
            Tipo = "Habitación",
            Descripcion = "Habitación sencilla y limpia en el paradero La Pascana. Movilidad a toda hora y rodeada de mercado, bancos y restaurantes.",
            Ubicacion = "Av. Túpac Amaru Km 11, Comas",
            Distrito = "Comas",
            PrecioNoche = 35,
            TarifaLimpieza = 10,
            MaxHuespedes = 2,
            Habitaciones = 1,
            Rating = 4.0,
            TotalResenas = 11,
            GradienteColor = "linear-gradient(135deg, #64748B, #CBD5E1)",
            EstadoPublicacion = "Activo",
            ImagenUrl = "https://i.postimg.cc/8kwHRWC7/697d12b9cf342.jpg",
            Servicios = new() { "Cama matrimonial", "Baño privado", "Agua caliente" },
            AnfitrionNombre = "Julio Fernández",
            AnfitrionInicial = "JF",
            AnfitrionDesde = "2024",
            PoliticaCancelacion = "Flexible",
            Reglas = "No mascotas, presentar DNI al llegar",
            Resenas = new()
        },
        new Alojamiento
        {
            Id = 13,
            Nombre = "Espacioso Depa en San Juan de Miraflores",
            Tipo = "Departamento",
            Descripcion = "Ubicado a 3 cuadras del Mall del Sur. Disfruta de un espacio completo con balcón, ideal para tu viaje de negocios o paseo familiar.",
            Ubicacion = "Calle Los Lirios 230, San Juan de Miraflores",
            Distrito = "San Juan de Miraflores",
            PrecioNoche = 110,
            TarifaLimpieza = 35,
            MaxHuespedes = 5,
            Habitaciones = 2,
            Rating = 4.6,
            TotalResenas = 29,
            GradienteColor = "linear-gradient(135deg, #EC4899, #F9A8D4)",
            EstadoPublicacion = "Activo",
            ImagenUrl = "https://i.postimg.cc/PfK4WZqP/DEPA-SANJUAN-MIRADLORES.jpg",
            Servicios = new() { "Wifi 5G", "Smart TV 55\"", "Cocina completa", "Balcón" },
            AnfitrionNombre = "Paola Vargas",
            AnfitrionInicial = "PV",
            AnfitrionDesde = "2021",
            PoliticaCancelacion = "Moderada - 3 días antes",
            Reglas = "Sin fiestas, apagar luces al salir",
            Resenas = new()
        },
        new Alojamiento
        {
            Id = 14,
            Nombre = "Casa Completa en Ate",
            Tipo = "Casa",
            Descripcion = "Alojamiento entero cerca al Real Plaza Puruchuco. Casa de dos pisos con mucha seguridad en condominio cerrado.",
            Ubicacion = "Av. Prolongación Javier Prado, Ate",
            Distrito = "Ate",
            PrecioNoche = 130,
            TarifaLimpieza = 40,
            MaxHuespedes = 5,
            Habitaciones = 3,
            Rating = 4.4,
            TotalResenas = 16,
            GradienteColor = "linear-gradient(135deg, #F97316, #FDBA74)",
            EstadoPublicacion = "Activo",
            ImagenUrl = "https://i.postimg.cc/y6nPm9N8/ATE.webp",
            Servicios = new() { "Seguridad 24/7", "Estacionamiento", "Lavadora", "TV Cable" },
            AnfitrionNombre = "Diego Huamán",
            AnfitrionInicial = "DH",
            AnfitrionDesde = "2020",
            PoliticaCancelacion = "Moderada - 5 días antes",
            Reglas = "Respetar los horarios del condominio",
            Resenas = new()
        },
        new Alojamiento
        {
            Id = 15,
            Nombre = "Estudio Práctico en Santa Anita",
            Tipo = "Departamento",
            Descripcion = "Mini estudio perfecto para parejas. A minutos del Óvalo Santa Anita, rodeado de transporte fácil hacia cualquier punto de Lima.",
            Ubicacion = "Calle Los Ficus 112, Santa Anita",
            Distrito = "Santa Anita",
            PrecioNoche = 70,
            TarifaLimpieza = 20,
            MaxHuespedes = 2,
            Habitaciones = 1,
            Rating = 4.3,
            TotalResenas = 25,
            GradienteColor = "linear-gradient(135deg, #06B6D4, #67E8F9)",
            EstadoPublicacion = "Activo",
            ImagenUrl = "https://i.postimg.cc/rygJ1WpF/SANTA-ANITA.webp",
            Servicios = new() { "Wifi", "Microondas", "Frigobar", "Agua caliente" },
            AnfitrionNombre = "Karina Mendoza",
            AnfitrionInicial = "KM",
            AnfitrionDesde = "2023",
            PoliticaCancelacion = "Flexible - 24 horas antes",
            Reglas = "No fumar dentro del estudio",
            Resenas = new()
        },
        new Alojamiento
        {
            Id = 16,
            Nombre = "Habitación Tranquila en Villa María del Triunfo",
            Tipo = "Habitación",
            Descripcion = "Descansa en una zona residencial tranquila de VMT (José Gálvez). Habitación con vista a la calle, mucha luz y ambiente familiar.",
            Ubicacion = "Jr. Progreso 456, Villa María del Triunfo",
            Distrito = "Villa María del Triunfo",
            PrecioNoche = 40,
            TarifaLimpieza = 10,
            MaxHuespedes = 1,
            Habitaciones = 1,
            Rating = 4.1,
            TotalResenas = 9,
            GradienteColor = "linear-gradient(135deg, #84CC16, #D9F99D)",
            EstadoPublicacion = "Activo",
            ImagenUrl = "https://i.postimg.cc/RCGR7f0S/VILA-MARIA-TRIUNFO.jpg",
            Servicios = new() { "Cama cómoda", "Ropero", "Baño compartido" },
            AnfitrionNombre = "Teresa Aguilar",
            AnfitrionInicial = "TA",
            AnfitrionDesde = "2024",
            PoliticaCancelacion = "Flexible",
            Reglas = "Mantener el orden en áreas comunes",
            Resenas = new()
        }
    };

    public Alojamiento? ObtenerAlojamiento(int id) =>
        ObtenerAlojamientos().FirstOrDefault(a => a.Id == id);

    public List<Alojamiento> FiltrarAlojamientos(BusquedaFiltros filtros)
    {
        var lista = ObtenerAlojamientos().AsEnumerable();

        if (!string.IsNullOrWhiteSpace(filtros.Ubicacion))
            lista = lista.Where(a => a.Distrito.Contains(filtros.Ubicacion, StringComparison.OrdinalIgnoreCase)
                                  || a.Nombre.Contains(filtros.Ubicacion, StringComparison.OrdinalIgnoreCase));

        if (filtros.PrecioMaximo < 500)
            lista = lista.Where(a => a.PrecioNoche <= filtros.PrecioMaximo);

        if (filtros.TiposSeleccionados.Count > 0)
            lista = lista.Where(a => filtros.TiposSeleccionados.Contains(a.Tipo));

        if (filtros.RatingMinimo == "4+ estrellas")
            lista = lista.Where(a => a.Rating >= 4.0);
        else if (filtros.RatingMinimo == "4.5+ estrellas")
            lista = lista.Where(a => a.Rating >= 4.5);
        else if (filtros.RatingMinimo == "5 estrellas")
            lista = lista.Where(a => a.Rating >= 5.0);

        if (filtros.Huespedes > 1)
            lista = lista.Where(a => a.MaxHuespedes >= filtros.Huespedes);

        return lista.ToList();
    }

    // =============================================
    // RESERVAS
    // =============================================

    public List<Reserva> ObtenerReservasProximas() => new()
    {
        new Reserva
        {
            Id = 1,
            AlojamientoNombre = "Departamento en Miraflores",
            Ubicacion = "Miraflores, Lima",
            FechaEntrada = new DateTime(2026, 5, 15),
            FechaSalida = new DateTime(2026, 5, 20),
            NumHuespedes = 2,
            Total = 825,
            Estado = "Confirmada",
            AnfitrionNombre = "María Andrades"
        },
        new Reserva
        {
            Id = 2,
            AlojamientoNombre = "Casa en La Molina",
            Ubicacion = "La Molina, Lima",
            FechaEntrada = new DateTime(2026, 5, 28),
            FechaSalida = new DateTime(2026, 6, 3),
            NumHuespedes = 4,
            Total = 1200,
            Estado = "Pendiente",
            AnfitrionNombre = "Carlos Rodríguez"
        }
    };

    public List<Reserva> ObtenerReservasPasadas() => new()
    {
        new Reserva
        {
            Id = 3,
            AlojamientoNombre = "Habitación en Barranco",
            Ubicacion = "Barranco, Lima",
            FechaEntrada = new DateTime(2026, 5, 1),
            FechaSalida = new DateTime(2026, 5, 5),
            NumHuespedes = 1,
            Total = 400,
            Estado = "Completada",
            AnfitrionNombre = "Ana Torres"
        }
    };

    public List<Reserva> ObtenerReservasCanceladas() => new();

    // =============================================
    // USUARIO
    // =============================================

    public Usuario ObtenerUsuarioActual() => new Usuario
    {
        Id = 1,
        Nombre = "Juan",
        Apellido = "Pérez",
        Email = "juan@ejemplo.com",
        Telefono = "+51 999 999 999",
        Ciudad = "Lima",
        Bio = "Amante de los viajes y la buena comida.",
        TipoUsuario = "Cliente",
        Verificado = true,
        MiembroDesde = "Enero 2024",
        Rating = 4.9,
        TotalResenas = 48,
        TotalReservas = 15,
        TotalNoches = 62,
        GastoTotal = 8500
    };

    // =============================================
    // MENSAJES
    // =============================================

    public List<Conversacion> ObtenerConversaciones() => new()
    {
        new Conversacion
        {
            Id = 1,
            ContactoNombre = "María Andrades",
            ContactoInicial = "MA",
            UltimoMensaje = "Hola, ¿tienes preguntas del alojamiento?",
            MensajesNoLeidos = 2,
            EstaEnLinea = true,
            Rol = "Anfitrión",
            Mensajes = new()
            {
                new() { Id = 1, AutorNombre = "María Andrades", AutorInicial = "MA", Texto = "¡Hola Juan! ¿Cómo estás? Espero que tengas todo lo que necesitas.", Hora = "10:30 AM", EsMio = false },
                new() { Id = 2, AutorNombre = "Tú", AutorInicial = "JP", Texto = "¡Hola María! Excelente lugar, muchas gracias. Solo quería preguntar sobre las reglas de la casa.", Hora = "10:35 AM", EsMio = true },
                new() { Id = 3, AutorNombre = "María Andrades", AutorInicial = "MA", Texto = "Claro, no hay problema. Las reglas principales son: sin mascotas, sin fiestas después de las 22:00, y por favor mantener la casa limpia. ¿Alguna otra pregunta?", Hora = "10:40 AM", EsMio = false },
                new() { Id = 4, AutorNombre = "Tú", AutorInicial = "JP", Texto = "Perfecto, entendido. ¡Muchas gracias!", Hora = "10:45 AM", EsMio = true }
            }
        },
        new Conversacion
        {
            Id = 2,
            ContactoNombre = "Carlos Ruiz",
            ContactoInicial = "CR",
            UltimoMensaje = "¿A qué hora puedo hacer el check-in?",
            EstaEnLinea = false,
            Rol = "Anfitrión",
            Mensajes = new()
            {
                new() { Id = 1, AutorNombre = "Carlos Ruiz", AutorInicial = "CR", Texto = "¿A qué hora puedo hacer el check-in?", Hora = "Ayer", EsMio = false }
            }
        },
        new Conversacion
        {
            Id = 3,
            ContactoNombre = "Laura Mendez",
            ContactoInicial = "LM",
            UltimoMensaje = "Muchas gracias, todo estuvo perfecto!",
            EstaEnLinea = false,
            Rol = "Anfitrión",
            Mensajes = new()
            {
                new() { Id = 1, AutorNombre = "Laura Mendez", AutorInicial = "LM", Texto = "Muchas gracias, todo estuvo perfecto!", Hora = "Lunes", EsMio = false }
            }
        }
    };

    // =============================================
    // NOTIFICACIONES
    // =============================================

    public List<Notificacion> ObtenerNotificaciones() => new()
    {
        new Notificacion
        {
            Id = 1,
            Titulo = "Nueva reserva confirmada",
            Descripcion = "Tu reserva en \"Departamento en Miraflores\" ha sido confirmada para el 15-20 de mayo.",
            Tiempo = "Hace 2 horas",
            Categoria = "Reserva",
            Leida = false,
            AccionTexto = "Ver",
            AccionUrl = "/mis-reservas"
        },
        new Notificacion
        {
            Id = 2,
            Titulo = "Mensaje nuevo de María Andrades",
            Descripcion = "María te ha enviado un mensaje. \"¿Tienes alguna pregunta sobre el alojamiento?\"",
            Tiempo = "Hace 4 horas",
            Categoria = "Mensaje",
            Leida = true,
            AccionTexto = "Responder",
            AccionUrl = "/mensajes"
        },
        new Notificacion
        {
            Id = 3,
            Titulo = "Recordatorio: Pendiente de pago",
            Descripcion = "Tu reserva en \"Casa en La Molina\" está pendiente de pago. S/ 1,200 vence en 2 días.",
            Tiempo = "Hace 1 día",
            Categoria = "Pago",
            Leida = true,
            AccionTexto = "Pagar",
            AccionUrl = "/pagos"
        },
        new Notificacion
        {
            Id = 4,
            Titulo = "¡Se abre una nueva habitación que te interesa!",
            Descripcion = "Un nuevo alojamiento en Miraflores coincide con tus filtros de búsqueda.",
            Tiempo = "Hace 3 días",
            Categoria = "General",
            Leida = true,
            AccionTexto = "Ver",
            AccionUrl = "/buscar"
        },
        new Notificacion
        {
            Id = 5,
            Titulo = "Reseña recibida",
            Descripcion = "María Andrades dejó una reseña de 5 estrellas en tu perfil.",
            Tiempo = "Hace 5 días",
            Categoria = "General",
            Leida = true,
            AccionTexto = "Ver",
            AccionUrl = "/perfil"
        }
    };

    // =============================================
    // PANEL ANFITRION
    // =============================================

    public List<Alojamiento> ObtenerAlojamientosAnfitrion() =>
        ObtenerAlojamientos().Take(3).ToList();

    public (int ReservasActivas, decimal IngresosEsteMes, double RatingPromedio, int TasaOcupacion)
        ObtenerEstadisticasAnfitrion() => (24, 8500m, 4.8, 92);

    // =============================================
    // ADMIN
    // =============================================

    public (int Usuarios, int Alojamientos, int Reservas, decimal Ingresos)
        ObtenerEstadisticasAdmin() => (1250, 345, 890, 125430m);

    public List<(string Nombre, string Anfitrion, string Tipo, string Fecha)>
        ObtenerAlojamientosPendientes() => new()
    {
        ("Habitación Barranco", "Carlos R.", "Habitación", "2026-05-08"),
        ("Casa Miraflores", "Laura M.", "Casa", "2026-05-07")
    };

    public List<(string Id, string Usuario, string Tipo, string Estado, string Fecha)>
        ObtenerReportesAdmin() => new()
    {
        ("#REP001", "Juan P.", "Comportamiento Inapropiado", "En Revisión", "2026-05-07"),
        ("#REP002", "María A.", "Incumplimiento", "Resuelto", "2026-05-05")
    };

    // =============================================
    // HELPERS
    // =============================================

    public string GenerarEstrellas(double rating)
    {
        int llenas = (int)Math.Round(rating);
        var sb = new System.Text.StringBuilder();
        for (int i = 0; i < 5; i++)
            sb.Append(i < llenas ? "★" : "☆");
        return sb.ToString();
    }
}
