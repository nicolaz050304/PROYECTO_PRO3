package pe.edu.pe.pucp.proyecto.web.rs;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import pe.edu.pe.pucp.proyecto.web.niubiz.NiubizClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Recurso REST de pagos con Niubiz (SANDBOX). El frontend pide una sesión, abre el lightbox
 * checkout.js con el sessionKey, obtiene un transactionToken y aquí se autoriza la transacción.
 *
 * Base: http://localhost:8080/BunkiBackend/webresources/NiubizRS
 */
@Path("NiubizRS")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NiubizRS {

    private final NiubizClient client = new NiubizClient();

    /** POST NiubizRS/sesion -> { merchantId, sessionKey, purchaseNumber, amount } para el lightbox. */
    @POST
    @Path("sesion")
    public Response crearSesion(SesionRequest req) {
        if (req == null || req.getAmount() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "amount es obligatorio y debe ser > 0")).build();
        }
        try {
            String purchaseNumber = generarPurchaseNumber();
            String sessionKey = client.crearSesion(req.getAmount(), req.getClientIp());
            Map<String, Object> out = new HashMap<>();
            out.put("merchantId", NiubizClient.MERCHANT_ID);
            out.put("sessionKey", sessionKey);
            out.put("purchaseNumber", purchaseNumber);
            out.put("amount", req.getAmount());
            return Response.ok(out).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_GATEWAY)
                    .entity(Map.of("error", "No se pudo iniciar el pago con Niubiz", "detalle", String.valueOf(e.getMessage()))).build();
        }
    }

    /** POST NiubizRS/autorizar -> { aprobado, detalle }. Body: transactionToken, purchaseNumber, amount. */
    @POST
    @Path("autorizar")
    public Response autorizar(AutorizarRequest req) {
        if (req == null || req.getTransactionToken() == null || req.getPurchaseNumber() == null || req.getAmount() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "transactionToken, purchaseNumber y amount son obligatorios")).build();
        }
        try {
            NiubizClient.Resultado r = client.autorizar(req.getTransactionToken(), req.getPurchaseNumber(), req.getAmount());
            Map<String, Object> out = new HashMap<>();
            out.put("aprobado", r.aprobado());
            out.put("httpStatus", r.httpStatus());
            out.put("detalle", r.body());
            return Response.ok(out).build();   // siempre 200; el flag 'aprobado' indica el resultado
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_GATEWAY)
                    .entity(Map.of("error", "No se pudo autorizar el pago", "detalle", String.valueOf(e.getMessage()))).build();
        }
    }

    /** Número de compra único (numérico). Sandbox exige que no se repita entre autorizaciones. */
    private static String generarPurchaseNumber() {
        long n = System.currentTimeMillis() % 1000000000L; // 9 dígitos
        return String.valueOf(n);
    }

    public static class SesionRequest {
        private double amount;
        private String clientIp;
        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }
        public String getClientIp() { return clientIp; }
        public void setClientIp(String clientIp) { this.clientIp = clientIp; }
    }

    public static class AutorizarRequest {
        private String transactionToken;
        private String purchaseNumber;
        private double amount;
        public String getTransactionToken() { return transactionToken; }
        public void setTransactionToken(String transactionToken) { this.transactionToken = transactionToken; }
        public String getPurchaseNumber() { return purchaseNumber; }
        public void setPurchaseNumber(String purchaseNumber) { this.purchaseNumber = purchaseNumber; }
        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }
    }
}
