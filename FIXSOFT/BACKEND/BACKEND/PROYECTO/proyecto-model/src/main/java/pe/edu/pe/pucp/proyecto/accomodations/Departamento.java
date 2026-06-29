package pe.edu.pe.pucp.proyecto.accomodations;

import pe.edu.pe.pucp.proyecto.users.Anfitrion;

public class Departamento extends Alojamiento {
    private int numPiso;
    private String nroDepartamento;
    private int nroHabitaciones;

    // Constructor vacío para flexibilidad del DAO
    public Departamento() {
        super();
    }

    public Departamento(String nombre, String descripcion, double precioPorNoche, String direccion,
                        int capacidadMax, double calificacionPromedio, boolean disponibilidad,
                        Anfitrion duenho, String pais, int numPiso, String nroDepartamento,
                        int nroHabitaciones) {

        super(nombre, descripcion, precioPorNoche, direccion, capacidadMax,
                calificacionPromedio, disponibilidad, duenho, pais);

        this.numPiso = numPiso;
        this.nroDepartamento = nroDepartamento;
        this.nroHabitaciones = nroHabitaciones;
    }

    @Override
    public void consultarDatos() {
        // Imprime datos generales (incluyendo coordenadas y validación)
        super.consultarDatos();
        System.out.println("--- Detalles del Departamento ---");
        System.out.println("Piso: " + numPiso);
        System.out.println("Nro. Departamento: " + nroDepartamento);
        System.out.println("Habitaciones: " + nroHabitaciones);
        System.out.println("---------------------------------");
    }

    // --- GETTERS Y SETTERS ---
    public int getNumPiso() { return numPiso; }
    public void setNumPiso(int numPiso) { this.numPiso = numPiso; }

    public String getNroDepartamento() { return nroDepartamento; }
    public void setNroDepartamento(String nroDepartamento) { this.nroDepartamento = nroDepartamento; }

    public int getNroHabitaciones() { return nroHabitaciones; }
    public void setNroHabitaciones(int nroHabitaciones) { this.nroHabitaciones = nroHabitaciones; }
}