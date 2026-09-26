# Domain Services

## Introduction

Domain Services contain the business logic that does not naturally belong to a single entity: logic that coordinates several entities, needs to read or persist state through Output Ports, or must be validated against the role of the currently authenticated `User`.

Each Domain Service is exposed to the outside world through one or more Input Ports (use cases) and depends exclusively on Output Ports (repositories and other technical ports) or on other services' Input Ports — never on a persistence framework, a controller, or a DTO. This preserves the dependency rule described in `Software Architecture.md`: dependencies always point toward the domain.

This document is the index of the Services layer. Each service listed below has its own detailed specification in `SDD/services/`. Wherever the business specification is silent or ambiguous, services apply the decisions recorded in [Business Decisions](Business%20Decisions.md) (`DEC-xx`).

---

# Decomposition Criterion

The Services layer is deliberately fine-grained: 37 services instead of one per functional objective. A service is split away from a sibling only when at least one of the following holds — mechanical read/write duplication ("CQRS for its own sake") is explicitly **not** treated as sufficient reason on its own unless paired with a genuine difference in audience:

1. **Distinct objective codes** — the business specification already separates them (OBJ-07 Cart vs. OBJ-08 Order; the "Gestión de devoluciones" vs. "Gestión de reembolsos" processes in §3.1).
2. **Distinct actor or trigger** — the operations are invoked by different roles, or by fundamentally different business events (a `Buyer` requesting a return is not the same event as an `Administrator` reviewing it; stock arriving is not the same event as a sale being confirmed).
3. **Distinct dependencies** — the operations orchestrate a materially different set of other services (`PaymentConfirmationService` depends on billing and inventory consumption; `OrderConfirmationService` depends only on reservation).
4. **Explicit textual separation** — the objective's own wording names two activities (OBJ-02: *"Gestionar el registro **y** administración de vendedores"*).
5. **Meaningfully broader read audience** — a consultation operation is open to roles the write side is not (anyone can browse the catalog; only the owning Seller can publish to it).
6. **Disjoint aggregate for the same read-only role** — each administrative report reads an aggregate no other report reads (DEC-29).

A service is deliberately **not** split further when its operations share the same actor, the same triggering event, and the same reason to change — splitting `CartService`'s `addItemToCart`/`removeItemFromCart`, or `UserManagementService`'s registration/status operations, would be fragmentation without a distinguishing cause. Two services intentionally group a manual operation with a system-triggered one on the same aggregate, because both change for the same reason: `StockAdjustmentService` (manual correction + reservation release) and `RefundProcessingService` (refund opening + refund decision).

---

# Service Index, Grouped by Capability Area

## Authentication & Authorization

| Service | Responsibility |
| --- | --- |
| [Authentication Service](../services/Authentication%20Service.md) | Functional verification of credentials (RG-01). |
| [Authorization Service](../services/Authorization%20Service.md) | Centralized enforcement of the Authorization Matrix (RG-03), shared by every other service. |

## User Administration — OBJ-01

| Service | Responsibility |
| --- | --- |
| [User Management Service](../services/User%20Management%20Service.md) | Registers internal-profile users; manages the status of buyers and internal users and the commercial status of buyers. |

## Buyers — OBJ-03

| Service | Responsibility |
| --- | --- |
| [Buyer Registration Service](../services/Buyer%20Registration%20Service.md) | Buyer self-registration and self-managed profile. |

## Sellers — OBJ-02

| Service | Responsibility |
| --- | --- |
| [Seller Registration Service](../services/Seller%20Registration%20Service.md) | Incorporation of a new `Seller` and their first `Warehouse` by an `Administrator` ("registro"). |
| [Seller Administration Service](../services/Seller%20Administration%20Service.md) | Update and status management of an existing `Seller` ("administración"). |

## Warehouses — OBJ-04

| Service | Responsibility |
| --- | --- |
| [Warehouse Management Service](../services/Warehouse%20Management%20Service.md) | Registration and administration of `Warehouse` records. |

## Product Catalog — OBJ-05

| Service | Responsibility |
| --- | --- |
| [Product Catalog Service](../services/Product%20Catalog%20Service.md) | Publication, update, and status lifecycle of a `Product`, Seller-only. |
| [Catalog Consultation Service](../services/Catalog%20Consultation%20Service.md) | Public catalog browsing, open to any authenticated user. |

## Inventory — OBJ-06

| Service | Responsibility |
| --- | --- |
| [Stock Inbound Service](../services/Stock%20Inbound%20Service.md) | Records `INBOUND` movements; creates the inventory record on the first inbound. |
| [Stock Reservation Service](../services/Stock%20Reservation%20Service.md) | Records `RESERVATION` movements, triggered by `OrderConfirmationService`. |
| [Stock Sale Outbound Service](../services/Stock%20Sale%20Outbound%20Service.md) | Records `SALE_OUTBOUND` movements, triggered by `PaymentConfirmationService`. |
| [Stock Adjustment Service](../services/Stock%20Adjustment%20Service.md) | Records `ADJUSTMENT` movements, including reservation release and condition changes. |
| [Stock Return Service](../services/Stock%20Return%20Service.md) | Records `RETURN` movements, triggered by `ReturnCompletionService`. |
| [Inventory Consultation Service](../services/Inventory%20Consultation%20Service.md) | Stock levels and movement traceability, read-only. |

