# 🌱 EcoRewards - Sistema de Recompensas Ecológicas

## 📋 Descripción

EcoRewards es una aplicación móvil Android que promueve el reciclaje mediante un sistema de recompensas. El proyecto consta de dos aplicaciones complementarias:

- **EcoRewards User**: Aplicación para usuarios finales que permite canjear puntos por productos
- **EcoHelper Admin**: Aplicación para administradores que gestiona el escaneo de residuos y asignación de puntos

## 🎯 Objetivo

Fomentar la conciencia ambiental y el reciclaje mediante un sistema gamificado que recompensa a los usuarios por reciclar diferentes tipos de residuos.

## ✨ Características Principales

### 🔐 Sistema de Autenticación

- Registro de usuarios con validación de correo electrónico
- Inicio de sesión seguro con Firebase Authentication
- Recuperación de contraseña por correo electrónico
- Opción "Recordar sesión" para mayor comodidad

### 📱 EcoRewards User App

- **Gestión de Puntos**: Visualización de EcoPoints acumulados
- **Tienda Virtual**: Canje de puntos por productos como:
  - Agua (200 puntos)
  - Galletas (150 puntos)
  - Lapiceros (140 puntos)
  - Lápices (140 puntos)
- **Interfaz Intuitiva**: Diseño moderno y fácil de usar

### 🤖 EcoHelper Admin App

- **Escaneo Inteligente**: Reconocimiento automático de residuos usando TensorFlow Lite
- **Categorías Soportadas**:
  - Botellas de plástico (10 puntos)
  - Botellas de vidrio (15 puntos)
  - Latas (20 puntos)
  - Bolsas de papel (5 puntos)
  - Cartón (15 puntos)
- **Asignación Automática**: Puntos otorgados automáticamente al usuario correspondiente
- **Validación de Usuarios**: Búsqueda y verificación de usuarios por correo electrónico

## 🛠️ Tecnologías Utilizadas

### Frontend

- **Android Native** (Java/Kotlin)
- **Material Design** para la interfaz de usuario
- **ViewBinding** para manejo de vistas
- **CameraX** para funcionalidad de cámara

### Backend & Servicios

- **Firebase Authentication** para autenticación de usuarios
- **Firebase Realtime Database** para almacenamiento de datos
- **Firebase Firestore** para gestión de documentos
- **Firebase Storage** para almacenamiento de archivos

### Machine Learning

- **TensorFlow Lite** para reconocimiento de imágenes
- **Modelo personalizado** entrenado para clasificación de residuos
- **GPU Acceleration** para procesamiento optimizado

## 📁 Estructura del Proyecto

```
EcoRewards/
├── app-user-ecorewards/          # Aplicación de usuario
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── java/com/example/ecorewardsuser/
│   │   │   │   ├── Login.java           # Autenticación
│   │   │   │   ├── Registrarse.java     # Registro de usuarios
│   │   │   │   ├── MenuPrincipal.java   # Menú principal
│   │   │   │   ├── MainTienda.java      # Tienda virtual
│   │   │   │   └── Canje.java           # Confirmación de canje
│   │   │   └── res/                     # Recursos de la aplicación
│   │   └── build.gradle.kts
│   └── build.gradle.kts
├── app-admin-ecorewards/         # Aplicación de administrador
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── java/com/example/ecohelper/
│   │   │   │   ├── Escaneo.kt           # Escaneo de residuos
│   │   │   │   ├── RegistroEx.java      # Registro de entregas
│   │   │   │   ├── ClassifyTf.kt        # Clasificación ML
│   │   │   │   └── MenuPrincipal.java   # Menú administrador
│   │   │   └── res/                     # Recursos de la aplicación
│   │   └── build.gradle.kts
│   └── build.gradle.kts
└── README.md
```

## 🚀 Instalación y Configuración

### Prerrequisitos

- Android Studio Arctic Fox o superior
- JDK 17 o superior
- Dispositivo Android con API 23+ (Android 6.0+)
- Cuenta de Firebase

### Pasos de Instalación

1. **Clonar el repositorio**

   ```bash
   git clone https://github.com/ucv-renacyt/ucv-ecorewards.git
   cd ucv-ecorewards
   ```

2. **Configurar Firebase**

   - Crear un proyecto en [Firebase Console](https://console.firebase.google.com/)
   - Descargar `google-services.json` para cada aplicación
   - Colocar los archivos en:
     - `app-user-ecorewards/app/google-services.json`
     - `app-admin-ecorewards/app/google-services.json`

3. **Compilar las aplicaciones**

   ```bash
   # Compilar aplicación de usuario
   cd app-user-ecorewards
   ./gradlew assembleDebug

   # Compilar aplicación de administrador
   cd ../app-admin-ecorewards
   ./gradlew assembleDebug
   ```

4. **Instalar en dispositivo**
   - Conectar dispositivo Android o usar emulador
   - Instalar ambas aplicaciones desde Android Studio

## 📖 Uso del Sistema

### Para Usuarios Finales

1. **Registro**: Crear cuenta con correo electrónico y contraseña de 6 dígitos
2. **Inicio de Sesión**: Acceder con credenciales registradas
3. **Acumular Puntos**: Entregar residuos en puntos de recolección
4. **Canjear Productos**: Usar puntos en la tienda virtual

### Para Administradores

1. **Inicio de Sesión**: Acceder con credenciales de administrador
2. **Escaneo**: Usar la cámara para escanear residuos entregados
3. **Validación**: Verificar el tipo de residuo detectado
4. **Asignación**: Buscar usuario por correo y asignar puntos automáticamente

## 🔧 Configuración de Firebase

### Estructura de Base de Datos

```json
{
  "Users": {
    "userId": {
      "nombre": "Nombre del Usuario",
      "apellidos": "Apellidos del Usuario",
      "correo": "usuario@email.com",
      "puntos": 150
    }
  }
}
```

### Servicios Habilitados

- Authentication (Email/Password)
- Realtime Database
- Firestore
- Storage

---
