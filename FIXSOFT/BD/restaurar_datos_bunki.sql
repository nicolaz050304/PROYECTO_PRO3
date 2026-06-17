-- ============================================================
-- RESTAURACIÓN DE DATOS — Bunki / FIXSOFT
-- Correr TODO de una vez (botón del rayo "Execute all", o Ctrl+Shift+Enter).
-- Restaura: usuarios, alojamientos (con columna imagen_url + imágenes),
--           reservas y reseñas. Es re-ejecutable (limpia antes de insertar).
--
-- IMPORTANTE: las contraseñas se insertan en TEXTO PLANO ('demo1234').
-- Como el backend es solo-hash, DESPUÉS de correr esto hay que correr el
-- runner MigrarPasswords (el mismo que usaste antes) para hashearlas.
-- ============================================================
USE FIXSOFT;
SET SQL_SAFE_UPDATES = 0;

-- ---------- Limpieza total (re-ejecutable) ----------
SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM resenha;
DELETE FROM reserva;
DELETE FROM alojamiento;
DELETE FROM usuario;
ALTER TABLE resenha      AUTO_INCREMENT = 1;
ALTER TABLE reserva      AUTO_INCREMENT = 1;
ALTER TABLE alojamiento  AUTO_INCREMENT = 1;
ALTER TABLE usuario      AUTO_INCREMENT = 1;
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 0) Asegurar columna imagen_url (por si se recreó la tabla sin ella)
--    Si ya existe, este ALTER dará error "Duplicate column" -> ignóralo
--    y sigue; las imágenes igual se setean en el INSERT de abajo.
-- ============================================================
-- ALTER TABLE alojamiento ADD COLUMN imagen_url VARCHAR(500) NULL AFTER pais;
--   (Déjalo comentado. Si al final imagen_url NO existe, descoméntalo,
--    córrelo solo, y vuelve a correr el bloque de alojamientos.)

-- ============================================================
-- 1) USUARIOS  (admin -> anfitriones -> invitados)
-- ============================================================
INSERT INTO usuario
(id_usuario, username, correo, password, nombre, apellido_paterno, apellido_materno, pais,
 estado_sesion, estado_actual, telefono, puntuacion_promedio, tipo_documento, numero_documento,
 nivel_acceso, tipo_usuario, estado_validacion, id_admin_validador)
VALUES
(1, 'admin', 'admin@bunki.pe', 'demo1234', 'Ana', 'Soto', 'Ríos', 'Perú',
 0, 'DISPONIBLE', '+51999000111', 0.00, 'DNI', '40111222', 5, 'ADMINISTRADOR', 'APROBADO', NULL),
(2, 'lucia.ramos', 'lucia@bunki.pe', 'demo1234', 'Lucía', 'Ramos', 'Quispe', 'Perú',
 0, 'DISPONIBLE', '+51999111222', 4.80, 'DNI', '45678912', 1, 'ANFITRION', 'APROBADO', 1),
(3, 'carlos.vega', 'carlos@bunki.pe', 'demo1234', 'Carlos', 'Vega', 'Torres', 'Perú',
 0, 'DISPONIBLE', '+51999333444', 4.60, 'DNI', '41222333', 1, 'ANFITRION', 'APROBADO', 1),
(4, 'maria.flores', 'maria@bunki.pe', 'demo1234', 'María', 'Flores', 'Díaz', 'Perú',
 0, 'DISPONIBLE', '+51988555666', 0.00, 'DNI', '70111222', 1, 'INVITADO', 'APROBADO', 1),
(5, 'jose.castro', 'jose@bunki.pe', 'demo1234', 'José', 'Castro', 'León', 'Perú',
 0, 'DISPONIBLE', '+51977888999', 0.00, 'DNI', '72333444', 1, 'INVITADO', 'APROBADO', 1);

-- ============================================================
-- 2) ALOJAMIENTOS (6) — con imagen_url ya incluida
-- ============================================================
INSERT INTO alojamiento
(id_alojamiento, nombre, descripcion, precio_por_noche, direccion, capacidad_max,
 calificacion_promedio, disponibilidad, pais, imagen_url, latitud, longitud, id_duenho, tipo,
 num_pisos, con_patio, num_cocheras, num_habitaciones_casa,
 num_piso, nro_departamento, nro_habitaciones_departamento,
 nro_habitacion, tipo_cama, con_banho_privado,
 estado_validacion, id_admin_validador)
VALUES
(1, 'Departamento moderno en Miraflores',
 'Acogedor departamento a dos cuadras del malecón, con vista parcial al mar.',
 220.00, 'Av. Larco 345, Miraflores', 4, 4.8, 1, 'Perú',
 'https://images.unsplash.com/photo-1484154218962-a197022b5858?q=80&w=1200&auto=format&fit=crop',
 -12.121100, -77.029700, 2, 'DEPARTAMENTO',
 NULL, NULL, NULL, NULL, 8, '802', 2, NULL, NULL, NULL, 'APROBADO', 1),

(2, 'Casa amplia en La Molina',
 'Casa familiar con jardín, cochera doble y zona de parrilla.',
 380.00, 'Calle Las Praderas 120, La Molina', 8, 4.9, 1, 'Perú',
 'https://images.unsplash.com/photo-1717416700879-805562f2e7b9?q=80&w=1200&auto=format&fit=crop',
 -12.079200, -76.944700, 2, 'CASA',
 2, 1, 2, 4, NULL, NULL, NULL, NULL, NULL, NULL, 'APROBADO', 1),

