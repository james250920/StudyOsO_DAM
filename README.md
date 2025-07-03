# 📚 StudyOso - Planificador de Estudios para Android

**StudyOso** es una aplicación móvil moderna para Android desarrollada en **Kotlin** que ayuda a estudiantes universitarios a organizar y gestionar sus actividades académicas de manera eficiente. La aplicación sigue principios de **Clean Architecture** y utiliza las mejores prácticas de desarrollo Android moderno.

---

## 🏛 Arquitectura del Proyecto

### **Arquitectura General**
El proyecto implementa una arquitectura basada en **MVVM (Model-View-ViewModel)** con separación de capas clara:

- **🎨 Presentation Layer:** Interfaces de usuario construidas con **Jetpack Compose** y gestión de estado con **ViewModels**
- **📊 Data Layer:** Persistencia local con **Room Database** y repositorios para acceso a datos
- **🔧 Domain Layer:** Modelos de dominio y lógica de negocio pura
- **⚙️ ViewModel Layer:** Gestión de estado de UI y comunicación entre capas

### **Principios Aplicados**
- ✅ **Single Responsibility Principle**
- ✅ **Inversión de Dependencias** 
- ✅ **Separación de Responsabilidades**
- ✅ **Reactive Programming** con StateFlow
- ✅ **Clean Code** y mantenibilidad

---

## 🗂 Estructura Real del Proyecto

```plaintext
app/src/main/java/com/menfroyt/studyoso/
├── 📱 MainActivity.kt                    # Actividad principal
├── 🧭 navigation/                        # Sistema de navegación
│   ├── NavegacionApp.kt                 # Configuración de navegación
│   ├── DrawerContent.kt                 # Menú lateral
│   └── NavigationConstants.kt           # Constantes de rutas
├── 🎨 presentation/                      # Capa de presentación (UI)
│   ├── auth/                            # Autenticación
│   │   ├── LoginScreen.kt
│   │   ├── RegisterScreen.kt
│   │   └── SessionManager.kt
│   ├── components/                      # Componentes principales
│   │   ├── DashboardScreen.kt           # Panel principal
│   │   ├── CalendarioScreen.kt          # Calendario académico
│   │   └── MatrizEisenhowerScreen.kt    # Matriz de productividad
│   ├── curso/                           # Gestión de cursos
│   │   ├── AgregarCursosScreen.kt
│   │   ├── ListCursoScreen.kt
│   │   └── DetalleCursoScreen.kt
│   ├── tarea/                           # Gestión de tareas
│   │   ├── AddTaskScreen.kt
│   │   └── ListTaskScreen.kt
│   ├── calificacion/                    # Gestión de calificaciones
│   │   ├── AgregarCalificacionScreen.kt
│   │   ├── ListCalificacionScreen.kt
│   │   └── DetalleCalificacionesScreen.kt
│   ├── usuario/                         # Perfil de usuario
│   │   └── PerfilScreen.kt
│   └── home/                            # Pantalla principal
│       ├── HomeScreen.kt
│       └── InicioScreen.kt
├── 🗃️ data/                             # Capa de datos
│   ├── db/                              # Base de datos
│   │   └── AppDatabase.kt               # Configuración Room
│   ├── dao/                             # Data Access Objects
│   │   ├── UsuarioDao.kt
│   │   ├── CursoDao.kt
│   │   ├── TareaDao.kt
│   │   ├── HorarioDao.kt
│   │   ├── CalificacionDao.kt
│   │   └── TipoPruebaDao.kt
│   ├── entities/                        # Entidades de BD
│   │   ├── Usuario.kt
│   │   ├── Curso.kt
│   │   ├── Tarea.kt
│   │   ├── Horario.kt
│   │   ├── Calificacion.kt
│   │   └── TipoPrueba.kt
│   └── repositories/                    # Repositorios
│       ├── UsuarioRepository.kt
│       ├── CursoRepository.kt
│       ├── TareaRepository.kt
│       ├── HorarioRepository.kt
│       ├── CalificacionRepository.kt
│       └── TipoPruebaRepository.kt
├── 🔗 domain/                           # Modelos de dominio
│   ├── Usuario.kt
│   ├── Curso.kt
│   ├── Tarea.kt
│   ├── Horario.kt
│   ├── Calificacion.kt
│   ├── TipoPrueba.kt
│   └── CursoConTiposPrueba.kt
├── 🎯 ViewModel/                        # ViewModels por módulo
│   ├── usuario/
│   ├── curso/
│   ├── tarea/
│   ├── calificacion/
│   ├── tipoPrueba/
│   └── Horario/
├── 🛠️ utils/                            # Utilidades
│   ├── PermissionHelper.kt
│   ├── WorkManagerHelper.kt
│   └── hashPassword.kt
└── 🎨 ui/theme/                         # Tema de la aplicación
    └── StudyOsoTheme.kt
```

