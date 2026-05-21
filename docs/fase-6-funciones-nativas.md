# Fase 6: Funciones nativas

## Objetivo

Agregar capacidades propias de Android para que la app movil no dependa solo de controles web.

## Cambios realizados

- Permisos Android:
  - Camara.
  - Notificaciones.
  - Vibracion / feedback tactil.

- Camara:
  - El formulario de producto tiene un boton "Camara / escaner".
  - Abre la app de camara del dispositivo.
  - Si el SKU esta vacio, al volver de la camara se genera un SKU temporal `CAM-xxxxxx`.

- Notificaciones:
  - Se crea un canal de notificaciones.
  - Al guardar producto se muestra una notificacion nativa.

- Guardado local / cache:
  - Los productos cargados desde API se guardan en cache local.
  - Los estantes cargados desde API se guardan en cache local.

- Modo offline basico:
  - Si falla la conexion al cargar productos, se muestra la ultima lista en cache.
  - Si falla la conexion al cargar estantes, se muestra la ultima lista en cache.
  - Si falla la conexion en reportes, se genera el resumen con cache local.

- Compartir / exportar reportes:
  - La pantalla Reportes tiene boton "Compartir reporte".
  - Usa el menu nativo de Android para enviar texto por apps instaladas.

- Feedback tactil:
  - Las acciones principales usan feedback haptico junto con mensajes tipo app.

## Limitacion actual

La funcion de camara no decodifica codigos de barra reales todavia. Para escaneo real de codigos se recomienda integrar una libreria especializada en una fase posterior, por ejemplo ML Kit Barcode Scanning o ZXing.

La app movil no elimina productos ni edita estantes. Esas funciones quedan reservadas para la web.

## Verificacion

La app compila correctamente con:

```text
.\gradlew.bat assembleDebug
```

Resultado:

```text
BUILD SUCCESSFUL
```
