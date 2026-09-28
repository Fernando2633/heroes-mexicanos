# Documentación de la Práctica: Sistema de Gestión de Héroes Mexicanos

**Asignatura:** Desarrollo de Aplicaciones Web / Web Client  
**Tecnologías:** Java 17+, Spring Boot, Spring Data JPA, MySQL / H2, HTML5, CSS3, JavaScript (Fetch API), Arquitectura Hexagonal.

---

## 1. Estructura de Arquitectura Hexagonal (Ports and Adapters)

El backend ha sido estructurado siguiendo estrictamente el patrón de **Arquitectura Hexagonal**. El dominio permanece totalmente aislado de frameworks externos (sin anotaciones de Spring ni JPA en la capa del dominio).

```
com.webclient.practica
├── domain                           # DOMINIO (Núcleo de negocio desacoplado)
│   ├── Heroe.java                   # Entidad pura de dominio en Java
│   └── exception                    # Excepciones de negocio
│       ├── HeroeNotFoundException.java
│       └── BusinessRuleException.java
│
├── port                             # PUERTOS (Contratos de comunicación)
│   ├── in
│   │   └── HeroeServicePort.java    # Puerto de Entrada (Casos de uso para el exterior)
│   └── out
│       └── HeroeRepositoryPort.java # Puerto de Salida (Operaciones de persistencia requeridas)
│
├── application                      # SERVICIOS DE APLICACIÓN
│   └── HeroeService.java            # Implementa casos de uso y aplica las 10 reglas de negocio
│
└── adapter                          # ADAPTADORES (Mecanismos externos)
    ├── in.rest                      # Adaptador REST (Entrada HTTP)
    │   ├── HeroeController.java     # REST Controller (@RestController, /api/v1/heroes)
    │   ├── dto                      # DTOs de Request, Response y Error
    │   └── exception
    │       └── GlobalExceptionHandler.java # Manejo global de excepciones HTTP (400, 404, 500)
    │
    └── out.persistence              # Adaptador de Persistencia (Salida Base de Datos)
        ├── HeroeJpaEntity.java      # Entidad mapeada para JPA (@Entity, @Table)
        ├── SpringDataHeroeRepository.java # JpaRepository para MySQL/H2
        ├── HeroePersistenceAdapter.java  # Adaptador JPA que implementa HeroeRepositoryPort
        └── inmemory
            └── InMemoryHeroeAdapter.java # Adaptador Secundario En Memoria (Sección 25)
```

---

## 2. Respuestas a las Preguntas de Reflexión (Sección 24)

### 1. ¿Qué problema busca resolver la Arquitectura Hexagonal?
Evita el acoplamiento directo entre la lógica central del negocio y la tecnología externa (bases de datos, frameworks REST, interfaces de usuario). Esto permite modificar o reemplazar la infraestructura sin alterar la lógica de negocio ni romper el núcleo del sistema.

### 2. ¿Por qué el dominio no debería depender directamente de JPA?
Porque JPA es un detalle de implementación de persistencia. Si el dominio dependiera de JPA (`@Entity`, `@Column`, `@Table`), cualquier cambio en el ORM o migración a una base de datos no relacional (MongoDB, Redis, in-memory) obligaría a reescribir y contaminar la lógica de negocio.

### 3. ¿Qué función cumplen los puertos de entrada?
Definen las operaciones que el sistema ofrece hacia el mundo exterior (los casos de uso de la aplicación). Actúan como una interfaz por la cual controladores HTTP, servicios de mensajería o CLI invocan las funcionalidades de negocio.

### 4. ¿Qué función cumplen los puertos de salida?
Definen cómo el dominio solicita datos u operaciones de persistencia hacia el exterior (por ejemplo, guardar un héroe, buscar por ID). El dominio no sabe cómo ni dónde se guardan los datos, solo especifica la interfaz que los adaptadores deben cumplir.

### 5. ¿Cuál es la responsabilidad del adaptador de persistencia?
Conectar la interfaz del puerto de salida (`HeroeRepositoryPort`) con la tecnología concreta de persistencia (Spring Data JPA / MySQL / In-Memory), traduciendo los modelos de dominio a entidades de persistencia y viceversa.

