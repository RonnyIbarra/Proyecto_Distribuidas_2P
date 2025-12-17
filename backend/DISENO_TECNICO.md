# Documento de Diseño Técnico - LogiFlow Phase 1

## 1. Introducción

LogiFlow es una plataforma de gestión de entregas distribuidas que permite a los clientes crear pedidos, asignar repartidores y gestionar facturas de forma centralizada. Este documento describe la arquitectura, decisiones de diseño y la implementación de la Fase 1.

## 2. Arquitectura General

### 2.1 Diagrama de Arquitectura de Alto Nivel

```
┌─────────────────────────────────────────────────────────┐
│                    CLIENTE (Frontend)                   │
└────────────────────┬────────────────────────────────────┘
                     │ HTTP/REST
┌────────────────────v─────────────────────────────────────┐
│              API REST - Spring Boot                      │
│  (Puerto 8081 - Servidor de Desarrollo)                │
├─────────────────────────────────────────────────────────┤
│  ┌──────────────────────────────────────────────────┐  │
│  │           Autenticación & Autorización           │  │
│  │  (JWT - JJWT 0.12.3, BCrypt, Roles)            │  │
│  └──────────────────────────────────────────────────┘  │
│                      │                                  │
├──────────┬───────────┼───────────┬───────────────────┤
│          │           │           │                   │
v          v           v           v                   v
────────────────────────────────────────────────────────
Controlador  Controlador Controlador Controlador Config
Autenticación Pedidos    Flota      Facturación Seguridad
│            │          │          │
v            v          v          v
────────────────────────────────────────────────────────
Servicio     Servicio   Servicio   Servicio
Autenticación Pedidos    Flota      Facturación
│            │          │          │
v            v          v          v
────────────────────────────────────────────────────────
Repositorio  Repositorio Repositorio Repositorio
Usuario/Rol  Pedidos      Vehículos   Facturas
│
v
┌────────────────────────────────────────────────────────┐
│    Base de Datos (H2 en memoria / PostgreSQL)         │
│  - Usuarios, Roles, Pedidos, Vehículos, Facturas     │
└────────────────────────────────────────────────────────┘
```

## 3. Componentes Principales

### 3.1 Capa de Controladores (REST API)

**Controladores Implementados:**

1. **ControladorAutenticacion** (`/api/auth`)
   - `POST /login` - Iniciar sesión
   - `POST /register` - Registrar usuario
   - `POST /token/refresh` - Renovar token
   - `GET /validate` - Validar token

2. **ControladorPedidoEntrega** (`/api/pedidos`)
   - `POST /` - Crear pedido
   - `GET /{id}` - Obtener por ID
   - `GET /numero/{numero}` - Obtener por número
   - `GET /cliente/{idCliente}` - Obtener por cliente
   - `GET /zona/{zona}` - Filtrar por zona
   - `GET /estado/{estado}` - Filtrar por estado
   - `PATCH /{id}/estado` - Actualizar estado
   - `PATCH /{id}/asignar` - Asignar repartidor
   - `PATCH /{id}/cancelar` - Cancelar pedido

3. **ControladorFlota** (`/api/fleet`)
   - `POST /vehicles` - Crear vehículo
   - `GET /vehicles/{id}` - Obtener vehículo
   - `GET /vehicles/placa/{placa}` - Obtener por placa
   - `GET /vehicles/propietario/{id}` - Vehículos por propietario
   - `GET /vehicles/disponibles` - Vehículos disponibles
   - `GET /vehicles/tipo/{tipo}` - Vehículos por tipo
   - `PATCH /vehicles/{id}/estado` - Actualizar estado
   - `DELETE /vehicles/{id}` - Eliminar vehículo

4. **ControladorFacturacion** (`/api/facturas`)
   - `POST /` - Crear factura
   - `GET /{id}` - Obtener factura
   - `GET /numero/{numero}` - Obtener por número
   - `GET /cliente/{idCliente}` - Facturas de cliente
   - `GET /pedido/{idPedido}` - Facturas de pedido
   - `PATCH /{id}/estado` - Actualizar estado

### 3.2 Capa de Servicios (Lógica de Negocio)

**Arquitectura de Servicios:**

```
Interfaces de Servicios
├── ServicioAutenticacion
├── ServicioPedidoEntrega
├── ServicioFlota
├── ServicioFacturacion
└── ProveedorTokenJwt

Implementaciones (en carpeta /impl)
├── ServicioAutenticacionImpl
├── ServicioPedidoEntregaImpl
├── ServicioFlotaImpl
├── ServicioFacturacionImpl
└── ProveedorTokenJwtImpl
```

