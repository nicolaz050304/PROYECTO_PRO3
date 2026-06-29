package pe.edu.pe.pucp.proyecto.accomodations;

import pe.edu.pe.pucp.proyecto.users.Anfitrion;

public class Habitacion extends Alojamiento {
    private String nroHabitacion;
    private String tipoCama;
    private boolean conbanhoPrivado;

    public Habitacion() {
        super();
    }

    public Habitacion(String nombre, String descripcion, double precioPorNoche,
                      String direccion, int capacidadMax, double calificacionPromedio,
                      boolean disponibilidad, Anfitrion duenho, String pais,
                      String nroHabitacion, String tipoCama, boolean conbanhoPrivado) {

        super(nombre, descripcion, precioPorNoche, direccion, capacidadMax,
                calificacionPromedio, disponibilidad, duenho, pais);

        this.nroHabitacion = nroHabitacion;
        this.tipoCama = tipoCama;
        this.conbanhoPrivado = conbanhoPrivado;
    }

    @Override
    public void consultarDatos() {
        // Imprime la información general heredada
        super.consultarDatos();
        System.out.println("--- Detalles de la Habitación ---");
        System.out.println("Número: " + nroHabitacion);
        System.out.println("Tipo de cama: " + tipoCama);
        System.out.println("Baño privado: " + (conbanhoPrivado ? "Sí" : "No"));
        System.out.println("---------------------------------");
    }

    // --- GETTERS Y SETTERS ---
    public String getNroHabitacion() { return nroHabitacion; }
    public void setNroHabitacion(String nroHabitacion) { this.nroHabitacion = nroHabitacion; }

    public String getTipoCama() { return tipoCama; }
    public void setTipoCama(String tipoCama) { this.tipoCama = tipoCama; }

    public boolean isConbanhoPrivado() { return conbanhoPrivado; }
    public void setConbanhoPrivado(boolean conbanhoPrivado) { this.conbanhoPrivado = conbanhoPrivado; }
}