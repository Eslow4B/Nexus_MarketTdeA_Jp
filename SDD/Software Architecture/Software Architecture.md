# Software Architecture

## Overview

NexusMarket follows a **Hexagonal Architecture (Ports and Adapters)** combined with **Domain-Driven Design (DDD)** principles.

The primary objective of this architecture is to isolate the business domain from external technologies, ensuring that business rules remain independent from frameworks, databases, communication protocols, and infrastructure concerns.

This approach promotes maintainability, scalability, testability, and technology independence.

---

# Architectural Principles

The architecture is based on the following principles:

- Domain-first design.
- Separation of concerns.
- Dependency inversion.
- Technology independence.
- High cohesion.
- Low coupling.
- Explicit boundaries between layers.

The domain contains all business rules and never depends on external technologies.

---

# Architecture Layers

The application is organized into four major components:

```text
Application
│
├── Adapters
│
├── Domain
│
└── Infrastructure
```

Each component has a clearly defined responsibility.

---

# Package Structure

```text
src/
└── main/
    └── java/
        └── application/
            │
            ├── NexusMarketApplication.java
            │
            ├── adapters/
            │   │
            │   ├── in/
            │   │   └── rest/
            │   │       ├── controllers/
            │   │       ├── requests/
            │   │       ├── responses/
            │   │       └── mappers/
            │   │
            │   └── out/
            │       └── persistence/
            │           └── mysql/
            │               ├── adapters/
            │               ├── entities/
            │               ├── repositories/
            │               └── mappers/
            │
            ├── domain/
            │   ├── models/
            │   ├── valueobjects/
            │   ├── enums/
            │   ├── services/
            │   ├── exceptions/
            │   └── ports/
            │       ├── in/
            │       └── out/
            │
            └── infrastructure/
                ├── config/
                ├── database/
                └── security/
```

## Note on Persistence Technology

Unlike the reference banking system — which persists an immutable `AuditLog` in MongoDB alongside relational data in MySQL — the current NexusMarket domain model does not include an entity that requires document-oriented, flexible-schema storage. For this reason, the package structure above only defines a `mysql` persistence adapter for now.

**Decision (Services deliverable):** No `mongodb` adapter is introduced at this stage. `InventoryMovement` already provides traceability for stock changes as a regular relational entity (see Domain Model.md), and no other document-oriented concept was identified while defining the Services layer. Consequently, the `spring-boot-starter-mongodb` and `spring-boot-starter-mongodb-test` dependencies were removed from `pom.xml`. They should only be reintroduced if a concrete document-oriented entity (for example, a consolidated `OperationLog` backing the Administrative Reporting services) is added to the Domain Model and this note is revisited.

Connection settings live in `application.properties` and are read from the `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` environment variables. Tests use an in-memory H2 database (`src/test/resources/application.properties`), so they never require a running MySQL server.

---

# Layer Responsibilities

## Application

The `application` package represents the root of the project.

It contains the application entry point and all architectural components.

### Responsibilities

- Application bootstrap.
- Component organization.
- Dependency composition.

---

## NexusMarketApplication.java

### Description

`NexusMarketApplication.java` is the application's entry point.

### Responsibilities

- Initialize the application.
- Load the infrastructure.
- Configure dependency injection.
- Start the REST server.

---

# Adapters

The adapters connect external technologies with the business domain.

Adapters translate external requests into domain operations and transform domain objects into technology-specific representations.

The domain never communicates directly with external systems.

---

## Input Adapters

Input adapters expose the application to external clients.

Current implementation:

```text
adapters/in/rest
```

### Responsibilities

- Receive HTTP requests.
- Validate incoming data.
- Convert Request DTOs into Domain Models.
- Execute application use cases.
- Convert domain results into Response DTOs.

---

### Controllers

Controllers expose REST endpoints.

Responsibilities:

- Receive HTTP requests.
- Delegate execution to the domain.
- Return HTTP responses.

Controllers must never implement business rules.

---

### Requests

Request DTOs represent incoming HTTP payloads.

Responsibilities:

- Receive client data.
- Validate input.
- Transport data into the application.

These objects must not contain business logic.

---

### Responses

Response DTOs represent outgoing HTTP responses.

Responsibilities:

- Return processed information.
- Hide internal domain implementation.
- Standardize API responses.

---

### Mappers

