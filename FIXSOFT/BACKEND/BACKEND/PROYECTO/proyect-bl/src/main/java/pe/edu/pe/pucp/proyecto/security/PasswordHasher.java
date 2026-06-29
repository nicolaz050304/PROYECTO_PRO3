package pe.edu.pe.pucp.proyecto.security;

import org.mindrot.jbcrypt.BCrypt;

/** Hashing de contraseñas con BCrypt: centraliza hash y verificación. */
public final class PasswordHasher {

    private PasswordHasher() { }

    /** Hash BCrypt (con salt) de una contraseña en texto plano. */
    public static String hash(String plano) {
        return BCrypt.hashpw(plano, BCrypt.gensalt(12));
    }

    /** Verifica una contraseña plana contra un hash BCrypt. Tolera hashes mal formados. */
    public static boolean verify(String plano, String hash) {
        if (plano == null || hash == null) return false;
        try {
            return BCrypt.checkpw(plano, hash);
        } catch (IllegalArgumentException ex) {
            return false; // el valor almacenado no es un hash BCrypt válido
        }
    }

    /** ¿El valor almacenado ya parece un hash BCrypt? (60 chars, prefijo $2a/$2b/$2y$). */
    public static boolean looksHashed(String valor) {
        return valor != null
            && valor.length() == 60
            && (valor.startsWith("$2a$") || valor.startsWith("$2b$") || valor.startsWith("$2y$"));
    }
}
