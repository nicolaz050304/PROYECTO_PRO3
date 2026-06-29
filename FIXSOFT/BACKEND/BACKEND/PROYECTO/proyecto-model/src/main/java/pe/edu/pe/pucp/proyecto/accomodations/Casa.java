package pe.edu.pe.pucp.proyecto.accomodations;

import pe.edu.pe.pucp.proyecto.users.Anfitrion;

public class Casa extends Alojamiento {

    private int numPisos;
    private boolean conPatio;
    private int numCocheras;
    private int numHabitaciones;

    public Casa() {
        super();
    }

    public Casa(String nombre, String descripcion, double precioPorNoche,
                String direccion, int capacidadMax, double calificacionPromedio,
                boolean disponibilidad, Anfitrion duenho, String pais,
                int numPisos, boolean conPatio, int numCocheras, int numHabitaciones) {

        super(nombre, descripcion, precioPorNoche, direccion, capacidadMax,
                calificacionPromedio, disponibilidad, duenho, pais);

        this.numPisos = numPisos;
        this.conPatio = conPatio;
        this.numCocheras = numCocheras;
        // CORREGIDO: Faltaba asignar el número de habitaciones
        this.numHabitaciones = numHabitaciones;
    }

    @Override
    public void consultarDatos() {
        // Llamamos al padre para ver los datos comunes (incluyendo latitud/longitud)
        super.consultarDatos();
        System.out.println("--- Detalles de la Casa ---");
        System.out.println("Pisos: " + numPisos);
        System.out.println("Habitaciones: " + numHabitaciones);
        System.out.println("Cocheras: " + numCocheras);
        System.out.println("¿Tiene patio?: " + (conPatio ? "Sí" : "No"));
        System.out.println("---------------------------");
    }

    // --- GETTERS Y SETTERS ---
    public int getNumPisos() { return numPisos; }
    public void setNumPisos(int numPisos) { this.numPisos = numPisos; }

    public boolean isConPatio() { return conPatio; }
    public void setConPatio(boolean conPatio) { this.conPatio = conPatio; }

    public int getNumCocheras() { return numCocheras; }
    public void setNumCocheras(int numCocheras) { this.numCocheras = numCocheras; }

    public int getNumHabitaciones() { return numHabitaciones; }
    public void setNumHabitaciones(int numHabitaciones) { this.numHabitaciones = numHabitaciones; }
}