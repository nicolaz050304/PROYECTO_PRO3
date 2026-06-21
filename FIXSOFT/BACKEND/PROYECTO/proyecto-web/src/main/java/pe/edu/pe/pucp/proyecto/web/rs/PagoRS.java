package pe.edu.pe.pucp.proyecto.web.rs;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import pe.edu.pe.pucp.proyecto.economy.Pago;
import pe.edu.pe.pucp.proyecto.economy.bl.PagoBL;
import pe.edu.pe.pucp.proyecto.economy.implbl.PagoBLImpl;
import pe.edu.pe.pucp.proyecto.reservation.Reserva;
import pe.edu.pe.pucp.proyecto.reservation.bl.ReservaBL;
import pe.edu.pe.pucp.proyecto.reservation.implbl.ReservaBLImpl;
import pe.edu.pe.pucp.proyecto.accomodations.Alojamiento;
import pe.edu.pe.pucp.proyecto.accomodations.bl.AlojamientoBL;
import pe.edu.pe.pucp.proyecto.accomodations.implbl.AlojamientoBLImpl;
import pe.edu.pe.pucp.proyecto.users.Usuario;
import pe.edu.pe.pucp.proyecto.users.bl.UsuarioBL;
import pe.edu.pe.pucp.proyecto.users.implbl.UsuarioBLImpl;
import pe.edu.pe.pucp.proyecto.web.dto.PagoDTO;
import pe.edu.pe.pucp.proyecto.web.mapper.PagoMapper;

// OpenPDF (fork libre de iText) para generar el comprobante en PDF.
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Recurso REST de Pago (RF13). Solo expone DTOs planos (nunca la entidad Pago,
 * cuya Reserva tiene ciclos) y delega en la BL existente.
 *
 * El pago se registra a partir del id de la reserva (no de un body): cuando se llega
 * a pagar, la reserva YA existe en BD, así que basta su id; la BL carga sus datos
 * (monto y moneda) y aplica la regla de negocio. La comisión (10%), el monto
 * neto/bruto y el estado los calcula PagoBL.registrarPagoDeReserva, no este recurso.
 *
 * Base de despliegue:
 *   http://localhost:8080/BunkiBackend/webresources/PagoRS
 */
