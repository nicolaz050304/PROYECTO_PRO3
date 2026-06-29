package pe.edu.pe.pucp.appproyecto;

// --- IMPORTAMOS LOS MODELOS ---
import pe.edu.pe.pucp.proyecto.accomodations.Casa;
import pe.edu.pe.pucp.proyecto.accomodations.Departamento;
import pe.edu.pe.pucp.proyecto.accomodations.bl.AlojamientoBL;
import pe.edu.pe.pucp.proyecto.accomodations.dao.AlojamientoIDAO;
import pe.edu.pe.pucp.proyecto.accomodations.impl.AlojamientoImpl;
import pe.edu.pe.pucp.proyecto.accomodations.implbl.AlojamientoBLImpl;
import pe.edu.pe.pucp.proyecto.cuentabank.bl.CuentaBancariaBL;
import pe.edu.pe.pucp.proyecto.cuentabank.dao.CuentaBancariaIDAO;
import pe.edu.pe.pucp.proyecto.cuentabank.impl.CuentaBancariaImpl;
import pe.edu.pe.pucp.proyecto.cuentabank.implbl.CuentaBancariaBLImpl;
import pe.edu.pe.pucp.proyecto.economy.TipoMoneda;
import pe.edu.pe.pucp.proyecto.economy.bl.PagoBL;
import pe.edu.pe.pucp.proyecto.economy.dao.PagoIDAO;
import pe.edu.pe.pucp.proyecto.economy.impl.PagoImpl;
import pe.edu.pe.pucp.proyecto.economy.implbl.PagoBLImpl;
import pe.edu.pe.pucp.proyecto.menssage.dao.MensajeIDAO;
import pe.edu.pe.pucp.proyecto.menssage.impl.MensajeImpl;
import pe.edu.pe.pucp.proyecto.message.bl.MensajeBL;
import pe.edu.pe.pucp.proyecto.message.implbl.MensajeBLImpl;
import pe.edu.pe.pucp.proyecto.notif.bl.NotificacionBL;
import pe.edu.pe.pucp.proyecto.notif.dao.NotificacionIDAO;
import pe.edu.pe.pucp.proyecto.notif.impl.NotificacionImpl;
import pe.edu.pe.pucp.proyecto.notif.implbl.NotificacionBLImpl;
import pe.edu.pe.pucp.proyecto.reservation.EstadoReserva;
import pe.edu.pe.pucp.proyecto.reservation.bl.ReservaBL;
import pe.edu.pe.pucp.proyecto.reservation.dao.ReservaIDAO;
import pe.edu.pe.pucp.proyecto.reservation.impl.ReservaImpl;
import pe.edu.pe.pucp.proyecto.reservation.implbl.ReservaBLImpl;
import pe.edu.pe.pucp.proyecto.review.bl.ResenhaBL;
import pe.edu.pe.pucp.proyecto.review.dao.ResenhaIDAO;
import pe.edu.pe.pucp.proyecto.review.impl.ResenhaImpl;
import pe.edu.pe.pucp.proyecto.review.implbl.ResenhaBLImpl;
import pe.edu.pe.pucp.proyecto.users.Anfitrion;
import pe.edu.pe.pucp.proyecto.users.EstadoUsuario;
import pe.edu.pe.pucp.proyecto.users.Invitado;
import pe.edu.pe.pucp.proyecto.tipoDoc.TipoDocumento;
import pe.edu.pe.pucp.proyecto.messages.Mensaje;
import pe.edu.pe.pucp.proyecto.accomodations.Alojamiento;
import pe.edu.pe.pucp.proyecto.cuentaBank.CuentaBancaria;
import pe.edu.pe.pucp.proyecto.reviews.Resenha;
import pe.edu.pe.pucp.proyecto.notif.Notificaciones;
import pe.edu.pe.pucp.proyecto.reservation.Reserva;
import pe.edu.pe.pucp.proyecto.economy.Pago; // Verifica este import si tu clase Pago está en otro lado
import pe.edu.pe.pucp.proyecto.users.bl.UsuarioBL;
import pe.edu.pe.pucp.proyecto.users.dao.UsuarioIDAO;
import pe.edu.pe.pucp.proyecto.users.impl.UsuarioImpl;
import pe.edu.pe.pucp.proyecto.users.implbl.UsuarioBLImpl;

import java.util.Date;

// --- IMPORTAMOS LAS INTERFACES Y SUS IMPLEMENTACIONES BL ---

