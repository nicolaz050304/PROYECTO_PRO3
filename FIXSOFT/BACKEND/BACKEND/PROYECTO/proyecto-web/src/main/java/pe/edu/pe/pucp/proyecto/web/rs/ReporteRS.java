package pe.edu.pe.pucp.proyecto.web.rs;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import pe.edu.pe.pucp.proyecto.accomodations.Alojamiento;
import pe.edu.pe.pucp.proyecto.accomodations.bl.AlojamientoBL;
import pe.edu.pe.pucp.proyecto.accomodations.implbl.AlojamientoBLImpl;
import pe.edu.pe.pucp.proyecto.economy.Pago;
import pe.edu.pe.pucp.proyecto.economy.bl.PagoBL;
import pe.edu.pe.pucp.proyecto.economy.implbl.PagoBLImpl;
import pe.edu.pe.pucp.proyecto.reservation.EstadoReserva;
import pe.edu.pe.pucp.proyecto.reservation.Reserva;
import pe.edu.pe.pucp.proyecto.reservation.bl.ReservaBL;
import pe.edu.pe.pucp.proyecto.reservation.implbl.ReservaBLImpl;
import pe.edu.pe.pucp.proyecto.review.bl.ResenhaBL;
import pe.edu.pe.pucp.proyecto.review.implbl.ResenhaBLImpl;
import pe.edu.pe.pucp.proyecto.users.Usuario;
import pe.edu.pe.pucp.proyecto.users.bl.UsuarioBL;
import pe.edu.pe.pucp.proyecto.users.implbl.UsuarioBLImpl;
import pe.edu.pe.pucp.proyecto.web.dto.FilaOcupacionDTO;
import pe.edu.pe.pucp.proyecto.web.dto.FilaRentabilidadDTO;
import pe.edu.pe.pucp.proyecto.web.dto.FilaSatisfaccionDTO;
import pe.edu.pe.pucp.proyecto.web.dto.FilaUsuarioDTO;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Recurso REST de Reportes (RF20/RF21 + RNF11) generados con JasperReports, exportables a PDF y CSV.
 *
 * Base de despliegue:
 *   http://localhost:8080/BunkiBackend/webresources/ReporteRS
 */
@Path("ReporteRS")
public class ReporteRS {

    private final PagoBL pagoBL = new PagoBLImpl();
    private final ReservaBL reservaBL = new ReservaBLImpl();
    private final AlojamientoBL alojamientoBL = new AlojamientoBLImpl();
    private final UsuarioBL usuarioBL = new UsuarioBLImpl();
    private final ResenhaBL resenhaBL = new ResenhaBLImpl();

    private static final SimpleDateFormat ISO = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat HUMANA = new SimpleDateFormat("dd/MM/yyyy");
    private static final DecimalFormat MONEDA = new DecimalFormat("#,##0.00");

    // ============================================================
    // RF20 · RENTABILIDAD
    // ============================================================

    @GET
    @Path("rentabilidad/pdf")
    @Produces("application/pdf")
    public Response rentabilidadPdf(@QueryParam("desde") String desde, @QueryParam("hasta") String hasta) {
        return responder(() -> {
            Map<String, Object> params = new HashMap<>();
            List<FilaRentabilidadDTO> filas = construirRentabilidad(desde, hasta, params);
            return generarPdf("/reportes/rentabilidad.jrxml", params, filas);
        }, "application/pdf", "attachment; filename=bunki_rentabilidad.pdf");
    }

    @GET
    @Path("rentabilidad/csv")
    @Produces("text/csv")
    public Response rentabilidadCsv(@QueryParam("desde") String desde, @QueryParam("hasta") String hasta) {
        return responder(() -> {
            List<FilaRentabilidadDTO> filas = construirRentabilidad(desde, hasta, new HashMap<>());
            StringBuilder sb = new StringBuilder("﻿");   // BOM: Excel abre UTF-8 con acentos OK
            sb.append("Fecha,Concepto,Bruto,Comision,Neto\n");
            for (FilaRentabilidadDTO f : filas) {
                sb.append(csv(f.getFecha())).append(',')
                  .append(csv(f.getConcepto())).append(',')
                  .append(csvNum(f.getMontoBruto())).append(',')
                  .append(csvNum(f.getComision())).append(',')
                  .append(csvNum(f.getMontoNeto())).append('\n');
            }
            return sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        }, "text/csv; charset=UTF-8", "attachment; filename=bunki_rentabilidad.csv");
    }

