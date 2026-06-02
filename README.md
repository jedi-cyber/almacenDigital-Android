# Almacén 3D - Sistema de Gestión Logística Inteligente

![Android](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?logo=kotlin&logoColor=white)
![Three.js](https://img.shields.io/badge/3D_Engine-Three.js-black?logo=three.js&logoColor=white)
![Status](https://img.shields.io/badge/Status-Production--Ready-success)

**Almacén 3D** es una solución tecnológica avanzada diseñada para transformar la gestión de inventarios tradicional en una experiencia visual e interactiva. Combina la robustez de una aplicación nativa Android con la potencia de un motor gráfico 3D para optimizar la localización y el flujo de mercancías dentro de centros de distribución.

---

## 🌟 Características Principales

*   **Visor 3D Interactivo:** Visualización espacial del almacén para localizar productos con precisión.
*   **Ruta Guiada:** Sistema que traza el camino más corto hacia el producto seleccionado.
*   **Escáner QR/Barras:** Integración nativa con la cámara para identificación rápida de SKUs.
*   **Sincronización en la Nube:** API REST para gestión de datos en tiempo real.
*   **Modo Resiliente:** Caché local que permite el funcionamiento en zonas con baja cobertura.
*   **Interfaz Profesional:** Diseño moderno basado en Material Design 3 optimizado para terminales industriales.

---

## 🚀 Guía de Instalación para Terceros

### 1. Requisitos Previos
*   **Android Studio** (Ladybug o superior).
*   **Servidor Web:** XAMPP, WAMP o un Hosting con soporte PHP/MySQL.
*   **Dispositivo Android:** Android 10 (API 29) o superior.

### 2. Configuración del Backend (API)
La aplicación requiere conectarse a una base de datos centralizada:
1.  **Base de Datos:** Crea una base de datos MySQL e importa el archivo `.sql` del proyecto.
2.  **API PHP:** Sube la carpeta del backend a tu servidor.
3.  **Conexión:** Edita el archivo de configuración de la API con las credenciales de tu base de datos.
4.  **Prueba:** Verifica el acceso a los endpoints (ej.: `http://tu-servidor/api/productos.php`).

### 3. Instalación de la Aplicación

#### Opción A: Desde Código Fuente
1.  Clona el repositorio: `git clone https://github.com/jedi-cyber/almacenDigital-Android.git`.
2.  Abre la carpeta en **Android Studio**.
3.  Sincroniza Gradle y ejecuta la app en un dispositivo físico o emulador.

#### Opción B: Generar APK (Desarrolladores)
1.  En Android Studio, ve a `Build` -> `Build Bundle(s) / APK(s)` -> `Build APK(s)`.
2.  Una vez generado, instala el archivo `app-debug.apk` en tu dispositivo.

#### Opción C: Descarga Directa (Releases)
Si solo deseas instalar la aplicación sin compilar el código:
1.  Ve a la sección de **[Releases](https://github.com/jedi-cyber/almacenDigital-Android.git/releases)** en este repositorio de GitHub.
2.  Busca la versión más reciente (etiquetada como `Latest`).
3.  En el apartado **Assets**, descarga el archivo `.apk` (ej: `almacen3d-v1.0.apk`).
4.  Transfiere el archivo a tu dispositivo Android e instálalo (asegúrate de permitir la instalación de fuentes desconocidas si es necesario).

### 4. Configuración Final
1.  Abre la app e ignora el error de conexión inicial.
2.  Toca el botón **"Configurar Servidor"** en la pantalla de login.
3.  Introduce la URL de tu API (ej: `http://192.168.1.50/api/`).
4.  Inicia sesión con las credenciales configuradas en la base de datos.

---

## 🛠️ Stack Tecnológico

*   **Frontend Móvil:** Kotlin, Jetpack libraries (CameraX, WebKit), Material Design 3.
*   **Motor 3D:** Three.js integrado mediante WebView optimizado.
*   **Backend:** PHP 8.x con arquitectura orientada a servicios.
*   **Persistencia:** MySQL (Remoto) y SharedPreferences (Local).

---

## 📂 Estructura del Proyecto
*   `/app`: Código fuente Android (Kotlin).
*   `/app/src/main/assets`: Activos del visor 3D.
*   `/docs`: Documentación detallada de las fases de desarrollo.

---
*Desarrollado para la optimización y digitalización de procesos logísticos.*
