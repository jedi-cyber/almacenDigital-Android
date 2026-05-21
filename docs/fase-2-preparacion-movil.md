# Fase 2: Preparacion de la app movil

## Objetivo

Preparar `Almacen3D2` para dejar de ser solo un contenedor WebView y convertirse en una aplicacion movil con estructura propia.

## Cambios realizados

- La app inicia en un dashboard movil nativo.
- La app movil queda preparada con pantallas propias.
- Se agregaron modelos base para datos compartidos:
  - `Product`
  - `Shelf`
- Se agrego una configuracion inicial de API:
  - `ApiConfig`
  - `WarehouseApiClient`
- Se agregaron recursos visuales para una interfaz movil:
  - colores
  - estilos
  - fondos de tarjetas y botones

## Estructura preparada

```text
app/src/main/java/com/example/almacen3d/
  MainActivity.kt
  model/
    Product.kt
    Shelf.kt
  network/
    ApiConfig.kt
    WarehouseApiClient.kt
```

## API compartida

La app apunta inicialmente a:

```text
http://192.168.18.189/almacenDigital/api/
```

Esta URL apunta a la PC donde corre XAMPP dentro de la red local.

## Estado de la interfaz web embebida

El `WebView` fue retirado de la app movil. La interfaz web y la administracion 3D completa pertenecen a la version web.

## Criterio de cierre

La Fase 2 queda completa cuando:

- Android compila correctamente.
- La pantalla inicial es movil y no abre la web automaticamente.
- Existen paquetes base para modelos y red.
- La app movil no abre la interfaz web embebida.
- La Fase 3 puede empezar implementando pantallas moviles reales.