    /** Construye las filas de rentabilidad (Pago real cruzado con Reserva) y llena los parámetros. */
    private List<FilaRentabilidadDTO> construirRentabilidad(String desde, String hasta, Map<String, Object> params) {
        Map<Integer, Reserva> reservasPorId = new HashMap<>();
        List<Reserva> reservas = reservaBL.listarTodos();
        if (reservas != null) {
            for (Reserva r : reservas) if (r != null) reservasPorId.put(r.getIdReserva(), r);
        }
        Map<Integer, String> nombreAlojamiento = new HashMap<>();

        List<FilaRentabilidadDTO> filas = new ArrayList<>();
        double totalBruto = 0, totalComision = 0, totalNeto = 0;

        List<Pago> pagos = pagoBL.listarTodos();
        if (pagos != null) {
            for (Pago p : pagos) {
                if (p == null || p.getReserva() == null) continue;
                Reserva r = reservasPorId.get(p.getReserva().getIdReserva());

                Date fecha = (r != null)
                        ? (r.getFechaContacto() != null ? r.getFechaContacto() : r.getFechaInicio())
                        : null;
                String fechaIso = fecha != null ? ISO.format(fecha) : null;

                if (desde != null && !desde.isEmpty() && (fechaIso == null || fechaIso.compareTo(desde) < 0)) continue;
                if (hasta != null && !hasta.isEmpty() && (fechaIso == null || fechaIso.compareTo(hasta) > 0)) continue;

                String concepto = "Reserva";
                if (r != null && r.getAlojamiento() != null) {
                    int idAlo = r.getAlojamiento().getIdAlojamiento();
                    String nombre = nombreAlojamientoCache(idAlo, nombreAlojamiento);
                    concepto = (nombre != null ? nombre : "Alojamiento") + "  -  Reserva #" + r.getIdReserva();
                }

                double bruto = p.getMontoBruto();
                double neto = p.getMontoNeto();
                double comision = bruto - neto;

                filas.add(new FilaRentabilidadDTO(
                        fecha != null ? HUMANA.format(fecha) : "-", concepto, bruto, comision, neto));
                totalBruto += bruto;
                totalComision += comision;
                totalNeto += neto;
            }
        }
        filas.sort(Comparator.comparing(FilaRentabilidadDTO::getFecha));

        params.put("P_DESDE", (desde != null && !desde.isEmpty()) ? desde : "Inicio");
        params.put("P_HASTA", (hasta != null && !hasta.isEmpty()) ? hasta : "Hoy");
        params.put("P_GENERADO", HUMANA.format(new Date()));
        params.put("P_TOTAL_BRUTO", "S/ " + MONEDA.format(totalBruto));
        params.put("P_TOTAL_COMISION", "S/ " + MONEDA.format(totalComision));
        params.put("P_TOTAL_NETO", "S/ " + MONEDA.format(totalNeto));
        params.put("P_NUM_TX", String.valueOf(filas.size()));
        return filas;
    }

    // ============================================================
    // RF21 · OCUPACIÓN / DEMANDA
    // ============================================================

    @GET
    @Path("ocupacion/pdf")
    @Produces("application/pdf")
    public Response ocupacionPdf() {
        return responder(() -> {
            Map<String, Object> params = new HashMap<>();
            List<FilaOcupacionDTO> filas = construirOcupacion(params);
            return generarPdf("/reportes/ocupacion.jrxml", params, filas);
        }, "application/pdf", "attachment; filename=bunki_ocupacion.pdf");
    }

