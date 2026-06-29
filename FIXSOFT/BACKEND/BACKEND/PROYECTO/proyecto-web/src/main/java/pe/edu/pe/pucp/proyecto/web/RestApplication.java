package pe.edu.pe.pucp.proyecto.web;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Activa JAX-RS. Los recursos quedan bajo:
 *   http://localhost:8080/BunkiBackend/webresources/...
 */
@ApplicationPath("webresources")
public class RestApplication extends Application {
}
