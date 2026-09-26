# Domain Model

## Introduction

The Domain Model represents the core business entities of NexusMarket, a digital marketplace that intermediates commercial transactions between buyers and sellers. These entities encapsulate the business rules, data, relationships, and lifecycle concepts described in the system specification.

The model follows Object-Oriented Design and Domain-Driven Design (DDD) principles. Inheritance is used to represent genuine domain specialization, while explicit object relationships are preferred over generic identifier fields.

The model distinguishes between:

* **Users**, which represent every participant of the marketplace and their assigned role.
* **Products**, which represent the goods offered for sale, either physical or digital.
* **Warehouses and Inventory**, which represent the physical storage and stock management of physical products.
* **Orders**, which represent the central commercial process of the marketplace.
* **Invoices, Shipments, Returns and Refunds**, which represent the commercial and logistics processes that follow an order.

An order generates an invoice once payment is confirmed, generates one shipment per origin warehouse when it includes physical products, and may later generate a return and a subsequent refund.

Where the business specification is silent or ambiguous, the model applies the decisions recorded in [Business Decisions](Business%20Decisions.md), referenced below by their code (`DEC-xx`).

---

# Domain Class Hierarchy

```text
User
├── Buyer
└── Seller

Product (Abstract)
├── PhysicalProduct
└── DigitalProduct

Warehouse
Inventory
InventoryMovement
Order
OrderItem
Invoice
Shipment
ReturnRequest
ReturnItem
Refund
```

`ReturnRequest` represents the business concept of a *Return* ("Devolución"); it is named this way because `return` is a reserved keyword in Java.

---

# Domain Relationships

```text
User
   │
   ├── Buyer
   └── Seller

Warehouse
   │
   └── owner : Seller (optional — absent for Marketplace-owned warehouses)

Product
   │
   └── seller : Seller

Inventory
   │
   ├── product : PhysicalProduct
   └── warehouse : Warehouse (Marketplace-owned, or owned by the product's seller)
          │
          └── generates ─────> InventoryMovement
                                  └── order : Order (optional)

Buyer
   │
   └── places ───────────────> Order

Order
   │
   ├── buyer : Buyer
   ├── items : List<OrderItem>
   ├── generates ─────────────> Invoice
   ├── generates ─────────────> Shipment (one per origin warehouse, physical products only)
   └── may generate ──────────> ReturnRequest

OrderItem
   │
   ├── product : Product
   └── reservedInventory : Inventory (optional — physical items once confirmed)

ReturnRequest
   │
   ├── order : Order
   ├── items : List<ReturnItem>
   └── may generate ──────────> Refund

ReturnItem
   │
   └── product : PhysicalProduct
```

---

# Entities

---

# User

## Description

Represents any participant of NexusMarket who interacts with the platform according to the responsibilities defined by their role.

Participants whose role does not require additional attributes or relationships (`ADMINISTRATOR`, `LOGISTICS_OPERATOR`, `SUPERVISOR`) are represented directly as `User` instances. Participants whose role requires additional attributes or relationships (`BUYER`, `SELLER`) are represented by a specialized subclass.

`User` is the root of the person hierarchy in NexusMarket. Unlike systems where a person may exist independently of a system identity (for example, a bank customer without a system user), every participant of NexusMarket interacts with the platform directly as a `User`. For this reason, a separate `Person` abstraction was considered but discarded: it would have had exactly one specialization (`User`) and no independent use anywhere in the domain, so it added no real abstraction — only the appearance of one.

`username` and `password` are included because a marketplace requires knowing who is currently authenticated, which is a functional requirement of the domain regardless of implementation. The system specification excludes **technical** authentication mechanisms (hashing algorithms, JWT, security frameworks) from its scope, not the existence of credentials themselves. The concrete mechanism used to validate credentials and issue a session belongs to the Services layer, not to this model.

## Attributes

