# Fase 7: Pruebas y entrega

## Objetivo

Validar la app movil, generar APKs y dejar documentado como ejecutar la version web y la version Android por separado.

## Validaciones realizadas

### Web

Proyecto:

```text
C:\xampp\htdocs\almacenDigital
```

Comando ejecutado:

```text
npm run build
```

Resultado:

```text
Build correcto
```

Build generado en:

```text
C:\xampp\htdocs\almacenDigital\dist
```

### API local

Endpoints revisados:

```text
http://127.0.0.1/almacenDigital/api/productos.php
http://127.0.0.1/almacenDigital/api/config.php
```

Resultado:

```text
HTTP 200
```

### Android debug

Proyecto:

```text
C:\Users\HP\AndroidStudioProjects\Almacen3D2
```

Comando ejecutado:

```text
.\gradlew.bat assembleDebug
```

Resultado:

```text
BUILD SUCCESSFUL
```

APK generado:

```text
C:\Users\HP\AndroidStudioProjects\Almacen3D2\app\build\outputs\apk\debug\app-debug.apk
```

### Android release

Comando ejecutado:

```text
.\gradlew.bat assembleRelease
```

Resultado:

```text
BUILD SUCCESSFUL
```

APK generado:

```text
C:\Users\HP\AndroidStudioProjects\Almacen3D2\app\build\outputs\apk\release\app-release-unsigned.apk
```

Nota: el APK release generado esta sin firma de distribucion. Para instalarlo como APK final fuera de Android Studio se debe configurar una firma release con keystore.

## Prueba en emulador

No se pudo ejecutar automaticamente desde terminal porque `adb` no esta disponible en el PATH.

Pasos desde Android Studio:

1. Abrir `C:\Users\HP\AndroidStudioProjects\Almacen3D2`.
2. Iniciar un emulador.
3. Ejecutar la app con el boton Run.
4. Mantener XAMPP activo con Apache y MySQL.
5. Usar esta URL de API en Configuracion:

```text
http://192.168.18.189/almacenDigital/api/
```

Pantallas a revisar:

- Inicio.
- Productos.
- Crear producto.
- Editar producto.
- Estantes.
- Reportes.
- Compartir reporte.
- Camara / escaner.
- Ruta guiada movil.

Restriccion: eliminar productos y editar estantes se prueba solo en la version web.

## Prueba en celular fisico

Pasos:

1. Conectar el celular a la misma red Wi-Fi que la PC.
2. Activar opciones de desarrollador y depuracion USB.
3. Iniciar XAMPP con Apache y MySQL.
4. Obtener la IP local de la PC.
5. En la app, ir a Configuracion.
6. Confirmar la URL de API usando la IP local de la PC:

```text
http://192.168.18.189/almacenDigital/api/
```

## Errores de conexion revisados

- La API local responde con HTTP 200.
- La app soporta cache local si no hay conexion.
- La app usa `192.168.18.189` como IP local de la PC.

## Ejecucion separada

### Web

```text
cd C:\xampp\htdocs\almacenDigital
npm install
npm run dev
```

Abrir:

```text
http://localhost:5173/
```

Para build web:

```text
npm run build
```

### Movil

```text
Abrir C:\Users\HP\AndroidStudioProjects\Almacen3D2 en Android Studio
```

Compilar debug:

```text
.\gradlew.bat assembleDebug
```

Compilar release:

```text
.\gradlew.bat assembleRelease
```

## Pendiente manual

- Probar visualmente en emulador desde Android Studio.
- Probar visualmente en celular fisico.
- Configurar keystore si se requiere APK release firmado.