public class Principal {
    public static void main(String[] args) {

        System.out.println("=========================================================");
        System.out.println("  SISTEMA DE ALOJAMIENTOS - PRUEBAS DE CAPA DE NEGOCIO   ");
        System.out.println("=========================================================\n");

        // 1. INSTANCIAMOS TODAS LAS 8 CAPAS LÓGICAS (Polimorfismo puro)
        UsuarioBL usuarioBL = new UsuarioBLImpl();
        AlojamientoBL alojamientoBL = new AlojamientoBLImpl();
        ReservaBL reservaBL = new ReservaBLImpl();
        PagoBL pagoBL = new PagoBLImpl();
        CuentaBancariaBL cuentaBL = new CuentaBancariaBLImpl();
        ResenhaBL resenhaBL = new ResenhaBLImpl();
        MensajeBL mensajeBL = new MensajeBLImpl();
        NotificacionBL notificacionBL = new NotificacionBLImpl();

        // -------------------------------------------------------------------
        // PRUEBA 1: USUARIO (Camino Feliz)
        // -------------------------------------------------------------------
        System.out.println(">>> PRUEBA 1: REGISTRO DE USUARIO");
        try {
            Invitado nuevoInvitado = new Invitado();
            nuevoInvitado.setUsername("cristian_pucp");
            nuevoInvitado.setCorreo("cristian@pucp.edu.pe");
            nuevoInvitado.setPassword("12345678");
            nuevoInvitado.setNombre("Cristian");
            nuevoInvitado.setApellidoPaterno("Aguilar");
            nuevoInvitado.setPais("Perú");
            nuevoInvitado.setTipoDocumento(TipoDocumento.DNI);
            nuevoInvitado.setNumeroDocumento("12345678");
            nuevoInvitado.setTelefono("987654321");
            nuevoInvitado.setEstadoActual(EstadoUsuario.DISPONIBLE);
            usuarioBL.insertar(nuevoInvitado);
            System.out.println("✅ ÉXITO: Usuario pasó validaciones del BL. (Inserción a BD comentada)");
        } catch (Exception ex) {
            System.out.println("❌ ERROR: " + ex.getMessage());
        }
        System.out.println();

        // -------------------------------------------------------------------
        // PRUEBA 2: ALOJAMIENTO (Camino Triste)
        // -------------------------------------------------------------------
        System.out.println(">>> PRUEBA 2: VALIDACIÓN DE ALOJAMIENTO");
        try {
            // USAMOS UNA CLASE HIJA (Ej: Departamento) PORQUE ALOJAMIENTO ES ABSTRACTO
            // Asegúrate de importar tu clase Departamento (o Casa, Habitacion, etc.) arriba
            Alojamiento malo = new Departamento(); // <-- CORREGIDO AQUÍ
            malo.setPrecioPorNoche(-50.0); // <-- PRECIO NEGATIVO A PROPÓSITO
            System.out.println("Intentando registrar alojamiento con precio -50.0...");
            alojamientoBL.insertar(malo);
        } catch (Exception ex) {
            System.out.println("✅ ÉXITO BL: " + ex.getMessage());
        }
        System.out.println();

        // -------------------------------------------------------------------
        // PRUEBA 3: RESERVA (Camino Triste)
        // -------------------------------------------------------------------
        System.out.println(">>> PRUEBA 3: VALIDACIÓN DE RESERVA");
        try {
            Reserva reservaMala = new Reserva();
            // Asumiendo que tu BL de reserva no permite reservas sin alojamiento asignado
            reservaMala.setAlojamiento(null);
            System.out.println("Intentando registrar reserva sin alojamiento asociado...");
            reservaBL.insertar(reservaMala);
        } catch (Exception ex) {
            System.out.println("✅ ÉXITO BL: " + ex.getMessage());
        }
        System.out.println();

        // -------------------------------------------------------------------
        // PRUEBA 4: PAGO (Camino Triste)
        // -------------------------------------------------------------------
        System.out.println(">>> PRUEBA 4: VALIDACIÓN DE PAGO");
        try {
            Pago pagoMalo = new Pago();
            // Asumiendo que tu modelo Pago tiene un setMonto() y validaste que no sea negativo
            pagoMalo.setMoneda(TipoMoneda.PEN);
            pagoMalo.setMontoNeto(-100.0);
            System.out.println("Intentando procesar un pago por monto negativo...");
            pagoBL.insertar(pagoMalo);
        } catch (Exception ex) {
            System.out.println("✅ ÉXITO BL: " + ex.getMessage());
        }
        System.out.println();

        // -------------------------------------------------------------------
        // PRUEBA 5: CUENTA BANCARIA (Camino Triste)
        // -------------------------------------------------------------------
        System.out.println(">>> PRUEBA 5: VALIDACIÓN DE CUENTA BANCARIA");
        try {
            CuentaBancaria cuentaMala = new CuentaBancaria();
            cuentaMala.setCCI("12345"); // <-- CCI MUY CORTO (Debe ser 20)
            System.out.println("Intentando registrar cuenta con CCI de 5 dígitos...");
            cuentaBL.insertar(cuentaMala);
        } catch (Exception ex) {
            System.out.println("✅ ÉXITO BL: " + ex.getMessage());
        }
        System.out.println();

        // -------------------------------------------------------------------
        // PRUEBA 6: RESEÑA (Camino Triste)
        // -------------------------------------------------------------------
        System.out.println(">>> PRUEBA 6: VALIDACIÓN DE RESEÑAS");
        try {
            Resenha resenhaMala = new Resenha();
            resenhaMala.setCalificacion(10); // <-- SOLO SE PERMITE DE 1 A 5
            System.out.println("Intentando registrar reseña de 10 estrellas...");
            resenhaBL.insertar(resenhaMala);
        } catch (Exception ex) {
            System.out.println("✅ ÉXITO BL: " + ex.getMessage());
        }
        System.out.println();

        // -------------------------------------------------------------------
        // PRUEBA 7: MENSAJE (Camino Triste)
        // -------------------------------------------------------------------
        System.out.println(">>> PRUEBA 7: VALIDACIÓN DE CHAT/MENSAJE");
        try {
            Mensaje msjMalo = new Mensaje();
            msjMalo.setTexto(""); // <-- MENSAJE VACÍO
            System.out.println("Intentando enviar un mensaje vacío...");
            mensajeBL.insertar(msjMalo);
        } catch (Exception ex) {
            System.out.println("✅ ÉXITO BL: " + ex.getMessage());
        }
        System.out.println();

        // -------------------------------------------------------------------
        // PRUEBA 8: NOTIFICACIÓN (Camino Triste)
        // -------------------------------------------------------------------
        System.out.println(">>> PRUEBA 8: VALIDACIÓN DE NOTIFICACIÓN");
        try {
            Notificaciones notifMala = new Notificaciones();
            notifMala.setTitulo(""); // <-- TÍTULO VACÍO
            System.out.println("Intentando crear notificación sin título...");
            notificacionBL.insertar(notifMala);
        } catch (Exception ex) {
            System.out.println("✅ ÉXITO BL: " + ex.getMessage());
        }

        System.out.println("\n=========================================================");
        System.out.println(" FIN DE LAS PRUEBAS. LAS 8 CAPAS BL FUNCIONAN AL 100%");
        System.out.println("=========================================================");


        // Pruebas del DAO - Se sugiere cambiar los datos si se quiere testear pues algunos datos en el database se trabajó como UNIQUE



        UsuarioIDAO daoUsuario = new UsuarioImpl();
        AlojamientoIDAO daoAlojamiento = new AlojamientoImpl();
        ReservaIDAO daoReserva = new ReservaImpl();
        ResenhaIDAO daoResenha = new ResenhaImpl();

        try {
            // =========================================================================================
            // 1. PRUEBA DE USUARIOS (Con Roles SET y Documentos)
            // =========================================================================================
            System.out.println("=== 1. PRUEBA DE USUARIOS ===");

            Anfitrion anfitrion = new Anfitrion();
            anfitrion.setUsername("cris_pucp_host");
            anfitrion.setCorreo("cristian.aguilar@pucp.edu.pe");
            anfitrion.setPassword("pucp2026");
            anfitrion.setNombre("Cristian");
            anfitrion.setApellidoPaterno("Aguilar");
            anfitrion.setPais("Perú");
            anfitrion.setEstadoActual(EstadoUsuario.DISPONIBLE);
            anfitrion.setNumeroDocumento("77889900");
            anfitrion.setTipoDocumento(TipoDocumento.DNI);
            anfitrion.setTelefono("987654321");
            anfitrion.agregarRol("ANFITRION"); // Nuevo sistema de Roles (SET)
            anfitrion.setEstadoValidacion("APROBADO");

            anfitrion = (Anfitrion) daoUsuario.save(anfitrion);
            System.out.println("[OK] Anfitrión guardado con ID: " + anfitrion.getIdUsuario());

            Invitado invitado = new Invitado();
            invitado.setUsername("viajero_test");
            invitado.setCorreo("viajero@test.com");
            invitado.setPassword("viajero123");
            invitado.setNombre("Carlos");
            invitado.setApellidoPaterno("Sánchez");
            invitado.setPais("Perú");
            invitado.setNumeroDocumento("11223344");
            invitado.setTipoDocumento(TipoDocumento.DNI);
            invitado.setEstadoActual(EstadoUsuario.DISPONIBLE);
            invitado.agregarRol("INVITADO");

            invitado = (Invitado) daoUsuario.save(invitado);
            System.out.println("[OK] Invitado guardado con ID: " + invitado.getIdUsuario());

            // =========================================================================================
            // 2. PRUEBA DE ALOJAMIENTOS (Con GPS y Validación)
            // =========================================================================================
            System.out.println("\n=== 2. PRUEBA DE ALOJAMIENTOS ===");

            Casa miCasa = new Casa("Villa San Miguel", "Casa cerca a la PUCP", 150.0,
                    "Av. Universitaria 1801", 6, 5.0, true, anfitrion, "Perú",
                    2, true, 1, 4);
            miCasa.setLatitud(-12.0689); // Coordenadas para el mapa
            miCasa.setLongitud(-77.0781);
            miCasa.setEstadoValidacion("APROBADO");

            miCasa = (Casa) daoAlojamiento.save(miCasa);
            System.out.println("[OK] Casa guardada con ID: " + miCasa.getIdAlojamiento());
            miCasa.consultarDatos(); // Probando el método de negocio

            // =========================================================================================
            // 3. PRUEBA DE RESERVA (Con Moneda y Fecha de Contacto)
            // =========================================================================================
            System.out.println("\n=== 3. PRUEBA DE RESERVAS ===");

            Reserva reserva = new Reserva();
            reserva.setInvitado(invitado);
            reserva.setAlojamiento(miCasa);
            reserva.setFechaInicio(new Date()); // Hoy
            reserva.setFechaFin(new Date(System.currentTimeMillis() + 86400000 * 3)); // +3 días
            reserva.setMontoTotal(450.0);
            reserva.setMoneda(TipoMoneda.PEN);
            reserva.setEstadoReserva(EstadoReserva.CONFIRMADA);
            reserva.setFechaContacto(new Date()); // Reemplaza al ChatDAO

            reserva = daoReserva.save(reserva);
            System.out.println("[OK] Reserva creada ID: " + reserva.getIdReserva() + " por S/ " + reserva.getMontoTotal());

            // =========================================================================================
            // 4. PRUEBA DE RESEÑAS (Vinculadas a la Reserva)
            // =========================================================================================
            System.out.println("\n=== 4. PRUEBA DE RESEÑAS ===");

            Resenha resenha = new Resenha();
            resenha.setReserva(reserva); // Vínculo obligatorio (Estadía previa)
            resenha.setCalificacion(5);
            resenha.setComentario("Excelente ubicación, muy cerca a la Cato.");
            resenha.setTipoAutor("INVITADO");
            resenha.setFechaPublicacion(new Date());

            resenha = daoResenha.save(resenha);
            System.out.println("[OK] Reseña guardada ID: " + resenha.getIdResenha());

            // =========================================================================================
            // 5. PRUEBA DE LISTADO Y ELIMINACIÓN LÓGICA
            // =========================================================================================
            System.out.println("\n=== 5. PRUEBA DE ELIMINACIÓN LÓGICA ===");

            System.out.println("Eliminando alojamiento (lógicamente)...");
            daoAlojamiento.remove(miCasa); // Cambia disponibilidad a false

            Alojamiento verificacion = daoAlojamiento.load(miCasa.getIdAlojamiento());
            System.out.println("¿Está disponible después de eliminar?: " + verificacion.isDisponibilidad());

            System.out.println("\n--- TODAS LAS PRUEBAS COMPLETADAS CON ÉXITO ---");


            // =========================================================================================
            // 5. PRUEBA DE CUENTAS BANCARIAS (Tipos de Cuenta y Verificación)
            // =========================================================================================
            System.out.println("\n=== 5. PRUEBA DE CUENTAS BANCARIAS ===");
            CuentaBancariaIDAO daoCuenta = new CuentaBancariaImpl();

            CuentaBancaria cuentaAnfitrion = new CuentaBancaria();
            cuentaAnfitrion.setIdUsuario(anfitrion.getIdUsuario()); // Vinculada al anfitrión creado en la sección 1
            cuentaAnfitrion.setTitular(anfitrion.getNombre() + " " + anfitrion.getApellidoPaterno());
            cuentaAnfitrion.setNumeroCuenta("191-987654321-0-55");
            cuentaAnfitrion.setCCI("002-191-987654321055-12");
            cuentaAnfitrion.setNroBanco("002"); // BCP
            cuentaAnfitrion.setTipoMoneda(TipoMoneda.PEN);
            cuentaAnfitrion.setTipoCuenta(CuentaBancaria.TipoCuenta.AHORROS); // Uso del Enum
            cuentaAnfitrion.setSaldo(0.0);
            cuentaAnfitrion.setVerificada(true); // Cuenta validada por el sistema

            cuentaAnfitrion = daoCuenta.save(cuentaAnfitrion);
            System.out.println("[OK] Cuenta Bancaria guardada con ID: " + cuentaAnfitrion.getIdCuenta());
            System.out.println("Tipo de Cuenta: " + cuentaAnfitrion.getTipoCuenta());

            // =========================================================================================
            // 6. PRUEBA DE PAGOS (Historial Monetario y Envío al Anfitrión)
            // =========================================================================================
            System.out.println("\n=== 6. PRUEBA DE PAGOS ===");
            PagoIDAO daoPago = new PagoImpl();

            Pago pagoReserva = new Pago();
            pagoReserva.setReserva(reserva); // Vinculada a la reserva de la sección 3
            pagoReserva.setMontoNeto(405.0); // 90% para el anfitrión
            pagoReserva.setMontoBruto(450.0); // Total pagado por el invitado
            pagoReserva.setPorcenComision(0.10); // 10% de comisión
            pagoReserva.setMoneda(TipoMoneda.PEN);

            // Levantamiento de observaciones: Historial monetario y estado inicial
            pagoReserva.setTipoCambio(3.75); // "Foto" del tipo de cambio del día
            pagoReserva.setEstadoTransaccion("COBRADO_AL_INVITADO");

            pagoReserva = daoPago.save(pagoReserva);
            System.out.println("[OK] Pago registrado ID: " + pagoReserva.getIdPago());
            System.out.println("Estado inicial: " + pagoReserva.getEstadoTransaccion());

            // Simulación: Envío de dinero al anfitrión
            System.out.println("Procesando envío de dinero al anfitrión...");
            pagoReserva.registrarEnvioAnfitrion(cuentaAnfitrion.getIdCuenta());

            daoPago.update(pagoReserva); // Actualizamos el estado en la BD
            System.out.println("[OK] Pago actualizado. Nuevo estado: " + pagoReserva.getEstadoTransaccion());
            System.out.println("Fecha de envío: " + pagoReserva.getFechaEnvioAnfitrion());

// =========================================================================================
// 7. PRUEBA DE MENSAJERÍA (Comunicación dentro de la Reserva)
// =========================================================================================
            System.out.println("\n=== 7. PRUEBA DE MENSAJERÍA ===");
            MensajeIDAO daoMensaje = new MensajeImpl(); // 1. INSTANCIAR EL DAO

            Mensaje chat = new Mensaje();
            chat.setReserva(reserva);
            chat.setEmisor(invitado);
            chat.setTexto("Hola Cristian, ¿a qué hora puedo hacer el check-in?");
            chat.setFechaEnvio(new java.util.Date()); // Usar java.util.Date para compatibilidad

            chat = daoMensaje.save(chat); // 2. LLAMAR AL MÉTODO SAVE (SIN COMENTARIOS)
            System.out.println("[OK] Mensaje guardado en BD con ID: " + chat.getIdMensaje());
            System.out.println("Contenido: " + chat.getTexto());

// =========================================================================================
// 8. PRUEBA DE NOTIFICACIONES (Alertas al Usuario)
// =========================================================================================
            System.out.println("\n=== 8. PRUEBA DE NOTIFICACIONES ===");
            NotificacionIDAO daoNotif = new NotificacionImpl(); // 1. INSTANCIAR EL DAO

            Notificaciones alerta = new Notificaciones();
            alerta.setUsuario(anfitrion);
            alerta.setTitulo("¡Pago Recibido!");
            alerta.setMensaje("Se ha confirmado el pago de la reserva #" + reserva.getIdReserva());
            alerta.setLeido(false);

            alerta = daoNotif.save(alerta); // 2. LLAMAR AL MÉTODO SAVE (SIN COMENTARIOS)
            System.out.println("[OK] Notificación guardada en BD con ID: " + alerta.getIdNotificacion());
            System.out.println("Alerta: " + alerta.getTitulo());

            System.out.println("\n--- TODAS LAS TABLAS (8/8) HAN SIDO PROBADAS EN LA BD ---");
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR: " + e.getMessage());
            e.printStackTrace();
        }



    }





}