| Attribute | Type       | Description                                                                          |
| --------- | ---------- | ------------------------------------------------------------------------------------- |
| id        | String     | Identity document number of the person (DEC-02). Must be unique across the platform. |
| fullName  | String     | Full name of the person. Must not be blank.                                           |
| email     | String     | Primary email address, used for access and communication. Unique; stored in lowercase and validated for format (DEC-03). |
| role      | SystemRole | Defines the participant's responsibilities and permissions within the marketplace.    |
| status    | UserStatus | Current operational status of the user within the marketplace.                        |
| username  | String     | Login name used during authentication. Unique; stored in lowercase (DEC-03).         |
| password  | String     | Secure password hash stored by the system.                                            |

## Behavior

* `isActive()` — the user may access and operate on the platform (`status = ACTIVE`).

## Relationships

* A `User` may be specialized as `Buyer` or `Seller` when their role requires additional attributes or relationships.
* A `User` whose role is `ADMINISTRATOR`, `LOGISTICS_OPERATOR`, or `SUPERVISOR` is represented directly by this class, without further specialization. A plain `User` can never carry the `BUYER` or `SELLER` role.

## Business Rule

```text
The identity document (id), the email, and the username of a User must be
unique across the platform. Email and username are compared
case-insensitively.
```

---

# Buyer

## Description

Represents a user who purchases products published on NexusMarket.

A buyer never manages information belonging to other buyers, warehouses, or seller inventories.

## Inherits From

`User` — its `role` is always `BUYER`.

## Attributes

| Attribute            | Type              | Description                                              |
| --------------------- | ----------------- | ---------------------------------------------------------- |
| primaryAddress        | String            | Habitual address used for order deliveries. Mandatory.   |
| additionalAddresses   | List\<String\>    | Secondary delivery addresses. Empty by default.           |
| commercialStatus      | CommercialStatus  | Condition of the buyer for placing new orders.            |

## Behavior

* `hasAddress(address)` — the address is the buyer's primary address or one of the additional ones; used to validate an order's delivery address (DEC-18).

## Relationships

* A `Buyer` places zero or more `Order` instances, with at most one in `CART` status at a time (DEC-17).

## Business Rule

```text
The commercialStatus of a Buyer governs new purchasing activity (DEC-07):
ACTIVE may manage the cart and confirm orders; RESTRICTED may manage the
cart but not confirm new orders; SUSPENDED may do neither. Orders already
confirmed are never affected.
```

---

# Seller

## Description

Represents a user responsible for registering and managing their own products on NexusMarket ("Vendedor: Responsable de registrar y administrar sus productos").

Sellers cannot self-register; they are incorporated into the platform by an `Administrator`, together with their first warehouse (DEC-09).

This class has no attributes of its own beyond those inherited from `User`. It exists as a distinct type so that ownership relationships (`Product.seller`, `Warehouse.owner`) can only ever reference a seller, and its role is always fixed to `SELLER`.

## Inherits From

`User` — its `role` is always `SELLER`.

## Relationships

* A `Seller` owns zero or more `Warehouse` instances, expressed through `Warehouse.owner`.
* A `Seller` publishes zero or more `Product` instances, expressed through `Product.seller`.
* Both relationships are navigated from the owned side only. The warehouses and products of a seller are retrieved through their repositories rather than held as lists inside `Seller`: every entity is immutable (`final` fields), and holding the relationship in both directions would make it impossible to construct a `Seller` and its `Warehouse` instances, since each would need the other to exist first.

## Business Rule

```text
A Seller cannot self-register. Sellers are incorporated into the
platform by an Administrator, together with their first Warehouse.

While a Seller is not ACTIVE, their products are not available for sale
(DEC-08).
```

---

# Product (Abstract)

## Description

Represents a good offered for sale on NexusMarket, published by a seller.

Physical products require inventory tracking and dispatch, while digital products are delivered immediately after payment confirmation. This behavioral difference is represented through specialization rather than through a type attribute; as a consequence, the type of a product can never change after it is registered (DEC-12).