**Responsabilidades:**

- **ServicioAutenticacion**: Gestión de usuarios, registro, login, validación de credenciales
- **ServicioPedidoEntrega**: CRUD de pedidos, validación de zonas, cálculo de costos
- **ServicioFlota**: CRUD de vehículos, filtrado de disponibilidad
- **ServicioFacturacion**: CRUD de facturas, cálculo de totales
- **ProveedorTokenJwt**: Generación y validación de tokens JWT

### 3.3 Capa de Repositorios (Acceso a Datos)

```
RepositorioUsuario extends JpaRepository<Usuario, Long>
├── findByCorreo(String)

RepositorioRol extends JpaRepository<Rol, Long>
├── findByNombre(String)

RepositorioPedidoEntrega extends JpaRepository<PedidoEntrega, Long>
├── findByNumeroPedido(String)
├── findByIdCliente(Long)
├── findByZona(String)
└── findByEstado(String)

RepositorioVehiculo extends JpaRepository<Vehiculo, Long>
├── findByPlaca(String)
├── findByPropietarioId(Long)
├── findByEstado(String)
└── findByTipoAndEstado(String, String)

RepositorioFactura extends JpaRepository<Factura, Long>
├── findByNumeroFactura(String)
├── findByIdCliente(Long)
├── findByIdPedidoEntrega(Long)
└── findByEstado(String)
```

### 3.4 Modelos de Datos

**Entidades Implementadas:**

1. **Rol**
   - Enum: CLIENTE, REPARTIDOR, SUPERVISOR, GERENTE, ADMIN
   - Relación: Uno a muchos con Usuario

2. **Usuario**
   - Campos: correo (único), contraseña (BCrypt), nombreCompleto, telefonoContacto, rol, activo
   - Timestamps: fechaCreacion, fechaActualizacion

3. **PedidoEntrega**
   - Campos: numeroPedido (UUID), origen, destino, peso, zona, costEstimado
   - Estados: RECIBIDO, CONFIRMADO, ASIGNADO, EN_TRANSITO, ENTREGADO, CANCELADO
   - Tipos: ENTREGA_URBANA_RAPIDA, ENTREGA_INTERMUNICIPAL, ENTREGA_NACIONAL
   - Zonas: QUITO_NORTE, QUITO_CENTRO, QUITO_SUR, QUITO_VALLE, AMBATO, LATACUNGA, PICHINCHA

4. **Vehiculo**
   - Campos: placa (única), tipo, capacidad, costoPorKm, propietarioId
   - Estados: DISPONIBLE, EN_RUTA, MANTENIMIENTO
   - Tipos: MOTOCICLETA, VEHICULO_LIVIANO, CAMION_MEDIANO, CAMION_PESADO

5. **Factura**
   - Campos: numeroFactura (UUID), subtotal, impuesto, total (calculado)
   - Estados: BORRADOR, EMITIDA, PAGADA, CANCELADA
   - Total = subtotal + impuesto

## 4. Flujo de Autenticación

### 4.1 Diagrama de Secuencia

```
Cliente                Backend              Base de Datos
   │                     │                       │
   │─────POST /register──>│                       │
   │   (correo, pass,     │                       │
   │    rol, nombre)      │                       │
   │                      │────INSERT Usuario────>│
   │                      │<───Usuario creado────│
   │                      │                       │
   │  Token JWT (acceso   │                       │
   │  + recuperación)     │                       │
   │<─────200 OK──────────│                       │
   │  {tokenAcceso,       │                       │
   │   tokenRecuperacion} │                       │
   │                      │                       │
   │─────POST /login──────>│                       │
   │  (correo, password)  │                       │
   │                      │──SELECT Usuario─────>│
   │                      │<──Usuario encontrado─│
   │                      │                       │
   │  [BCrypt validation] │                       │
   │                      │                       │
   │  Tokens generados    │                       │
   │<─────200 OK──────────│                       │
   │  {tokenAcceso, ...}  │                       │
   │                      │                       │
   │  [Token guardado     │                       │
   │   en cliente]        │                       │
   │                      │                       │
   │─GET /pedidos/────────>│  (Header: Authorization: Bearer <token>)
   │                      │                       │
   │  [JWT validación     │                       │
   │   en ProveedorToken] │                       │
   │                      │──SELECT Pedidos─────>│
   │<─────200 OK──────────│<──Pedidos────────────│
   │  [lista pedidos]     │                       │
```