## Cart & Order Lifecycle — OBJ-07, OBJ-08

| Service | Responsibility |
| --- | --- |
| [Cart Service](../services/Cart%20Service.md) | Add/change/remove items in a `CART`-status `Order` (OBJ-07). |
| [Order Confirmation Service](../services/Order%20Confirmation%20Service.md) | `CART` → `PENDING_PAYMENT`: re-validates availability, freezes prices, fixes the delivery address, reserves inventory. |
| [Payment Confirmation Service](../services/Payment%20Confirmation%20Service.md) | Confirmed payment: `PENDING_PAYMENT` → `PAID` (or `DELIVERED` for digital-only), orchestrates sale outbound, billing, digital delivery, and shipment creation. Rejected payment: delegates to cancellation. |
| [Order Fulfillment Tracking Service](../services/Order%20Fulfillment%20Tracking%20Service.md) | `PAID` → `SHIPPED` → `DELIVERED`, driven by all the order's shipments. |
| [Order Cancellation Service](../services/Order%20Cancellation%20Service.md) | Reverts a pending order back to `CART`, releases its reservations (no `CANCELLED` status exists). |
| [Order Consultation Service](../services/Order%20Consultation%20Service.md) | Order information, read-only, multi-role audience; shows delivered digital items. |

## Billing — OBJ-09

| Service | Responsibility |
| --- | --- |
| [Invoice Generation Service](../services/Invoice%20Generation%20Service.md) | Generates an `Invoice` on `PAID`, system-triggered. |
| [Invoice Consultation Service](../services/Invoice%20Consultation%20Service.md) | Invoice information, Buyer/Administrator. |

## Shipment — OBJ-10

| Service | Responsibility |
| --- | --- |
| [Shipment Creation Service](../services/Shipment%20Creation%20Service.md) | Creates one `Shipment` per origin warehouse when a physical order is `PAID`, system-triggered. |
| [Shipment Tracking Service](../services/Shipment%20Tracking%20Service.md) | Advances `ShipmentStatus`, Logistics Operator. |
| [Shipment Consultation Service](../services/Shipment%20Consultation%20Service.md) | Shipment information, multi-role audience. |

## Returns & Refunds — OBJ-11

| Service | Responsibility |
| --- | --- |
| [Return Request Service](../services/Return%20Request%20Service.md) | Buyer requests a return for a `DELIVERED` order. |
| [Return Review Service](../services/Return%20Review%20Service.md) | Administrator approves/rejects the return. |
| [Return Completion Service](../services/Return%20Completion%20Service.md) | Confirms physical receipt of the returned product; triggers stock reincorporation and opens the refund. |
| [Refund Processing Service](../services/Refund%20Processing%20Service.md) | Opens the `Refund` in `PENDING` for a completed return; the Administrator resolves it. |
| [Return And Refund Consultation Service](../services/Return%20And%20Refund%20Consultation%20Service.md) | Return/refund status, Buyer/Administrator. |

## Administrative Reporting — OBJ-12

Scope defined by DEC-29: one read-only report per business area, Supervisor-only.

| Service | Responsibility |
| --- | --- |
| [User Reporting Service](../services/User%20Reporting%20Service.md) | Users by role and status; buyers by commercial status. |
| [Catalog Reporting Service](../services/Catalog%20Reporting%20Service.md) | Products by status, type, and seller. |
| [Inventory Reporting Service](../services/Inventory%20Reporting%20Service.md) | Stock levels and movement traceability. |
| [Order Reporting Service](../services/Order%20Reporting%20Service.md) | Orders by status and invoiced amount. |
| [Shipment Reporting Service](../services/Shipment%20Reporting%20Service.md) | Shipments by status and warehouse; pending dispatches. |
| [Return Refund Reporting Service](../services/Return%20Refund%20Reporting%20Service.md) | Return and refund activity. |

---

# Orchestration Overview

Orchestration between services is explicit in each service's own dependency list. The chain that matters most is the central commercial transaction:

```text
CartService                                   (Order: CART)
   │ buyer confirms
   ▼
OrderConfirmationService ──uses──> StockReservationService     (Order: PENDING_PAYMENT)
   │
   │ payment signal
   ▼
PaymentConfirmationService
   │  confirmed ──uses──> StockSaleOutboundService
   │            ──uses──> InvoiceGenerationService              (Order: PAID; digital items delivered)
   │            ├─[physical items]──uses──> ShipmentCreationService
   │            │                           (one Shipment per origin warehouse, PREPARING)
   │            └─[digital only]────────────────────────────────> (Order: DELIVERED)
   │  rejected  ──uses──> OrderCancellationService ──uses──> StockAdjustmentService
   │                                                           (Order: CART)

ShipmentTrackingService  (Logistics Operator: PREPARING → IN_TRANSIT → DELIVERED)
   │ after every advance
   ▼
OrderFulfillmentTrackingService
   (all shipments IN_TRANSIT → Order: SHIPPED; all shipments DELIVERED → Order: DELIVERED)
```