    @GET
    @Path("ocupacion/csv")
    @Produces("text/csv")
    public Response ocupacionCsv() {
        return responder(() -> {
            List<FilaOcupacionDTO> filas = construirOcupacion(new HashMap<>());
            StringBuilder sb = new StringBuilder("﻿");
            sb.append("Alojamiento,Anfitrion,Reservas,Noches,Ingresos\n");
            for (FilaOcupacionDTO f : filas) {
                sb.append(csv(f.getAlojamiento())).append(',')
                  .append(csv(f.getAnfitrion())).append(',')
                  .append(f.getReservas()).append(',')
                  .append(f.getNoches()).append(',')
                  .append(csvNum(f.getIngresos())).append('\n');
            }
            return sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        }, "text/csv; charset=UTF-8", "attachment; filename=bunki_ocupacion.csv");
    }

    /** Agrega reservas (no canceladas) por alojamiento: nº reservas, noches e ingresos. Ordena por demanda. */
    private List<FilaOcupacionDTO> construirOcupacion(Map<String, Object> params) {
        // alojamientoId -> [reservas, noches, ingresos]
        Map<Integer, int[]> conteo = new HashMap<>();
        Map<Integer, double[]> ingresos = new HashMap<>();

        List<Reserva> reservas = reservaBL.listarTodos();
        if (reservas != null) {
            for (Reserva r : reservas) {
                if (r == null || r.getAlojamiento() == null) continue;
                if (r.getEstadoReserva() == EstadoReserva.CANCELADA) continue;   // no cuenta como demanda
                int idAlo = r.getAlojamiento().getIdAlojamiento();
                int noches = calcularNoches(r.getFechaInicio(), r.getFechaFin());
                conteo.computeIfAbsent(idAlo, k -> new int[2]);
                conteo.get(idAlo)[0] += 1;
                conteo.get(idAlo)[1] += noches;
                ingresos.computeIfAbsent(idAlo, k -> new double[1]);
                ingresos.get(idAlo)[0] += r.getMontoTotal();
            }
        }

        Map<Integer, String> nombreAlojamiento = new HashMap<>();
        Map<Integer, String> nombreAnfitrion = new HashMap<>();
        List<FilaOcupacionDTO> filas = new ArrayList<>();
        int totalReservas = 0, totalNoches = 0;
        double totalIngresos = 0;

        for (Map.Entry<Integer, int[]> e : conteo.entrySet()) {
            int idAlo = e.getKey();
            int nReservas = e.getValue()[0];
            int nNoches = e.getValue()[1];
            double ing = ingresos.getOrDefault(idAlo, new double[1])[0];

            String aloNombre = nombreAlojamientoCache(idAlo, nombreAlojamiento);
            String anfNombre = nombreAnfitrionCache(idAlo, nombreAnfitrion);

            filas.add(new FilaOcupacionDTO(
                    aloNombre != null ? aloNombre : "Alojamiento #" + idAlo,
                    anfNombre != null ? anfNombre : "-",
                    nReservas, nNoches, ing));
            totalReservas += nReservas;
            totalNoches += nNoches;
            totalIngresos += ing;
        }
        // Orden por demanda: más reservas primero; desempate por noches.
        filas.sort(Comparator.comparingInt(FilaOcupacionDTO::getReservas)
                .thenComparingInt(FilaOcupacionDTO::getNoches).reversed());

        params.put("P_GENERADO", HUMANA.format(new Date()));
        params.put("P_TOTAL_RESERVAS", String.valueOf(totalReservas));
        params.put("P_TOTAL_NOCHES", String.valueOf(totalNoches));
        params.put("P_TOTAL_INGRESOS", "S/ " + MONEDA.format(totalIngresos));
        params.put("P_NUM_ALOJ", String.valueOf(filas.size()));
        return filas;
    }

    // ============================================================
    // RF23 · ESTADO DE CUENTA DEL ANFITRIÓN
    // ============================================================

