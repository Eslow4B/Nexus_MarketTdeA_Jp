# Domain Value Objects

## Introduction

Value Objects represent immutable concepts within the NexusMarket domain.

Unlike Entities, Value Objects do not have their own identity. They are defined entirely by their values and are used to encapsulate controlled business concepts, improve domain expressiveness, and prevent the use of primitive values or scattered string literals throughout the application.

The NexusMarket domain uses Value Objects for business catalogs such as roles, statuses, and movement types.

All business catalogs inherit from `DomainCatalog`.

---

# Value Object Hierarchy

```text
DomainCatalog (Abstract)
├── SystemRole
├── UserStatus
├── CommercialStatus
├── ProductStatus
├── InventoryCondition
├── MovementType
├── OrderStatus
├── ShipmentStatus
├── ReturnStatus
└── RefundStatus
```

---

# DomainCatalog (Abstract)

## Description

Represents a generic business catalog used throughout the NexusMarket domain.

`DomainCatalog` provides a consistent structure for controlled business values that require a code, a human-readable name, and a business description.

This class cannot be instantiated directly.

## Attributes

| Attribute   | Type   | Description                                           |
| ----------- | ------ | ----------------------------------------------------- |
| code        | String | Unique business identifier of the catalog value.      |
| name        | String | Human-readable name displayed within the application. |
| description | String | Business definition of the catalog value.             |

## Characteristics

* Immutable.
* Equality and hashCode are computed exclusively from the `code` attribute. `name` and `description` are descriptive metadata and must not participate in equality comparisons — two catalog values with the same `code` are the same value regardless of any difference in their descriptive text.
* Catalog values are controlled by the domain.
* Catalog values must not be represented by arbitrary strings throughout the application.
* Each catalog value must have a unique `code`.
* `toString()` returns the `code`.
* Every concrete catalog exposes `values()` (every allowed value) and `fromCode(String)` (the controlled instance for a given code). Adapters use `fromCode` to translate a persisted or transported code back into the Value Object; an unknown code raises `DomainValidationException` instead of producing an arbitrary value.

---

# SystemRole

## Description

Represents the responsibilities and permissions assigned to a person within NexusMarket.

The role is a characteristic of `User` because it represents what the person means within the system. Each participant has exactly one role (RG-02).

## Inherits From

`DomainCatalog`

## Allowed Values

| Code                | Name                | Description                                                              |
| ------------------- | ------------------- | ------------------------------------------------------------------------- |
| BUYER               | Buyer               | Person who purchases products published on the marketplace.              |
| SELLER              | Seller              | Person responsible for registering and managing their own products.      |
| ADMINISTRATOR       | Administrator       | Person responsible for administering sellers and warehouses.             |
| LOGISTICS_OPERATOR  | Logistics Operator  | Person responsible for the physical operation of warehouses and dispatches. |
| SUPERVISOR          | Supervisor          | Person with a consultation and operational monitoring profile.           |

---

# UserStatus

## Description

Represents the current operational status of a user within the marketplace.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code     | Name     | Description                                             |
| -------- | -------- | ----------------------------------------------------------- |
| ACTIVE   | Active   | User can access and operate on the platform normally.    |
| INACTIVE | Inactive | User exists but is not currently active on the platform.  |
| BLOCKED  | Blocked  | User access has been suspended.                          |

## Behavior

* `allowsAccess()` — `true` only for `ACTIVE`. `INACTIVE` and `BLOCKED` users cannot authenticate.

---

# CommercialStatus

## Description

Represents the commercial condition of a buyer for placing new orders.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code       | Name       | Description                                          |
| ---------- | ---------- | ------------------------------------------------------- |
| ACTIVE     | Active     | Buyer can manage the cart and place new orders normally. |
| RESTRICTED | Restricted | Buyer may manage the cart but cannot confirm new orders until a pending situation is resolved. |
| SUSPENDED  | Suspended  | Buyer can neither manage the cart nor confirm new orders. |

The business specification defines this attribute as mandatory but does not list its values or their effects; they are fixed by DEC-07 in [Business Decisions](Business%20Decisions.md). Orders already confirmed are never affected by a change of commercial status.

## Behavior

* `allowsCartManagement()` — `true` unless `SUSPENDED`.
* `allowsOrderConfirmation()` — `true` only for `ACTIVE`.

---

# ProductStatus

## Description