---

## ⚙️ Stack Tecnológico Completo

### **🎨 Frontend & UI**
| Tecnología | Versión | Uso Principal |
|:-----------|:--------|:--------------|
| **Jetpack Compose** | 2025.05.00 | Framework de UI declarativo moderno |
| **Material Design 3** | 1.3.2 | Sistema de diseño y componentes |
| **Compose Navigation** | 2.9.0 | Navegación entre pantallas |
| **Compose Animation** | Latest | Animaciones fluidas y transiciones |

### **🗃️ Persistencia & Datos**
| Tecnología | Versión | Uso Principal |
|:-----------|:--------|:--------------|
| **Room Database** | 2.7.1 | Base de datos local SQLite |
| **Kotlin Coroutines** | 1.10.1 | Programación asíncrona |
| **StateFlow** | Latest | Gestión reactiva de estados |

### **🔧 Arquitectura & Gestión**
| Tecnología | Versión | Uso Principal |
|:-----------|:--------|:--------------|
| **ViewModel** | 2.8.0 | Gestión de estado de UI |
| **ViewModelFactory** | Latest | Inyección manual de dependencias |
| **WorkManager** | 2.10.1 | Tareas en segundo plano |
| **Kotlin Serialization** | 1.7.3 | Serialización de datos |

### **🔐 Seguridad & Utilidades**
| Tecnología | Versión | Uso Principal |
|:-----------|:--------|:--------------|
| **BCrypt** | 0.4 | Hashing seguro de contraseñas |
| **Coil** | 3.1.0 | Carga de imágenes |
| **Color Picker** | 0.7.0 | Selector de colores |

---

## 🚀 Funcionalidades Principales

### **👤 Gestión de Usuarios**
- ✅ **Registro e inicio de sesión** con validación
- ✅ **Gestión de sesiones** persistentes
- ✅ **Perfil de usuario** con estadísticas académicas
- ✅ **Hashing seguro** de contraseñas con BCrypt

### **� Gestión de Cursos**
- ✅ **CRUD completo** de cursos
- ✅ **Personalización visual** (colores, iconos)
- ✅ **Información detallada** (profesor, aula, créditos)
- ✅ **Horarios por curso** con gestión de días y horas

### **📅 Sistema de Calendario**
- ✅ **Tres vistas**: Mensual, Semanal y Diaria
- ✅ **Navegación fluida** entre períodos
- ✅ **Visualización de horarios** académicos
- ✅ **Indicadores visuales** de eventos
- ✅ **Diseño responsivo** para tablets y móviles
- ✅ **Animaciones modernas** Material Design 3

### **✅ Gestión de Tareas**
- ✅ **Matriz de Eisenhower** (Importante/Urgente)
- ✅ **Fechas de vencimiento** con validación
- ✅ **Asociación con cursos**
- ✅ **Estados de progreso** (Pendiente, Completada)
- ✅ **Notificaciones** con WorkManager

### **📊 Sistema de Calificaciones**
- ✅ **Tipos de pruebas** configurables por curso
- ✅ **Registro de calificaciones** por tipo
- ✅ **Cálculos automáticos** de promedios
- ✅ **Simulador de calificaciones**
- ✅ **Visualización estadística**

