# Bunki / FIXSOFT — Requisitos y mejoras agregadas

> Bitácora de lo implementado en las sesiones de cierre del proyecto.
> Backend: Java/Jakarta (GlassFish 8, JDK 21, MySQL en AWS RDS) en `BACKEND/BACKEND/PROYECTO`.
> Frontend: Blazor Server en `FRONTEND/FRONTEND/BunkiApp`.
> Patrón para tablas nuevas/columnas: **auto-migración** en el DAO (`CREATE TABLE IF NOT EXISTS` / `ALTER TABLE` previa consulta a `information_schema`), sin scripts manuales.

---

## 1. RF10 — Reprogramación de reservas (completa el Ciclo de Vida de la Reserva)
El RF10 pedía registro + **reprogramación** + cancelación. Faltaba la reprogramación.

- **Frontend:** botón "Reprogramar" en `Components/Shared/ReservaCard.razor` (visible en reservas Pendiente/Confirmada) y modal en `Components/Pages/MisReservas.razor`: elige nuevas fechas, valida que no sean pasadas y que salida > entrada, **comprueba disponibilidad** (no choca con otras reservas/bloqueos, excluyendo su propio rango), recalcula el total y persiste por `PUT ReservaRS/{id}`.
- **Backend:** `ReservaRS` PUT ya soportaba actualizar fechas y monto.
- **Verificado:** POST reserva (dic 1-5, S/400) → PUT (dic 10-14, S/600) → persiste correcto.

---

## 2. Servicios y Reglas de alojamiento (parte de RF05)
Antes los chips de "Servicios" y "Reglas" en *Publicar alojamiento* **no se guardaban** (el backend no los modelaba).

- **Backend:** columnas `servicios` y `reglas` (CSV en TEXT) **auto-migradas** en `AlojamientoImpl`; agregadas a `Alojamiento` (modelo), `AlojamientoDTO`, `AlojamientoMapper` (toDTO/toEntity) e INSERT/UPDATE/SELECT.
- **Frontend:** `Alojamiento.Servicios`/`Reglas` ahora son `List<string>`; `CrearAlojamiento.razor` guarda ambos y los **preselecciona al editar**; `DetalleAlojamiento.razor` muestra "Características" y "Reglas de la casa".
- **Verificado:** POST alojamiento con servicios/reglas → persiste y se lee igual.

---

## 3. Bloqueo de fechas por el anfitrión (RF extra)
El anfitrión puede bloquear rangos de su alojamiento (mantenimiento, uso personal).

- **Backend:** tabla `bloqueo_fecha` auto-creada; `BloqueoFecha` (modelo), DAO/BL, `BloqueoFechaRS` (GET `alojamiento/{id}`, POST, DELETE `{id}`). Los bloqueos se **fusionan** en `ReservaRS/ocupadas/{id}`, así el calendario del huésped los bloquea automáticamente.
- **Frontend:** `Models/Bloqueo.cs`, `Services/BloqueoServiceRest.cs`, calendario `bunkiDatePicker.initBloqueoRango` y modal "Bloquear fechas" en `MisAlojamientos.razor`.
- **Verificado en navegador:** crear bloqueo → aparece como ocupado en el detalle del huésped.

---

## 4. Denuncias (RF extra de moderación)
El huésped denuncia un alojamiento; el admin lo gestiona.

- **Backend:** tabla `denuncia` auto-creada; `Denuncia` (estado EN_REVISION/RESUELTO/CERRADO), DAO/BL, `DenunciaRS` (GET todas, POST, PUT `{id}/estado`). El anfitrión denunciado se deriva del alojamiento.
- **Frontend:** `Models/Denuncia.cs`, `Services/DenunciaServiceRest.cs`; botón "Reportar este alojamiento" + modal en `DetalleAlojamiento.razor` (solo autenticado y no-dueño); pestaña Denuncias de `AdminModeracion.razor` con datos reales + cambio de estado; contador real en `Admin.razor`.
- **Verificado:** crear (UI) → aparece en el panel admin → cambiar estado.

---

## 5. Incidencias / Soporte (RF17/RF18 — gestión de incidencias)
Antes era 100% mock. Ahora es una feature real.

- **Backend:** tabla `incidencia` auto-creada; `Incidencia` (estado ABIERTO/EN_PROCESO/RESUELTO/CERRADO, prioridad ALTA/MEDIA/BAJA), DAO/BL, `IncidenciaRS` (GET, POST, PUT `{id}/estado`).
- **Frontend:** `Models/Incidencia.cs`, `Services/IncidenciaServiceRest.cs`; formulario "Reportar un problema" en `Ayuda.razor` (/ayuda); `AdminIncidencias.razor` y pestaña de `AdminModeracion.razor` con datos reales + cambio de estado; contador real en el dashboard.
- **Verificado:** crear ticket (UI) → admin lo ve y cambia estado.

---

## 6. Tipo de cambio real en pagos (RF13/RF14)
`PagoBLImpl` usaba una tasa quemada (3.75). Ahora lee la tasa real de la tabla `tipo_cambio` (`TipoCambioBL`) según la moneda (PEN = 1.0; fallback 1.0 si no hay tasa).

---

## 7. Número de huéspedes real (RF11/RF21)
El backend devolvía siempre `numHuespedes = 1`.

- **Backend:** columna `num_huespedes` **auto-migrada** en `ReservaImpl`; agregada a `Reserva` (modelo), DAO (INSERT/UPDATE/SELECT) y `ReservaMapper`.
- **Frontend:** ya enviaba el valor; ahora persiste y se muestra real.
- **Verificado:** POST reserva con 3 huéspedes → persiste y devuelve 3.

---

