CREATE DATABASE  IF NOT EXISTS `FIXSOFT` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `FIXSOFT`;
-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: bdyordan1.c4rjiwidlfs9.us-east-1.rds.amazonaws.com    Database: FIXSOFT
-- ------------------------------------------------------
-- Server version	8.4.8

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '';

--
-- Table structure for table `alojamiento`
--

DROP TABLE IF EXISTS `alojamiento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `alojamiento` (
  `id_alojamiento` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `precio_por_noche` decimal(10,2) NOT NULL,
  `direccion` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `capacidad_max` int NOT NULL,
  `calificacion_promedio` decimal(2,1) DEFAULT '0.0',
  `disponibilidad` tinyint(1) NOT NULL DEFAULT '1',
  `pais` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `imagen_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `latitud` decimal(10,8) DEFAULT NULL,
  `longitud` decimal(11,8) DEFAULT NULL,
  `id_duenho` int NOT NULL,
  `tipo` enum('CASA','DEPARTAMENTO','HABITACION') COLLATE utf8mb4_unicode_ci NOT NULL,
  `num_pisos` int DEFAULT NULL,
  `con_patio` tinyint(1) DEFAULT NULL,
  `num_cocheras` int DEFAULT NULL,
  `num_habitaciones_casa` int DEFAULT NULL,
  `num_piso` int DEFAULT NULL,
  `nro_departamento` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nro_habitaciones_departamento` int DEFAULT NULL,
  `nro_habitacion` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tipo_cama` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `con_banho_privado` tinyint(1) DEFAULT NULL,
  `estado_validacion` enum('PENDIENTE','APROBADO','RECHAZADO') COLLATE utf8mb4_unicode_ci DEFAULT 'PENDIENTE',
  `id_admin_validador` int DEFAULT NULL,
  PRIMARY KEY (`id_alojamiento`),
  KEY `fk_alojamiento_duenho` (`id_duenho`),
  KEY `fk_alojamiento_admin` (`id_admin_validador`),
  CONSTRAINT `fk_alojamiento_admin` FOREIGN KEY (`id_admin_validador`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_alojamiento_duenho` FOREIGN KEY (`id_duenho`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alojamiento`
--

LOCK TABLES `alojamiento` WRITE;
/*!40000 ALTER TABLE `alojamiento` DISABLE KEYS */;
INSERT INTO `alojamiento` VALUES (1,'Departamento moderno en Miraflores','Acogedor departamento a dos cuadras del malecón, con vista parcial al mar.',220.00,'Av. Larco 345, Miraflores',4,4.8,1,'Perú','https://images.unsplash.com/photo-1484154218962-a197022b5858?q=80&w=1200&auto=format&fit=crop',-12.12110000,-77.02970000,2,'DEPARTAMENTO',NULL,NULL,NULL,NULL,8,'802',2,NULL,NULL,NULL,'APROBADO',1),(2,'Casa amplia en La Molina','Casa familiar con jardín, cochera doble y zona de parrilla.',380.00,'Calle Las Praderas 120, La Molina',8,4.9,1,'Perú','https://images.unsplash.com/photo-1717416700879-805562f2e7b9?q=80&w=1200&auto=format&fit=crop',-12.07920000,-76.94470000,2,'CASA',2,1,2,4,NULL,NULL,NULL,NULL,NULL,NULL,'APROBADO',1),(3,'Habitación acogedora en Barranco','Habitación privada en zona bohemia, cerca de cafés y galerías.',95.00,'Jr. Unión 210, Barranco',2,4.5,1,'Perú','https://images.unsplash.com/photo-1616594039964-ae9021a400a0?q=80&w=1200&auto=format&fit=crop',-12.14900000,-77.02060000,3,'HABITACION',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'B-3','Queen',1,'APROBADO',1),(4,'Departamento con vista en San Isidro','Piso alto luminoso en el corazón financiero, ideal para viajes de trabajo.',260.00,'Calle Las Begonias 500, San Isidro',3,4.7,1,'Perú','https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?q=80&w=1200&auto=format&fit=crop',-12.09760000,-77.03650000,3,'DEPARTAMENTO',NULL,NULL,NULL,NULL,12,'1203',1,NULL,NULL,NULL,'APROBADO',1),(5,'Casa familiar en Surco','Espaciosa casa de dos pisos con patio, ideal para grupos y familias.',100.00,'Av. Caminos del Inca 800, Santiago de Surco',6,0.0,1,'Perú','https://images.unsplash.com/photo-1568605114967-8130f3a36994?q=80&w=1200&auto=format&fit=crop',0.00000000,0.00000000,2,'CASA',0,0,0,3,NULL,NULL,NULL,NULL,NULL,NULL,'PENDIENTE',1),(6,'Habitación céntrica en Jesús María','Habitación cómoda y bien conectada, cerca del Campo de Marte.',80.00,'Av. Brasil 1500, Jesús María',2,4.0,1,'Perú','https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?q=80&w=1200&auto=format&fit=crop',-12.07600000,-77.04920000,3,'HABITACION',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,'PENDIENTE',1),(7,'CASA DE JROCA','CASA DE JOTAROQUITA',200.00,'FUNDO PANDO, La Molina',3,0.0,1,'Perú','/uploads/8d7b5324e4e444588fa5e04f1557fecd.jpg',0.00000000,0.00000000,2,'CASA',0,0,0,2,NULL,NULL,NULL,NULL,NULL,NULL,'PENDIENTE',NULL);
/*!40000 ALTER TABLE `alojamiento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cuenta_bancaria`
--

DROP TABLE IF EXISTS `cuenta_bancaria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cuenta_bancaria` (
  `id_cuenta` int NOT NULL AUTO_INCREMENT,
  `id_usuario` int NOT NULL,
  `numero_cuenta` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tipo_moneda` enum('PEN','USD','EUR') COLLATE utf8mb4_unicode_ci NOT NULL,
  `cci` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nro_banco` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tipo_cuenta` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL,
  `titular` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `saldo` decimal(12,2) NOT NULL DEFAULT '0.00',
  `verificada` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_cuenta`),
  KEY `fk_cuenta_usuario` (`id_usuario`),
  CONSTRAINT `fk_cuenta_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cuenta_bancaria`
--

LOCK TABLES `cuenta_bancaria` WRITE;
/*!40000 ALTER TABLE `cuenta_bancaria` DISABLE KEYS */;
/*!40000 ALTER TABLE `cuenta_bancaria` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mensaje`
--

DROP TABLE IF EXISTS `mensaje`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mensaje` (
  `id_mensaje` int NOT NULL AUTO_INCREMENT,
  `texto` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_envio` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `emisor_id` int NOT NULL,
  `id_reserva` int NOT NULL,
  PRIMARY KEY (`id_mensaje`),
  KEY `fk_mensaje_reserva` (`id_reserva`),
  KEY `fk_mensaje_emisor` (`emisor_id`),
  CONSTRAINT `fk_mensaje_emisor` FOREIGN KEY (`emisor_id`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_mensaje_reserva` FOREIGN KEY (`id_reserva`) REFERENCES `reserva` (`id_reserva`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mensaje`
--

LOCK TABLES `mensaje` WRITE;
/*!40000 ALTER TABLE `mensaje` DISABLE KEYS */;
/*!40000 ALTER TABLE `mensaje` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notificaciones`
--

DROP TABLE IF EXISTS `notificaciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notificaciones` (
  `id_notificacion` int NOT NULL AUTO_INCREMENT,
  `titulo` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `mensaje` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `leido` tinyint(1) DEFAULT '0',
  `id_usuario` int NOT NULL,
  PRIMARY KEY (`id_notificacion`),
  KEY `fk_notif_usuario` (`id_usuario`),
  CONSTRAINT `fk_notif_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notificaciones`
--

LOCK TABLES `notificaciones` WRITE;
/*!40000 ALTER TABLE `notificaciones` DISABLE KEYS */;
/*!40000 ALTER TABLE `notificaciones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pago`
--

DROP TABLE IF EXISTS `pago`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pago` (
  `id_pago` int NOT NULL AUTO_INCREMENT,
  `monto_neto` decimal(10,2) NOT NULL,
  `monto_bruto` decimal(10,2) NOT NULL,
  `moneda` enum('PEN','USD','EUR') COLLATE utf8mb4_unicode_ci NOT NULL,
  `porcentaje_comision` decimal(5,2) NOT NULL,
  `tipo_cambio` decimal(10,4) NOT NULL DEFAULT '1.0000',
  `estado_transaccion` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT 'COBRADO_AL_INVITADO',
  `fecha_envio_anfitrion` date DEFAULT NULL,
  `id_cuenta_destino` int DEFAULT NULL,
  `fecha_pago` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `id_reserva` int NOT NULL,
  PRIMARY KEY (`id_pago`),
  KEY `fk_pago_reserva` (`id_reserva`),
  KEY `fk_pago_cuenta` (`id_cuenta_destino`),
  CONSTRAINT `fk_pago_cuenta` FOREIGN KEY (`id_cuenta_destino`) REFERENCES `cuenta_bancaria` (`id_cuenta`),
  CONSTRAINT `fk_pago_reserva` FOREIGN KEY (`id_reserva`) REFERENCES `reserva` (`id_reserva`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pago`
--

LOCK TABLES `pago` WRITE;
/*!40000 ALTER TABLE `pago` DISABLE KEYS */;
/*!40000 ALTER TABLE `pago` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resenha`
--

DROP TABLE IF EXISTS `resenha`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `resenha` (
  `id_resenha` int NOT NULL AUTO_INCREMENT,
  `calificacion` int NOT NULL,
  `comentario` text COLLATE utf8mb4_unicode_ci,
  `fecha_publicacion` date NOT NULL,
  `id_reserva` int NOT NULL,
  `tipo_autor` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `activo` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id_resenha`),
  KEY `fk_resenha_reserva` (`id_reserva`),
  CONSTRAINT `fk_resenha_reserva` FOREIGN KEY (`id_reserva`) REFERENCES `reserva` (`id_reserva`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resenha`
--

LOCK TABLES `resenha` WRITE;
/*!40000 ALTER TABLE `resenha` DISABLE KEYS */;
INSERT INTO `resenha` VALUES (1,5,'Excelente ubicacion, muy limpio y el anfitrion super atento. Volveria sin dudarlo.','2026-05-06',3,'INVITADO',1),(2,5,'Maria fue una huesped ejemplar, dejo todo en orden y muy buena comunicacion.','2026-05-07',3,'ANFITRION',1),(3,4,'Buena relacion calidad-precio, bien conectado. La habitacion algo pequena pero comoda.','2026-04-13',6,'INVITADO',1),(4,4,'Jose cumplio con las normas de la casa, todo bien durante su estadia.','2026-04-14',6,'ANFITRION',1),(5,1,'Comentario inapropiado de ejemplo para probar la moderacion del administrador.','2026-05-08',3,'INVITADO',0);
/*!40000 ALTER TABLE `resenha` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reserva`
--

DROP TABLE IF EXISTS `reserva`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reserva` (
  `id_reserva` int NOT NULL AUTO_INCREMENT,
  `fecha_inicio` date NOT NULL,
  `fecha_fin` date NOT NULL,
  `monto_total` decimal(10,2) NOT NULL,
  `estado` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDIENTE',
  `id_invitado` int NOT NULL,
  `id_alojamiento` int NOT NULL,
  `fecha_contacto` date DEFAULT NULL,
  `moneda` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PEN',
  `calificado_por_invitado` tinyint(1) NOT NULL DEFAULT '0',
  `calificado_por_anfitrion` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_reserva`),
  KEY `fk_reserva_invitado` (`id_invitado`),
  KEY `fk_reserva_alojamiento` (`id_alojamiento`),
  CONSTRAINT `fk_reserva_alojamiento` FOREIGN KEY (`id_alojamiento`) REFERENCES `alojamiento` (`id_alojamiento`),
  CONSTRAINT `fk_reserva_invitado` FOREIGN KEY (`id_invitado`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reserva`
--

LOCK TABLES `reserva` WRITE;
/*!40000 ALTER TABLE `reserva` DISABLE KEYS */;
INSERT INTO `reserva` VALUES (1,'2026-07-10','2026-07-13',660.00,'CONFIRMADA',4,1,'2026-06-14','PEN',0,0),(2,'2026-07-20','2026-07-25',1900.00,'CONFIRMADA',5,2,NULL,'PEN',0,0),(3,'2026-05-02','2026-05-05',285.00,'FINALIZADA',4,3,'2026-04-28','PEN',1,1),(4,'2026-06-01','2026-06-04',780.00,'CANCELADA',5,4,'2026-05-20','PEN',0,0),(5,'2026-08-01','2026-08-03',640.00,'CONFIRMADA',4,5,'2026-06-15','PEN',0,0),(6,'2026-04-10','2026-04-12',160.00,'FINALIZADA',5,6,'2026-04-05','PEN',1,0),(7,'2026-06-28','2026-06-30',540.00,'CONFIRMADA',5,2,NULL,'PEN',0,0),(8,'2026-06-20','2026-06-23',294.00,'CONFIRMADA',5,3,NULL,'PEN',0,0);
/*!40000 ALTER TABLE `reserva` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id_usuario` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `correo` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `apellido_paterno` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `apellido_materno` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pais` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `estado_sesion` tinyint(1) NOT NULL DEFAULT '0',
  `estado_actual` enum('DISPONIBLE','PENDIENTE_VALIDACION','SUSPENDIDO') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDIENTE_VALIDACION',
  `telefono` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `puntuacion_promedio` decimal(3,2) DEFAULT '0.00',
  `tipo_documento` enum('DNI','PASAPORTE','RUC','CE','SSN') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `numero_documento` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nivel_acceso` int DEFAULT '1',
  `fecha_contratacion` date DEFAULT NULL,
  `area_responsabilidad` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tipo_usuario` set('ADMINISTRADOR','ANFITRION','INVITADO') COLLATE utf8mb4_unicode_ci NOT NULL,
  `estado_validacion` enum('PENDIENTE','APROBADO','RECHAZADO') COLLATE utf8mb4_unicode_ci DEFAULT 'PENDIENTE',
  `id_admin_validador` int DEFAULT NULL,
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `correo` (`correo`),
  KEY `fk_usuario_admin` (`id_admin_validador`),
  CONSTRAINT `fk_usuario_admin` FOREIGN KEY (`id_admin_validador`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,'admin','admin@bunki.pe','$2a$12$aNwi8ZorckAuDAQGm3bwaObG.VX5W0c7JGFyNaB1wlSU6yPuGYkja','Ana','Soto','Ríos','Perú',0,'DISPONIBLE','+51999000111',NULL,NULL,'40111222',5,NULL,NULL,'ADMINISTRADOR','APROBADO',NULL),(2,'lucia.ramos','lucia@bunki.pe','$2a$12$750WSS0DGEYodADto7B2oOx0XEPDFYDu5ReB7n9ndBpuD1Y8rWZaO','Lucía','Ramos','Quispe','Perú',0,'DISPONIBLE','+51999111222',4.80,'DNI','45678912',NULL,NULL,NULL,'ANFITRION','APROBADO',1),(3,'carlos.vega','carlos@bunki.pe','$2a$12$HsQgBL.Fio/FN2RHT7ifLe.2AoNBqhR/bDwiF.xK3PwDIwejgMspm','Carlos','Vega','Torres','Perú',0,'DISPONIBLE','+51999333444',4.60,'DNI','41222333',NULL,NULL,NULL,'ANFITRION','APROBADO',1),(4,'maria.flores','maria@bunki.pe','$2a$12$K3xRpcAPOcoe2xKB/Y/o9e35blSaNA7XPq4TBucyx2ISOF2NGqlHC','María','Flores','Díaz','Perú',0,'DISPONIBLE','+51988555666',0.00,'DNI','70111222',NULL,NULL,NULL,'INVITADO','APROBADO',1),(5,'jose.castro','jose@bunki.pe','$2a$12$9cdStyivrZ.mkTAXOBwkBuo4KK16YxQ/8n.5emUmmpkmFs6bp6sNK','José','Castro','León','Perú',0,'DISPONIBLE','+51977888999',0.00,'DNI','72333444',NULL,NULL,NULL,'INVITADO','APROBADO',1),(6,'test','test@bunki.pe','$2a$12$XEkszhMEga25Ei/SX0fS5uR4Yl.nhJfF/z0ZO7YgB4osRU25uXL5K','TEST','DEMO','','Perú',0,'PENDIENTE_VALIDACION','',0.00,NULL,NULL,NULL,NULL,NULL,'INVITADO','PENDIENTE',NULL);
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'FIXSOFT'
--
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-17  0:06:37
