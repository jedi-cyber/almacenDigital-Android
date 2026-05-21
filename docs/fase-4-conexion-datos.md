# Fase 4: Conexion con datos

## Objetivo

Conectar las pantallas moviles nativas con la API PHP/MySQL del proyecto web.

## API revisada

Proyecto web:

```text
C:\xampp\htdocs\almacenDigital
```

Endpoints usados:

```text
GET    /api/productos.php
POST   /api/productos.php
GET    /api/config.php
```

La URL base configurada es:

```text
http://192.168.18.189/almacenDigital/api/
```

Debe apuntar a la PC donde corre XAMPP dentro de la red local.

## Cambios realizados

- Cliente HTTP en `WarehouseApiClient`.
- Parser JSON de productos y estantes.
- Lista real de productos.
- Lista real de estantes.
- Guardado de productos con `POST`.
- Edicion de productos con el mismo `POST` de upsert.
- Estados de carga, vacio y error.
- Reportes basicos usando productos y estantes reales.
- Busqueda local por SKU o nombre.
- Configuracion visual de URL base de API.

## Pantallas conectadas

- Productos:
  - carga desde API
  - filtro local
  - editar
  - crear

Nota: eliminar productos queda reservado para la version web.

- Estantes:
  - carga desde API
  - tarjetas por estante

- Reportes:
  - cantidad de productos
  - cantidad de estantes

Los estantes se consultan desde el movil, pero se administran solamente desde la web.
  - volumen total registrado

## Verificacion

La app compila correctamente con:

```text
.\gradlew.bat assembleDebug
```

La API local respondio correctamente en:

```text
http://127.0.0.1/almacenDigital/api/productos.php
http://127.0.0.1/almacenDigital/api/config.php
```
