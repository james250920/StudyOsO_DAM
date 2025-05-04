# 📚 Study Planner - Clean Architecture Overview

## 🏛 Arquitectura General

El proyecto sigue una arquitectura basada en **Clean Architecture** y **principios de capas**:

- **Presentation Layer:** Maneja la UI y la lógica de presentación usando `Jetpack Compose` y `ViewModel`.
- **Domain Layer:** Define la lógica de negocio pura, independiente de frameworks.
- **Data Layer:** Administra las fuentes de datos como bases de datos locales (`Room`).
- **DI Layer:** Gestiona la inyección de dependencias utilizando `Hilt`.

---

## 🗂 Estructura del Proyecto

```plaintext
├── data/
│   ├── datasource/       # Interfaces con la fuente de datos (Room DAO)
│   ├── model/             # Entidades de base de datos (Room)
│   └── repository/        # Implementaciones de los repositorios
│
├── domain/
│   ├── model/             # Modelos de negocio (puros, sin dependencia de Android)
│   ├── repository/        # Contratos de los repositorios
│   └── usecase/           # Casos de uso que encapsulan la lógica de negocio
│
├── presentation/
│   ├── ui/                # Pantallas y componentes de Jetpack Compose
│   └── viewmodel/         # ViewModels que gestionan el estado de la UI
│
├── di/
│   └── AssignmentModule.kt # Configuración de inyección de dependencias con Hilt
│
└── StudyPlannerApp.kt      # Clase Application configurada para Hilt
```

---

## ⚙️ Stack Tecnológico

| Tecnología              | Uso Principal                                   |
|:------------------------|:------------------------------------------------|
| Jetpack Compose          | Construcción de interfaces de usuario          |
| Room Database            | Persistencia de datos local                    |
| WorkManager              | Gestión de tareas programadas (notificaciones) |
| Hilt                     | Inyección de dependencias                      |
| Kotlin Coroutines        | Programación asíncrona                         |
| StateFlow                | Gestión de estados reactivos en ViewModels     |

---

## 🔄 Flujo de Datos

1. **UI (`Compose`)** interactúa con el **`ViewModel`**.
2. **`ViewModel`** invoca un **`UseCase`** del dominio.
3. **`UseCase`** solicita datos a través de un **`Repository`**.
4. **`RepositoryImpl`** consulta la base de datos mediante un **`DAO`** de `Room`.
5. **Room** retorna los datos, que fluyen de regreso hasta actualizar la UI.

```plaintext
[UI - Compose] → [ViewModel] → [UseCase] → [Repository Interface] → [Repository Implementation] → [DAO - Room] → [Database]
```

---

## 💉 Inyección de Dependencias (DI)

- Toda la creación e inyección de objetos es manejada automáticamente por **Hilt**.
- El archivo `AssignmentModule.kt` proporciona instancias de:
  - `AssignmentDao`
  - `AssignmentRepository`
- Esto permite desacoplar las capas y facilitar testing/mantenibilidad.

---

## 🛡️ Buenas Prácticas Aplicadas

- **Separación de responsabilidades** clara entre capas.
- **Inversión de dependencias** usando interfaces (`domain.repository`).
- **UI reactiva** utilizando `StateFlow` y `Compose`.
- **Persistencia segura** con **Room** y posibilidad de **encriptación local**.
- **Escalabilidad** para agregar nuevos features (modularización futura).

---

# 🚀 Contribuciones y Mejora Continua

Este proyecto fue diseñado para escalar en el futuro incluyendo:

- Integración de nuevas fuentes de datos (como APIs remotas).
- Soporte de múltiples features (GPA calculator, alarms, schedules).
- Testing Unitario y de Integración.
  
## 🎨 Prototipo UI - Figma

[🔗 Ver Prototipo en Figma](https://www.figma.com/proto/LTTCYMImqDx6jtSt18nfs1/prototipo?page-id=0%3A1&node-id=1-3&p=f&viewport=1281%2C383%2C0.14&t=NL7LMzLHlpdFVO82-1&scaling=scale-down&content-scaling=fixed&starting-point-node-id=1%3A3&show-proto-sidebar=1)