### **📱 Interfaz de Usuario**
- ✅ **Material Design 3** nativo
- ✅ **Tema oscuro/claro** automático
- ✅ **Navegación con drawer** lateral
- ✅ **Bottom navigation** para acceso rápido
- ✅ **Animaciones fluidas** entre pantallas
- ✅ **Diseño adaptativo** para diferentes tamaños

---

## 🔄 Flujo de Datos y Arquitectura

### **Patrón MVVM Implementado**
```plaintext
[🎨 UI Compose] 
    ↕️ observa StateFlow
[🎯 ViewModel] 
    ↕️ llama funciones suspend
[📦 Repository] 
    ↕️ abstrae acceso a datos
[🗃️ DAO Room] 
    ↕️ consultas SQL
[💾 SQLite Database]
```

### **Gestión de Estados**
1. **UI State**: Manejado con `@Composable` y `remember`
2. **Business State**: Gestionado con `StateFlow` en ViewModels
3. **Data State**: Persistido en Room Database
4. **Navigation State**: Controlado por Compose Navigation

### **Flujo de Operaciones**
1. **Usuario interactúa** con Compose UI
2. **UI invoca** función del ViewModel
3. **ViewModel ejecuta** lógica de negocio
4. **Repository accede** a datos via DAO
5. **Room retorna** datos como Flow
6. **StateFlow actualiza** automáticamente la UI

---

## 💾 Modelo de Datos

### **🗃️ Entidades Principales**

#### **👤 Usuario**
```kotlin
- idUsuario: Int (PK)
- nombre: String
- apellido: String
- fechaNacimiento: String
- correo: String (único)
- contrasena: String (hash BCrypt)
```

#### **📚 Curso**
```kotlin
- idCurso: Int (PK)
- nombreCurso: String
- color: String (hex)
- profesor: String?
- aula: String?
- creditos: Int?
- idUsuario: Int (FK → Usuario)
```

#### **✅ Tarea**
```kotlin
- idTarea: Int (PK)
- descripcion: String
- esImportante: Boolean
- esUrgente: Boolean
- fechaVencimiento: String
- fechaCreacion: String
- estado: String
- idUsuario: Int (FK → Usuario)
- idCurso: Int? (FK → Curso)
```

#### **📅 Horario**
```kotlin
- idHorario: Int (PK)
- idCurso: Int (FK → Curso)
- diaSemana: String
- horaInicio: String
- horaFin: String
- aula: String
```

#### **� Calificacion**
```kotlin
- idCalificacion: Int (PK)
- idCurso: Int (FK → Curso)
- idTipoPrueba: Int (FK → TipoPrueba)
- numeroPrueba: Int
- calificacion: Double
- fechaRegistro: String
```

### **🔗 Relaciones de Base de Datos**
- **Usuario → Cursos**: 1:N (Un usuario tiene múltiples cursos)
- **Usuario → Tareas**: 1:N (Un usuario tiene múltiples tareas)
- **Curso → Horarios**: 1:N (Un curso tiene múltiples horarios)
- **Curso → Calificaciones**: 1:N (Un curso tiene múltiples calificaciones)
- **Curso → TiposPrueba**: 1:N (Un curso tiene múltiples tipos de prueba)

---

## 🛠️ Configuración y Desarrollo

### **📋 Requisitos del Sistema**
- **Android Studio**: Hedgehog | 2023.1.1 o superior
- **Android SDK**: Nivel 30-35 (Android 11-15)
- **Kotlin**: 2.0.21 o superior
- **JDK**: 11 o superior

### **🚀 Instalación y Configuración**

1. **Clonar el repositorio**
   ```bash
   git clone [repository-url]
   cd StudyOsO_DAM
   ```

2. **Abrir en Android Studio**
   - Importar proyecto existente
   - Sync automático de Gradle

3. **Configurar dependencias**
   ```bash
   ./gradlew build
   ```

4. **Ejecutar la aplicación**
   - Conectar dispositivo Android o usar emulador
   - Run 'app' desde Android Studio

### **🔧 Configuraciones Importantes**

