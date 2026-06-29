package pe.edu.pe.pucp.appproyecto;

import pe.edu.pe.pucp.proyecto.security.PasswordHasher;
import pe.edu.pe.pucp.proyecto.users.Usuario;
import pe.edu.pe.pucp.proyecto.users.bl.UsuarioBL;
import pe.edu.pe.pucp.proyecto.users.implbl.UsuarioBLImpl;

/**
 * Migración de UNA sola vez (Fase 2 del hashing): convierte las contraseñas en
 * texto plano de la BD a hash BCrypt, reusando PasswordHasher de la Fase 1.
 *
 * Es IDEMPOTENTE: las contraseñas que ya son hash (looksHashed) se saltan, así
 * que correrlo dos veces no las re-hashea. Usa la BL (modificar persiste password
 * vía el UPDATE del DAO) y conecta a la misma RDS que el resto de AppProyecto.
 */
public class MigrarPasswords {
    public static void main(String[] args) {
        UsuarioBL bl = new UsuarioBLImpl();

        int migrados = 0;
        int yaHasheados = 0;
        int saltadosVacios = 0;

        System.out.println("=== MIGRACION DE CONTRASEÑAS A BCRYPT (una sola vez) ===");

        for (Usuario u : bl.listarTodos()) {
            String actual = u.getPassword();
            if (actual == null || actual.isBlank()) {
                saltadosVacios++;
                continue;
            }
            if (PasswordHasher.looksHashed(actual)) {
                yaHasheados++;
                continue;
            }
            u.setPassword(PasswordHasher.hash(actual)); // hashea su contraseña ACTUAL
            bl.modificar(u);
            migrados++;
            System.out.println("Migrado idUsuario=" + u.getIdUsuario());
        }

        System.out.println("Migracion lista. Migrados=" + migrados
                + ", ya hasheados=" + yaHasheados
                + ", sin password=" + saltadosVacios);
    }
}
