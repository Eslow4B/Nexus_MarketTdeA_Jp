# Seller Administration Service

## Description

Administers already-incorporated `Seller` participants — the "administración" half of OBJ-02 ("Gestionar el registro **y** administración de vendedores"), distinct from the "registro" half handled by [Seller Registration Service](Seller%20Registration%20Service.md).

## Responsibilities

* Update a `Seller`'s information.
* Change a `Seller`'s `UserStatus`.
* Consult `Seller` records.

## Authorized Roles

Administrator only — per the Authorization Matrix ("Seller registration"), which governs the `Administrator`'s authority over `Seller` accounts end-to-end, not just their initial incorporation.

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `updateSeller(sellerId, fullName, email)` | Updates a `Seller`'s information. The identity document (`id`) and the role cannot change. | Caller is `Administrator`; the `Seller` exists; the new `email` is not used by another `User`. | `Seller` updated. |
| `changeSellerStatus(sellerId, newStatus)` | Activates, deactivates, or blocks a `Seller`. | Caller is `Administrator`; the `Seller` exists. | `Seller.status` updated. While the seller is not `ACTIVE`, their products are not available for sale (DEC-08). |
| `consultSeller(sellerId)` | Retrieves a `Seller`'s information. | Caller is `Administrator`. | Returns `Seller` data. |

## Dependencies (Output Ports)

* `UserRepository`
* [Authorization Service](Authorization%20Service.md)

## Business Rules Enforced

* RG-02 — each `User` has exactly one `role`, unaffected by administration.
* "The identity document (id), the email, and the username of a User must be unique across the platform." (`User` Business Rule, `Domain Model.md`) — re-verified on `email` update.
* DEC-08 — changing a seller's status never rewrites their products' `ProductStatus` and never affects orders already confirmed; it only changes whether their products are available for sale.

## Exceptions

* `DuplicateUserDataException`
* `UnauthorizedOperationException`
* `ResourceNotFoundException`
* `DomainValidationException`
