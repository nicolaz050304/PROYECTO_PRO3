package pe.edu.pe.pucp.proyecto.web.dto;

/**
 * DTO plano de Cuenta Bancaria. Se expone esto (sin la entidad) para un contrato simple
 * y estable hacia el frontend: los enums viajan como String (tipoMoneda, tipoCuenta).
 */
public class CuentaBancariaDTO {

    private int idCuenta;
    private int idUsuario;
    private String numeroCuenta;
    private String tipoMoneda;
    private String cci;
    private String nroBanco;
    private String tipoCuenta;
    private String titular;
    private double saldo;
    private boolean verificada;
    // RF15: cuenta de cobro principal del anfitrión (recibe el abono automático de reservas).
    private boolean principal;

    public CuentaBancariaDTO() {
    }

    public int getIdCuenta() { return idCuenta; }
    public void setIdCuenta(int idCuenta) { this.idCuenta = idCuenta; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNumeroCuenta() { return numeroCuenta; }
    public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }

    public String getTipoMoneda() { return tipoMoneda; }
    public void setTipoMoneda(String tipoMoneda) { this.tipoMoneda = tipoMoneda; }

    public String getCci() { return cci; }
    public void setCci(String cci) { this.cci = cci; }

    public String getNroBanco() { return nroBanco; }
    public void setNroBanco(String nroBanco) { this.nroBanco = nroBanco; }

    public String getTipoCuenta() { return tipoCuenta; }
    public void setTipoCuenta(String tipoCuenta) { this.tipoCuenta = tipoCuenta; }

    public String getTitular() { return titular; }
    public void setTitular(String titular) { this.titular = titular; }

    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }

    public boolean isVerificada() { return verificada; }
    public void setVerificada(boolean verificada) { this.verificada = verificada; }

    public boolean isPrincipal() { return principal; }
    public void setPrincipal(boolean principal) { this.principal = principal; }
}
