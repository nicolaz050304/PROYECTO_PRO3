package pe.edu.pe.pucp.proyecto.users;

import pe.edu.pe.pucp.proyecto.accomodations.Alojamiento;
import pe.edu.pe.pucp.proyecto.tipoDoc.TipoDocumento;
import java.util.ArrayList;
import java.util.List;

public class Anfitrion extends Cliente {
    private List<Alojamiento> propiedades;

    public Anfitrion(){
        super();
        this.propiedades = new ArrayList<>();
        this.agregarRol("ANFITRION");
    }

    public Anfitrion(int idUsuario, String username, String correo, String password, String nombre,
                     String apellidoPaterno, String apellidoMaterno, String pais,
                     TipoDocumento tipoDocumento, String numeroDocumento, String telefono, // <-- Atributos del abuelo
                     double puntuacionPromedio, List<Alojamiento> propiedades,
                     boolean estadoSesion, EstadoUsuario estadoActual) {

        // MODIFICADO: Llamada al super de Cliente con los 11 parámetros exactos requeridos
        super(idUsuario, username, correo, password, nombre, apellidoPaterno, apellidoMaterno,
                pais, tipoDocumento, numeroDocumento, telefono,
                puntuacionPromedio, estadoSesion, estadoActual);

        this.propiedades = (propiedades != null) ? propiedades : new ArrayList<>();
        this.agregarRol("ANFITRION"); // Sincronizado con el SET de la DB
    }

    public List<Alojamiento> getAlojamientos() {
        return propiedades;
    }

    public void setAlojamientos(List<Alojamiento> propiedades) {
        this.propiedades = propiedades;
    }

    @Override
    public void consultarDatos() {
        super.consultarDatos(); // Llama a la consulta base de Cliente/Usuario
        System.out.println("Propiedades registradas: " + propiedades.size());
        System.out.println("===============================");
    }

    public void agregarAlojamiento(Alojamiento alo) {
        if (this.propiedades == null) this.propiedades = new ArrayList<>();
        this.propiedades.add(alo);
    }
}