### 4.2 Componentes de Seguridad

1. **Hash de Contraseña**: BCrypt con fuerza 10
2. **Generación de Token**:
   - Algoritmo: HMAC-SHA256 (HS256)
   - Claims: Subject (idUsuario), rol
   - Tiempos:
     - Token de acceso: 24 horas (86400000 ms)
     - Token de recuperación: 7 días (604800000 ms)

3. **Validación de Token**:
   - Verificación de firma
   - Validación de expiración
   - Extracción de claims (rol, usuario)

## 5. Decisiones de Diseño y Justificación

### 5.1 Arquitectura Monolítica vs Microservicios

**Decisión**: Monolítica (una sola aplicación Spring Boot)

**Justificación**:
- Fase 1 es inicial y requiere desarrollo rápido
- Menor complejidad de operación
- Facilita transacciones ACID locales
- Permite migrar a microservicios en fases posteriores sin cambios en la API

**Transición futura**: En Fase 2, se puede desplegar como Gateway + 4 microservicios con mínimos cambios:
- auth-service (puerto 8081)
- pedidos-service (puerto 8082)
- fleet-service (puerto 8083)
- billing-service (puerto 8084)

### 5.2 Gestión de Transacciones

**Decisión**: Transacciones locales en la capa de servicio (@Transactional)

**Justificación**:
- Garantiza consistencia ACID en operaciones monolíticas
- No requiere Saga Pattern en esta fase
- Mayor rendimiento sin coordinación distribuida

**Limitación aceptada**: En Fase 2, se implementará Saga Pattern para operaciones distribuidas entre servicios

### 5.3 Almacenamiento de Base de Datos

**Decisión**: H2 en memoria para desarrollo, PostgreSQL para producción

**Justificación**:
- H2 acelera desarrollo local sin dependencias externas
- PostgreSQL es robusto, escalable y estándar en industria
- Configuración parametrizada permite cambio sin código

### 5.4 Autenticación Stateless (JWT)

**Decisión**: JWT con JJWT 0.12.3

**Justificación**:
- Stateless: no requiere sesiones en servidor
- Escalable: múltiples instancias sin sincronización
- Self-contained: contiene información del usuario (rol)
- Estándar de industria para APIs REST

**Ventajas sobre sesiones tradicionales**:
- No requiere almacenamiento de sesión
- Facilita arquitectura distribuida
- Compatible con CORS y AJAX

### 5.5 Validación de Pedidos

**Decisión**: Validación de zona y tipo en capa de servicio

**Validación implementada**:

```java
// Zonas válidas
enum Zona { QUITO_NORTE, QUITO_CENTRO, QUITO_SUR, QUITO_VALLE, AMBATO, LATACUNGA, PICHINCHA }

// Tipos de entrega
enum TipoEntrega { ENTREGA_URBANA_RAPIDA, ENTREGA_INTERMUNICIPAL, ENTREGA_NACIONAL }

// Cálculo de costo
costEstimado = 2.50 (base urbana) + (peso * 0.10)
```

### 5.6 Ciclo de Vida de Pedidos

**Estados permitidos**:
```
RECIBIDO → CONFIRMADO → ASIGNADO → EN_TRANSITO → ENTREGADO
                                         ↓
                                    CANCELADO (en cualquier punto)
```

**Validación**: Se rechaza transición a estado CANCELADO después de ENTREGADO

### 5.7 Disponibilidad de Vehículos

**Lógica de filtrado**:

```java
// Obtener vehículos disponibles
List<Vehiculo> disponibles = repositorio.findByEstado("DISPONIBLE");

// Buscar por tipo y disponibilidad
List<Vehiculo> livianosDisponibles = 
    repositorio.findByTipoAndEstado("VEHICULO_LIVIANO", "DISPONIBLE");
```

### 5.8 Cálculo de Facturación

**Fórmula**:
```
Total = Subtotal + Impuesto
```

**Estados de factura**:
```
BORRADOR → EMITIDA → PAGADA
              ↓
          CANCELADA (antes de pagarse)
```

## 6. Patrones Implementados

### 6.1 Inyección de Dependencias (Spring DI)

