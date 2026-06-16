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
    private int estrellas;
    private String comentario;
    private String fechaPublicacion;
    private int alojamientoId;
    private String alojamientoNombre;
    private int reservaId;
    private String tipoAutor;
    private boolean activo;

    public ResenaDTO() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAutorNombre() { return autorNombre; }
    public void setAutorNombre(String autorNombre) { this.autorNombre = autorNombre; }

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
}
