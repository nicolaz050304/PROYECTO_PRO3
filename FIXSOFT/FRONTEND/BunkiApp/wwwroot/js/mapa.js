window.bunki = window.bunki || {};

bunki.mapa = (function () {
    let map = null;
    let markersLayer = null;
    let distritoLayer = null;
    let alojamientosPendientes = null;
    let intentosMarcadores = 0;

    const distritoCoords = {
        "Miraflores": [-12.1191, -77.0333],
        "La Molina": [-12.0764, -76.9453],
        "Barranco": [-12.1464, -77.0219],
        "San Isidro": [-12.0972, -77.0375],
        "Pueblo Libre": [-12.0778, -77.0622],
        "Chaclacayo": [-11.9761, -76.7628],
        "Los Olivos": [-11.9936, -77.0706],
        "San Juan de Lurigancho": [-12.0231, -76.9983],
        "San Martín de Porres": [-12.0250, -77.0889],
        "Villa El Salvador": [-12.2158, -76.9400],
        "Puente Piedra": [-11.8667, -77.0750],
        "Comas": [-11.9383, -77.0531],
        "San Juan de Miraflores": [-12.1569, -76.9697],
        "Ate": [-12.0253, -76.9178],
        "Santa Anita": [-12.0453, -76.9647],
        "Villa María del Triunfo": [-12.1764, -76.9422],
    };

    const limaCenter = [-12.0464, -77.0428];

    function init(elementId) {
        if (map) { map.remove(); map = null; markersLayer = null; distritoLayer = null; }

        const el = document.getElementById(elementId);
        if (!el) return;

        map = L.map(elementId, {
            center: limaCenter,
            zoom: 11,
            zoomControl: true,
            scrollWheelZoom: true,
        });

        L.tileLayer('https://tiles.stadiamaps.com/tiles/alidade_smooth/{z}/{x}/{y}{r}.png?api_key=274e9521-ccf4-41b7-86ba-6a9fb1647a5b', {
            attribution: '© Stadia Maps © OpenMapTiles © OpenStreetMap contributors',
            maxZoom: 20,
        }).addTo(map);

        markersLayer = L.layerGroup().addTo(map);

        // El contenedor puede aún tener tamaño 0 (recién montado / oculto): forzamos
        // un recálculo de tamaño una vez que el layout se estabilice.
        setTimeout(() => { if (map) map.invalidateSize(); }, 200);

        // Si llegaron marcadores antes de que el mapa estuviera listo, dibújalos ya.
        if (alojamientosPendientes !== null) {
            const pendientes = alojamientosPendientes;
            alojamientosPendientes = null;
            actualizarMarcadores(pendientes);
        }
    }

    function estaListo() {
        return map !== null && markersLayer !== null;
    }

    function crearIcono(precio) {
        return L.divIcon({
            className: '',
            html: `<div class="bunki-pin">S/ ${precio}</div>`,
            iconSize: [72, 32],
            iconAnchor: [36, 16],
            popupAnchor: [0, -22],
        });
    }

    function actualizarMarcadores(alojamientos) {
        // Posible carrera: init aún no terminó. Guardamos los datos pendientes y
        // reintentamos cada 150ms hasta ~15 veces (sin bucle infinito). init() también
        // dibujará lo pendiente en cuanto el mapa esté listo.
        if (!map || !markersLayer) {
            alojamientosPendientes = alojamientos;
            if (intentosMarcadores < 15) {
                intentosMarcadores++;
                setTimeout(() => actualizarMarcadores(alojamientosPendientes), 150);
            }
            return;
        }
        intentosMarcadores = 0;
        alojamientosPendientes = null;
        markersLayer.clearLayers();
        if (!alojamientos || alojamientos.length === 0) return;

        const bounds = [];

        alojamientos.forEach(function (a) {
            let lat, lng;
            if (a.latitud && a.longitud && a.latitud !== 0 && a.longitud !== 0) {
                lat = a.latitud; lng = a.longitud;
            } else {
                const base = distritoCoords[a.distrito] || limaCenter;
                lat = base[0] + (Math.random() - 0.5) * 0.014;
                lng = base[1] + (Math.random() - 0.5) * 0.014;
            }

            const marker = L.marker([lat, lng], { icon: crearIcono(a.precioNoche) });

            marker.bindPopup(`
                <div class="bunki-popup">
                    ${a.imagenUrl ? `<img src="${a.imagenUrl}" style="width:100%;height:90px;object-fit:cover;border-radius:6px;margin-bottom:8px;" />` : ''}
                    <strong>${a.nombre}</strong>
                    <div class="bunki-popup-loc">📍 ${a.distrito}</div>
                    <div class="bunki-popup-precio">S/ ${a.precioNoche}<span>/noche</span></div>
                    <div class="bunki-popup-rating">★ ${a.rating} · ${a.totalResenas} reseñas</div>
                </div>
            `, { maxWidth: 220 });

            marker.on('click', function () {
                const card = document.getElementById('card-' + a.id);
                if (card) {
                    card.scrollIntoView({ behavior: 'smooth', block: 'center' });
                    card.classList.add('card-highlight');
                    setTimeout(() => card.classList.remove('card-highlight'), 2000);
                }
            });

            markersLayer.addLayer(marker);
            bounds.push([lat, lng]);
        });

        if (bounds.length > 0) {
            map.fitBounds(bounds, { padding: [50, 50], maxZoom: 13 });
        }
    }

    // Resalta el polígono del distrito usando Nominatim + OSM
    async function resaltarDistrito(nombreDistrito) {
        // Limpiar capa anterior
        if (distritoLayer) {
            map.removeLayer(distritoLayer);
            distritoLayer = null;
        }

        if (!nombreDistrito || nombreDistrito.trim() === '') return;

        try {
            const query = encodeURIComponent(`${nombreDistrito}, Lima, Peru`);
            const url = `https://nominatim.openstreetmap.org/search?q=${query}&format=json&polygon_geojson=1&limit=1`;

            const res = await fetch(url, {
                headers: { 'Accept-Language': 'es' }
            });
            const data = await res.json();

            if (data && data.length > 0 && data[0].geojson) {
                distritoLayer = L.geoJSON(data[0].geojson, {
                    style: {
                        color: '#f59e0b',
                        weight: 3,
                        opacity: 0.9,
                        fillColor: '#f59e0b',
                        fillOpacity: 0.12,
                        dashArray: '6 4',
                    }
                }).addTo(map);

                map.fitBounds(distritoLayer.getBounds(), { padding: [40, 40] });
            }
        } catch (e) {
            console.warn('[bunki.mapa] No se pudo cargar el polígono del distrito:', e);
        }
    }

    function limpiarDistrito() {
        if (distritoLayer && map) {
            map.removeLayer(distritoLayer);
            distritoLayer = null;
        }
    }

    function invalidarTamano() {
        if (map) setTimeout(() => map.invalidateSize(), 150);
    }

    return { init, estaListo, actualizarMarcadores, resaltarDistrito, limpiarDistrito, invalidarTamano };
})();