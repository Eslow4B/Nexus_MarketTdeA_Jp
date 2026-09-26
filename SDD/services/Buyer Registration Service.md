# Buyer Registration Service

## Description

Handles self-registration of `Buyer` participants (OBJ-03). It is the only registration service open to an unauthenticated caller, since a `Buyer` — unlike a `Seller` — registers itself rather than being incorporated by an `Administrator` (DEC-01).

## Responsibilities

* Register a new `Buyer` with initial `commercialStatus = ACTIVE` and `status = ACTIVE`.
* Allow a `Buyer` to update their own profile (`primaryAddress`, `additionalAddresses`).
* Allow a `Buyer` to consult their own profile.

## Authorized Roles

Open to an unauthenticated visitor for the registration operation itself (DEC-01). Once registered, a `Buyer` may only manage their own profile — per the Domain Model's own description: *"A buyer never manages information belonging to other buyers, warehouses, or seller inventories."*

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `registerBuyer(id, fullName, email, username, password, primaryAddress, additionalAddresses)` | Creates a new `Buyer`. `id` is the person's identity document (DEC-02). | `id`, `email`, `username` are unique (email and username case-insensitively, DEC-03); `primaryAddress` is provided. | New `Buyer` persisted with `role = BUYER`, `commercialStatus = ACTIVE`, `status = ACTIVE`, empty `additionalAddresses` unless provided. |
| `updateBuyerProfile(buyerId, primaryAddress, additionalAddresses)` | Updates a `Buyer`'s own addresses. | Caller is the same `Buyer` (self). | `Buyer` updated. Orders already confirmed keep the delivery address they were confirmed with (DEC-18). |
| `consultBuyerProfile(buyerId)` | Retrieves a `Buyer`'s own profile. | Caller is the same `Buyer` (self) or `Administrator`. | Returns `Buyer` data. |

## Dependencies (Output Ports)

* `UserRepository`
* `PasswordHasher` — hashes the submitted password before the `Buyer` is built; only the hash is stored.
* [Authorization Service](Authorization%20Service.md) — for `updateBuyerProfile`/`consultBuyerProfile` only; `registerBuyer` precedes authentication.

## Business Rules Enforced

* "The identity document (id), the email, and the username of a User must be unique across the platform." (`User` Business Rule, `Domain Model.md`)
* "A buyer never manages information belonging to other buyers, warehouses, or seller inventories." (`Buyer` Description, `Domain Model.md`)
* A buyer cannot change their own `commercialStatus` or `status`; only the `Administrator` does (`UserManagementService`).

## Exceptions

* `DuplicateUserDataException`
* `UnauthorizedOperationException`
* `ResourceNotFoundException`
* `DomainValidationException` — raised by `Buyer` itself when mandatory data is missing, blank, or the email format is invalid.
