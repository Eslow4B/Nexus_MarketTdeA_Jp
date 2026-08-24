# Domain Model

## Introduction

The Domain Model represents the core business entities of NexusMarket, a digital marketplace that intermediates commercial transactions between buyers and sellers. These entities encapsulate the business rules, data, relationships, and lifecycle concepts described in the system specification.

The model follows Object-Oriented Design and Domain-Driven Design (DDD) principles. Inheritance is used to represent genuine domain specialization, while explicit object relationships are preferred over generic identifier fields.

The model distinguishes between:

* **Persons**, which represent identifiable people participating in the marketplace.
* **Users**, which represent the participants of the marketplace and their assigned role.
* **Products**, which represent the goods offered for sale, either physical or digital.
* **Warehouses and Inventory**, which represent the physical storage and stock management of physical products.
* **Orders**, which represent the central commercial process of the marketplace.
* **Invoices, Shipments, Returns and Refunds**, which represent the commercial and logistics processes that follow an order.

An order generates an invoice once payment is confirmed, may generate a shipment when it includes physical products, and may later generate a return and a subsequent refund.

---

# Domain Class Hierarchy

```text
Person (Abstract)
└── User
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
Return
Refund
```

---

# Domain Relationships

```text
Person
   │
   └── User
          ├── Buyer
          └── Seller

Seller
   │
   ├── manages ─────────────> Warehouse
   └── publishes ────────────> Product

Warehouse
   │
   └── owner : Seller (optional — absent for Marketplace-owned warehouses)

Product
   │
   └── seller : Seller

Inventory
   │
   ├── product : PhysicalProduct
   └── warehouse : Warehouse
          │
          └── generates ─────> InventoryMovement

Buyer
   │
   └── places ───────────────> Order

Order
   │
   ├── buyer : Buyer
   ├── items : List<OrderItem>
   ├── generates ─────────────> Invoice
   ├── generates ─────────────> Shipment (when it contains physical products)
   └── may generate ──────────> Return

OrderItem
   │
   └── product : Product

Return
   │
   ├── order : Order
   └── may generate ──────────> Refund
```

---

# Entities

---

# Person (Abstract)

## Description

Represents any identifiable person interacting with NexusMarket.

This abstract class centralizes the identity information shared by every participant of the marketplace, regardless of their role.

The role assigned to a person represents their responsibilities and permissions within the system.

This class cannot be instantiated directly.

## Attributes

| Attribute | Type       | Description                                                                    |
| --------- | ---------- | ------------------------------------------------------------------------------- |
| id        | String     | Unique identity document number of the person. Must be unique across the platform. |
| fullName  | String     | Full name of the person.                                                        |
| email     | String     | Primary email address, used for access and communication. Must be unique.       |
| role      | SystemRole | Defines the person's responsibilities and permissions within the marketplace.   |

## Relationships

* A `Person` is specialized as a `User` for participation within the marketplace.
* The `role` belongs to `Person` because it represents the person's meaning and responsibilities within the system, and is inherited by `User` and its specializations.

## Business Rule

```text
The identity document (id) and the email of a Person must be unique
across the platform.
```

---

# User

## Description

Represents a participant of NexusMarket who interacts with the platform according to the responsibilities defined by their role.

Participants whose role does not require additional attributes or relationships (`ADMINISTRATOR`, `LOGISTICS_OPERATOR`, `SUPERVISOR`) are represented directly as `User` instances. Participants whose role requires additional attributes or relationships (`BUYER`, `SELLER`) are represented by a specialized subclass.

## Inherits From

`Person`

## Attributes

| Attribute | Type       | Description                                                     |
| --------- | ---------- | ------------------------------------------------------------------ |
| status    | UserStatus | Current operational status of the user within the marketplace.  |

## Relationships

* A `User` inherits identity and role information from `Person`.
* A `User` may be specialized as `Buyer` or `Seller` when their role requires additional attributes or relationships.
* A `User` whose role is `ADMINISTRATOR`, `LOGISTICS_OPERATOR`, or `SUPERVISOR` is represented directly by this class, without further specialization.

---

# Buyer

## Description

Represents a user who purchases products published on NexusMarket.

A buyer never manages information belonging to other buyers, warehouses, or seller inventories.

## Inherits From

`User`

## Attributes

| Attribute            | Type              | Description                                              |
| --------------------- | ----------------- | ---------------------------------------------------------- |
| primaryAddress        | String            | Habitual address used for order deliveries.              |
| additionalAddresses   | List\<String\>    | Secondary delivery addresses. Empty by default.           |
| commercialStatus      | CommercialStatus  | Condition of the buyer for placing new orders.            |

## Relationships

* A `Buyer` places zero or more `Order` instances.

---

# Seller

