package pe.edu.pe.pucp.proyecto.messages;

import pe.edu.pe.pucp.proyecto.reservation.Reserva;
import pe.edu.pe.pucp.proyecto.users.Usuario;
import java.util.Date;

public class Mensaje {
    private int idMensaje;      // Cambiado de String a int (coherencia SQL)
    private String texto;
    private Date fechaEnvio;
    private Usuario emisor;     // Ahora es objeto Usuario para sacar su id_usuario
    private Reserva reserva;    // Ahora se llama reserva, no chatOrigen

    public Mensaje() {}

    // Getters y Setters corregidos para que el Main compile
    public int getIdMensaje() { return idMensaje; }
    public void setIdMensaje(int idMensaje) { this.idMensaje = idMensaje; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public Date getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(Date fechaEnvio) { this.fechaEnvio = fechaEnvio; }

    public Usuario getEmisor() { return emisor; }
    public void setEmisor(Usuario emisor) { this.emisor = emisor; }

    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }
}