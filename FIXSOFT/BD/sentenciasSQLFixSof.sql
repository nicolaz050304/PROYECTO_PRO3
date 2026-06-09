-- ======================================================
-- SEGURIDAD: Desactivamos checks para una limpieza total
-- ======================================================

-- si ya tienes el esquema ejecuta el use squema sino no ejecutes desde ahí ejecuta desde el segundo dolar
delimiter $
USE FIXSOFT
$
SET FOREIGN_KEY_CHECKS = 0;

-- ======================================================
-- DROP TABLES (Orden corregido: de hijos a padres)
-- ======================================================
DROP TABLE IF EXISTS pago;
DROP TABLE IF EXISTS notificaciones;
DROP TABLE IF EXISTS resenha;
DROP TABLE IF EXISTS mensaje;
DROP TABLE IF EXISTS reserva;
DROP TABLE IF EXISTS alojamiento;
DROP TABLE IF EXISTS cuenta_bancaria;
DROP TABLE IF EXISTS usuario;

-- Volvemos a activar los checks para la creación
SET FOREIGN_KEY_CHECKS = 1;

-- ======================================================
-- 1. USUARIO (Con soporte para auditoría administrativa)
-- ======================================================
CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    correo VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    apellido_paterno VARCHAR(50) NOT NULL,
    apellido_materno VARCHAR(50),
    pais VARCHAR(50) NOT NULL,
    estado_sesion BOOLEAN NOT NULL DEFAULT 0,
    estado_actual ENUM('DISPONIBLE','PENDIENTE_VALIDACION','SUSPENDIDO') NOT NULL DEFAULT 'PENDIENTE_VALIDACION',
    telefono VARCHAR(20), -- Cambiado a VARCHAR para soportar '+' o ceros iniciales
    puntuacion_promedio DECIMAL(3,2) DEFAULT 0.00,
    tipo_documento ENUM('DNI','PASAPORTE','RUC','CE','SSN'),
    numero_documento VARCHAR(20),
    nivel_acceso INT DEFAULT 1,
    fecha_contratacion DATE,
    area_responsabilidad VARCHAR(100),
    tipo_usuario SET('ADMINISTRADOR','ANFITRION','INVITADO') NOT NULL,
    -- Levantamiento: Campos para validaciones del equipo de administración
    estado_validacion ENUM('PENDIENTE', 'APROBADO', 'RECHAZADO') DEFAULT 'PENDIENTE',
    id_admin_validador INT, 
    CONSTRAINT fk_usuario_admin FOREIGN KEY (id_admin_validador) REFERENCES usuario(id_usuario)
);

-- ======================================================
-- 2. CUENTA BANCARIA (Soporte para tipos de cuenta y envíos)
-- ======================================================
CREATE TABLE cuenta_bancaria (
    id_cuenta INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL, -- Relación obligatoria: toda cuenta tiene un dueño
    numero_cuenta VARCHAR(50) NOT NULL,
    tipo_moneda ENUM('PEN','USD','EUR') NOT NULL,
    cci VARCHAR(50),
    nro_banco VARCHAR(50) NOT NULL,
    tipo_cuenta VARCHAR(25) NOT NULL, -- Satisface: "Tipos de cuenta bancaria" (Enum en Java)
    titular VARCHAR(100) NOT NULL,
    saldo DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    verificada BOOLEAN NOT NULL DEFAULT 0,
    CONSTRAINT fk_cuenta_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
);

-- ======================================================
-- 3. ALOJAMIENTO
-- ======================================================
CREATE TABLE alojamiento (
    id_alojamiento INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    precio_por_noche DECIMAL(10,2) NOT NULL,
    direccion VARCHAR(200) NOT NULL,
    capacidad_max INT NOT NULL,
    calificacion_promedio DECIMAL(2,1) DEFAULT 0.0,
    disponibilidad BOOLEAN NOT NULL DEFAULT 1,
    pais VARCHAR(50) NOT NULL,
    latitud DECIMAL(10, 8),
    longitud DECIMAL(11, 8),
    id_duenho INT NOT NULL,
    tipo ENUM('CASA','DEPARTAMENTO','HABITACION') NOT NULL,
    -- Campos específicos por tipo (Herencia en Java)
    num_pisos INT,
    con_patio BOOLEAN,
    num_cocheras INT,
    num_habitaciones_casa INT,
    num_piso INT,
    nro_departamento VARCHAR(20),
    nro_habitaciones_departamento INT,
    nro_habitacion VARCHAR(20),
    tipo_cama VARCHAR(50),
    con_banho_privado BOOLEAN,
    -- Levantamiento: Validación de administración para alojamientos
    estado_validacion ENUM('PENDIENTE', 'APROBADO', 'RECHAZADO') DEFAULT 'PENDIENTE',
    id_admin_validador INT,
    CONSTRAINT fk_alojamiento_duenho FOREIGN KEY (id_duenho) REFERENCES usuario(id_usuario),
    CONSTRAINT fk_alojamiento_admin FOREIGN KEY (id_admin_validador) REFERENCES usuario(id_usuario)
);