Responsible for converting between:

- Request DTO ↔ Domain Model
- Domain Model ↔ Response DTO

This prevents the domain from depending on transport objects.

---

# Output Adapters

Output adapters connect the domain with external resources.

Examples:

- Databases
- Notification services
- External APIs
- Messaging systems

Current implementation:

```text
Persistence
└── MySQL
```

A `MongoDB` adapter is not part of the current design (see the note under Package Structure), but the structure can accommodate one without affecting the domain.

---

## MySQL Adapter

Responsible for relational persistence for the entire domain model: `User` and its specializations, `Product` and its specializations, `Warehouse`, `Inventory`, `InventoryMovement`, `Order`, `OrderItem`, `Invoice`, `Shipment`, `ReturnRequest`, `ReturnItem`, and `Refund`.

### Components

#### Entities

Represent relational database tables.

#### Repositories

Implement persistence operations.

#### Mappers

Convert Domain Models into database entities.

#### Adapters

Implement Domain Output Ports.

---

# Domain

The Domain layer is the core of the application.

It contains all business rules and must remain independent from any external technology.

No class inside the domain may depend on:

- Spring
- JPA
- MongoDB
- HTTP
- REST
- JSON
- SQL

The domain may only use the Java standard library (for example, `java.time`, `java.math.BigDecimal`, `java.util.UUID`) and Lombok. Lombok is allowed because it is a compile-time code generator: it adds no runtime dependency and no framework annotation to the compiled domain classes.

---

## Models

Contain the business entities.

Examples:

- User
- Buyer
- Seller
- Product
- PhysicalProduct
- DigitalProduct
- Warehouse
- Inventory
- InventoryMovement
- Order
- OrderItem
- Invoice
- Shipment
- ReturnRequest
- ReturnItem
- Refund

These objects represent the marketplace business.

---

## Value Objects

Represent immutable business concepts, following the `DomainCatalog` pattern (business code, human-readable name, and description).

Examples:

- SystemRole
- UserStatus
- CommercialStatus
- ProductStatus
- InventoryCondition
- MovementType
- OrderStatus
- ShipmentStatus
- ReturnStatus
- RefundStatus

Value Objects are compared by value instead of identity — specifically, by their business `code`.

---

## Enums

Contain technical enumerations that do not require business behavior or a code/name/description structure.

No primitive enumerations have been identified for the current NexusMarket domain model; every controlled value found during analysis required business metadata and was therefore modeled as a `DomainCatalog`. This package is reserved for future technical values that do not carry business meaning of their own.

---

## Services

Contain business logic that does not naturally belong to a single entity.

The Services layer is composed of **37 fine-grained services**, one per distinct actor, triggering event, or objective sub-activity identified while analyzing the business specification — not one per OBJ code. The full index, the decomposition criterion used to justify each split, the orchestration chain between them, and the conventions every service follows are documented in `SDD/Domain/Domain Services.md`, and each service individually in `SDD/services/`. This document does not repeat that list to avoid the two drifting apart; it only names the two cross-cutting services every other one depends on:

- AuthenticationService — functional verification of credentials (RG-01). Only validates that a `User` is who they claim to be; the technical mechanism (hashing, tokens) is out of scope and belongs to `infrastructure/security`.
- AuthorizationService — centralized enforcement of RG-03 against the Authorization Matrix below, depended upon by every other service instead of each one re-implementing the same guard clause.

Services coordinate business operations while preserving domain integrity.

---

## Ports

Ports define communication contracts between the domain and external technologies.

The domain owns all interfaces.

---

### Input Ports

Represent application use cases. Input ports define what the system can do.

Conventions:

- **One Input Port per operation.** Each row of the "Operations" table in a file of `SDD/services/` becomes one interface, named after the operation in PascalCase with the `UseCase` suffix: `reserveStock` → `ReserveStockUseCase`, `requestReturn` → `RequestReturnUseCase`, `confirmOrder` → `ConfirmOrderUseCase`. The full list is not duplicated here, so it cannot drift from the service files.
- **Implemented by the owning service.** The service named in the file implements every Input Port of that file.
- **The actor is an explicit parameter.** Role-invoked use cases receive the authenticated `User` as their first parameter; the input adapter obtains it from the security context. Use cases that precede authentication (DEC-01) and system-triggered use cases do not receive it.
- **Only role-invoked use cases are exposed by input adapters.** System-triggered use cases (reservation, sale outbound, stock return, reservation release, invoice generation, shipment creation, order fulfillment tracking, refund opening, pending-order reversion) are only called by the service that orchestrates them. The only system-triggered use cases reachable from outside are those of `PaymentConfirmationService`, which receives the external payment signal (DEC-22).
- **Domain types in, domain types out.** Use cases receive primitive business data or domain objects and return domain objects; DTOs never cross the port.

