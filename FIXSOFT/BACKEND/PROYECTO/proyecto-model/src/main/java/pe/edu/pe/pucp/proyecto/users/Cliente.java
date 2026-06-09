package pe.edu.pe.pucp.proyecto.users;

import pe.edu.pe.pucp.proyecto.cuentaBank.CuentaBancaria;
import pe.edu.pe.pucp.proyecto.interfaceConsults.IConsultable;
import pe.edu.pe.pucp.proyecto.tipoDoc.TipoDocumento;

import java.util.ArrayList;
import java.util.List;

public abstract class Cliente extends Usuario implements IConsultable {

    // ELIMINAMOS telefono y tipoDocumento porque AHORA SE HEREDAN DEL PADRE (Usuario)
    private double puntuacionPromedio;

    // MODIFICADO (Corrección JP): Inversión de relación
    // Ahora el cliente tiene una lista de cuentas, no solo una.
    private List<CuentaBancaria> cuentasBancarias;

    public Cliente() {
        super();
        this.cuentasBancarias = new ArrayList<>();
    }

    public Cliente(int idUsuario, String username, String correo, String password, String nombre,
                   String apellidoPaterno, String apellidoMaterno, String pais,
                   TipoDocumento tipoDocumento, String numeroDocumento, String telefono, // <-- Atributos del padre
                   double puntuacionPromedio,
                   boolean estadoSesion, EstadoUsuario estadoActual) {

        // MODIFICADO: Le pasamos los 11 parámetros exactos al padre (Usuario)
        super(idUsuario, username, correo, password, nombre, apellidoPaterno, apellidoMaterno,
                pais, tipoDocumento, numeroDocumento, telefono, estadoSesion, estadoActual);

        this.puntuacionPromedio = puntuacionPromedio;
        this.cuentasBancarias = new ArrayList<>(); // Inicializamos la lista vacía
    }

    // --- GETTERS Y SETTERS EXCLUSIVOS DE CLIENTE ---
    // (Nota: getTelefono() y getTipoDocumento() ya no están aquí porque se heredan automáticamente de Usuario)

    public double getPuntuacionPromedio() {
        return puntuacionPromedio;
    }

    public void setPuntuacionPromedio(double puntuacionPromedio) {
        this.puntuacionPromedio = puntuacionPromedio;
    }

    // --- MÉTODOS DE LA LISTA DE CUENTAS (Refactor) ---

    public List<CuentaBancaria> getCuentasBancarias() {
        return cuentasBancarias;
    }

    public void setCuentasBancarias(List<CuentaBancaria> cuentasBancarias) {
        this.cuentasBancarias = cuentasBancarias;
    }

    // Método de utilidad para agregar una cuenta fácilmente
    public void agregarCuentaBancaria(CuentaBancaria cuenta) {
        if(this.cuentasBancarias == null) {
            this.cuentasBancarias = new ArrayList<>();
        }
        this.cuentasBancarias.add(cuenta);
    }

    @Override
    public void consultarDatos() {
        System.out.println("====== DATOS DEL CLIENTE ======");
        System.out.println("Nombre: " + this.getNombre() + " " + this.getApellidoPaterno());
        System.out.println("Username: " + this.getUsername());
        // Fíjate cómo podemos seguir usando getTipoDocumento() y getTelefono() porque se heredan del padre
        System.out.println("Documento: " + this.getTipoDocumento() + " - " + this.getNumeroDocumento());
        System.out.println("Teléfono: " + this.getTelefono());
        System.out.println("Puntuación Promedio: ★ " + this.getPuntuacionPromedio());
        System.out.println("Roles asignados: " + this.getRoles().toString());
        System.out.println("Cuentas asociadas: " + this.cuentasBancarias.size());
        System.out.println("===============================");
    }
}