-- ======================================================
-- 4. RESERVA
-- ======================================================
CREATE TABLE reserva (
    id_reserva INT AUTO_INCREMENT PRIMARY KEY,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    monto_total DECIMAL(10,2) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    id_invitado INT NOT NULL,
    id_alojamiento INT NOT NULL,
    fecha_contacto DATE, 
    moneda VARCHAR(10) NOT NULL DEFAULT 'PEN', -- Satisface: "Reserva debe tener moneda"
	calificado_por_invitado BOOLEAN NOT NULL DEFAULT 0,
    calificado_por_anfitrion BOOLEAN NOT NULL DEFAULT 0,
    CONSTRAINT fk_reserva_invitado FOREIGN KEY (id_invitado) REFERENCES usuario(id_usuario),
    CONSTRAINT fk_reserva_alojamiento FOREIGN KEY (id_alojamiento) REFERENCES alojamiento(id_alojamiento)
);

-- ======================================================
-- 5. MENSAJE
-- ======================================================
CREATE TABLE mensaje (
    id_mensaje INT AUTO_INCREMENT PRIMARY KEY,
    texto TEXT NOT NULL,
    fecha_envio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    emisor_id INT NOT NULL, 
    id_reserva INT NOT NULL, 
    CONSTRAINT fk_mensaje_reserva FOREIGN KEY (id_reserva) REFERENCES reserva(id_reserva),
    CONSTRAINT fk_mensaje_emisor FOREIGN KEY (emisor_id) REFERENCES usuario(id_usuario)
);

-- ======================================================
-- 6. RESENHA (Vinculada a Reserva para trazabilidad total)
-- ======================================================
CREATE TABLE resenha (
    id_resenha INT AUTO_INCREMENT PRIMARY KEY,
    calificacion INT NOT NULL,
    comentario TEXT,
    fecha_publicacion DATE NOT NULL,
    id_reserva INT NOT NULL, -- Satisface: "¿Cómo saber quién evalúa a quién?"
    tipo_autor VARCHAR(20) NOT NULL, -- 'ANFITRION' o 'INVITADO'
    activo TINYINT(1) DEFAULT 1,
    CONSTRAINT fk_resenha_reserva FOREIGN KEY (id_reserva) REFERENCES reserva(id_reserva)
);

-- ======================================================
-- 7. NOTIFICACIONES
-- ======================================================
CREATE TABLE notificaciones (
    id_notificacion INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    mensaje VARCHAR(255) NOT NULL,
    leido BOOLEAN DEFAULT 0,
    id_usuario INT NOT NULL,
    CONSTRAINT fk_notif_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

-- ======================================================
-- 8. PAGO (Ciclo financiero completo y auditoría)
-- ======================================================
CREATE TABLE pago (
    id_pago INT AUTO_INCREMENT PRIMARY KEY,
    monto_neto DECIMAL(10,2) NOT NULL, -- Lo que recibe el anfitrión
    monto_bruto DECIMAL(10,2) NOT NULL, -- Lo que paga el invitado
    moneda ENUM('PEN','USD','EUR') NOT NULL,
    porcentaje_comision DECIMAL(5,2) NOT NULL,
    -- Levantamiento: Historial monetario y tipo de cambio
    tipo_cambio DECIMAL(10,4) NOT NULL DEFAULT 1.0000, 
    -- Levantamiento: Manejo de envíos de dinero al anfitrión
    estado_transaccion VARCHAR(30) DEFAULT 'COBRADO_AL_INVITADO', 
    fecha_envio_anfitrion DATE, 
    id_cuenta_destino INT, -- Vínculo a la cuenta donde se envió el dinero
    fecha_pago TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_reserva INT NOT NULL,
    CONSTRAINT fk_pago_reserva FOREIGN KEY (id_reserva) REFERENCES reserva(id_reserva),
    CONSTRAINT fk_pago_cuenta FOREIGN KEY (id_cuenta_destino) REFERENCES cuenta_bancaria(id_cuenta)
);
ALTER DATABASE FIXSOFT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
SET FOREIGN_KEY_CHECKS = 0;
ALTER TABLE usuario
    CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

ALTER TABLE cuenta_bancaria
    CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

ALTER TABLE alojamiento
    CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

ALTER TABLE reserva
    CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

ALTER TABLE mensaje
    CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

ALTER TABLE resenha
    CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

ALTER TABLE notificaciones
    CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

ALTER TABLE pago
    CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
SET FOREIGN_KEY_CHECKS = 1;