---

### Output Ports

Represent dependencies required by the domain. Output ports define what the domain needs from external systems.

The complete set required by the 37 services:

| Output Port | Kind | Implemented in | Used by |
| --- | --- | --- | --- |
| UserRepository | Repository of the `User` aggregate (including `Buyer`, `Seller`) | `adapters/out/persistence/mysql` | Authentication, user, buyer, and seller services; Cart, Order Confirmation, Product Catalog, Warehouse Management; User Reporting |
| ProductRepository | Repository of the `Product` aggregate | `adapters/out/persistence/mysql` | Product Catalog, Catalog Consultation, Cart, Stock Inbound; Catalog Reporting |
| WarehouseRepository | Repository of the `Warehouse` aggregate | `adapters/out/persistence/mysql` | Warehouse Management, Seller Registration, Stock Inbound |
| InventoryRepository | Repository of the `Inventory` aggregate | `adapters/out/persistence/mysql` | Stock services, Inventory Consultation, Inventory Reporting |
| InventoryMovementRepository | Repository of `InventoryMovement` (append-only) | `adapters/out/persistence/mysql` | Stock services, Inventory Consultation, Inventory Reporting |
| OrderRepository | Repository of the `Order` aggregate (with `OrderItem`) | `adapters/out/persistence/mysql` | Cart and order services, Return Request, Order Reporting |
| InvoiceRepository | Repository of the `Invoice` aggregate | `adapters/out/persistence/mysql` | Invoice Generation, Invoice Consultation, Order Reporting |
| ShipmentRepository | Repository of the `Shipment` aggregate | `adapters/out/persistence/mysql` | Shipment services, Order Fulfillment Tracking, Shipment Reporting |
| ReturnRequestRepository | Repository of the `ReturnRequest` aggregate (with `ReturnItem`) | `adapters/out/persistence/mysql` | Return services, Return And Refund Consultation, Return Refund Reporting |
| RefundRepository | Repository of the `Refund` aggregate | `adapters/out/persistence/mysql` | Refund Processing, Return And Refund Consultation, Return Refund Reporting |
| PasswordHasher | Technical port: hashes a password and verifies a password against a hash | `infrastructure/security` | Authentication, Buyer Registration, Seller Registration, User Management |

The aggregate → repository mapping is defined in `Domain Model.md` ("Aggregates and Repositories"). `PasswordHasher` exists because `User.password` must hold a hash (RG-01 requires credentials) while the hashing algorithm is a technical mechanism excluded from the business scope (§3.2): the domain declares the need and infrastructure provides the algorithm.

---

## Authorization Matrix

RG-03 ("No participant may manage information outside the scope of their role") is enforced by `AuthorizationService` as a guard clause at the start of every use case, checked against the role of the currently authenticated `User`, so the check is written once and depended upon rather than repeated in each of the 37 services. The concrete permissions are fixed by the business specification and must not be inferred ad hoc while implementing each service.

Each row below is a capability area, not a single service name — several of the 37 services in `Domain Services.md` can share one row (for example, "Inventory administration" governs `StockInboundService`, `StockAdjustmentService`, and `InventoryConsultationService` alike):

| Use Case / Process                | Buyer | Seller | Logistics Operator | Administrator | Supervisor |
| ----------------------------------- | :---: | :----: | :------------------: | :-------------: | :----------: |
| Seller registration                 |       |        |                       | ✔                |              |
| User management (any role)          |       |        |                       | ✔                |              |
| Warehouse management                |       |        |                       | ✔                |              |
| Product registration                |       | ✔      |                       |                  |              |
| Inventory administration            |       | ✔      | ✔                     |                  |              |
| Order management (cart→delivered)   | ✔     | ✔      | ✔                     |                  |              |
| Shipment execution                  |       |        | ✔                     |                  |              |
| Return and refund management        | ✔     |        |                       | ✔                |              |
| Administrative reporting            |       |        |                       |                  | ✔            |