    @GET
    @Path("estado-anfitrion/pdf")
    @Produces("application/pdf")
    public Response estadoAnfitrionPdf(@QueryParam("anfitrion") int idAnfitrion) {
        return responder(() -> {
            Map<String, Object> params = new HashMap<>();
            List<FilaRentabilidadDTO> filas = construirEstadoAnfitrion(idAnfitrion, params);
            return generarPdf("/reportes/estado_anfitrion.jrxml", params, filas);
        }, "application/pdf", "attachment; filename=bunki_estado_anfitrion.pdf");
    }

    private List<FilaRentabilidadDTO> construirEstadoAnfitrion(int idAnfitrion, Map<String, Object> params) {
        Map<Integer, Reserva> reservasPorId = new HashMap<>();
        List<Reserva> reservas = reservaBL.listarTodos();
        if (reservas != null) for (Reserva r : reservas) if (r != null) reservasPorId.put(r.getIdReserva(), r);
        Map<Integer, String> nombreAlojamiento = new HashMap<>();

        List<FilaRentabilidadDTO> filas = new ArrayList<>();
        double tb = 0, tc = 0, tn = 0;
        List<Pago> pagos = pagoBL.listarPorAnfitrion(idAnfitrion);
        if (pagos != null) {
            for (Pago p : pagos) {
                if (p == null || p.getReserva() == null) continue;
                Reserva r = reservasPorId.get(p.getReserva().getIdReserva());
                Date fecha = (r != null) ? (r.getFechaContacto() != null ? r.getFechaContacto() : r.getFechaInicio()) : null;
                String concepto = "Reserva";
                if (r != null && r.getAlojamiento() != null) {
                    String nom = nombreAlojamientoCache(r.getAlojamiento().getIdAlojamiento(), nombreAlojamiento);
                    concepto = (nom != null ? nom : "Alojamiento") + "  -  Reserva #" + r.getIdReserva();
                }
                double bruto = p.getMontoBruto(), neto = p.getMontoNeto(), com = bruto - neto;
                filas.add(new FilaRentabilidadDTO(fecha != null ? HUMANA.format(fecha) : "-", concepto, bruto, com, neto));
                tb += bruto; tc += com; tn += neto;
            }
        }
        filas.sort(Comparator.comparing(FilaRentabilidadDTO::getFecha));

        String anfNombre = "Anfitrión #" + idAnfitrion;
        try {
            Usuario u = usuarioBL.obtenerPorId(idAnfitrion);
            if (u != null) {
                String full = ((u.getNombre() != null ? u.getNombre() : "") + " "
                        + (u.getApellidoPaterno() != null ? u.getApellidoPaterno() : "")).trim();
                if (!full.isEmpty()) anfNombre = full;
            }
        } catch (RuntimeException ex) { /* sin BD: queda el id */ }

        params.put("P_ANFITRION", anfNombre);
        params.put("P_GENERADO", HUMANA.format(new Date()));
        params.put("P_TOTAL_BRUTO", "S/ " + MONEDA.format(tb));
        params.put("P_TOTAL_COMISION", "S/ " + MONEDA.format(tc));
        params.put("P_TOTAL_NETO", "S/ " + MONEDA.format(tn));
        params.put("P_NUM_TX", String.valueOf(filas.size()));
        return filas;
    }

    // ============================================================
    // RF24 · SATISFACCIÓN / RESEÑAS
    // ============================================================

    @GET @Path("satisfaccion/pdf") @Produces("application/pdf")
    public Response satisfaccionPdf() {
        return responder(() -> { Map<String, Object> p = new HashMap<>(); return generarPdf("/reportes/satisfaccion.jrxml", p, construirSatisfaccion(p)); },
                "application/pdf", "attachment; filename=bunki_satisfaccion.pdf");
    }

