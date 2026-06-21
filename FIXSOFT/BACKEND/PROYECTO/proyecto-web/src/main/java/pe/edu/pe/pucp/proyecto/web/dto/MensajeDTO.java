package pe.edu.pe.pucp.proyecto.web.dto;

/**
 * DTO plano de Mensaje (RF17). Nunca se exponen las entidades (Mensaje tiene Usuario y
 * Reserva con ciclos): solo ids y los datos que el chat necesita.
 *
 * El chat se organiza POR RESERVA: cliente y anfitrión conversan sobre una reserva, por eso
 * idReserva identifica el hilo. nombreEmisor puede venir null si el emisor llega como proxy
 * solo-id desde el DAO; en ese caso el frontend resuelve el nombre.
 */
public class MensajeDTO {

    private int idMensaje;
    private String texto;
    private String fechaEnvio;   // formateada a String para el frontend
    private int idEmisor;
    private String nombreEmisor; // puede ser null si el emisor es proxy solo-id
    private int idReserva;
    private boolean leido;       // estado de lectura del mensaje

    public MensajeDTO() {}

    public int getIdMensaje() { return idMensaje; }
    public void setIdMensaje(int idMensaje) { this.idMensaje = idMensaje; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public String getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(String fechaEnvio) { this.fechaEnvio = fechaEnvio; }

    public int getIdEmisor() { return idEmisor; }
    public void setIdEmisor(int idEmisor) { this.idEmisor = idEmisor; }

    public String getNombreEmisor() { return nombreEmisor; }
    public void setNombreEmisor(String nombreEmisor) { this.nombreEmisor = nombreEmisor; }

    public int getIdReserva() { return idReserva; }
    public void setIdReserva(int idReserva) { this.idReserva = idReserva; }

    public boolean isLeido() { return leido; }
    public void setLeido(boolean leido) { this.leido = leido; }
}