This class cannot be instantiated directly.

## Attributes

| Attribute    | Type            | Description                                                              |
| ------------- | --------------- | --------------------------------------------------------------------------- |
| id            | String          | Unique identifier of the product.                                        |
| name          | String          | Commercial name of the product.                                          |
| description   | String          | Description of the product shown to buyers. Optional.                    |
| variants      | List\<String\>  | Variations of the product, such as color, size, or model. Empty by default. |
| price         | BigDecimal      | Current sale price. Greater than zero (DEC-11).                          |
| status        | ProductStatus   | Current status of the product within the catalog.                        |
| seller        | Seller          | Seller who owns and publishes the product.                               |

## Behavior

* `isAvailableForSale()` — the product may be shown in the public catalog and added to a new order: its status is `PUBLISHED` and its seller is `ACTIVE` (DEC-08).

## Relationships

* A `Product` is published by one `Seller`.
* A `Product` may be referenced by zero or more `OrderItem` instances.

## Business Rule

```text
A Product that is not available for sale (status SUSPENDED or
DISCONTINUED, or seller not ACTIVE) must not be added to a new Order.
Products already included in a confirmed Order are not affected
retroactively by a later change of status or price.
```

---

# PhysicalProduct

## Description

Represents a tangible product that requires inventory tracking and physical dispatch to be delivered to the buyer.

## Inherits From

`Product`

## Relationships

* A `PhysicalProduct` is tracked through `Inventory` records across one or more warehouses.
* Only a `PhysicalProduct` can be returned (DEC-24).

---

# DigitalProduct

## Description

Represents a product delivered electronically and immediately after payment confirmation, without requiring inventory or physical dispatch. It can never be returned (DEC-24).

## Inherits From

`Product`

---

# Warehouse

## Description

Represents a physical location where product inventory is stored and managed.

A warehouse may belong to the Marketplace itself or to a specific seller. Its owner is fixed at registration and never changes (DEC-10).

## Attributes

| Attribute | Type     | Description                                                                     |
| --------- | -------- | --------------------------------------------------------------------------------- |
| id        | String   | Unique identifier of the warehouse.                                             |
| name      | String   | Descriptive name of the warehouse.                                              |
| address   | String   | Physical location of the warehouse.                                             |
| owner     | Seller?  | Seller who owns the warehouse. Absent when the warehouse belongs to the Marketplace. |

## Behavior

* `isMarketplaceOwned()` — the warehouse has no seller owner.

## Relationships

* A `Warehouse` may belong to zero or one `Seller`. When absent, the warehouse belongs to the Marketplace.
* A `Warehouse` holds zero or more `Inventory` records.

---

# Inventory

## Description

Represents the available stock of a physical product within a specific warehouse.

Inventory must always be linked to exactly one product and one warehouse. Negative stock is never allowed under any circumstance. The record for a product/warehouse pair is created by its first `INBOUND` movement (DEC-13).

## Attributes

| Attribute          | Type              | Description                                              |
| ------------------- | ----------------- | ------------------------------------------------------------ |
| id                  | String              | Unique identifier of the inventory record.                |
| product             | PhysicalProduct     | Physical product tracked by this inventory record.        |
| warehouse           | Warehouse           | Warehouse where the stock is stored.                       |
| availableQuantity   | Integer             | Quantity available for sale (reserved units are already excluded). Never negative. |
| condition           | InventoryCondition  | Condition of every unit tracked by this record (DEC-15).   |

## Behavior

* `canReserve(quantity)` — the stock is not `DAMAGED` and `availableQuantity >= quantity`.

## Relationships

* An `Inventory` record is linked to exactly one `PhysicalProduct`.
* An `Inventory` record is linked to exactly one `Warehouse`, which must be Marketplace-owned or owned by the product's seller (DEC-13).
* An `Inventory` record may generate multiple `InventoryMovement` instances.

