package pe.edu.pe.pucp.proyecto.web.niubiz;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Base64;
import java.util.Locale;

/**
 * Cliente del SANDBOX de Niubiz (ex-VisaNet Perú). Usa las credenciales de PRUEBA públicas
 * documentadas por Niubiz (comercio de pruebas en SOLES) — NO son de producción y no mueven
 * dinero real. Flujo: token de seguridad (Basic auth) -> sesión de pago -> autorización.
 *
 * El front abre el lightbox checkout.js con merchantId + sessionKey; el lightbox devuelve un
 * transactionToken que este cliente autoriza en el paso final.
 */
public class NiubizClient {

    private static final String BASE = "https://apisandbox.vnforappstest.com";
    /** Comercio de PRUEBA en soles (público de Niubiz). Este (456879852) suele tener el antifraude
     *  en modo monitor y aprueba las tarjetas de prueba; el alterno 522591303/necomplus las rechaza. */
    public static final String MERCHANT_ID = "456879852";
    private static final String USER = "integraciones@niubiz.com.pe";
    private static final String PASS = "_7z3@8fF";

    private final HttpClient http = construirHttpClient();

    /**
     * HttpClient para el SANDBOX de Niubiz. La JVM de GlassFish no trae en su truststore la CA del
     * host de pruebas (apisandbox.vnforappstest.com), lo que da "PKIX path building failed". Como es
     * un entorno de PRUEBA (sin dinero ni datos reales), se usa un SSLContext que confía en ese host.
     * ÁMBITO: solo este cliente; el resto de la app no se ve afectada. NO usar este patrón en producción.
     */
    private static HttpClient construirHttpClient() {
        try {
            TrustManager[] confiarSandbox = new TrustManager[]{
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    }
            };
            SSLContext ssl = SSLContext.getInstance("TLS");
            ssl.init(null, confiarSandbox, new SecureRandom());
            return HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).sslContext(ssl).build();
        } catch (Exception e) {
            return HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build();
        }
    }

    /** Paso 1: token de seguridad (JWT) vía Basic auth. */
    public String tokenSeguridad() throws Exception {
        String basic = Base64.getEncoder()
                .encodeToString((USER + ":" + PASS).getBytes(StandardCharsets.UTF_8));
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + "/api.security/v1/security"))
                .timeout(Duration.ofSeconds(25))
                .header("Authorization", "Basic " + basic)
                .GET().build();
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() / 100 != 2) {
            throw new RuntimeException("Niubiz security HTTP " + resp.statusCode() + ": " + resp.body());
        }
        return resp.body().trim();
    }

    /** Paso 2: crea la sesión de pago y devuelve el sessionKey para el lightbox. */
    public String crearSesion(double amount, String clientIp) throws Exception {
        String token = tokenSeguridad();
        String ip = (clientIp == null || clientIp.isBlank()) ? "127.0.0.1" : clientIp;
        String body = "{\"channel\":\"web\",\"amount\":" + money(amount) + ","
                + "\"antifraud\":{\"clientIp\":\"" + ip + "\","
                + "\"merchantDefineData\":{\"MDD4\":\"" + USER + "\",\"MDD32\":\"BUNKI\","
                + "\"MDD75\":\"Registrado\",\"MDD77\":458}}}";
        HttpRequest req = HttpRequest.newBuilder(
                        URI.create(BASE + "/api.ecommerce/v2/ecommerce/token/session/" + MERCHANT_ID))
                .timeout(Duration.ofSeconds(25))
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body)).build();
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() / 100 != 2) {
            throw new RuntimeException("Niubiz session HTTP " + resp.statusCode() + ": " + resp.body());
        }
        String sessionKey = extraer(resp.body(), "sessionKey");
        if (sessionKey == null) {
            throw new RuntimeException("Niubiz session sin sessionKey: " + resp.body());
        }
        return sessionKey;
    }

    /** Paso 4: autoriza la transacción con el transactionToken del lightbox. */
    public Resultado autorizar(String transactionToken, String purchaseNumber, double amount) throws Exception {
        String token = tokenSeguridad();
        String body = "{\"channel\":\"web\",\"captureType\":\"manual\",\"countable\":true,"
                + "\"order\":{\"tokenId\":\"" + transactionToken + "\","
                + "\"purchaseNumber\":\"" + purchaseNumber + "\","
                + "\"amount\":" + money(amount) + ",\"currency\":\"PEN\"}}";
        HttpRequest req = HttpRequest.newBuilder(
                        URI.create(BASE + "/api.authorization/v3/authorization/ecommerce/" + MERCHANT_ID))
                .timeout(Duration.ofSeconds(25))
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body)).build();
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        // Niubiz devuelve 200 cuando la transacción es AUTORIZADA; 4xx cuando es denegada/inválida.
        boolean aprobado = resp.statusCode() / 100 == 2;
        return new Resultado(aprobado, resp.statusCode(), resp.body());
    }

    public record Resultado(boolean aprobado, int httpStatus, String body) {}

    /** Monto con punto decimal y 2 cifras (formato que exige Niubiz). */
    private static String money(double amount) {
        return String.format(Locale.US, "%.2f", amount);
    }

    /** Extrae el valor string de un campo JSON simple (sin dependencias). */
    private static String extraer(String json, String campo) {
        String marca = "\"" + campo + "\"";
        int i = json.indexOf(marca);
        if (i < 0) return null;
        int c = json.indexOf(':', i + marca.length());
        if (c < 0) return null;
        int q1 = json.indexOf('"', c + 1);
        int q2 = q1 < 0 ? -1 : json.indexOf('"', q1 + 1);
        if (q1 < 0 || q2 < 0) return null;
        return json.substring(q1 + 1, q2);
    }
}
