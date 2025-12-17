# Estructura Final - LogiFlow Phase 1

## Archivos a Mantener

### Estructura de Carpetas
```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/logiflow/backend/
│   │   │   ├── LogiFlowApplication.java
│   │   │   ├── controllers/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── BillingController.java
│   │   │   │   ├── DeliveryOrderController.java
│   │   │   │   └── FlotaController.java
│   │   │   ├── repositorios/
│   │   │   │   ├── RepositorioFactura.java
│   │   │   │   ├── RepositorioPedidoEntrega.java
│   │   │   │   ├── RepositorioRol.java
│   │   │   │   ├── RepositorioUsuario.java
│   │   │   │   └── RepositorioVehiculo.java
│   │   │   ├── servicios/
│   │   │   │   ├── ProveedorTokenJwt.java
│   │   │   │   ├── ServicioAutenticacion.java
│   │   │   │   ├── ServicioFacturacion.java
│   │   │   │   ├── ServicioFlota.java
│   │   │   │   ├── ServicioPedidoEntrega.java
│   │   │   │   └── impl/
│   │   │   │       ├── ProveedorTokenJwtImpl.java
│   │   │   │       ├── ServicioAutenticacionImpl.java
│   │   │   │       ├── ServicioFacturacionImpl.java
│   │   │   │       ├── ServicioFlotaImpl.java
│   │   │   │       └── ServicioPedidoEntregaImpl.java
│   │   │   └── dto/
│   │   │       ├── SolicitudLoginAutenticacion.java
│   │   │       ├── SolicitudRegistroAutenticacion.java
│   │   │       ├── RespuestaTokenAutenticacion.java
│   │   │       ├── RespuestaUsuario.java
│   │   │       ├── SolicitudCrearPedidoEntrega.java
│   │   │       ├── RespuestaPedidoEntrega.java
│   │   │       ├── SolicitudCrearVehiculo.java
│   │   │       ├── RespuestaVehiculo.java
│   │   │       ├── SolicitudCrearFactura.java
│   │   │       └── RespuestaFactura.java
│   │   ├── resources/
│   │   │   ├── application.yaml
│   │   │   ├── db/migration/
│   │   │   │   ├── V1__Initial_Schema.sql
│   │   │   │   └── V2__Insert_Initial_Roles.sql
│   │   │   ├── static/
│   │   │   └── templates/
│   │   └── test/
│   │       ├── java/com/logiflow/backend/
│   │       │   ├── AuthServiceTest.java
│   │       │   ├── DeliveryOrderServiceTest.java
│   │       │   └── AuthControllerTest.java
│   │       └── resources/
│   │           └── application-test.yaml
├── pom.xml
└── mvnw
```

## Patrón de Nomenclatura

### Controllers (Inglés con una palabra en Español si es flota)
- `AuthController` - Autenticación y gestión de usuarios
- `DeliveryOrderController` - Órdenes de entrega
- `FlotaController` - Gestión de flota (vehículos)
- `BillingController` - Facturación

### Services (Español)
- `ServicioAutenticacion` / `ServicioAutenticacionImpl` - Lógica de autenticación
- `ServicioPedidoEntrega` / `ServicioPedidoEntregaImpl` - Lógica de pedidos
- `ServicioFlota` / `ServicioFlotaImpl` - Lógica de vehículos
- `ServicioFacturacion` / `ServicioFacturacionImpl` - Lógica de facturación
- `ProveedorTokenJwt` / `ProveedorTokenJwtImpl` - Gestión de tokens JWT

### Repositories (Español)
- `RepositorioUsuario` - Acceso a datos de usuarios
- `RepositorioRol` - Acceso a datos de roles
- `RepositorioPedidoEntrega` - Acceso a datos de órdenes
- `RepositorioVehiculo` - Acceso a datos de vehículos
- `RepositorioFactura` - Acceso a datos de facturas

### DTOs (Español para nombre general)
**Request:**
- `SolicitudLoginAutenticacion`
- `SolicitudRegistroAutenticacion`
- `SolicitudCrearPedidoEntrega`
- `SolicitudCrearVehiculo`
- `SolicitudCrearFactura`

**Response:**
- `RespuestaTokenAutenticacion`
- `RespuestaUsuario`
- `RespuestaPedidoEntrega`
- `RespuestaVehiculo`
- `RespuestaFactura`

## Rutas de API

### Autenticación
- `POST /api/auth/login` - Iniciar sesión
- `POST /api/auth/register` - Registrarse
- `POST /api/auth/token/refresh` - Renovar token
- `GET /api/auth/validate` - Validar token

### Órdenes de Entrega
- `POST /api/pedidos` - Crear orden
- `GET /api/pedidos/{id}` - Obtener por ID
- `GET /api/pedidos/numero/{orderNumber}` - Obtener por número
- `GET /api/pedidos/cliente/{customerId}` - Órdenes del cliente
- `GET /api/pedidos/zona/{zone}` - Órdenes por zona
- `GET /api/pedidos/estado/{status}` - Órdenes por estado
- `PATCH /api/pedidos/{id}/estado` - Actualizar estado
- `PATCH /api/pedidos/{id}/asignar` - Asignar repartidor
- `PATCH /api/pedidos/{id}/cancelar` - Cancelar orden

### Flota
- `POST /api/fleet/vehicles` - Crear vehículo
- `GET /api/fleet/vehicles/{id}` - Obtener vehículo
- `GET /api/fleet/vehicles/plate/{plate}` - Obtener por placa
- `GET /api/fleet/vehicles/owner/{ownerId}` - Vehículos del propietario
- `GET /api/fleet/vehicles/available` - Vehículos disponibles
- `GET /api/fleet/vehicles/type/{type}` - Vehículos por tipo
- `PATCH /api/fleet/vehicles/{id}/status` - Actualizar estado
- `DELETE /api/fleet/vehicles/{id}` - Eliminar vehículo

### Facturación
- `POST /api/facturas` - Crear factura
- `GET /api/facturas/{id}` - Obtener factura
- `GET /api/facturas/numero/{invoiceNumber}` - Obtener por número
- `GET /api/facturas/cliente/{customerId}` - Facturas del cliente
- `GET /api/facturas/pedido/{orderId}` - Facturas del pedido
- `PATCH /api/facturas/{id}/estado` - Actualizar estado

## Archivos a Eliminar

✅ Eliminados:
- `controladores/` (carpeta completa - Spanish names only)
- `configuracion/` (carpeta completa)
- `entity/` (carpeta completa - duplicada con modelos)
- `models/` (carpeta completa - English names)
- `modelos/` (carpeta completa - Spanish entities)
- `config/` (carpeta completa)
- `exception/` (carpeta completa)
- `repository/` (carpeta completa)
- `repositories/` (carpeta completa - English repository names)
- `services/` (carpeta completa - English service names)
- Todos los DTOs duplicados en inglés de la carpeta `dto/`
- `FleetController.java` (duplicado)

## Configuración Principal

- **pom.xml**: Todas las dependencias necesarias (Spring Boot 4.0, JWT, JPA, Flyway, OpenAPI)
- **application.yaml**: Configuración H2, JWT, Flyway, logging
- **Migrations**: V1 (esquema), V2 (roles iniciales)

## Próximos Pasos

1. Validar que el proyecto compila: `mvn clean compile -DskipTests`
2. Ejecutar pruebas: `mvn test`
3. Levantar aplicación: `mvn spring-boot:run`
4. Acceder a Swagger: `http://localhost:8080/swagger-ui.html`