#### **Gradle (app/build.gradle.kts)**
```kotlin
compileSdk = 35
minSdk = 30
targetSdk = 35

// Compose BOM para compatibilidad
implementation(platform("androidx.compose:compose-bom:2025.05.00"))

// Room para base de datos
implementation("androidx.room:room-runtime:2.7.1")
ksp("androidx.room:room-compiler:2.7.1")

// WorkManager para notificaciones
implementation("androidx.work:work-runtime-ktx:2.10.1")
```

#### **Room Database**
```kotlin
@Database(
    entities = [Usuario::class, Curso::class, Tarea::class, 
               Horario::class, Calificacion::class, TipoPrueba::class],
    version = 1,
    exportSchema = true
)
```

---

## 🧪 Testing y Calidad

### **🔍 Herramientas de Testing**
- **JUnit**: Testing unitario
- **Espresso**: Testing de UI
- **Room Testing**: Testing de base de datos
- **Coroutines Testing**: Testing asíncrono

### **📊 Métricas de Calidad**
- **Compilación**: ✅ Sin errores
- **Warnings**: ⚠️ Solo deprecaciones menores
- **Performance**: ✅ Optimizado para 60 FPS
- **Memory**: ✅ Sin memory leaks detectados

---

## 🚀 Despliegue y Distribución

### **📦 Build Configuration**
```kotlin
buildTypes {
    release {
        isMinifyEnabled = false
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
}
```

### **🔐 Seguridad**
- ✅ **Contraseñas hasheadas** con BCrypt
- ✅ **Validación de inputs** en frontend y backend
- ✅ **Sesiones seguras** con SharedPreferences cifradas
- ✅ **Permisos mínimos** requeridos

---

## 🎨 Diseño y UX

### **🎨 Prototipo en Figma**
[🔗 **Ver Prototipo Completo en Figma**](https://www.figma.com/proto/LTTCYMImqDx6jtSt18nfs1?page-id=0%3A1&node-id=1-3&p=f&viewport=1281%2C383%2C0.14&t=NL7LMzLHlpdFVO82-1&scaling=scale-down&content-scaling=fixed&starting-point-node-id=1%3A3&show-proto-sidebar=1)

### **✨ Características de Diseño**
- **Material Design 3** nativo
- **Animaciones fluidas** con Compose Animation
- **Tema adaptativo** claro/oscuro
- **Tipografía optimizada** para legibilidad
- **Colores accesibles** según WCAG 2.1
- **Responsive design** para tablets y móviles

---

## 🤝 Contribución y Desarrollo Futuro

### **🔮 Roadmap Futuro**
- [ ] **Integración en la nube** (Firebase/AWS)
- [ ] **Sincronización multi-dispositivo**
- [ ] **Widgets de escritorio**
- [ ] **Exportación de reportes** (PDF)
- [ ] **Integración con calendarios** externos
- [ ] **Análisis avanzado** con Machine Learning
- [ ] **Modo colaborativo** para grupos de estudio

### **🛠️ Mejoras Técnicas Planeadas**
- [ ] **Migración a Hilt** para inyección de dependencias
- [ ] **Implementación de UseCase** layer
- [ ] **Testing automatizado** completo
- [ ] **CI/CD pipeline** con GitHub Actions
- [ ] **Modularización** por features
- [ ] **Performance optimization** avanzada

---

## 👥 Equipo de Desarrollo

### **🏗️ Arquitectura & Backend**
- Diseño de base de datos relacional
- Implementación de repositorios y ViewModels
- Configuración de WorkManager para notificaciones

### **🎨 Frontend & UX/UI**
- Diseño de interfaces con Jetpack Compose
- Implementación de Material Design 3
- Animaciones y transiciones fluidas

### **🔧 DevOps & Testing**
- Configuración de build system
- Testing de componentes
- Optimización de performance

---

## 📄 Licencia

Este proyecto está desarrollado como parte de un proyecto académico para **Desarrollo de Aplicaciones Móviles (DAM)**.

---

## 📞 Contacto y Soporte

Para consultas, sugerencias o reportes de bugs:
- 📧 **Email**: [contacto@studyoso.com]
- 📱 **GitHub Issues**: [Crear Issue]
- 📚 **Documentación**: [Wiki del proyecto]

---

### 🌟 **¡Gracias por usar StudyOso! Organiza tu futuro académico hoy.**



