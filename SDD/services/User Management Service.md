# User Management Service

## Description

Administers the information of every `User` in the Marketplace regardless of role (OBJ-01: *"Administrar la información de todos los usuarios del Marketplace"*). This is the broadest of the user-related services: it covers what `BuyerRegistrationService` and `SellerRegistrationService` do not — namely the internal, administrative-profile users (`ADMINISTRATOR`, `LOGISTICS_OPERATOR`, `SUPERVISOR`) and the operational status of `User` instances not already covered by a more specific administration service.

`Seller` status is explicitly out of scope here — it is owned by [Seller Administration Service](Seller%20Administration%20Service.md), so the two services never compete to write the same field. `changeUserStatus` in this service applies to `Buyer` and to the internal-profile roles (`ADMINISTRATOR`, `LOGISTICS_OPERATOR`, `SUPERVISOR`).

## Responsibilities

* Register internal-profile users (`ADMINISTRATOR`, `LOGISTICS_OPERATOR`, `SUPERVISOR`), which are represented directly as `User` per `Domain Model.md` and are not self-registered nor incorporated through `SellerRegistrationService`.
* Change the `UserStatus` (`ACTIVE`, `INACTIVE`, `BLOCKED`) of a `Buyer` or an internal-profile `User`.
* Change a `Buyer`'s `CommercialStatus` (`ACTIVE`, `RESTRICTED`, `SUSPENDED`), whose effects are fixed by DEC-07 — no other service writes this field: `BuyerRegistrationService` only sets it to `ACTIVE` at registration, and a `Buyer` cannot lift their own restriction.
* Consult `User` information across the platform, including `Seller` records (read-only; writes to `Seller` remain the responsibility of `Seller Registration Service`/`Seller Administration Service`).

## Authorized Roles

Administrator only — per the Authorization Matrix in `Software Architecture.md` ("User management (any role)", DEC-04).

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `registerInternalUser(id, fullName, email, role, username, password)` | Creates a new `User` with role `ADMINISTRATOR`, `LOGISTICS_OPERATOR`, or `SUPERVISOR`. `id` is the person's identity document (DEC-02). | Caller is `Administrator`; `role` is one of the three internal roles; `id`, `email`, `username` are unique (DEC-03). | New `User` persisted with `status = ACTIVE`. |
| `changeUserStatus(userId, newStatus)` | Updates the operational status of a `Buyer` or internal-profile `User`. | Caller is `Administrator`; the target `User` exists and is not a `Seller`; an `Administrator` cannot change their own status (DEC-26). | `User.status` updated. |
| `changeBuyerCommercialStatus(buyerId, newStatus)` | Updates a `Buyer`'s `CommercialStatus`. | Caller is `Administrator`; the target `Buyer` exists. | `Buyer.commercialStatus` updated. Orders already confirmed are not affected (DEC-07). |
| `consultUser(userId)` | Retrieves a single `User`'s information, of any role. | Caller is `Administrator`. | Returns `User` data. |
| `listUsers(filter)` | Retrieves `User` records matching a filter (by role or status). | Caller is `Administrator`. | Returns matching `User` records (possibly empty). |

## Dependencies (Output Ports)

* `UserRepository`
* `PasswordHasher` — hashes the initial password of an internal user before it is built; only the hash is stored.
* [Authorization Service](Authorization%20Service.md)

## Business Rules Enforced

* RG-02 — each `User` has exactly one `role`, enforced by construction: a plain `User` cannot be created with the `BUYER` or `SELLER` role.
* "The identity document (id), the email, and the username of a User must be unique across the platform." (`User` Business Rule, `Domain Model.md`)
* An `Administrator` cannot deactivate or block themselves, so the platform is never left without an active administrator by accident (DEC-26).

## Exceptions

* `UnauthorizedOperationException`
* `DuplicateUserDataException`
* `ResourceNotFoundException`
* `DomainValidationException` — raised by `User` itself (for example, a `BUYER`/`SELLER` role for an internal user, or an invalid email).