## Business Rule

```text
Available quantity must never become negative as a result of any inventory movement.

Inventory that is non-existent (no record, or not enough available units)
or marked as DAMAGED must not be reserved under any circumstance (DEC-14).

A product's inventory may only be stored in a Marketplace warehouse or in
a warehouse owned by the product's own seller (DEC-13).
```

---

# InventoryMovement

## Description

Represents a significant change applied to an inventory record, such as an incoming stock entry, a reservation, a sale, an adjustment, or a return.

An inventory movement provides traceability for changes in stock, in the same way an operation record provides traceability for actions performed on any business entity.

## Attributes

| Attribute      | Type           | Description                                       |
| --------------- | -------------- | ----------------------------------------------------- |
| id              | String         | Unique identifier of the movement.                 |
| inventory       | Inventory      | Inventory record affected by the movement.         |
| movementType    | MovementType   | Category of the inventory movement.                |
| quantity        | Integer        | Quantity involved. Greater than zero, except for `ADJUSTMENT`, where it is the signed correction (zero when only the condition changed). |
| movementDate    | LocalDateTime  | Date and time when the movement occurred.          |
| order           | Order?         | Order that caused the movement. Mandatory for `RESERVATION`, `SALE_OUTBOUND`, and `RETURN`, and for the `ADJUSTMENT` that releases a reservation. |
| reason          | String?        | Reason for the movement. Mandatory for a manual `ADJUSTMENT` (one without an order). |

## Relationships

* An `InventoryMovement` affects exactly one `Inventory` record.
* An `InventoryMovement` may reference the `Order` that caused it, which lets a sale outbound or a reservation release be matched with the original reservation.

---

# Order

## Description

Represents a purchase commitment made by a buyer. Its lifecycle is the central business process of NexusMarket.

## Attributes

| Attribute       | Type              | Description                                      |
| ---------------- | ----------------- | ---------------------------------------------------- |
| id               | String            | Unique identifier of the order.                    |
| buyer            | Buyer             | Buyer who placed the order.                        |
| items            | List\<OrderItem\> | Products and quantities included in the order. A product appears at most once (DEC-17). |
| status           | OrderStatus       | Current stage of the order lifecycle.               |
| creationDate     | LocalDateTime     | Date and time when the order was created.           |
| deliveryAddress  | String?           | Delivery address chosen among the buyer's addresses at confirmation, kept as a snapshot. Only for orders with physical products (DEC-18). |

## Behavior

* `containsPhysicalProducts()` / `getPhysicalItems()` — physical items of the order.
* `findItem(product)` — the item corresponding to a product, if any.
* `getOriginWarehouses()` — the warehouses the physical items were reserved from (one shipment each).
* `getTotalAmount()` — sum of the items' subtotals (DEC-23).
* `isFinalized()` — the order is `DELIVERED` and can no longer be modified.

## Relationships

* An `Order` is placed by one `Buyer`.
* An `Order` contains one or more `OrderItem` instances once it leaves `CART`.
* An `Order` generates one `Invoice` once payment is confirmed.
* An `Order` with physical products generates one `Shipment` per origin warehouse (DEC-21).
* An `Order` may generate at most one `ReturnRequest` after being delivered (DEC-24).

## Business Rules

```text
An Order in DELIVERED status is finalized and must not be modified
under any circumstance. DELIVERED is the single terminal status and
means "Entregado / Finalizado" (DEC-19). A return never modifies the Order.

Once an Order leaves CART, it contains at least one item; if it contains
physical products, it has a delivery address and every physical item
has been reserved from an inventory record.

An Order can only be cancelled while in PENDING_PAYMENT, returning to
CART and releasing its reservations (DEC-20).
```

## Business Rule — Digital Fulfillment Path

