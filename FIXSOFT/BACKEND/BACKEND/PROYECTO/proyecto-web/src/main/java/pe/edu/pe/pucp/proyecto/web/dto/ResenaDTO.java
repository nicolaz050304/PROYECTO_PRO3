package pe.edu.pe.pucp.proyecto.web.dto;

/**
 * DTO plano de Reseña. Único objeto expuesto por los endpoints: sin entidades
 * del modelo, sin objetos anidados -> evita los ciclos de serialización
 * (Resenha->Reserva->Invitado/Alojamiento->...).
 *
 * Campos camelCase; el System.Text.Json del frontend C# mapea camelCase ->
 * PascalCase case-insensitive y calza con la clase Resena ampliada
 * (AlojamientoId, ReservaId, TipoAutor, Activo). fechaPublicacion viaja como String
 * ISO "yyyy-MM-dd" (dato real, sin líos de zona horaria); el C# lo deserializa a
 * DateTime? y aplica el formato bonito al mostrar.
 */
public class ResenaDTO {

    private int id;
    private String autorNombre;
    // Id del autor de la reseña (para que el frontend muestre editar/eliminar solo en las propias).
    private int autorId;
    private int estrellas;
    private String comentario;
    private String fechaPublicacion;
    private int alojamientoId;
    private String alojamientoNombre;
    private int reservaId;
    private String tipoAutor;
    private boolean activo;
    // RF19: a qué apunta la reseña del huésped: "ALOJAMIENTO" (default) o "ANFITRION".
    private String objetivo;
    // Anfitrión (dueño del alojamiento de la reserva). Útil para las reseñas objetivo=ANFITRION.
    private int anfitrionId;
    private String anfitrionNombre;

    public ResenaDTO() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAutorNombre() { return autorNombre; }
    public void setAutorNombre(String autorNombre) { this.autorNombre = autorNombre; }

    public int getAutorId() { return autorId; }
    public void setAutorId(int autorId) { this.autorId = autorId; }

    public int getEstrellas() { return estrellas; }
    public void setEstrellas(int estrellas) { this.estrellas = estrellas; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public String getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(String fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }

    public int getAlojamientoId() { return alojamientoId; }
    public void setAlojamientoId(int alojamientoId) { this.alojamientoId = alojamientoId; }

    public String getAlojamientoNombre() { return alojamientoNombre; }
    public void setAlojamientoNombre(String alojamientoNombre) { this.alojamientoNombre = alojamientoNombre; }

    public int getReservaId() { return reservaId; }
    public void setReservaId(int reservaId) { this.reservaId = reservaId; }

    public String getTipoAutor() { return tipoAutor; }
    public void setTipoAutor(String tipoAutor) { this.tipoAutor = tipoAutor; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }

    public int getAnfitrionId() { return anfitrionId; }
    public void setAnfitrionId(int anfitrionId) { this.anfitrionId = anfitrionId; }

    public String getAnfitrionNombre() { return anfitrionNombre; }
    public void setAnfitrionNombre(String anfitrionNombre) { this.anfitrionNombre = anfitrionNombre; }
}