## Description

Represents a user responsible for registering and managing products and warehouses on NexusMarket.

Sellers cannot self-register; they are incorporated into the platform by an `Administrator`.

A seller has no additional attributes of its own beyond those inherited from `User`. What distinguishes a seller within the domain is the set of entities it owns — its warehouses and its products — rather than additional scalar attributes.

## Inherits From

`User`

## Relationships

* A `Seller` manages zero or more `Warehouse` instances.
* A `Seller` publishes zero or more `Product` instances.

## Business Rule

```text
A Seller cannot self-register. Sellers are incorporated into the
platform by an Administrator.
```

---

# Product (Abstract)

## Description

Represents a good offered for sale on NexusMarket, published by a seller.

Physical products require inventory tracking and dispatch, while digital products are delivered immediately after payment confirmation. This behavioral difference is represented through specialization rather than through a type attribute.

This class cannot be instantiated directly.

## Attributes

| Attribute    | Type            | Description                                                              |
| ------------- | --------------- | --------------------------------------------------------------------------- |
| id            | String          | Unique identifier of the product.                                        |
| name          | String          | Commercial name of the product.                                          |
| description   | String          | Description of the product shown to buyers.                              |
| variants      | List\<String\>  | Variations of the product, such as color, size, or model. Empty by default. |
| status        | ProductStatus   | Current status of the product within the catalog.                        |
| seller        | Seller          | Seller who owns and publishes the product.                               |

## Relationships

* A `Product` is published by one `Seller`.
* A `Product` may be referenced by zero or more `OrderItem` instances.

---

# PhysicalProduct

## Description

Represents a tangible product that requires inventory tracking and physical dispatch to be delivered to the buyer.

## Inherits From

`Product`

## Relationships

* A `PhysicalProduct` is tracked through `Inventory` records across one or more warehouses.

---

# DigitalProduct

## Description

Represents a product delivered electronically and immediately after payment confirmation, without requiring inventory or physical dispatch.

## Inherits From

`Product`

---

# Warehouse

## Description

Represents a physical location where product inventory is stored and managed.

A warehouse may belong to the Marketplace itself or to a specific seller.

## Attributes

| Attribute | Type     | Description                                                                     |
| --------- | -------- | --------------------------------------------------------------------------------- |
| id        | String   | Unique identifier of the warehouse.                                             |
| name      | String   | Descriptive name of the warehouse.                                              |
| address   | String   | Physical location of the warehouse.                                             |
| owner     | Seller?  | Seller who owns the warehouse. Absent when the warehouse belongs to the Marketplace. |

## Relationships

* A `Warehouse` may belong to zero or one `Seller`. When absent, the warehouse belongs to the Marketplace.
* A `Warehouse` holds zero or more `Inventory` records.

---

# Inventory

## Description

Represents the available stock of a physical product within a specific warehouse.

Inventory must always be linked to exactly one product and one warehouse. Negative stock is never allowed under any circumstance.

## Attributes

| Attribute          | Type              | Description                                              |
| ------------------- | ----------------- | ------------------------------------------------------------ |
| id                  | String              | Unique identifier of the inventory record.                |
| product             | PhysicalProduct     | Physical product tracked by this inventory record.        |
| warehouse           | Warehouse           | Warehouse where the stock is stored.                       |
| availableQuantity   | Integer             | Current quantity available for sale. Must never be negative. |
| condition           | InventoryCondition  | Current condition of the stock tracked by this record.     |

## Relationships

* An `Inventory` record is linked to exactly one `PhysicalProduct`.
* An `Inventory` record is linked to exactly one `Warehouse`.
* An `Inventory` record may generate multiple `InventoryMovement` instances.

## Business Rule