(3, 'Habitación acogedora en Barranco',
 'Habitación privada en zona bohemia, cerca de cafés y galerías.',
 95.00, 'Jr. Unión 210, Barranco', 2, 4.5, 1, 'Perú',
 'https://images.unsplash.com/photo-1616594039964-ae9021a400a0?q=80&w=1200&auto=format&fit=crop',
 -12.149000, -77.020600, 3, 'HABITACION',
 NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'B-3', 'Queen', 1, 'APROBADO', 1),

(4, 'Departamento con vista en San Isidro',
 'Piso alto luminoso en el corazón financiero, ideal para viajes de trabajo.',
 260.00, 'Calle Las Begonias 500, San Isidro', 3, 4.7, 1, 'Perú',
 'https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?q=80&w=1200&auto=format&fit=crop',
 -12.097600, -77.036500, 3, 'DEPARTAMENTO',
 NULL, NULL, NULL, NULL, 12, '1203', 1, NULL, NULL, NULL, 'APROBADO', 1),

(5, 'Casa familiar en Surco',
 'Espaciosa casa de dos pisos con patio, ideal para grupos y familias.',
 320.00, 'Av. Caminos del Inca 800, Santiago de Surco', 6, 4.6, 1, 'Perú',
 'https://images.unsplash.com/photo-1568605114967-8130f3a36994?q=80&w=1200&auto=format&fit=crop',
 -12.145000, -76.992500, 2, 'CASA',
 2, 1, 1, 3, NULL, NULL, NULL, NULL, NULL, NULL, 'APROBADO', 1),

(6, 'Habitación céntrica en Jesús María',
 'Habitación cómoda y bien conectada, cerca del Campo de Marte.',
 80.00, 'Av. Brasil 1500, Jesús María', 2, 4.2, 1, 'Perú',
 'https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?q=80&w=1200&auto=format&fit=crop',
 -12.076000, -77.049200, 3, 'HABITACION',
 NULL, NULL, NULL, NULL, NULL, NULL, NULL, '5', 'Matrimonial', 0, 'APROBADO', 1);

-- ============================================================
-- 3) RESERVAS (6) — IDs fijos. Estados en MAYÚSCULAS (enum del backend).
-- ============================================================
INSERT INTO reserva
(id_reserva, fecha_inicio, fecha_fin, monto_total, estado, id_invitado, id_alojamiento,
 fecha_contacto, moneda, calificado_por_invitado, calificado_por_anfitrion)
VALUES
(1, '2026-07-10', '2026-07-13', 660.00,  'CONFIRMADA', 4, 1, '2026-06-14', 'PEN', 0, 0),
(2, '2026-07-20', '2026-07-25', 1900.00, 'PENDIENTE',  5, 2, '2026-06-15', 'PEN', 0, 0),
(3, '2026-05-02', '2026-05-05', 285.00,  'FINALIZADA', 4, 3, '2026-04-28', 'PEN', 1, 1),
(4, '2026-06-01', '2026-06-04', 780.00,  'CANCELADA',  5, 4, '2026-05-20', 'PEN', 0, 0),
(5, '2026-08-01', '2026-08-03', 640.00,  'CONFIRMADA', 4, 5, '2026-06-15', 'PEN', 0, 0),
(6, '2026-04-10', '2026-04-12', 160.00,  'FINALIZADA', 5, 6, '2026-04-05', 'PEN', 1, 0);

-- ============================================================
-- 4) RESEÑAS (5) — la última (id 5) va oculta (activo=0) para moderación.
-- ============================================================
INSERT INTO resenha
(id_resenha, calificacion, comentario, fecha_publicacion, id_reserva, tipo_autor, activo)
VALUES
(1, 5, 'Excelente ubicacion, muy limpio y el anfitrion super atento. Volveria sin dudarlo.',
 '2026-05-06', 3, 'INVITADO', 1),
(2, 5, 'Maria fue una huesped ejemplar, dejo todo en orden y muy buena comunicacion.',
 '2026-05-07', 3, 'ANFITRION', 1),
(3, 4, 'Buena relacion calidad-precio, bien conectado. La habitacion algo pequena pero comoda.',
 '2026-04-13', 6, 'INVITADO', 1),
(4, 4, 'Jose cumplio con las normas de la casa, todo bien durante su estadia.',
 '2026-04-14', 6, 'ANFITRION', 1),
(5, 1, 'Comentario inapropiado de ejemplo para probar la moderacion del administrador.',
 '2026-05-08', 3, 'INVITADO', 0);

-- ============================================================
-- 5) VERIFICACIÓN (debe mostrar: usuarios 5, alojamientos 6, reservas 6, resenhas 5)
-- ============================================================
SELECT 'usuarios' AS tabla, COUNT(*) AS filas FROM usuario
UNION ALL SELECT 'alojamientos', COUNT(*) FROM alojamiento
UNION ALL SELECT 'reservas', COUNT(*) FROM reserva
UNION ALL SELECT 'resenhas', COUNT(*) FROM resenha;
USE FIXSOFT;
ALTER TABLE alojamiento ADD COLUMN imagen_url VARCHAR(500) NULL AFTER pais;