```text
The SHIPPED status represents the physical departure of goods from a
warehouse (business specification, Domain 7, "Despachado: Salida física
de la bodega") and therefore only applies to an Order that contains at
least one PhysicalProduct. An Order composed only of DigitalProduct items
can never be SHIPPED.

An Order composed exclusively of DigitalProduct items never generates a
Shipment and must skip SHIPPED entirely: once its status becomes PAID,
it transitions directly to DELIVERED, since digital products are
delivered immediately upon payment confirmation.

An Order that mixes PhysicalProduct and DigitalProduct items follows the
full lifecycle through SHIPPED, driven by the Shipments of its physical
items. The DigitalProduct items within such an Order are made available
to the Buyer as soon as the Order reaches PAID, independently of the
Order's status continuing to advance afterward for the physical items —
this delivery action does not require, and must not wait for, a change
in the Order's own status.

"Delivered" for a digital item means it becomes available to the Buyer
when consulting the Order; the technical means of access is out of
scope and not modeled (DEC-28).
```

## Business Rule — Physical Fulfillment Path (DEC-21)

```text
When a physical Order reaches PAID, one Shipment is created per origin
warehouse, in PREPARING ("Pagado: Inicio de procesos de alistamiento").

The Order moves to SHIPPED only when all its Shipments are IN_TRANSIT or
later (all goods left their warehouses), and to DELIVERED only when all
its Shipments are DELIVERED. Creating a Shipment never changes the
Order's status by itself.
```

---

# OrderItem

## Description

Represents a single product and quantity included within an order, together with the unit price at the moment of purchase.

## Attributes

| Attribute          | Type       | Description                                              |
| ------------------- | ---------- | ------------------------------------------------------------ |
| product             | Product    | Product included in the order.                            |
| quantity            | Integer    | Quantity of the product requested. Greater than zero.      |
| unitPrice           | BigDecimal | Price captured when added to the cart, refreshed at confirmation, and frozen afterwards (DEC-16). |
| reservedInventory   | Inventory? | Inventory record the units were reserved from. Only for physical items, once the order is confirmed; always a single record (DEC-14). |

## Behavior

* `isPhysical()` — the product is a `PhysicalProduct`.
* `getSubtotal()` — `unitPrice × quantity`.

## Relationships

* An `OrderItem` references exactly one `Product`.
* A physical `OrderItem` references the single `Inventory` record its units were reserved from, which must belong to the same product.

---

# Invoice

## Description

Represents the commercial and financial information associated with a confirmed order.

## Attributes

| Attribute    | Type          | Description                             |
| ------------- | ------------- | ------------------------------------------- |
| id            | String        | Unique identifier of the invoice.        |
| order         | Order         | Order this invoice belongs to.           |
| issueDate     | LocalDateTime | Date and time when the invoice was issued. |
| totalAmount   | BigDecimal    | Total amount billed: exactly the order's total amount, with no taxes or shipping costs (DEC-23). |

## Relationships

* An `Invoice` belongs to exactly one `Order`, and an `Order` has at most one `Invoice`.

---

# Shipment

## Description

Represents the logistics process required to deliver, from one warehouse to the buyer, the physical products of an order reserved in that warehouse.

## Attributes

| Attribute        | Type            | Description                                          |
| ----------------- | --------------- | --------------------------------------------------------- |
| id                | String          | Unique identifier of the shipment.                     |
| order             | Order           | Order being shipped. Must contain physical products.    |
| originWarehouse   | Warehouse       | Warehouse from which the products are dispatched; one of the order's origin warehouses. |
| status            | ShipmentStatus  | Current logistics status of the shipment.               |
| creationDate      | LocalDateTime   | Date and time when the shipment was created.            |

## Behavior

* `getItems()` — the physical items of the order reserved in the origin warehouse, which this shipment carries.

## Relationships

* A `Shipment` belongs to exactly one `Order`; an `Order` has one `Shipment` per origin warehouse (DEC-21).
* A `Shipment` originates from one `Warehouse`.

---

# ReturnRequest