Only the rows *Seller registration*, *Product registration*, *Inventory administration*, *Order management*, and *Refund management* appear in the business specification's Responsibility Matrix (§12). The remaining rows — user management, warehouse management, shipment execution, returns, and administrative reporting — are processes in scope (§3.1) that the matrix does not assign; they are assigned from the Participants table (§5), as recorded in DEC-04 of `SDD/Domain/Business Decisions.md`.

Within *Order management*, each role participates differently (DEC-05): the **Buyer** drives the lifecycle of their own orders; the **Seller** consults the orders that contain their products and sees only their own items; the **Logistics Operator** participates through the order's shipments and consults orders with physical products. Logistics Operators are not assigned to specific warehouses (DEC-06).

*Returns* follow the same actors as refunds: the Buyer requests; the Administrator reviews, confirms the physical receipt, and processes the refund.

Two use cases fall outside this table because they precede authentication and therefore have no role to check (DEC-01):

- **User authentication** (`AuthenticateUserUseCase`) — invoked by any registered `User` attempting to log in.
- **Buyer self-registration** (`RegisterBuyerUseCase`) — invoked by an unauthenticated visitor becoming a `Buyer`.

System-triggered operations (reservation, sale outbound, stock return, reservation release, invoice generation, shipment creation, order fulfillment tracking, refund opening, pending-order reversion) are not exposed to any role; they are invoked only by the service that orchestrates them, as documented in each service file. *Administrative reporting* covers six read-only reports whose scope is fixed by DEC-29.

**Invoice generation** (`GenerateInvoiceUseCase`, implemented by `InvoiceGenerationService`) is not directly exposed to any role either: it is triggered internally by `PaymentConfirmationService` when an `Order` transitions to `PAID`, as part of the "Transacción" step of the business flow, not as a standalone action a user requests. Read-only consultation of an already-generated `Invoice` (`InvoiceConsultationService`) is open to the owning `Buyer` and to the `Administrator`.

A violation of this matrix must raise `UnauthorizedOperationException`, not a generic exception.

---

## Exceptions

Contains business exceptions, defined by mapping each validation rule of the business specification (RG-01 to RG-03, the critical validations of §11) and each `DEC-xx` to an exception.

Every business exception extends the abstract `DomainException` (itself a `RuntimeException`), so that input adapters can recognize and translate any business failure uniformly, and never confuse it with a technical error.

### Access

- UnauthorizedOperationException — raised when a use case is invoked by a role outside the Authorization Matrix above (RG-03), on a resource the caller does not own, or by an unauthenticated caller when authentication is required (RG-01).
- InvalidCredentialsException — raised by `AuthenticationService` when a login (email or username, DEC-03) and password pair does not match a registered `User`.
- UserNotActiveException — raised by `AuthenticationService` when a `User` whose status is `INACTIVE` or `BLOCKED` attempts to authenticate.

### Users

- DuplicateUserDataException — raised when the identity document, email, or username of a `User` is not unique across the platform.
- SellerSelfRegistrationNotAllowedException — raised when an authenticated user who is not an `Administrator` attempts to create a `Seller` (in particular, a user trying to register themselves as a seller).
- BuyerNotEligibleForOrderException — raised when a `Buyer`'s `commercialStatus` does not allow the requested action: managing the cart while `SUSPENDED`, or confirming an order while `RESTRICTED` or `SUSPENDED` (DEC-07).

### Catalog and Inventory

- ProductNotAvailableException — raised when a `Product` that is not available for sale (`SUSPENDED`, `DISCONTINUED`, or whose seller is not `ACTIVE`) is added to a cart or is still in a cart being confirmed (DEC-08, DEC-16), or when a `DISCONTINUED` product is modified (DEC-27).
- InsufficientInventoryException — raised when no inventory record of the product has enough available units for a reservation (including when no record exists), or when a negative adjustment would leave the stock below zero (DEC-14).
- DamagedInventoryReservationException — raised when the only inventory records with enough units for a reservation are marked `DAMAGED`.

### Orders and Logistics

