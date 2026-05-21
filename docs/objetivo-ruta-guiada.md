# Objetivo funcional: busqueda y ruta guiada

## Proposito

El 3D representa el almacen real. La app movil debe ayudar al usuario a encontrar rapidamente un producto dentro de ese almacen.

## Flujo principal movil

1. El usuario entra a Productos.
2. Busca por SKU o nombre.
3. Selecciona Ruta.
4. La app muestra:
   - estante
   - seccion aproximada
   - nivel aproximado
   - lado del estante
   - pasos guiados para llegar al producto
5. Si necesita apoyo visual, abre Ruta 3D desde la app movil.

## Implementacion actual

La ruta se calcula usando:

- `shelfId`: estante donde esta el producto.
- `localX`: lado o seccion dentro del estante.
- `localY`: nivel bajo, medio o alto.
- `localZ`: parte frontal, central o posterior.
- dimensiones del estante cuando estan disponibles.

## Criterio de experiencia movil

La busqueda y la ruta deben ser mas importantes que la administracion del 3D en movil. La app movil usa una vista 3D de navegacion, similar a un mapa del almacen, pero no muestra controles administrativos de la web.

La app movil solo puede buscar, crear y editar productos. No puede eliminar productos ni editar estantes; esas tareas pertenecen a la version web.

## Mejora futura recomendada

- Mostrar un mini mapa 2D nativo del almacen.
- Dibujar una linea desde entrada hasta estante.
- Crear un mini mapa 2D movil para reemplazar la necesidad de abrir la web.
- Mejorar el modo Ruta 3D para que funcione como "Google Maps del almacen": pasos, avance, llegada y resaltado del producto.
- Integrar escaneo real de codigo de barras para buscar el producto automaticamente.