The post-sale process is a separate chain, started only on a `DELIVERED` order:

```text
ReturnRequestService (Buyer)                  (Return: REQUESTED)
   ▼
ReturnReviewService (Administrator)           (Return: APPROVED | REJECTED)
   ▼
ReturnCompletionService (Administrator)       (Return: COMPLETED)
   ├──uses──> StockReturnService              (RETURN movements)
   └──uses──> RefundProcessingService.openRefund   (Refund: PENDING)
                 ▼
RefundProcessingService.resolveRefund (Administrator)   (Refund: PROCESSED | REJECTED)
```

Buyer cancellation (`OrderCancellationService` → `StockAdjustmentService`) is the only other write chain. Stock is reincorporated and the refund is only opened once physical receipt is confirmed, never at mere commercial approval (DEC-25).

The dependency graph between services has no cycles: orchestrating services depend on the services they trigger, never the other way around.

---

# General Design Rules for Services

## Structure

* A service exposes its use cases through Input Port interfaces owned by the domain; adapters depend on those interfaces, never on the concrete service class.
* A service depends only on Output Port interfaces (repositories and technical ports such as `PasswordHasher`) or on other services' Input Ports for orchestration — never directly on a persistence framework, a controller, or a DTO.
* A service is split away from a sibling only per the "Decomposition Criterion" above — granularity is a tool for clarity, not an end in itself.

## Authorization and the calling user

* Every operation open to a role receives the authenticated `User` who performs it (the *actor*). The operation tables in `SDD/services/` omit this parameter to keep them readable; "Caller is …" in the preconditions always refers to it. Operations that precede authentication (DEC-01) and system-triggered operations receive no actor.
* Authorization (RG-03, per the Authorization Matrix) is checked through `AuthorizationService` as a guard clause at the start of every role-invoked operation, before any state is read or written. System-triggered operations are only reachable from the service that orchestrates them and are never exposed to an adapter as a role-invoked use case.

## Validation

* Validation that requires reading external state — uniqueness checks, the current persisted status of an entity, ownership of a resource — is a service's responsibility, because a single entity cannot validate it against itself alone.
* Validation that only needs the data an entity already holds (mandatory attributes, quantities, lifecycle consistency, return quantities against the order, inventory location, invoice and refund amounts) is an invariant of the entity itself and raises `DomainValidationException` when the service builds it; services do not duplicate it. See "Immutability, Identity, and Construction Invariants" in `Domain Model.md`.
* An operation that references an entity by its identifier raises `ResourceNotFoundException` when the entity does not exist; consultation operations that browse or filter return an empty result instead.

## State changes

* Entities are immutable: a service changes the state of an entity by building a new instance with the new values and persisting it through the aggregate's repository (see "Aggregates and Repositories" in `Domain Model.md`).
* A service that creates an entity assigns its identifier (a UUID; the identity document for a `User`, DEC-02) and its dates (the current date and time) before building it.
* A service must never leave domain state inconsistent: when an operation requires coordinating more than one aggregate, the service is responsible for the coordination, even when the individual steps are delegated to other services (see "Orchestration Overview" above).
* Every orchestrating operation is atomic: if any step fails, none of the changes made by that operation are kept. The domain declares this requirement; the transaction mechanism that guarantees it belongs to infrastructure (see `Software Architecture.md`).

## Traceability

* Every business rule enforced by a service must trace back to an explicit rule already documented in `Domain Model.md`, `Domain Value Objects.md`, `Business Decisions.md`, or the business specification — a service must not invent a rule that is not grounded in those documents.
* Every exception raised by a service must be one of the business exceptions listed in `Software Architecture.md`, section "Exceptions" — never a generic or technical exception.

---

# How to Read a Service Specification

Every file in `SDD/services/` follows the same structure:

| Section | Content |
| --- | --- |
| Description | What the service does and why it is a separate service (Decomposition Criterion). |
| Responsibilities | The business actions it owns. |
| Authorized Roles | Who may invoke it, per the Authorization Matrix, or "not directly invoked" for system-triggered services. |
| Operations | One row per operation. Each operation becomes one Input Port (use case) in the ports stage. Parameters are business data; the actor is implicit. |
| Dependencies (Output Ports) | Repositories and technical ports it uses, plus the other services it orchestrates (linked). |
| Business Rules Enforced | The rules it guarantees, quoted from the Domain documents, the specification, or a `DEC-xx`. |
| Exceptions | The business exceptions it may raise. |
