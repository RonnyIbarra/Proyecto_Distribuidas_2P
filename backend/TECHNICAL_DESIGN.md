# LogiFlow - Fase 1: Diseño Técnico

## 1. Introducción

Este documento describe la arquitectura técnica implementada en la Fase 1 del proyecto LogiFlow, una plataforma integral de gestión de operaciones para EntregaExpress S.A.

## 2. Arquitectura General

### 2.1 Componentes Principales

```
┌─────────────────────────────────────────────────────────────┐
│                     Cliente/Frontend                         │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           │ HTTP/HTTPS
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                    API Gateway (Port 8080)                   │
│  - Enrutamiento                                              │
│  - Validación JWT                                            │
│  - Rate Limiting                                             │
│  - Logging Centralizado                                      │
└──────┬──────────┬──────────┬──────────┬─────────────────────┘
       │          │          │          │
       ▼          ▼          ▼          ▼
   ┌─────────┐ ┌────────┐ ┌──────────┐ ┌─────────┐
   │ Auth    │ │Pedidos │ │Fleet     │ │Billing  │
   │Service  │ │Service │ │Service   │ │Service  │
   └────┬────┘ └───┬────┘ └────┬─────┘ └────┬────┘
        │          │           │            │
        └──────────┴───────────┴────────────┘
                   │
                   ▼
        ┌──────────────────────┐
        │   PostgreSQL DB      │
        │  (H2 para desarrollo)│
        └──────────────────────┘
```

### 2.2 Capas de la Aplicación

#### Capa de Presentación (Controllers)
- `AuthController`: Gestiona autenticación y registro
- `DeliveryOrderController`: CRUD de pedidos
- `FleetController`: Gestión de vehículos y repartidores
- `BillingController`: Gestión de facturas

#### Capa de Lógica de Negocio (Services)
- `AuthService`: Autenticación y gestión de usuarios
- `DeliveryOrderService`: Lógica de creación y asignación de pedidos
- `FleetService`: Gestión de flota y disponibilidad
- `BillingService`: Cálculo de tarifas y facturación
- `JwtTokenProvider`: Generación y validación de JWT

#### Capa de Acceso a Datos (Repositories)
- `UserRepository`: Acceso a usuarios
- `RoleRepository`: Acceso a roles
- `DeliveryOrderRepository`: Acceso a pedidos
- `VehicleRepository`: Acceso a vehículos
- `InvoiceRepository`: Acceso a facturas

#### Capa de Modelos (Models)
- `User`: Representa a un usuario del sistema
- `Role`: Define roles del sistema
- `DeliveryOrder`: Representa un pedido
- `Vehicle`: Representa un vehículo
- `Invoice`: Representa una factura

## 3. Flujos de Negocio Implementados

### 3.1 Flujo de Autenticación

```
1. Cliente envía credenciales (email, password)
2. AuthService valida contra BD
3. PasswordEncoder verifica contraseña
4. JwtTokenProvider genera tokens (access + refresh)
5. Token se retorna al cliente
6. Cliente incluye token en header Authorization para próximas peticiones
```

### 3.2 Flujo de Creación de Pedido

```
1. Cliente envía petición POST /api/pedidos
2. Gateway valida JWT
3. DeliveryOrderController recibe petición
4. DeliveryOrderService:
   a. Valida tipo de entrega (URBAN_RAPID, INTERMUNICIPAL, NATIONAL)
   b. Valida zona de cobertura
   c. Calcula costo estimado
   d. Genera número de pedido único
5. DeliveryOrderRepository persiste en BD
6. Retorna DeliveryOrderResponse con status RECEIVED
```

### 3.3 Flujo de Asignación de Repartidor

```
1. Supervisor solicita asignación: PATCH /api/pedidos/{id}/asignar
2. DeliveryOrderService:
   a. Obtiene pedido de BD
   b. Asigna riderId y vehicleId
   c. Cambia status a ASSIGNED
3. Retorna pedido actualizado
```

### 3.4 Flujo de Facturación

```
1. Cuando se confirma entrega, se crea factura
2. CreateInvoiceRequest con subtotal y tax
3. BillingService calcula total = subtotal + tax
4. Factura se crea en estado DRAFT
5. Estado se puede cambiar a ISSUED, PAID o CANCELLED
```

## 4. Decisiones Técnicas Justificadas

### 4.1 Spring Boot 4.0.0
- **Razón**: Framework maduro, amplio ecosistema, excelente soporte
- **Beneficios**: Rápido desarrollo, configuración mínima, muchas librerías

### 4.2 JWT para Autenticación
- **Razón**: Stateless, escalable, compatible con frontend y móvil
- **Implementación**: JJWT 0.12.3 con tokens firmados y validación de expiración
- **Beneficio**: No requiere sesiones en servidor, facilita microservicios

### 4.3 PostgreSQL con H2 en Desarrollo
- **Razón**: PostgreSQL para producción, H2 en memoria para testing
- **Beneficio**: Mismo código sin cambios, desarrollo rápido

### 4.4 Flyway para Migraciones
- **Razón**: Control de versión de BD, ejecución automática
- **Beneficio**: Reproducibilidad, auditoría, facilita CI/CD

