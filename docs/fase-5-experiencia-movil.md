# Fase 5: Experiencia movil

## Objetivo

Mejorar la experiencia de uso para que la app se sienta como aplicacion movil y no como una web adaptada.

## Cambios realizados

- Se agrego navegacion inferior:
  - Inicio
  - Productos
  - Estantes
  - Reportes
- La interfaz web embebida fue retirada para no mezclar experiencias.
- Se agregaron filtros rapidos en Productos:
  - Todos
  - Sin categoria
  - Primer estante
- El buscador movil filtra en vivo por SKU o nombre.
- Los botones principales tienen altura tactil de 48 dp o mas.
- Los formularios usan campos verticales comodos para pantalla pequena.
- Los estados de carga, vacio, exito y error se muestran en bloques visuales.
- Las acciones de guardar muestran mensajes tipo app.
- Las listas se renderizan como tarjetas tactiles.

## Pantallas mejoradas

- Dashboard.
- Productos.
- Formulario de producto.
- Estantes.
- Reportes.
- Configuracion.

## Restriccion funcional

La app movil permite buscar, crear y editar productos. No permite eliminar productos ni editar estantes; esas acciones se realizan desde la version web.

## Verificacion

La app compila correctamente con:

```text
.\gradlew.bat assembleDebug
```

Resultado:

```text
BUILD SUCCESSFUL
```

## Siguiente fase sugerida

La Fase 6 puede enfocarse en funciones nativas:

- Camara o escaner.
- Permisos Android.
- Cache local.
- Modo offline basico.
- Compartir o exportar reportes.
