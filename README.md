# Almacén 3D - Guía de Instalación y Configuración

Este repositorio contiene el código de la **aplicación móvil nativa Android** para el sistema de gestión de almacenes con visor 3D interactivo. Para que el sistema funcione correctamente, es necesario configurar el backend y el visor web.

---

## 🛠️ Prerrequisitos

Antes de comenzar, asegúrate de tener instalado:
*   **Android Studio** (Ladybug o superior).
*   **Git** para clonar el repositorio.
*   Un servidor local (**XAMPP**, WAMP) o un hosting para la API (PHP).
*   Un navegador moderno para el visor 3D.

---

## 🚀 Guía de Instalación para Terceros

### 1. Clonar el Proyecto
Copia el código en tu máquina local:
```bash
git clone https://github.com/USUARIO/ALMACEN-3D-ANDROID.git
```

### 2. Configuración del Backend (API y Base de Datos)
La aplicación móvil no funciona sola; requiere conectarse a una base de datos MySQL a través de una API PHP.

1.  **Base de Datos:** Importa el archivo SQL del proyecto (normalmente `almacen_db.sql`) en tu servidor MySQL (phpMyAdmin).
2.  **API:** Sube la carpeta del backend (API PHP) a tu servidor (ej: `htdocs/almacen-api/`).
3.  **Conexión:** Edita el archivo de configuración en la API (ej: `db_config.php`) con tus credenciales de base de datos (host, usuario, contraseña).
4.  **Prueba:** Abre en el navegador `http://tu-servidor/almacen-api/productos.php`. Si recibes un JSON (vacío o con datos), la API está lista.

### 3. Configuración del Visor 3D (Web)
El visor 3D es el componente visual que muestra las rutas.
*   **Opción A (Recomendada):** Despliégalo en **GitHub Pages**. Sube la carpeta `docs` de la web a un repositorio y activa Pages en la configuración.
*   **Opción B (Local):** Alójalo en la misma carpeta de tu servidor local (ej: `htdocs/visor3d/`).

### 4. Instalación de la App Android

#### Desde el código fuente:
1.  Abre **Android Studio** y selecciona `Open` -> Busca la carpeta `Almacen3D2`.
2.  Espera a que termine el **Gradle Sync**.
3.  Conecta tu celular físico (con Depuración USB activada) o inicia un emulador.
4.  Haz clic en el botón **Run (Play)** para instalar la app.

#### Generar APK para otros dispositivos:
Si quieres pasarle la app a otra persona sin usar Android Studio:
1. En Android Studio, ve a `Build` -> `Build Bundle(s) / APK(s)` -> `Build APK(s)`.
2. Una vez termine, haz clic en `locate` en el aviso emergente.
3. Envía el archivo `app-debug.apk` al dispositivo e instálalo manualmente.

### 5. Configuración Final en el Celular
Una vez instalada la app:
1.  Abre la aplicación.
2.  En la pantalla de Login, toca el botón **"Configurar Servidor"**.
3.  Introduce la URL completa de tu API (ej: `http://192.168.1.50/almacen-api/`). 
    *   *Nota: Si usas servidor local, usa la IP de tu PC, no "localhost".*
4.  Inicia sesión con tus credenciales (Usuario/Contraseña configurados en la DB).

---

## 📱 Funciones que puedes probar
*   **Búsqueda de Productos:** Encuentra artículos por nombre o SKU.
*   **Escáner QR/Barras:** Usa la cámara para identificar productos rápidamente.
*   **Ruta 3D:** Selecciona un producto y toca "Ver Ruta" para abrir el visor interactivo.
*   **Edición de Stock:** Actualiza información directamente desde el móvil.

---

## ⚠️ Solución de Problemas Comunes
*   **Error de Conexión:** Verifica que tu celular y tu PC estén en la misma red Wi-Fi si usas servidor local.
*   **El Visor 3D no carga:** Asegúrate de que la URL en la configuración de la app apunte correctamente a la API y que el visor tenga acceso a internet.
*   **Gradle Error:** Asegúrate de tener conexión a internet para descargar las dependencias la primera vez.

---
*Desarrollado para la optimización y digitalización logística.*