    @GET @Path("satisfaccion/csv") @Produces("text/csv")
    public Response satisfaccionCsv() {
        return responder(() -> {
            List<FilaSatisfaccionDTO> filas = construirSatisfaccion(new HashMap<>());
            StringBuilder sb = new StringBuilder("﻿").append("Alojamiento,Anfitrion,Promedio,Resenas\n");
            for (FilaSatisfaccionDTO f : filas)
                sb.append(csv(f.getAlojamiento())).append(',').append(csv(f.getAnfitrion())).append(',')
                  .append(csvNum(f.getPromedio())).append(',').append(f.getResenas()).append('\n');
            return sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        }, "text/csv; charset=UTF-8", "attachment; filename=bunki_satisfaccion.csv");
    }

    private List<FilaSatisfaccionDTO> construirSatisfaccion(Map<String, Object> params) {
        Map<Integer, double[]> calif = resenhaBL.calificacionesPorAlojamiento();   // id -> [promedio, conteo]
        Map<Integer, String> nombreAlo = new HashMap<>();
        Map<Integer, String> nombreAnf = new HashMap<>();
        List<FilaSatisfaccionDTO> filas = new ArrayList<>();
        double sumProm = 0; int totalResenas = 0;
        if (calif != null) {
            for (Map.Entry<Integer, double[]> e : calif.entrySet()) {
                int idAlo = e.getKey();
                double prom = e.getValue().length > 0 ? e.getValue()[0] : 0;
                int cnt = e.getValue().length > 1 ? (int) e.getValue()[1] : 0;
                if (cnt <= 0) continue;
                filas.add(new FilaSatisfaccionDTO(
                        defOr(nombreAlojamientoCache(idAlo, nombreAlo), "Alojamiento #" + idAlo),
                        defOr(nombreAnfitrionCache(idAlo, nombreAnf), "-"), prom, cnt));
                sumProm += prom; totalResenas += cnt;
            }
        }
        filas.sort(Comparator.comparingDouble(FilaSatisfaccionDTO::getPromedio).reversed());
        double promGlobal = filas.isEmpty() ? 0 : sumProm / filas.size();
        params.put("P_GENERADO", HUMANA.format(new Date()));
        params.put("P_NUM_ALOJ", String.valueOf(filas.size()));
        params.put("P_TOTAL_RESENAS", String.valueOf(totalResenas));
        params.put("P_PROMEDIO_GLOBAL", new DecimalFormat("0.00").format(promGlobal));
        return filas;
    }

    // ============================================================
    // RF25 · USUARIOS (directorio por rol/estado)
    // ============================================================

    @GET @Path("usuarios/pdf") @Produces("application/pdf")
    public Response usuariosPdf() {
        return responder(() -> { Map<String, Object> p = new HashMap<>(); return generarPdf("/reportes/usuarios.jrxml", p, construirUsuarios(p)); },
                "application/pdf", "attachment; filename=bunki_usuarios.pdf");
    }

    @GET @Path("usuarios/csv") @Produces("text/csv")
    public Response usuariosCsv() {
        return responder(() -> {
            List<FilaUsuarioDTO> filas = construirUsuarios(new HashMap<>());
            StringBuilder sb = new StringBuilder("﻿").append("Nombre,Correo,Rol,Estado\n");
            for (FilaUsuarioDTO f : filas)
                sb.append(csv(f.getNombre())).append(',').append(csv(f.getCorreo())).append(',')
                  .append(csv(f.getRol())).append(',').append(csv(f.getEstado())).append('\n');
            return sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        }, "text/csv; charset=UTF-8", "attachment; filename=bunki_usuarios.csv");
    }

