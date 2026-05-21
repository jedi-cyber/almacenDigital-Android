# Fase 3: Pantallas moviles principales

## Objetivo

Crear las primeras pantallas moviles nativas para que la app deje de depender del WebView como experiencia principal.

## Pantallas agregadas

- Inicio / dashboard.
- Productos.
- Formulario de producto.
- Estantes.
- Reportes.
- Configuracion.
- Ruta guiada movil como alternativa de uso en campo.

## Flujo actual

El dashboard abre pantallas nativas con botones tactiles y navegacion interna. El boton atras de Android vuelve al dashboard cuando el usuario esta dentro de una pantalla movil.

Productos ya permite:

- Entrar a la lista.
- Buscar visualmente por SKU o nombre.
- Abrir formulario para agregar producto.
- Abrir formulario con datos demo para simular detalle/edicion.

Estantes ya permite:

- Ver tarjetas moviles de ubicaciones.
- Preparar la futura vista de capacidad por estante.

Reportes ya permite:

- Entrar a resumen de inventario.
- Entrar a resumen de ocupacion.

Configuracion ya permite:

- Ver y editar visualmente la URL base de API.
- Preparar persistencia futura de ajustes.

## Pendiente para Fase 4

- Conectar productos reales desde la API.
- Conectar estantes reales desde la API.
- Guardar productos desde el formulario.
- Persistir la URL de API.
- Mostrar estados de carga, error y vacio con datos reales.

## Verificacion

La app compila correctamente con:

```text
.\gradlew.bat assembleDebug
```

Resultado esperado:

```text
BUILD SUCCESSFUL
```