## 8. Eliminación de TODOS los datos mock visibles
Se reemplazó el contenido falso por datos reales del backend (los `Provider` consultan REST; `DataService` queda solo como respaldo offline). Cambios:

- **`Admin.razor` (dashboard):** KPIs (usuarios/alojamientos/reservas/ingresos) reales; tendencias "% vs mes anterior" **calculadas de verdad** (mes vs mes por fecha); "Actividad reciente" derivada de filas reales (reservas + denuncias + pendientes).
- **`MisAlojamientos.razor`:** métricas por propiedad reales (reseñas/reservas/ingresos); se quitó "Vistas" (sin fuente).
- **`PanelAnfitrion.razor`:** ingresos 6 meses, reservas recientes, feed de actividad, ocupación y rating — todo real.
- **`AdminAnalytics.razor`:** evolución de calificación = promedio mensual acumulado real de las reseñas.
- **`Home.razor`:** "Alojamientos destacados" (mejor valorados reales) y **testimonios** = reseñas reales (4–5★ con comentario).
- **`AdminNotificaciones.razor` + campana de `AdminLayout.razor`:** feed real (denuncias/incidencias/reservas) + identidad del admin logueado.
- **`AdminPerfil.razor`:** datos del admin logueado + cambio de contraseña real.
- **`AdminUsuarios.razor`:** "Crear Administrador" persiste de verdad (crea un Administrador real), "Editar" persiste, sin lista demo falsa.

> Único stub que queda (por decisión del equipo): la **pasarela de pago** es simulada (`Task.Delay`); la reserva y el registro de pago/comisión/comprobante **sí** son reales.

---

## 9. Pasarela de pago real — Niubiz sandbox (RF13)
Se integró **Niubiz (ex-VisaNet Perú)** en modo **sandbox**, usando las credenciales de prueba públicas de Niubiz (comercio de prueba en soles `522591303`) — **sin necesidad de crear cuenta ni RUC**.

- **Backend:** `web/niubiz/NiubizClient.java` (token de seguridad → sesión → autorización v3, con `SSLContext` que confía en el host sandbox de Niubiz, ámbito solo ese cliente) y `web/rs/NiubizRS.java` (`POST /sesion`, `POST /autorizar`).
- **Frontend:** `Services/NiubizServiceRest.cs`, `wwwroot/js/niubiz.js` (lightbox `checkout.js` que devuelve el `transactionToken` a Blazor) y opción **"Tarjeta vía Niubiz (pasarela)"** en `Pagos.razor`. Flujo: sesión → lightbox → token → autorización → si aprueba, crea reserva + registra pago.
- **Respaldo:** se mantiene el pago simulado (tarjeta/transferencia/billetera) como fallback para la demo.
- **Modelo "action":** el lightbox (checkout.js) hace POST del `transactionToken` a `/niubiz/callback` (endpoint Minimal API en `Program.cs`, con `DisableAntiforgery`), que autoriza, crea/confirma la reserva + pago y redirige a `/pagos?pagoOk=1` (modal de éxito) o `?pagoError=1`.
- **Tarjetas de prueba Niubiz (sandbox):** APROBADA `4919 1481 0785 9067` (venc. futuro, CVV 123); RECHAZADA `4111 1111 1111 1111`. (La tarjeta debe pasar validación Luhn; el lightbox rechaza números inválidos.)
- **Verificado por curl:** `/sesion` devuelve `sessionKey` real; `/autorizar` con token inválido devuelve `aprobado:false` (HTTP 400). El lightbox abre correctamente en el navegador.

## 10. Mejoras de UX/UI y accesibilidad
Tras una auditoría de UX/UI se cerraron los hallazgos prioritarios:

- **Accesibilidad de modales** (`wwwroot/js/modal-a11y.js`): todos los modales (`.modal-overlay`) reciben `role="dialog"` + `aria-modal` + `aria-labelledby`, **foco inicial** y restauración del foco, **trampa de foco** (Tab no se escapa), y **cierre con Escape** y por **clic en el fondo**, sin modificar cada componente.
- **Legibilidad** (`styles.css`): token `--ink-faint` oscurecido (#9A9488 → #7E7869) para mejor contraste de textos de apoyo, conservando los 3 niveles de tinta.
- **Toasts con auto-cierre** (`Components/Shared/AlertMessage.razor`): parámetro `AutoDismissMs`; los avisos flotantes se ocultan solos (~4–5 s).
- **z-index ordenado**: nuevo token `--z-popover: 650` para los calendarios flatpickr (antes `9999/99999 !important`), dentro de la escala semántica (sticky→overlay→dropdown→modal→popover→toast).

## 11. (Sesión previa) Otros RF ya cerrados antes de estas sesiones
Moderación de alojamientos (aprobar/rechazar), recuperar/cambiar contraseña, **JasperReports** (RF20 rentabilidad, RF21 ocupación, RF22/RF24 satisfacción, RF25 usuarios, RF26 voucher — PDF + CSV) y favoritos persistentes (RF27).

---

## Estado de la auditoría RF01–RF22 (verificado contra código)

**Implementados (19):** RF01, RF03, RF04, RF05, RF06, RF07, RF08, RF09, RF10, RF11, RF12, RF13, RF14, RF16, RF17, RF18, RF20, RF21, RF22.

**Parciales (3):**
- **RF02 — Validación documentaria:** modelo/DAO listos, pero `Documentos.razor` simula la subida y falta endpoint + UI admin para aprobar/rechazar.
- **RF15 — Cuentas bancarias:** se vinculan cuentas (CCI), pero faltan saldos/depósitos/retiros.
- **RF19 — Reseñas:** la reseña al **alojamiento** funciona; falta calificar al **anfitrión** con su promedio propio.

**Notas menores:** RF08 no filtra por país en la UI (campo existe en BD); RF13 la pasarela es simulada (datos reales).
