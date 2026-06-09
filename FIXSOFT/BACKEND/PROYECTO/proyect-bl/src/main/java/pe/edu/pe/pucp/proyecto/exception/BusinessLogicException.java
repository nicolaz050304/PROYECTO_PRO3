package pe.edu.pe.pucp.proyecto.exception;

public class BusinessLogicException extends Exception {
    public BusinessLogicException() {
        super();
    }

    public BusinessLogicException(String message) {
        super(message);
    }

    public BusinessLogicException(Exception ex) {
        super(ex);
    }
}