- InvalidOrderStatusTransitionException — raised when an operation requires the `Order` to be in a status it is not in (for example, confirming an order that is not a `CART`, or requesting a return for an order that is not `DELIVERED`).
- OrderAlreadyDeliveredException — raised when any operation attempts to modify a `DELIVERED` (finalized) `Order`.
- InvalidDeliveryAddressException — raised when an order with physical products is confirmed without a delivery address, or with one that is not among the buyer's registered addresses (DEC-18).
- InvalidShipmentStatusTransitionException — raised when a `Shipment` is not advanced exactly one step forward (`PREPARING → IN_TRANSIT → DELIVERED`).

### Returns and Refunds

- InvalidReturnStatusTransitionException — raised when a `ReturnRequest` or its `Refund` is not in the status required by the operation (reviewing a return that is not `REQUESTED`, completing one that is not `APPROVED`, opening a refund for a return that is not `COMPLETED`, resolving a refund that is not `PENDING`), or when a second return or refund is requested for the same order or return (DEC-24, DEC-25).

### General

- ResourceNotFoundException — raised when an operation references an entity (user, product, warehouse, inventory record, order, invoice, shipment, return, or refund) that does not exist. Consultation operations that browse or filter return an empty result instead.
- DomainValidationException — raised by an entity or Value Object itself when it is constructed with data that violates its own invariants (missing mandatory attribute, blank full name, invalid email, negative stock, unknown catalog code). See "Immutability, Identity, and Construction Invariants" in `Domain Model.md`.

Business exceptions belong exclusively to the domain. Translating them into a protocol response (for example, an HTTP status) is the responsibility of the input adapter, never of the domain. Suggested translation for the REST adapter:

| Exceptions | HTTP status |
| --- | --- |
| `InvalidCredentialsException` | 401 Unauthorized |
| `UnauthorizedOperationException`, `SellerSelfRegistrationNotAllowedException`, `UserNotActiveException` | 403 Forbidden |
| `ResourceNotFoundException` | 404 Not Found |
| `DuplicateUserDataException`, every `Invalid…TransitionException`, `OrderAlreadyDeliveredException` | 409 Conflict |
| `DomainValidationException`, `InvalidDeliveryAddressException` | 400 Bad Request |
| `BuyerNotEligibleForOrderException`, `ProductNotAvailableException`, `InsufficientInventoryException`, `DamagedInventoryReservationException` | 422 Unprocessable Entity |

---

# Infrastructure

Infrastructure contains technical configuration required by the application.

It does not contain business logic.

---

## Config

Responsible for application configuration.

Examples:

- REST configuration
- Serialization
- Environment configuration

---

## Database

Contains database initialization and connection configuration.

Examples:

- MySQL configuration
- Connection pools
- Transaction management: every use case that the domain declares atomic (see "State changes" in `Domain Services.md`) runs inside a single transaction opened around the Input Port call. The domain states the requirement; this layer provides the mechanism, so no transaction annotation ever appears in the domain.

---

## Security

Contains authentication and authorization configuration.

Examples:

- `PasswordHasher` implementation (the concrete hashing algorithm behind the domain's Output Port).
- Authentication filters, which resolve the authenticated `User` that input adapters pass to each use case as its actor.
- Session or token handling.

Business authorization (RG-03) is **not** configured here: it is enforced in the domain by `AuthorizationService`. Security configuration only establishes *who* the caller is.

---

# Dependency Flow

Dependencies always point toward the domain.

```text
REST Controller
        │
        ▼
Input Port
        │
        ▼
Domain Service
        │
        ▼
Output Port
        │
        ▼
Persistence Adapter
        │
        ▼
Database
```

The domain never depends on adapters or infrastructure.

---

# Benefits

This architecture provides:

- Technology independence.
- High maintainability.
- Clear separation of concerns.
- Improved testability.
- Easier scalability.
- Better support for Domain-Driven Design.
- Easy replacement of frameworks or databases.
- Reusable business logic.
- Long-term maintainability.

---

# Architectural Constraints

The following rules must always be respected:

1. Business logic belongs exclusively to the Domain layer.
2. Controllers must not contain business rules.
3. DTOs must never enter the Domain layer.
4. Persistence entities must never be exposed through the API.
5. Communication between technologies and the Domain must occur only through Ports.
6. Adapters implement Ports but never define business rules.
7. Infrastructure depends on the Domain, never the opposite.
8. Every dependency must point toward the Domain.
9. Business entities must remain framework-independent.
10. The Domain must be fully testable without requiring infrastructure components.
