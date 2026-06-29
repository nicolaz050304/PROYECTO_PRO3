package pe.edu.pe.pucp.proyecto.reservation;

import pe.edu.pe.pucp.proyecto.accomodations.Alojamiento;
import pe.edu.pe.pucp.proyecto.economy.TipoMoneda;
import pe.edu.pe.pucp.proyecto.users.Invitado;

import java.util.Date;

public class Reserva {
    private int idReserva;
    private Date fechaInicio;
    private Date fechaFin;
    private double montoTotal;
    private Invitado invitado;
    private Alojamiento alojamiento;

    // MODIFICADO: Reemplazamos Chat por fechaContacto
    private Date fechaContacto;

    private EstadoReserva estadoReserva;
    private TipoMoneda moneda;

    // Ver quien califica la reserva
    private boolean calificadoPorInvitado;
    private boolean calificadoPorAnfitrion;

    // Nº de huéspedes de la reserva (lo elige el invitado al reservar). Default 1.
    private int numHuespedes = 1;

    public Reserva(){}

    // MODIFICADO: Constructor actualizado
    public Reserva(int idReserva, Date fechaInicio, Date fechaFin, double montoTotal,
                   Invitado invitado, Alojamiento alojamiento, Date fechaContacto,
                   EstadoReserva estadoReserva, TipoMoneda moneda) {
        this.idReserva = idReserva;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.montoTotal = montoTotal;
        this.invitado = invitado;
        this.alojamiento = alojamiento;
        this.fechaContacto = fechaContacto; // Se guarda la fecha de contacto directamente
        this.estadoReserva = estadoReserva;
        this.moneda = moneda; // Agregado el uso de la moneda
        // Inicializamos en false por defecto al crear una reserva nueva
        this.calificadoPorInvitado=false;
        this.calificadoPorAnfitrion=false;
    }

    // --- GETTERS Y SETTERS ---
    public int getIdReserva() { return idReserva; }
    public void setIdReserva(int idReserva) { this.idReserva = idReserva; }

    public Date getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(Date fechaInicio) { this.fechaInicio = fechaInicio; }

    public Date getFechaFin() { return fechaFin; }
    public void setFechaFin(Date fechaFin) { this.fechaFin = fechaFin; }

    public double getMontoTotal() { return montoTotal; }
    public void setMontoTotal(double montoTotal) { this.montoTotal = montoTotal; }

    public Invitado getInvitado() { return invitado; }
    public void setInvitado(Invitado invitado) { this.invitado = invitado; }

    public Alojamiento getAlojamiento() { return alojamiento; }
    public void setAlojamiento(Alojamiento alojamiento) { this.alojamiento = alojamiento; }

    // NUEVO: Getters y Setters de Fecha de Contacto
    public Date getFechaContacto() { return fechaContacto; }
    public void setFechaContacto(Date fechaContacto) { this.fechaContacto = fechaContacto; }

    public EstadoReserva getEstadoReserva() { return estadoReserva; }
    public void setEstadoReserva(EstadoReserva estadoReserva) { this.estadoReserva = estadoReserva; }

    // NUEVO: Getters y Setters de Moneda
    public TipoMoneda getMoneda() { return moneda; }
    public void setMoneda(TipoMoneda moneda) { this.moneda = moneda; }

    // NUEVO: Getters y Setters de calificadoPorInvitado
    public boolean isCalificadoPorInvitado() {return calificadoPorInvitado;}
    public void setCalificadoPorInvitado(boolean calificadoPorInvitado) {
        this.calificadoPorInvitado = calificadoPorInvitado;
    }

    // NUEVO: Getters y Setters de calificadoPorAnfitrion
    public boolean isCalificadoPorAnfitrion() {return calificadoPorAnfitrion;}
    public void setCalificadoPorAnfitrion(boolean calificadoPorAnfitrion) {
        this.calificadoPorAnfitrion = calificadoPorAnfitrion;
    }

    public int getNumHuespedes() { return numHuespedes; }
    public void setNumHuespedes(int numHuespedes) { this.numHuespedes = numHuespedes; }
}