@Path("PagoRS")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PagoRS {

    private final PagoBL pagoBL = new PagoBLImpl();
    private final ReservaBL reservaBL = new ReservaBLImpl();
    // La reserva trae el alojamiento y el invitado SOLO con su id (carga ligera del DAO de reserva),
    // por eso necesitamos estas BL para recargarlos completos (nombre, dirección, apellidos) en el comprobante.
    private final AlojamientoBL alojamientoBL = new AlojamientoBLImpl();
    private final UsuarioBL usuarioBL = new UsuarioBLImpl();

    /**
     * POST PagoRS/reserva/{idReserva} -> registra el pago de esa reserva.
     * Carga la reserva por id (obtenerPorId de la BL), delega el cálculo a la BL
     * y devuelve el PagoDTO creado. 404 si la reserva no existe; 400 ante error de negocio.
     */
    @POST
    @Path("reserva/{idReserva}")
    public Response registrarDeReserva(@PathParam("idReserva") int idReserva) {
        try {
            // La reserva ya existe cuando se paga: la cargamos por su id.
            Reserva reserva = reservaBL.obtenerPorId(idReserva);
            if (reserva == null) {
                return Response.status(Response.Status.NOT_FOUND)
                               .entity("{\"error\":\"Reserva no encontrada\"}").build();
            }

            // La BL aplica la comisión del 10% y guarda el pago; nos devuelve su id.
            int idPago = pagoBL.registrarPagoDeReserva(reserva);
            Pago creado = pagoBL.obtenerPorId(idPago);

            return Response.status(Response.Status.CREATED)
                           .entity(PagoMapper.toDTO(creado)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    /**
     * GET PagoRS/anfitrion/{idAnfitrion} -> pagos recibidos por un anfitrión.
     * Son los pagos de las reservas de SUS alojamientos (la BL/DAO cruza pago->reserva->alojamiento
     * y filtra por el dueño). Alimenta el panel de ganancias del anfitrión: monto_neto es lo que
     * le queda tras la comisión.
     */
    @GET
    @Path("anfitrion/{idAnfitrion}")
    public List<PagoDTO> listarPorAnfitrion(@PathParam("idAnfitrion") int idAnfitrion) {
        List<PagoDTO> salida = new ArrayList<>();
        try {
            for (Pago p : pagoBL.listarPorAnfitrion(idAnfitrion)) {
                salida.add(PagoMapper.toDTO(p));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return salida;
    }

    /** GET PagoRS -> lista todos los pagos (útil para verificar). */
    @GET
    public List<PagoDTO> listar() {
        List<PagoDTO> salida = new ArrayList<>();
        List<Pago> lista = pagoBL.listarTodos();
        if (lista != null) {
            for (Pago p : lista) {
                salida.add(PagoMapper.toDTO(p));
            }
        }
        return salida;
    }

    /**
     * GET PagoRS/{idPago}/comprobante -> genera y devuelve el comprobante de pago en PDF (RF16).
     *
     * El frontend solo abre esta URL y el navegador descarga el archivo directamente: por eso
     * NO devolvemos JSON sino los bytes del PDF con @Produces("application/pdf") y la cabecera
     * Content-Disposition "attachment", que FUERZA la descarga en vez de abrirlo en el navegador.
     *
     * Sobre los montos del comprobante:
     *   - montoBruto = total cobrado al huésped.
     *   - comisión   = lo que retiene la plataforma (bruto - neto).
     *   - montoNeto  = lo que finalmente recibe el anfitrión.
     */
    @GET
    @Path("{idPago}/comprobante")
    @Produces("application/pdf")
    public Response descargarComprobante(@PathParam("idPago") int idPago) {
        try {
            Pago pago = pagoBL.obtenerPorId(idPago);
            if (pago == null) {
                return Response.status(Response.Status.NOT_FOUND)
                               .entity("{\"error\":\"Pago no encontrado\"}").build();
            }

            byte[] pdfBytes = generarPdfComprobante(pago);
            // "attachment" -> el navegador descarga el archivo en vez de abrirlo inline.
            return Response.ok(pdfBytes)
                .header("Content-Disposition", "attachment; filename=comprobante_bunki_" + idPago + ".pdf")
                .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"error\":\"No se pudo generar el comprobante\"}").build();
        }
    }

    /**
     * GET PagoRS/reserva/{idReserva}/comprobante -> mismo comprobante PDF (RF16) pero ubicado por la RESERVA.
     *
     * El frontend conoce el id de la reserva (no el del pago), así que buscamos el pago de esa reserva
     * y reutilizamos la MISMA generación de PDF (generarPdfComprobante) que el endpoint por idPago.
     */
    @GET
    @Path("reserva/{idReserva}/comprobante")
    @Produces("application/pdf")
    public Response comprobantePorReserva(@PathParam("idReserva") int idReserva) {
        try {
            Pago pago = pagoBL.buscarPorReserva(idReserva);
            if (pago == null) {
                return Response.status(Response.Status.NOT_FOUND)
                               .entity("{\"error\":\"No hay pago para esta reserva\"}").build();
            }
            byte[] pdf = generarPdfComprobante(pago);
            return Response.ok(pdf)
                .header("Content-Disposition", "attachment; filename=comprobante_bunki_reserva_" + idReserva + ".pdf")
                .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"error\":\"No se pudo generar el comprobante\"}").build();
        }
    }

    /**
     * Genera el PDF del comprobante a partir de un Pago y devuelve sus bytes.
     *
     * Se extrajo a un método común para que los dos endpoints (por idPago y por idReserva) compartan
     * exactamente la misma generación sin duplicar la lógica de OpenPDF.
     *
     * Sobre los montos del comprobante:
     *   - montoBruto = total cobrado al huésped.
     *   - comisión   = lo que retiene la plataforma (bruto - neto).
     *   - montoNeto  = lo que finalmente recibe el anfitrión.
     */
    private byte[] generarPdfComprobante(Pago pago) throws Exception {
        // La reserva que viene DENTRO del pago puede ser un proxy (solo el id), por eso la
        // recargamos completa por su id: así obtenemos el alojamiento y el invitado poblados.
        Reserva reserva = null;
        if (pago.getReserva() != null) {
            reserva = reservaBL.obtenerPorId(pago.getReserva().getIdReserva());
        }

        // Generamos el PDF en memoria (no se escribe a disco): el WAR responde con los bytes.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document doc = new Document();
            PdfWriter.getInstance(doc, baos);
            doc.open();

            // === TÍTULO ===
            Font fTitulo = new Font(Font.HELVETICA, 20, Font.BOLD);
            Paragraph titulo = new Paragraph("Bunki - Comprobante de Pago", fTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            doc.add(titulo);
            doc.add(new Paragraph(" ")); // espacio

            // === DATOS DEL COMPROBANTE (tabla de 2 columnas: etiqueta | valor) ===
            String simbolo = simboloMoneda(pago.getMoneda());
            double comision = pago.getMontoBruto() - pago.getMontoNeto();
            SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");

            PdfPTable tabla = new PdfPTable(2);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{1.2f, 2f});

            agregarFila(tabla, "N° de comprobante:", String.valueOf(pago.getIdPago()));
            agregarFila(tabla, "Fecha de emisión:", fmt.format(new Date()));

            if (reserva != null) {
                // La reserva trae alojamiento e invitado SOLO con su id (carga ligera del DAO de reserva):
                // sus nombres/dirección/apellidos vienen null. Por eso los recargamos COMPLETOS con sus
                // BL antes de armar el comprobante (con manejo de null: si no se encuentran, queda "-").
                Alojamiento alojCompleto = null;
                if (reserva.getAlojamiento() != null) {
                    alojCompleto = alojamientoBL.obtenerPorId(reserva.getAlojamiento().getIdAlojamiento());
                }
                Usuario huespedCompleto = null;
                if (reserva.getInvitado() != null) {
                    huespedCompleto = usuarioBL.obtenerPorId(reserva.getInvitado().getIdUsuario());
                }

                // Alojamiento: no existe getUbicacion(); usamos la dirección (y el país como apoyo).
                String nombreAloj = "-";
                String ubicacion = "-";
                if (alojCompleto != null) {
                    nombreAloj = textoSeguro(alojCompleto.getNombre());
                    String dir = alojCompleto.getDireccion();
                    String pais = alojCompleto.getPais();
                    if (dir != null && pais != null)      ubicacion = dir + ", " + pais;
                    else if (dir != null)                 ubicacion = dir;
                    else if (pais != null)                ubicacion = pais;
                }
                agregarFila(tabla, "Alojamiento:", nombreAloj);
                agregarFila(tabla, "Ubicación:", ubicacion);

                // Huésped: no existe getApellidos(); concatenamos apellido paterno + materno.
                String huesped = "-";
                if (huespedCompleto != null) {
                    String nom = textoSeguro(huespedCompleto.getNombre());
                    String apePat = huespedCompleto.getApellidoPaterno();
                    String apeMat = huespedCompleto.getApellidoMaterno();
                    StringBuilder sb = new StringBuilder(nom);
                    if (apePat != null && !apePat.isBlank()) sb.append(" ").append(apePat);
                    if (apeMat != null && !apeMat.isBlank()) sb.append(" ").append(apeMat);
                    huesped = sb.toString().trim();
                    if (huesped.isEmpty()) huesped = "-";
                }
                agregarFila(tabla, "Huésped:", huesped);

                String estadia = (reserva.getFechaInicio() != null ? fmt.format(reserva.getFechaInicio()) : "-")
                               + " a "
                               + (reserva.getFechaFin() != null ? fmt.format(reserva.getFechaFin()) : "-");
                agregarFila(tabla, "Fechas de estadía:", estadia);
                // Nota: la entidad Reserva no expone N° de huéspedes, por eso no se incluye esa fila.
            }

            agregarFila(tabla, "Moneda:", pago.getMoneda() != null ? pago.getMoneda().name() : "-");
            agregarFila(tabla, "Monto bruto:", formatoMonto(simbolo, pago.getMontoBruto()));
            // porcenComision viene como fracción (0.1 = 10%): lo mostramos como porcentaje.
            int porcentaje = (int) Math.round(pago.getPorcenComision() * 100);
            agregarFila(tabla, "Comisión (" + porcentaje + "%):", formatoMonto(simbolo, comision));
            agregarFila(tabla, "Monto neto (al anfitrión):", formatoMonto(simbolo, pago.getMontoNeto()));
            agregarFila(tabla, "Estado:", textoSeguro(pago.getEstadoTransaccion()));

            doc.add(tabla);

            // === PIE ===
            doc.add(new Paragraph(" "));
            Font fPie = new Font(Font.HELVETICA, 9, Font.ITALIC);
            Paragraph pie = new Paragraph(
                "Documento generado por Bunki. Comprobante de la transacción realizada en la plataforma.", fPie);
            pie.setAlignment(Element.ALIGN_CENTER);
            doc.add(pie);

        doc.close();

        return baos.toByteArray();
    }

    // --- Helpers del comprobante PDF ---

    /** Añade una fila etiqueta|valor a la tabla del comprobante (etiqueta en negrita). */
    private void agregarFila(PdfPTable tabla, String etiqueta, String valor) {
        Font fEtq = new Font(Font.HELVETICA, 11, Font.BOLD);
        Font fVal = new Font(Font.HELVETICA, 11, Font.NORMAL);
        PdfPCell celdaEtq = new PdfPCell(new Phrase(etiqueta, fEtq));
        PdfPCell celdaVal = new PdfPCell(new Phrase(valor != null ? valor : "-", fVal));
        celdaEtq.setPadding(5f);
        celdaVal.setPadding(5f);
        tabla.addCell(celdaEtq);
        tabla.addCell(celdaVal);
    }

    /** Símbolo de la moneda; cae en el código (PEN/USD/EUR) si llegara otra o null. */
    private String simboloMoneda(pe.edu.pe.pucp.proyecto.economy.TipoMoneda moneda) {
        if (moneda == null) return "";
        switch (moneda) {
            case PEN: return "S/ ";
            case USD: return "$ ";
            case EUR: return "€ ";
            default:  return moneda.name() + " ";
        }
    }

    /** Formatea el monto con su símbolo y 2 decimales. */
    private String formatoMonto(String simbolo, double monto) {
        return simbolo + String.format("%.2f", monto);
    }

    /** Evita nulls en los textos: devuelve "-" si el valor es null o vacío. */
    private String textoSeguro(String valor) {
        return (valor == null || valor.isBlank()) ? "-" : valor;
    }
}