    private List<FilaUsuarioDTO> construirUsuarios(Map<String, Object> params) {
        List<FilaUsuarioDTO> filas = new ArrayList<>();
        int admins = 0, anfitriones = 0, clientes = 0;
        List<Usuario> usuarios = usuarioBL.listarTodos();
        if (usuarios != null) {
            for (Usuario u : usuarios) {
                if (u == null) continue;
                String rol = rolPrincipal(u);
                String estado = u.getEstadoActual() != null ? u.getEstadoActual().name() : "-";
                String nombre = ((u.getNombre() != null ? u.getNombre() : "") + " "
                        + (u.getApellidoPaterno() != null ? u.getApellidoPaterno() : "")).trim();
                filas.add(new FilaUsuarioDTO(
                        nombre.isEmpty() ? (u.getUsername() != null ? u.getUsername() : "-") : nombre,
                        u.getCorreo() != null ? u.getCorreo() : "-", rol, estado));
                if ("Administrador".equals(rol)) admins++;
                else if ("Anfitrión".equals(rol)) anfitriones++;
                else clientes++;
            }
        }
        filas.sort(Comparator.comparing(FilaUsuarioDTO::getRol).thenComparing(FilaUsuarioDTO::getNombre));
        params.put("P_GENERADO", HUMANA.format(new Date()));
        params.put("P_TOTAL", String.valueOf(filas.size()));
        params.put("P_ADMINS", String.valueOf(admins));
        params.put("P_ANFITRIONES", String.valueOf(anfitriones));
        params.put("P_CLIENTES", String.valueOf(clientes));
        return filas;
    }

    private String rolPrincipal(Usuario u) {
        java.util.Set<String> roles = u.getRoles();
        if (roles != null) {
            if (roles.contains("ADMINISTRADOR")) return "Administrador";
            if (roles.contains("ANFITRION")) return "Anfitrión";
        }
        return "Cliente";
    }

    // ============================================================
    // RF26 · VOUCHER DE RESERVA
    // ============================================================

    @GET @Path("voucher/pdf") @Produces("application/pdf")
    public Response voucherPdf(@QueryParam("reserva") int idReserva) {
        return responder(() -> {
            Reserva r = reservaBL.obtenerPorId(idReserva);
            if (r == null) throw new IllegalStateException("Reserva no encontrada");
            Map<String, Object> params = new HashMap<>();
            String alojamiento = "Alojamiento", ubicacion = "-", anfitrion = "-";
            if (r.getAlojamiento() != null) {
                try {
                    Alojamiento alo = alojamientoBL.obtenerPorId(r.getAlojamiento().getIdAlojamiento());
                    if (alo != null) {
                        alojamiento = alo.getNombre() != null ? alo.getNombre() : alojamiento;
                        ubicacion = alo.getDireccion() != null ? alo.getDireccion() : "-";
                        if (alo.getDuenho() != null) {
                            Usuario an = usuarioBL.obtenerPorId(alo.getDuenho().getIdUsuario());
                            if (an != null) anfitrion = ((an.getNombre() != null ? an.getNombre() : "") + " "
                                    + (an.getApellidoPaterno() != null ? an.getApellidoPaterno() : "")).trim();
                        }
                    }
                } catch (RuntimeException ex) { /* sin BD */ }
            }
            String huesped = "-";
            if (r.getInvitado() != null) {
                try {
                    Usuario h = usuarioBL.obtenerPorId(r.getInvitado().getIdUsuario());
                    if (h != null) huesped = ((h.getNombre() != null ? h.getNombre() : "") + " "
                            + (h.getApellidoPaterno() != null ? h.getApellidoPaterno() : "")).trim();
                } catch (RuntimeException ex) { /* sin BD */ }
            }
            int noches = calcularNoches(r.getFechaInicio(), r.getFechaFin());
            params.put("P_NUM_RESERVA", "#" + r.getIdReserva());
            params.put("P_ESTADO", r.getEstadoReserva() != null ? r.getEstadoReserva().name() : "-");
            params.put("P_HUESPED", huesped.isEmpty() ? "-" : huesped);
            params.put("P_ALOJAMIENTO", alojamiento);
            params.put("P_UBICACION", ubicacion);
            params.put("P_ANFITRION", anfitrion.isEmpty() ? "-" : anfitrion);
            params.put("P_ENTRADA", r.getFechaInicio() != null ? HUMANA.format(r.getFechaInicio()) : "-");
            params.put("P_SALIDA", r.getFechaFin() != null ? HUMANA.format(r.getFechaFin()) : "-");
            params.put("P_NOCHES", String.valueOf(noches));
            params.put("P_TOTAL", "S/ " + MONEDA.format(r.getMontoTotal()));
            params.put("P_GENERADO", HUMANA.format(new Date()));
            return generarPdf("/reportes/voucher.jrxml", params, null);
        }, "application/pdf", "attachment; filename=bunki_voucher.pdf");
    }

