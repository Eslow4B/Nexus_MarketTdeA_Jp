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

---

# SystemRole

## Description

Represents the responsibilities and permissions assigned to a person within NexusMarket.

The role is a characteristic of `Person` because it represents what the person means within the system. Each participant has exactly one role.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code                | Name                | Description                                                              |
| ------------------- | ------------------- | ------------------------------------------------------------------------- |
| BUYER               | Buyer               | Person who purchases products published on the marketplace.              |
| SELLER              | Seller              | Person responsible for registering and managing products and warehouses. |
| ADMINISTRATOR       | Administrator       | Person responsible for incorporating sellers and managing warehouses.    |
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

---

# CommercialStatus

## Description

Represents the commercial condition of a buyer for placing new orders.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code       | Name       | Description                                          |
| ---------- | ---------- | ------------------------------------------------------- |
| ACTIVE     | Active     | Buyer can place new orders normally.                  |
| RESTRICTED | Restricted | Buyer has limitations due to a pending situation.      |
| SUSPENDED  | Suspended  | Buyer is temporarily prevented from placing new orders. |

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
| RETURN            | Return           | Stock reincorporated as a result of an approved return.          |

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
   │
   ▼
PENDING_PAYMENT
   │
   ▼
 PAID
   │
   ▼
SHIPPED
   │
   ▼
DELIVERED
```

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

---

# RefundStatus

## Description

Represents the current status of a refund associated with an approved return.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code       | Name       | Description                                     |
| ----------- | ---------- | ------------------------------------------------------ |
| PENDING     | Pending    | Refund has been requested and awaits processing.  |
| PROCESSED   | Processed  | Refund has been completed and funds returned.       |
| REJECTED    | Rejected   | Refund request has been denied.                     |

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

## Relationship With Entities

Entities reference Value Objects rather than primitive strings whenever the referenced value represents a controlled business concept.

Examples:

```text
Person.role : SystemRole

User.status : UserStatus

Buyer.commercialStatus : CommercialStatus

Product.status : ProductStatus

Inventory.condition : InventoryCondition

InventoryMovement.movementType : MovementType

Order.status : OrderStatus

Shipment.status : ShipmentStatus

Return.status : ReturnStatus

Refund.status : RefundStatus
```

This approach improves type safety, domain expressiveness, maintainability, and consistency with Domain-Driven Design principles.
