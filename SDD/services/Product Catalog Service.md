# Product Catalog Service

## Description

Manages the registration, update, and lifecycle of `Product` entries — both `PhysicalProduct` and `DigitalProduct` (OBJ-05, write side).

Browsing the catalog is a distinct concern with a materially different authorization audience (any authenticated `User`, not just the owning `Seller`) and is handled separately by [Catalog Consultation Service](Catalog%20Consultation%20Service.md).

## Responsibilities

* Register a new `Product` owned by the calling `Seller`, with its type, variants, and price.
* Update a `Product`'s own information, `variants`, and `price`.
* Change a `Product`'s `ProductStatus` (`PUBLISHED`, `SUSPENDED`, `DISCONTINUED`).

## Authorized Roles

Seller-only, and restricted to `Product` instances the calling `Seller` itself owns — per the Authorization Matrix ("Product registration"). Unlike `WarehouseManagementService` (single-verb OBJ-04), these three operations stay in one service because they share the same actor, the same owned aggregate, and the same reason to change (the Seller managing their own listing) — only the read audience differs enough to warrant its own service.

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `publishProduct(name, description, variants, price, type)` | Creates a new `PhysicalProduct` or `DigitalProduct` (`type`) owned by the calling `Seller`. | Caller is an active `Seller`; `price > 0`. | New `Product` persisted with `status = PUBLISHED` (DEC-12). A physical product without stock is visible but cannot be reserved until stock is registered. |
| `updateProduct(productId, name, description, variants, price)` | Updates a `Product`'s own information and price. The type cannot change (DEC-12). | Caller is the owning `Seller`; the `Product` is not `DISCONTINUED`. | `Product` updated. Orders already confirmed keep their own `unitPrice` (DEC-16). |
| `changeProductStatus(productId, newStatus)` | Moves a `Product` between `PUBLISHED`, `SUSPENDED`, `DISCONTINUED`. | Caller is the owning `Seller`; the `Product` is not `DISCONTINUED`. | `Product.status` updated; orders already confirmed are not affected. |

## Dependencies (Output Ports)

* `ProductRepository`
* `UserRepository` — to obtain the calling `Seller`.
* [Authorization Service](Authorization%20Service.md)

## Business Rules Enforced

* "Product is abstract; PhysicalProduct and DigitalProduct represent genuine behavioral specializations... Only PhysicalProduct participates in Inventory." (Domain Design Rules, `Domain Model.md`)
* "A Product that is not available for sale must not be added to a new Order. Products already included in a confirmed Order are not affected retroactively by a later change of status or price." (`Product` Business Rule, `Domain Model.md` — enforced jointly with [Cart Service](Cart%20Service.md) and [Order Confirmation Service](Order%20Confirmation%20Service.md), which raise `ProductNotAvailableException`).
* `DISCONTINUED` is *"permanently removed from commercialization"* (`Domain Value Objects.md`), so a discontinued product can no longer be updated nor change status (DEC-27).
* DEC-11 (price), DEC-12 (registration as `PUBLISHED`, immutable type).

## Exceptions

* `UnauthorizedOperationException`
* `ResourceNotFoundException`
* `ProductNotAvailableException` — raised when attempting to update or change the status of a `DISCONTINUED` product.
* `DomainValidationException` — raised by `Product` itself (for example, a blank name or a price that is not greater than zero).
