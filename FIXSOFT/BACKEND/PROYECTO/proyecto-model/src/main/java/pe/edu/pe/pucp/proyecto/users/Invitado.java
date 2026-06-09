package pe.edu.pe.pucp.proyecto.users;

import pe.edu.pe.pucp.proyecto.reservation.EstadoReserva;
import pe.edu.pe.pucp.proyecto.reservation.Reserva;
import pe.edu.pe.pucp.proyecto.reviews.Resenha;
import pe.edu.pe.pucp.proyecto.tipoDoc.TipoDocumento;

import java.util.ArrayList;
import java.util.List;

public class Invitado extends Cliente {
    private List<Reserva> reservas;
    private List<Resenha> resenhas;

    public Invitado(){
        super();
        this.reservas = new ArrayList<>();
        this.resenhas = new ArrayList<>();
        this.agregarRol("INVITADO");
    }

    public Invitado(int idUsuario, String username, String correo, String password, String nombre,
                    String apellidoPaterno, String apellidoMaterno, String pais,
                    TipoDocumento tipoDocumento, String numeroDocumento, String telefono, // <-- Atributos heredados
                    double puntuacionPromedio,
                    boolean estadoSesion, EstadoUsuario estadoActual) {

        // MODIFICADO: Llamada al super de Cliente con los 11 parámetros exactos
        super(idUsuario, username, correo, password, nombre, apellidoPaterno, apellidoMaterno,
                pais, tipoDocumento, numeroDocumento, telefono,
                puntuacionPromedio, estadoSesion, estadoActual);

        this.reservas = new ArrayList<>();
        this.resenhas = new ArrayList<>();
        this.agregarRol("INVITADO");
    }

    @Override
    public void consultarDatos() {
        super.consultarDatos();
        System.out.println("Reservas realizadas: " + reservas.size());
        System.out.println("===============================");
    }

    // --- MÉTODOS DE NEGOCIO ---

    public void agendarReserva(Reserva reserva) {
        if (this.getEstadoActual() == EstadoUsuario.DISPONIBLE) {
            this.reservas.add(reserva);
            System.out.println("Reserva añadida exitosamente al historial.");
        } else {
            System.out.println("Error: Su cuenta no está habilitada para reservar.");
        }
    }

    public void cancelarReserva(int codReserva) {
        for (Reserva res : reservas) {
            if (res.getIdReserva() == codReserva) {
                res.setEstadoReserva(EstadoReserva.CANCELADA);
                System.out.println("Reserva " + codReserva + " cancelada correctamente.");
                return;
            }
        }
        System.out.println("Reserva no encontrada.");
    }

    public void mostrarReservas(String estado) {
        System.out.println("Listado de reservas en estado: " + estado);
        for (Reserva r : reservas) {
            if (r.getEstadoReserva().toString().equalsIgnoreCase(estado)) {
                System.out.println("- ID: " + r.getIdReserva() + " | Alojamiento: " + r.getAlojamiento().getNombre());
            }
        }
    }

    // Getters y Setters adicionales si los necesitas
    public List<Reserva> getReservas() { return reservas; }
    public void setReservas(List<Reserva> reservas) { this.reservas = reservas; }
    public List<Resenha> getResenhas() { return resenhas; }
    public void setResenhas(List<Resenha> resenhas) { this.resenhas = resenhas; }
}