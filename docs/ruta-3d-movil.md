# Ruta 3D movil

## Objetivo

La app movil debe fusionar:

- representacion 3D del almacen
- busqueda de productos
- ruta guiada estilo mapa

No debe mostrar la interfaz administrativa completa de la web.

## Implementacion

La app Android abre el build web en un modo especial:

```text
index.html?mode=mobile-route&sku={SKU}
```

Ese modo:

- mantiene la escena 3D
- activa la busqueda/ruta del producto
- muestra controles de avanzar/retroceder ruta
- oculta gestion de estantes
- oculta edicion administrativa
- oculta registro web de productos
- oculta reportes web

## Flujo

1. Usuario busca producto en la app movil.
2. Usuario toca Ruta.
3. Usuario toca Abrir ruta 3D.
4. Android abre la escena en modo movil.
5. La ruta se dibuja desde la entrada hasta el producto.
6. El usuario avanza los pasos de ruta.

## Regla de producto

Movil:

- buscar productos
- crear productos
- editar productos
- ver ruta guiada
- abrir Ruta 3D

Web:

- editar estantes
- eliminar productos
- administrar configuracion completa del 3D