Represents the current status of a product within the catalog.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code          | Name          | Description                                             |
| -------------- | ------------- | ------------------------------------------------------------ |
| PUBLISHED      | Published     | Product is visible and available in the public catalog.  |
| SUSPENDED      | Suspended     | Product is temporarily hidden from the public catalog.    |
| DISCONTINUED   | Discontinued  | Product is permanently removed from commercialization.    |

The catalog has no "draft" value: a product is registered directly as `PUBLISHED`, and its seller may set it to `SUSPENDED` while it is not ready (DEC-12).

## Behavior

* `allowsNewOrders()` — `true` only for `PUBLISHED`. A product is available for sale only when this is `true` **and** its seller is active (DEC-08).

---

# InventoryCondition

## Description

Represents the physical condition of the stock tracked by an inventory record. Inventory marked as `DAMAGED` must never be reserved, regardless of the available quantity.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code      | Name      | Description                                             |
| ---------- | --------- | ------------------------------------------------------------ |
| AVAILABLE  | Available | Stock is in good condition and may be reserved or sold.  |
| DAMAGED    | Damaged   | Stock is damaged and must not be reserved or sold.        |

The condition applies to every unit of the record; when only some units are damaged, they are removed with a negative `ADJUSTMENT` instead (DEC-15).

## Behavior

* `isReservable()` — `true` only for `AVAILABLE`.

---

# MovementType

## Description

Represents the category of a significant change applied to an inventory record.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code             | Name             | Description                                                  |
| ----------------- | ---------------- | ------------------------------------------------------------------ |
| INBOUND           | Inbound          | Incoming stock registered into the warehouse.                  |
| RESERVATION       | Reservation      | Stock reserved as part of an order in progress.                 |
| SALE_OUTBOUND     | Sale Outbound    | Stock removed as a result of a completed sale.                  |
| ADJUSTMENT        | Adjustment       | Manual correction of the available quantity.                    |
| RETURN            | Return           | Stock reincorporated as a result of a completed return.          |

## Reservation Release

The catalog intentionally has no dedicated code for releasing a `RESERVATION` (when a payment is rejected or an Order in `PENDING_PAYMENT` is cancelled before completing the purchase — a `CART` holds no reservation). This case is recorded as an `ADJUSTMENT` movement that references the cancelled `Order` and restores the previously reserved quantity to `Inventory.availableQuantity`, keeping the catalog aligned with the five movement types defined by the business specification instead of introducing a sixth one.

## Movement Semantics

| Code          | Effect on `availableQuantity` | References an `Order` |
| ------------- | ----------------------------- | --------------------- |
| INBOUND       | Increases                     | No                    |
| RESERVATION   | Decreases (units set aside for the order) | Yes       |
| SALE_OUTBOUND | None — the units already left at reservation; it closes the traceability chain | Yes |
| ADJUSTMENT    | Signed correction (zero when only the condition changes) | Only when it releases a reservation; otherwise a reason is mandatory |
| RETURN        | Increases, on the inventory record the units originally left from (DEC-25) | Yes |

---

# OrderStatus

## Description

Represents the current stage of an order within its lifecycle.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code              | Name              | Description                                                |
| ------------------ | ----------------- | ----------------------------------------------------------------- |
| CART               | Cart              | Provisional selection of products, not yet confirmed.          |
| PENDING_PAYMENT    | Pending Payment   | Order confirmed and awaiting payment validation.                |
| PAID               | Paid              | Payment confirmed; preparation process may begin.                |
| SHIPPED            | Shipped           | Order has left the warehouse.                                    |
| DELIVERED          | Delivered         | Order has been successfully delivered to the buyer.               |

## Lifecycle

```text
CART
   │  ▲
   ▼  │ (cancellation / rejected payment — DEC-20)
PENDING_PAYMENT
   │
   ▼
 PAID
   │
   ├──[Order contains at least one physical item]──> SHIPPED ──> DELIVERED
   │
   └──[Order contains only digital items]───────────────────────> DELIVERED
```

