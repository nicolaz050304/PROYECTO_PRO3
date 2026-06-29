package pe.edu.pe.pucp.proyecto.reviews;

// NUEVO: Importamos Reserva
import pe.edu.pe.pucp.proyecto.reservation.Reserva;
import java.util.Date;

public class Resenha {
    private int idResenha;
    private int calificacion;
    private String comentario;
    private Date fechaPublicacion;
    private boolean activo;

    // MODIFICADO: Eliminados idAlojamiento e idInvitado.
    // Agregamos tipoAutor y el objeto Reserva. Reserva ps vamos a manejar las resenhas respecto al tiempo
    private String tipoAutor;
    private Reserva reserva;

    // RF19: a QUÉ apunta la reseña del huésped: "ALOJAMIENTO" (el lugar, comportamiento histórico)
    // o "ANFITRION" (la persona). Default ALOJAMIENTO para no alterar las reseñas existentes.
    private String objetivo = "ALOJAMIENTO";

    // Constructor vacío
    public Resenha() {
    }

    // MODIFICADO: Constructor actualizado
    public Resenha(int idResenha, int calificacion, String comentario, Date fechaPublicacion, String tipoAutor, Reserva reserva) {
        this.idResenha = idResenha;
        this.calificacion = calificacion;
        this.comentario = comentario;
        this.fechaPublicacion = fechaPublicacion;
        this.tipoAutor = tipoAutor; // "ANFITRION" o "INVITADO"
        this.reserva = reserva;
        this.activo = true;
    }

    // Constructor sin relaciones (mantenido por si lo usas en pruebas)
    public Resenha(int idResenha, int calificacion, String comentario, Date fechaPublicacion) {
        this.idResenha = idResenha;
        this.calificacion = calificacion;
        this.comentario = comentario;
        this.fechaPublicacion = fechaPublicacion;
        this.activo = true;
    }

    // --- GETTERS Y SETTERS ---
    public int getIdResenha() { return idResenha; }
    public void setIdResenha(int idResenha) { this.idResenha = idResenha; }

    public int getCalificacion() { return calificacion; }
    public void setCalificacion(int calificacion) { this.calificacion = calificacion; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public Date getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(Date fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    // NUEVOS Getters y Setters
    public String getTipoAutor() { return tipoAutor; }
    public void setTipoAutor(String tipoAutor) { this.tipoAutor = tipoAutor; }

    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }

    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }

    // --- MÉTODOS DE NEGOCIO ---
    public void editarResenha(String nuevoComentario, int nuevaCalificacion) {
        this.comentario = nuevoComentario;
        this.calificacion = nuevaCalificacion;
    }

    public void eliminarResenha() {
        this.activo = false;
        System.out.println("La reseña " + idResenha + " ha sido marcada como eliminada.");
    }
}