### 4.5 Transacciones ACID Locales
- **Razón**: Garantizar consistencia en operaciones críticas
- **Implementación**: @Transactional en servicios
- **Ejemplo**: Creación de pedido + cálculo de costo en una transacción

### 4.6 Validación con Jakarta Validation
- **Razón**: Validación declarativa en DTOs
- **Beneficio**: Mensajes de error claros, validación centralizada

### 4.7 Manejo de Excepciones Global
- **Razón**: Respuestas consistentes
- **Implementación**: GlobalExceptionHandler con @RestControllerAdvice
- **Beneficio**: Stack traces no se exponen al cliente

## 5. Estructura de Carpetas

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/logiflow/backend/
│   │   │   ├── controllers/      # REST endpoints
│   │   │   ├── models/           # Entidades JPA
│   │   │   ├── repositories/     # Acceso a datos
│   │   │   ├── services/         # Interfaces de servicios
│   │   │   │   └── impl/         # Implementaciones
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── config/           # Configuraciones
│   │   │   ├── exception/        # Manejo de excepciones
│   │   │   └── LogiFlowApplication.java
│   │   └── resources/
│   │       ├── application.yaml
│   │       ├── db/migration/     # Scripts Flyway
│   │       └── openapi/          # Documentación OpenAPI
│   └── test/
│       ├── java/com/logiflow/backend/
│       │   ├── service/          # Tests unitarios
│       │   └── controller/       # Tests integración
│       └── resources/
│           └── application-test.yaml
├── pom.xml
└── HELP.md
```

## 6. Modelos de Datos

### 6.1 Tabla users
```sql
id | email | password | full_name | phone_number | active | role_id | created_at | updated_at
```

### 6.2 Tabla delivery_orders
```sql
id | order_number | customer_id | origin | destination | delivery_type | weight | status | 
assigned_rider_id | assigned_vehicle_id | zone | estimated_cost | notes | created_at | updated_at
```

### 6.3 Tabla vehicles
```sql
id | plate | type | capacity | status | base_cost_per_km | owner_id | created_at | updated_at
```

### 6.4 Tabla invoices
```sql
id | invoice_number | delivery_order_id | customer_id | status | subtotal | tax | total | 
description | created_at | updated_at
```

## 7. APIs Expuestas

### 7.1 Autenticación
- `POST /api/auth/register` - Registrar nuevo usuario
- `POST /api/auth/login` - Login
- `POST /api/auth/token/refresh` - Renovar token
- `GET /api/auth/validate` - Validar token

### 7.2 Pedidos
- `POST /api/pedidos` - Crear pedido
- `GET /api/pedidos/{id}` - Obtener pedido
- `GET /api/pedidos/cliente/{customerId}` - Pedidos del cliente
- `GET /api/pedidos/zona/{zone}` - Pedidos por zona
- `PATCH /api/pedidos/{id}/estado` - Actualizar estado
- `PATCH /api/pedidos/{id}/asignar` - Asignar repartidor
- `PATCH /api/pedidos/{id}/cancelar` - Cancelar pedido

### 7.3 Flota
- `POST /api/flota/vehiculos` - Crear vehículo
- `GET /api/flota/vehiculos/{id}` - Obtener vehículo
- `GET /api/flota/vehiculos/disponibles` - Vehículos disponibles
- `PATCH /api/flota/vehiculos/{id}/estado` - Cambiar estado
- `DELETE /api/flota/vehiculos/{id}` - Eliminar vehículo

### 7.4 Facturación
- `POST /api/facturas` - Crear factura
- `GET /api/facturas/{id}` - Obtener factura
- `GET /api/facturas/cliente/{customerId}` - Facturas de cliente
- `PATCH /api/facturas/{id}/estado` - Cambiar estado

## 8. Seguridad

### 8.1 Autenticación JWT
- Token contiene: userId, role, expiración
- Validación en cada petición (excepto /auth/register y /auth/login)
- Refresh token para renovar access token

### 8.2 Validación de Entrada
- DTOs con anotaciones Jakarta Validation
- Validación automática con @Valid
- Mensajes de error descriptivos

### 8.3 Encriptación de Contraseñas
- BCryptPasswordEncoder (force = 10)
- Nunca se almacenan en texto plano

## 9. Testing

### 9.1 Tests Unitarios
- AuthServiceTest: Registro, login, validación de token
- DeliveryOrderServiceTest: Creación, actualización de estado

### 9.2 Tests de Integración
- AuthControllerTest: Endpoints de autenticación
- MockMvc para simular peticiones HTTP

### 9.3 Cobertura Esperada
- Mínimo 70% en servicios principales
- Todos los flujos críticos cubiertos

## 10. Monitoreo y Observabilidad

### 10.1 Logging
- Logs a nivel DEBUG en servicios
- Logs de peticiones HTTP
- Stack traces en casos de error

### 10.2 Métricas
- Health check en `/actuator/health`
- Métricas en `/actuator/metrics`

## 11. Próximos Pasos - Fase 2

- GraphQL para consultas complejas
- RabbitMQ/Kafka para eventos
- WebSocket para tiempo real
- Notificaciones (SMS, email, push)

## 12. Conclusión

La Fase 1 establece una base sólida con servicios REST completamente funcionales, autenticación JWT, y operaciones CRUD transaccionales. La arquitectura es escalable y preparada para agregar features adicionales en futuras fases.