## Description

Represents a buyer's request to return one or more products from a delivered order ("Devolución" in the business specification).

## Attributes

| Attribute    | Type               | Description                                       |
| ------------ | ------------------ | ------------------------------------------------------ |
| id           | String             | Unique identifier of the return.                    |
| order        | Order              | Order associated with the return. Must be `DELIVERED`. |
| items        | List\<ReturnItem\> | Physical products and quantities returned. At least one. |
| reason       | String             | Reason provided by the buyer for the return.         |
| status       | ReturnStatus       | Current status of the return process.                |
| requestDate  | LocalDateTime      | Date and time when the buyer requested the return.  |

## Behavior

* `getRefundableAmount()` — for each returned item, the unit price paid in the order multiplied by the returned quantity (DEC-25).

## Relationships

* A `ReturnRequest` belongs to exactly one `Order`; an `Order` has at most one `ReturnRequest` (DEC-24).
* A `ReturnRequest` contains one or more `ReturnItem` instances.
* A `ReturnRequest` may generate one `Refund`.

## Business Rule

```text
Only physical products of a DELIVERED order can be returned, each in a
quantity between 1 and the purchased quantity (DEC-24). The order itself
is never modified by the return (DEC-19).
```

---

# ReturnItem

## Description

Represents a physical product and the quantity of it returned as part of a `ReturnRequest`.

## Attributes

| Attribute | Type            | Description                                                   |
| --------- | --------------- | ------------------------------------------------------------- |
| product   | PhysicalProduct | Physical product being returned. Must be part of the order.   |
| quantity  | Integer         | Quantity returned. Greater than zero, not above the purchased quantity. |

---

# Refund

## Description

Represents the reimbursement of funds to a buyer as a result of a completed return.

## Attributes

| Attribute     | Type          | Description                                |
| ------------- | ------------- | ---------------------------------------------- |
| id            | String        | Unique identifier of the refund.            |
| returnRequest | ReturnRequest | Return that originated the refund. Must be `COMPLETED`. |
| amount        | BigDecimal    | Amount reimbursed to the buyer. Never above the return's refundable amount. |
| status        | RefundStatus  | Current status of the refund process.        |
| creationDate  | LocalDateTime | Date and time when the refund was created.  |

## Relationships

* A `Refund` belongs to exactly one `ReturnRequest`.

## Business Rule

```text
A Refund is opened in PENDING automatically once the returned product has
been physically received (ReturnRequest COMPLETED), for the return's
refundable amount. The Administrator then resolves it as PROCESSED or
REJECTED; stock reincorporated at completion is not reverted by a
rejection, since the units are physically in the warehouse (DEC-25).
```

---

# Domain Design Rules

## User

* `User` is the root of the person hierarchy in NexusMarket; there is no separate `Person` abstraction, because it would have had exactly one specialization and no independent use in the domain.
* `User` is not abstract: it is instantiated directly for roles that require no additional attributes (`ADMINISTRATOR`, `LOGISTICS_OPERATOR`, `SUPERVISOR`).
* `username` and `password` are functional domain requirements (the system must know who is authenticated); only the technical mechanism used to validate them is excluded from scope and deferred to the Services layer.

## Buyer and Seller

* `Buyer` and `Seller` are specializations of `User`, created because their roles require additional attributes or relationships not shared by every user.
* `Seller` is justified as a class by its relationships (it is the only type that may own a `Warehouse` or publish a `Product`), not by scalar attributes.
* `Buyer` and `Seller` fix their own `role` (`BUYER`, `SELLER`) instead of receiving it as a constructor parameter, and a plain `User` rejects those two roles, so the class and the role of a participant can never disagree (RG-02).

## Products

* `Product` is abstract; `PhysicalProduct` and `DigitalProduct` represent genuine behavioral specializations rather than a simple type flag.
* Only `PhysicalProduct` participates in `Inventory` and in returns.

## Warehouses and Inventory