    // ============================================================
    // Helpers
    // ============================================================

    private String defOr(String v, String def) { return (v != null && !v.isEmpty()) ? v : def; }

    private String nombreAlojamientoCache(int idAlo, Map<Integer, String> cache) {
        if (cache.containsKey(idAlo)) return cache.get(idAlo);
        String nombre = null;
        try {
            Alojamiento alo = alojamientoBL.obtenerPorId(idAlo);
            nombre = alo != null ? alo.getNombre() : null;
        } catch (RuntimeException ex) { nombre = null; }
        cache.put(idAlo, nombre);
        return nombre;
    }

    private String nombreAnfitrionCache(int idAlo, Map<Integer, String> cache) {
        if (cache.containsKey(idAlo)) return cache.get(idAlo);
        String nombre = null;
        try {
            Alojamiento alo = alojamientoBL.obtenerPorId(idAlo);
            if (alo != null && alo.getDuenho() != null) {
                Usuario u = usuarioBL.obtenerPorId(alo.getDuenho().getIdUsuario());
                if (u != null) {
                    String n = u.getNombre() != null ? u.getNombre() : "";
                    String ap = u.getApellidoPaterno() != null ? u.getApellidoPaterno() : "";
                    nombre = (n + " " + ap).trim();
                }
            }
        } catch (RuntimeException ex) { nombre = null; }
        cache.put(idAlo, nombre);
        return nombre;
    }

    private int calcularNoches(Date inicio, Date fin) {
        if (inicio == null || fin == null) return 1;
        long ms = fin.getTime() - inicio.getTime();
        int dias = (int) (ms / (1000L * 60 * 60 * 24));
        return Math.max(dias, 1);
    }

    private byte[] generarPdf(String plantilla, Map<String, Object> params, List<?> filas) throws Exception {
        return JasperExportManager.exportReportToPdf(llenar(plantilla, params, filas));
    }

    /** Número para CSV: 2 decimales SIN separador de miles (la coma de miles rompería las columnas). */
    private String csvNum(double v) {
        return String.format(java.util.Locale.US, "%.2f", v);
    }

    /** Escapa un campo CSV: comillas dobles si contiene coma, comilla o salto; comillas duplicadas. */
    private String csv(String v) {
        if (v == null) return "";
        if (v.contains(",") || v.contains("\"") || v.contains("\n")) {
            return "\"" + v.replace("\"", "\"\"") + "\"";
        }
        return v;
    }

    /** Compila la plantilla del classpath y la llena con el datasource (0 filas -> band <noData>). */
    private JasperPrint llenar(String plantilla, Map<String, Object> params, List<?> filas) throws Exception {
        try (InputStream in = getClass().getResourceAsStream(plantilla)) {
            if (in == null) throw new IllegalStateException("Plantilla no encontrada: " + plantilla);
            JasperReport jr = JasperCompileManager.compileReport(in);
            List<?> datos = filas != null ? filas : Collections.emptyList();
            return JasperFillManager.fillReport(jr, params, new JRBeanCollectionDataSource(datos));
        }
    }

    /** Envuelve la generación: éxito -> bytes con headers; error -> 500 JSON. */
    private Response responder(GeneradorBytes gen, String contentType, String disposition) {
        try {
            byte[] bytes = gen.generar();
            return Response.ok(bytes)
                    .header("Content-Type", contentType)
                    .header("Content-Disposition", disposition)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @FunctionalInterface
    private interface GeneradorBytes {
        byte[] generar() throws Exception;
    }
}