```text
Available quantity must never become negative as a result of any inventory movement.

Inventory that is non-existent (availableQuantity = 0) or marked as DAMAGED
must not be reserved under any circumstance.
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
| quantity        | Integer        | Quantity involved in the movement.                 |
| movementDate    | LocalDateTime  | Date and time when the movement occurred.          |

## Relationships

* An `InventoryMovement` affects exactly one `Inventory` record.

---

# Order

## Description

Represents a purchase commitment made by a buyer. Its lifecycle is the central business process of NexusMarket.

## Attributes

| Attribute    | Type              | Description                                      |
| ------------- | ----------------- | ---------------------------------------------------- |
| id            | String            | Unique identifier of the order.                    |
| buyer         | Buyer             | Buyer who placed the order.                        |
| items         | List\<OrderItem\> | Products and quantities included in the order.     |
| status        | OrderStatus       | Current stage of the order lifecycle.               |
| creationDate  | LocalDateTime     | Date and time when the order was created.           |

## Relationships

* An `Order` is placed by one `Buyer`.
* An `Order` contains one or more `OrderItem` instances.
* An `Order` generates one `Invoice` once payment is confirmed.
* An `Order` may generate one `Shipment` when it contains physical products.
* An `Order` may generate one `Return` after being delivered.

## Business Rule

```text
An Order in DELIVERED status is finalized and must not be modified
under any circumstance.
```

---

# OrderItem

## Description

Represents a single product and quantity included within an order, together with the unit price at the moment of purchase.

## Attributes

| Attribute  | Type       | Description                                              |
| ----------- | ---------- | ------------------------------------------------------------ |
| product     | Product    | Product included in the order.                            |
| quantity    | Integer    | Quantity of the product requested.                         |
| unitPrice   | BigDecimal | Price of the product at the moment the order was placed.  |

## Relationships

* An `OrderItem` references exactly one `Product`.

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
| totalAmount   | BigDecimal    | Total amount billed for the order.       |

## Relationships

* An `Invoice` belongs to exactly one `Order`.

---

# Shipment

## Description

Represents the logistics process required to deliver the physical products of an order from a warehouse to the buyer.

## Attributes

| Attribute        | Type            | Description                                          |
| ----------------- | --------------- | --------------------------------------------------------- |
| id                | String          | Unique identifier of the shipment.                     |
| order             | Order           | Order being shipped.                                    |
| originWarehouse   | Warehouse       | Warehouse from which the products are dispatched.       |
| status            | ShipmentStatus  | Current logistics status of the shipment.               |

## Relationships

* A `Shipment` belongs to exactly one `Order`.
* A `Shipment` originates from one `Warehouse`.

---

# Return

## Description

Represents a buyer's request to return one or more products from a delivered order.

## Attributes

| Attribute | Type          | Description                                       |
| --------- | ------------- | ------------------------------------------------------ |
| id        | String        | Unique identifier of the return.                    |
| order     | Order         | Order associated with the return.                    |
| reason    | String        | Reason provided by the buyer for the return.         |
| status    | ReturnStatus  | Current status of the return process.                |

## Relationships

* A `Return` belongs to exactly one `Order`.
* A `Return` may generate one `Refund`.

---

# Refund

## Description

Represents the reimbursement of funds to a buyer as a result of an approved return.

## Attributes

| Attribute | Type          | Description                                |
| --------- | ------------- | ---------------------------------------------- |
| id        | String        | Unique identifier of the refund.            |
| return    | Return        | Return that originated the refund.           |
| amount    | BigDecimal    | Amount reimbursed to the buyer.              |
| status    | RefundStatus  | Current status of the refund process.        |

## Relationships

* A `Refund` belongs to exactly one `Return`.

---

# Domain Design Rules

## Person and User

* `User` inherits from `Person`.
* `role` is defined in `Person` and inherited by `User` and its specializations.
* Unlike `Person`, `User` is not abstract: it is instantiated directly for roles that require no additional attributes (`ADMINISTRATOR`, `LOGISTICS_OPERATOR`, `SUPERVISOR`).

## Buyer and Seller

* `Buyer` and `Seller` are specializations of `User`, created because their roles require additional attributes or relationships not shared by every user.
* `Seller` is justified as a class primarily by its relationships (ownership of `Warehouse` and `Product` instances), not by scalar attributes.

## Products

* `Product` is abstract; `PhysicalProduct` and `DigitalProduct` represent genuine behavioral specializations rather than a simple type flag.
* Only `PhysicalProduct` participates in `Inventory`.

## Warehouses and Inventory

* A `Warehouse` distinguishes Marketplace-owned from Seller-owned warehouses through an optional `owner` relationship, rather than a type attribute.
* `Inventory` availability must never become negative.
* `InventoryMovement` records provide traceability for stock changes, following the same operation-traceability pattern used across the domain.

## Orders and Post-Sale Processes

* `Order` is the aggregate root of the purchase process and owns its `OrderItem` collection.
* `Invoice`, `Shipment`, `Return`, and `Refund` are separate entities generated from an `Order`, reflecting distinct business processes (billing, logistics, and post-sale) rather than attributes of `Order`.

## General Business Rules

The following rules apply across the domain rather than to a single entity:

* **RG-02** — Each person has exactly one role. For this reason, `Person.role` is modeled as a single `SystemRole` value rather than a collection.
* **RG-01** — Every operation on the platform must be executed by an authenticated user. This rule constrains system access and will be enforced by the Authentication and Authorization services; it does not require a dedicated domain entity.
* **RG-03** — No participant may manage information outside the scope of their role (for example, a `Buyer` must never access another buyer's data, and only a `Seller` manages its own `Warehouse` and `Product` instances). This rule will be enforced by the Authorization services documented in the next deliverable.