### 6. ¿Qué ventajas tendría cambiar MySQL por otra base de datos?
Permite adaptar el sistema a nuevas necesidades operativas (por ejemplo, menor latencia, soporte NoSQL o bases de datos en memoria para pruebas rápidas) con riesgo cero de romper las reglas de negocio de la aplicación.

### 7. ¿Qué componente debería modificarse si se cambia la tecnología de persistencia?
Únicamente el **Adaptador de Salida** (`HeroePersistenceAdapter` o la implementación de `HeroeRepositoryPort`). El Dominio, los Puertos, los Servicios de Aplicación y los Controllers REST **permanecen 100% intactos**.

### 8. ¿Por qué el frontend no debe conectarse directamente a MySQL?
Por razones fundamentales de **seguridad** (no exponer credenciales de base de datos ni permitir inyecciones SQL en el cliente), **mantenibilidad** (el frontend solo debe conocer la API de comunicación REST/JSON) y **encapsulamiento de reglas de negocio**.

### 9. ¿Qué ventajas ofrece separar backend y frontend?
Permite desacoplar el desarrollo (equipos independientes de frontend y backend), reutilizar el backend para múltiples clientes (web, móvil, aplicaciones de terceros), y desplegar/escalar los componentes de forma autónoma.

### 10. ¿Qué diferencias existen entre un Controller y un caso de uso?
- El **Controller** se encarga únicamente de recibir peticiones HTTP, desmaquetar el JSON a DTOs, validar el formato de entrada y devolver respuestas con códigos de estado HTTP adecuados.
- El **Caso de Uso** (`HeroeService`) reside en el núcleo de la aplicación y ejecuta la lógica pura del negocio (validaciones de fechas, unicidad de nombres, reglas de registro/modificación).

---

## 3. Demostración del Segundo Adaptador en Memoria (Sección 25)

Como requiere la sección 25 del documento de la práctica, se implementó `InMemoryHeroeAdapter.java`.

- **Componentes modificados:** Ninguno en el núcleo del sistema. Solo se añadió la clase del nuevo adaptador en el paquete de adaptadores.
- **Componentes que permanecieron sin cambios:** `Heroe` (Dominio), `HeroeServicePort` (Puerto de Entrada), `HeroeRepositoryPort` (Puerto de Salida), `HeroeService` (Servicio), `HeroeController` (Adaptador REST) y los DTOs.
- Para conmutar la persistencia de JPA/MySQL a In-Memory, solo se cambia el cualificador `@Qualifier("inMemoryHeroeAdapter")` en la inyección de dependencias de `HeroeService`.

---

## 4. Guía de Ejecución y Pruebas

### Opción A: Ejecución Directa de Todo el Sistema (Spring Boot + Web App Integrada)
1. Ejecutar en la terminal:
   ```bash
   ./mvnw spring-boot:run
   ```
2. Abrir en el navegador web:
   ```text
   http://localhost:8080
   ```
3. El frontend consumirá la API REST expuesta en `http://localhost:8080/api/v1/heroes`.

### Opción B: Cliente Web Independiente
1. Abrir el archivo `frontend/index.html` en cualquier navegador o servidor estático (Live Server).
2. La interfaz se conectará automáticamente a la API REST expuesta por el backend.

### Pruebas de API con Postman
Importar el archivo `postman_collection.json` en Postman. Incluye las 11 pruebas requeridas:
1. Registrar un héroe exitosamente (`POST`)
2. Probar registro con datos faltantes/inválidos (regresa `400 Bad Request`)
3. Consultar todos los héroes (`GET`)
4. Consultar héroe por ID existente (`GET`)
5. Consultar héroe por ID inexistente (regresa `404 Not Found`)
6. Modificar héroe existente (`PUT`)
7. Intentar modificar héroe inexistente (`404 Not Found`)
8. Eliminar héroe por ID (`DELETE`)
9. Buscar por época histórica (`GET /api/v1/heroes/epoca/{epoca}`)
10. Buscar por movimiento (`GET /api/v1/heroes/movimiento/{movimiento}`)
11. Buscar por estado de nacimiento (`GET /api/v1/heroes/estado/{estado}`)