```java
@Service
public class ServicioPedidoEntregaImpl {
    private final RepositorioPedidoEntrega repositorio;
    
    // Constructor injection
    public ServicioPedidoEntregaImpl(RepositorioPedidoEntrega repositorio) {
        this.repositorio = repositorio;
    }
}
```

### 6.2 Patrón Repository

Acceso a datos abstraído mediante interfaces JPA:

```java
public interface RepositorioPedidoEntrega extends JpaRepository<PedidoEntrega, Long> {
    PedidoEntrega findByNumeroPedido(String numeroPedido);
}
```

### 6.3 Builder Pattern (Lombok)

```java
@Data @Builder
public class RespuestaPedidoEntrega {
    // Generado automáticamente por Lombok
}
```

### 6.4 Transactional Pattern

```java
@Service
@Transactional
public class ServicioPedidoEntregaImpl {
    // Rollback automático en caso de excepción
}
```

## 7. Seguridad

### 7.1 Validación de Entrada

- `@Valid` en controladores para validar DTOs
- Anotaciones: `@Email`, `@Size`, `@Positive`, `@NotBlank`

### 7.2 Protección contra Ataques

- **CSRF**: Deshabilitado para APIs stateless (JWT)
- **SQL Injection**: Mitigado con Prepared Statements (JPA)
- **XSS**: Responsabilidad del frontend (sanitización)

### 7.3 Cifrado de Contraseñas

- BCryptPasswordEncoder con fuerza 10
- Iteraciones: 2^10 = 1024 (balance velocidad/seguridad)

## 8. Testing

### 8.1 Estrategia de Testing

**Pendiente para Fase 1 (versión mínima)**:
- Tests unitarios: 15-20 casos
- Tests de integración: 10 casos con TestContainers

**Casos clave**:

```
✓ Creación de usuario con validación de email
✓ Login con credenciales correctas/incorrectas
✓ Generación y validación de tokens JWT
✓ Creación de pedido con validación de zona
✓ Asignación de repartidor disponible
✓ Rechazo de solicitud sin autenticación (401)
✓ Rechazo de solicitud sin permisos (403)
✓ Cambio de estado de pedido validado
✓ Cálculo de costo estimado
✓ Cálculo de factura (subtotal + impuesto)
```

## 9. Configuración y Deployment

### 9.1 Configuración por Ambiente

**application.yaml** (desarrollo):
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: create-drop
  datasource:
    url: jdbc:h2:mem:testdb
```

**application-prod.yaml** (producción):
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
  datasource:
    url: jdbc:postgresql://localhost:5432/logiflow
```

### 9.2 Puerto de Ejecución

- **Desarrollo**: 8081 (para no conflictuar con otros servicios)
- **Producción**: 8080 (estándar)

### 9.3 Dependencias Principales

- Spring Boot 4.0.0
- Spring Data JPA + Hibernate 7.1.8
- JJWT 0.12.3 (JWT)
- Lombok (código limpio)
- H2 Database (desarrollo)
- Jakarta Validation

## 10. Limitaciones y Trabajo Futuro

### 10.1 Limitaciones de Fase 1

1. **Sin API Gateway**: Se implementará en Fase 2
2. **Sin Rate Limiting**: Se agregará con Spring Cloud Config
3. **Sin Logging Centralizado**: Se usará ELK Stack en Fase 2
4. **Sin Cache**: Se implementará Redis en Fase 2
5. **Sin Métricas**: Se agregará Prometheus/Grafana en Fase 2

### 10.2 Mejoras Planeadas (Fase 2)

1. Desplegar como 4 microservicios independientes
2. Implementar API Gateway con Spring Cloud Gateway
3. Agregar Saga Pattern para transacciones distribuidas
4. Implementar Event Sourcing para auditoría
5. Cache con Redis
6. Message Queue (RabbitMQ) para eventos asíncronos
7. Circuit Breaker (Resilience4j)
8. Búsqueda con Elasticsearch

## 11. Conclusiones

LogiFlow Phase 1 implementa una arquitectura monolítica robusta y escalable que proporciona las funcionalidades esenciales de gestión de entregas. El diseño permite migración a arquitectura de microservicios sin cambios significativos en la API pública.

La autenticación JWT stateless facilita escalabilidad horizontal, mientras que las transacciones locales garantizan consistencia de datos en esta fase inicial.

---

**Fecha**: 16 de Diciembre de 2025  
**Versión**: 1.0  
**Estado**: Completado para Fase 1
