# Catalog Consultation Service

## Description

Exposes the public product catalog for browsing (OBJ-05, read side; step 4 of the business flow, *"Los productos se hacen visibles en el catálogo público"*). Split from [Product Catalog Service](Product%20Catalog%20Service.md) because its authorization audience is materially broader: any authenticated `User` may browse the catalog, while only the owning `Seller` may modify it.

## Responsibilities

* Retrieve the products that are available for sale, filtered by name, product type (physical or digital), or `Seller`.
* Retrieve a single product's public information.
* Allow a `Seller` to see all of their own products, including those not available for sale.

## Authorized Roles

Any authenticated `User` — buyers to purchase, sellers to check competing listings or their own, logistics/administration to operate. Not tied to a single row of the Authorization Matrix because it is a read-only concern open to every role, unlike every write operation in this document set.

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `consultCatalog(filter)` | Retrieves the public catalog. `filter` may combine name, type, and seller. | Caller is any authenticated `User`. | Returns the matching products that are available for sale (`PUBLISHED` and seller `ACTIVE`, DEC-08). Possibly empty. |
| `consultProduct(productId)` | Retrieves a single product's public information. | Caller is any authenticated `User`; the product is available for sale, or the caller is its owning `Seller`. | Returns `Product` data. |
| `consultMyProducts(filter)` | Retrieves the calling seller's own products in any status. | Caller is a `Seller`. | Returns the seller's products. Possibly empty. |

## Dependencies (Output Ports)

* `ProductRepository` (read-only usage)
* [Authorization Service](Authorization%20Service.md) — only to identify the calling `Seller` for `consultMyProducts` and owner access in `consultProduct`.

## Business Rules Enforced

* Only products available for sale are shown in the public catalog (`Product.isAvailableForSale()`, DEC-08). A physical product without stock is still shown (DEC-12); stock is checked at order confirmation.
* Availability rules for purchasing are enforced when a `Product` is added to or confirmed in an `Order`, by `CartService` and `OrderConfirmationService`, not by browsing itself.

## Exceptions

* `ResourceNotFoundException` — raised by `consultProduct` when the product does not exist or is not visible to the caller. Browsing with filters returns an empty result instead.
