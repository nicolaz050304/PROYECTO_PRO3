package pe.edu.pe.pucp.proyecto.cuentaBank;

import pe.edu.pe.pucp.proyecto.economy.TipoMoneda;

public class CuentaBancaria {
        public enum TipoCuenta {
            AHORROS, CORRIENTE, SUELDO, INTERBANCARIA
        }

        private int idCuenta;
        private int idUsuario; // <--- VÍNCULO CRÍTICO CON EL DUEÑO
        private String numeroCuenta;
        private TipoMoneda tipoMoneda;
        private String CCI;
        private String nroBanco;
        private TipoCuenta tipoCuenta;
        private String titular;
        private double saldo;
        private boolean verificada;

        public CuentaBancaria() {
            this.verificada = false;
        }

        // Constructor actualizado con idUsuario
        public CuentaBancaria(int idCuenta, int idUsuario, String numeroCuenta, TipoMoneda tipoMoneda,
                              String CCI, double saldo, String nroBanco, TipoCuenta tipoCuenta, String titular) {
            this.idCuenta = idCuenta;
            this.idUsuario = idUsuario;
            this.numeroCuenta = numeroCuenta;
            this.tipoMoneda = tipoMoneda;
            this.CCI = CCI;
            this.saldo = saldo;
            this.nroBanco = nroBanco;
            this.tipoCuenta = tipoCuenta;
            this.titular = titular;
            this.verificada = false;
        }

        // --- GETTERS Y SETTERS ---
        public int getIdUsuario() { return idUsuario; }
        public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

        public int getIdCuenta() { return idCuenta; }
        public void setIdCuenta(int idCuenta) { this.idCuenta = idCuenta; }

        public String getNumeroCuenta() { return numeroCuenta; }
        public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }

        public TipoMoneda getTipoMoneda() { return tipoMoneda; }
        public void setTipoMoneda(TipoMoneda tipoMoneda) { this.tipoMoneda = tipoMoneda; }

        public String getCCI() { return CCI; }
        public void setCCI(String CCI) { this.CCI = CCI; }

        public void setVerificada(boolean verificada) { this.verificada = verificada; }
        public boolean isVerificada() { return verificada; }

        public String getNroBanco() { return nroBanco; }
        public void setNroBanco(String nroBanco) { this.nroBanco = nroBanco; }

        public TipoCuenta getTipoCuenta() { return tipoCuenta; }
        public void setTipoCuenta(TipoCuenta tipoCuenta) { this.tipoCuenta = tipoCuenta; }

        public String getTitular() { return titular; }
        public void setTitular(String titular) { this.titular = titular; }

        public double getSaldo() { return saldo; }
        public void setSaldo(double saldo) { this.saldo = saldo; }

    // ============================================================
    // MÉTODOS DE NEGOCIO
    // ============================================================

    public boolean retirarDinero(double montoASacar) {
        if (montoASacar > saldo) {
            System.out.println("Error: Saldo insuficiente en cuenta " + tipoCuenta);
            return false;
        } else {
            saldo -= montoASacar;
            return true;
        }
    }

    public void recibirDeposito(double monto) {
        saldo += monto;
    }

    // NUEVO: Satisface "Manejar envíos de dinero al anfitrión"
    public boolean procesarEnvioDinero(double monto, String cciDestino) {
        if (!this.verificada) {
            System.out.println("Error: La cuenta de origen no ha sido verificada.");
            return false;
        }

        // Regla: Si es Interbancaria, el CCI es obligatorio
        if (this.tipoCuenta == TipoCuenta.INTERBANCARIA && (cciDestino == null || cciDestino.isEmpty())) {
            System.out.println("Error: Se requiere CCI para transferencias interbancarias.");
            return false;
        }

        return retirarDinero(monto);
    }

    public boolean verificarCuenta() {
        if (this.numeroCuenta == null || this.numeroCuenta.isEmpty()) {
            this.verificada = false;
            return false;
        }
        if (this.titular == null || this.titular.trim().length() < 3) {
            this.verificada = false;
            return false;
        }
        if (this.nroBanco == null || this.nroBanco.equals("")) {
            this.verificada = false;
            return false;
        }
        // Nueva validación de Tipo de Cuenta
        if (this.tipoCuenta == null) {
            this.verificada = false;
            return false;
        }

        this.verificada = true;
        return true;
    }
}