* A `Warehouse` distinguishes Marketplace-owned from Seller-owned warehouses through an optional `owner` relationship, rather than a type attribute.
* `Inventory` availability must never become negative, and its location is restricted by DEC-13.
* `InventoryMovement` records provide traceability for stock changes, following the same operation-traceability pattern used across the domain.

## Orders and Post-Sale Processes

* `Order` is the aggregate root of the purchase process and owns its `OrderItem` collection.
* `ReturnRequest` owns its `ReturnItem` collection.
* `Invoice`, `Shipment`, `ReturnRequest`, and `Refund` are separate entities generated from an `Order`, reflecting distinct business processes (billing, logistics, and post-sale) rather than attributes of `Order`.

## Aggregates and Repositories

Each aggregate is loaded and saved as a whole through exactly one repository (Output Port). Entities owned by an aggregate have no repository of their own.

| Aggregate root | Owned entities | Repository (Output Port) |
| --- | --- | --- |
| `User` (including `Buyer` and `Seller`) | — | `UserRepository` |
| `Product` (`PhysicalProduct`, `DigitalProduct`) | — | `ProductRepository` |
| `Warehouse` | — | `WarehouseRepository` |
| `Inventory` | — | `InventoryRepository` |
| `InventoryMovement` | — | `InventoryMovementRepository` |
| `Order` | `OrderItem` | `OrderRepository` |
| `Invoice` | — | `InvoiceRepository` |
| `Shipment` | — | `ShipmentRepository` |
| `ReturnRequest` | `ReturnItem` | `ReturnRequestRepository` |
| `Refund` | — | `RefundRepository` |

`InventoryMovement` is its own aggregate rather than part of `Inventory` because movements only accumulate (they are never modified) and loading the whole history every time a stock level changes would be unnecessary. Aggregates reference each other by holding the referenced entity (for example, `Shipment.order`), never by modifying it: a service that needs to change another aggregate does so through that aggregate's own repository.

## Immutability, Identity, and Construction Invariants

* Every entity is immutable: all fields are `final`, there are no setters, and every collection (`Order.items`, `ReturnRequest.items`, `Product.variants`, `Buyer.additionalAddresses`) is stored as an unmodifiable copy, so the caller that built the entity cannot alter it afterwards. A `null` collection is stored as an empty list. A change of state is represented by a new instance built by the corresponding service.
* Entities are compared by identity: two instances with the same `id` represent the same entity (`OrderItem` and `ReturnItem`, which have no `id` of their own, are the exception).
* Identifiers are assigned before an entity is constructed, since `id` is mandatory: a `User`'s `id` is the identity document supplied at registration (DEC-02); every other entity receives a UUID generated by the service that creates it.
* Each entity validates its own invariants when it is constructed — every rule that can be checked with the data the entity holds (mandatory attributes, non-negative quantities, lifecycle consistency of an `Order`, return quantities against the order, inventory location, invoice and refund amounts). A violation raises `DomainValidationException`.
* Rules that require persisted state (uniqueness, the current status stored for an entity, ownership against the authenticated user, the existence of other entities) remain the responsibility of the Services layer.

## General Business Rules

The following rules apply across the domain rather than to a single entity:

* **RG-02** — Each person has exactly one role. For this reason, `User.role` is modeled as a single `SystemRole` value rather than a collection.
* **RG-01** — Every operation on the platform must be executed by an authenticated user, except authentication itself and buyer self-registration (DEC-01). This rule constrains system access and is enforced by the Authentication and Authorization services; it does not require a dedicated domain entity.
* **RG-03** — No participant may manage information outside the scope of their role (for example, a `Buyer` must never access another buyer's data, only a `Seller` manages its own `Product` instances, and only an `Administrator` registers and administers `Warehouse` instances). This rule is enforced by `AuthorizationService` against the Authorization Matrix (see `Software Architecture.md` and DEC-04, DEC-05).