`SHIPPED` describes a physical departure from a warehouse and only applies to an Order that contains at least one `PhysicalProduct`. An Order composed exclusively of `DigitalProduct` items skips `SHIPPED` and transitions directly from `PAID` to `DELIVERED` (see Order's "Business Rule — Digital Fulfillment Path" in Domain Model.md). When an order has several shipments, it becomes `SHIPPED` once all of them are `IN_TRANSIT` and `DELIVERED` once all of them are `DELIVERED` (DEC-21).

The only backward transition is `PENDING_PAYMENT → CART` (DEC-20). `DELIVERED` is terminal and means "Entregado / Finalizado" (DEC-19). There is no `CANCELLED` value, since the business specification defines exactly these five stages.

---

# ShipmentStatus

## Description

Represents the current logistics status of a shipment.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code        | Name        | Description                                        |
| ------------ | ----------- | -------------------------------------------------------- |
| PREPARING    | Preparing   | Products are being packed at the origin warehouse.  |
| IN_TRANSIT   | In Transit  | Shipment has left the warehouse and is en route.    |
| DELIVERED    | Delivered   | Shipment has been delivered to the buyer.            |

## Lifecycle

```text
PREPARING ──> IN_TRANSIT ──> DELIVERED
```

Forward only, one step at a time, advanced by the Logistics Operator. `PREPARING` corresponds to the order's *alistamiento* (the order is `PAID`); `IN_TRANSIT` is the physical departure from the warehouse.

---

# ReturnStatus

## Description

Represents the current status of a product return request.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code        | Name        | Description                                       |
| ------------ | ----------- | -------------------------------------------------------- |
| REQUESTED    | Requested   | Return has been requested by the buyer.               |
| APPROVED     | Approved    | Return has been reviewed and approved.                |
| REJECTED     | Rejected    | Return request has been denied.                        |
| COMPLETED    | Completed   | Returned product has been received and processed.       |

## Lifecycle

```text
REQUESTED ──> APPROVED ──> COMPLETED
    │
    └───────> REJECTED
```

`APPROVED` is a commercial decision; `COMPLETED` confirms the physical receipt of the product, which is when stock is reincorporated (DEC-25). `REJECTED` and `COMPLETED` are terminal.

---

# RefundStatus

## Description

Represents the current status of a refund associated with a completed return.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code       | Name       | Description                                     |
| ----------- | ---------- | ------------------------------------------------------ |
| PENDING     | Pending    | Refund has been opened for a completed return and awaits the Administrator's decision. |
| PROCESSED   | Processed  | Refund has been completed and funds returned.       |
| REJECTED    | Rejected   | Refund request has been denied.                     |

## Lifecycle

```text
PENDING ──> PROCESSED
   │
   └──────> REJECTED
```

A refund is opened in `PENDING` automatically when its return is `COMPLETED`, and the Administrator resolves it. Both `PROCESSED` and `REJECTED` are terminal. Rejecting a refund never reverts stock already reincorporated (DEC-25).

---

# Value Object Design Rules

## Immutability

All Value Objects must be immutable after creation. Their values cannot be modified after the object has been instantiated.

## Equality

For `DomainCatalog` values, equality is based exclusively on the business `code`, not on the full set of attributes. Two catalog instances with the same `code` represent the same Value Object even if `name` or `description` differ.

## Controlled Values

Business catalogs must use controlled values defined by the domain. The application must avoid replacing these concepts with arbitrary strings such as `"ACTIVE"`, `"BLOCKED"`, or `"PUBLISHED"` throughout the codebase. Instead, the corresponding Value Object must be used.

## Business Versus Technical Enumerations

A business concept should be modeled as a `DomainCatalog` Value Object when it requires a business code, a display name, a business description, and controlled domain evolution — as is the case for every catalog listed above.

## Behavior on Value Objects

A catalog may expose simple, side-effect-free questions about its own values (`allowsAccess()`, `allowsCartManagement()`, `allowsOrderConfirmation()`, `allowsNewOrders()`, `isReservable()`). This keeps each rule next to the value it describes, so services ask the Value Object instead of comparing codes themselves. These methods never read external state.

## Persisting Value Objects

Adapters persist a catalog value by its `code` only, and rebuild it with `fromCode(code)`. `name` and `description` belong to the domain and are never stored or read from the database.

## Relationship With Entities

Entities reference Value Objects rather than primitive strings whenever the referenced value represents a controlled business concept.

Examples:

```text
User.role : SystemRole

User.status : UserStatus

Buyer.commercialStatus : CommercialStatus

Product.status : ProductStatus

Inventory.condition : InventoryCondition

InventoryMovement.movementType : MovementType

Order.status : OrderStatus

Shipment.status : ShipmentStatus

ReturnRequest.status : ReturnStatus

Refund.status : RefundStatus
```

This approach improves type safety, domain expressiveness, maintainability, and consistency with Domain-Driven Design principles.
