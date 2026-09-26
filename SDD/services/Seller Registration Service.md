# Seller Registration Service

## Description

Incorporates new `Seller` participants (OBJ-02, registration half). Its defining constraint is that a `Seller` cannot self-register — every `Seller` is created by an `Administrator`, consistent with step 1 of the business flow ("Incorporación: El Administrador registra al vendedor y su primera bodega").

Following that step literally, a seller is incorporated **together with their first warehouse**, in a single atomic operation (DEC-09). Additional warehouses are registered later through [Warehouse Management Service](Warehouse%20Management%20Service.md).

OBJ-02 literally reads *"Gestionar el registro **y** administración de vendedores"* — the objective itself names two distinct activities. Ongoing administration of an already-incorporated `Seller` (updates, status changes) is a separate concern, handled by [Seller Administration Service](Seller%20Administration%20Service.md).

## Responsibilities

* Register a new `Seller` on behalf of an `Administrator`.
* Register the seller's first `Warehouse`, owned by the new `Seller`, in the same operation.

## Authorized Roles

Administrator only — per the Authorization Matrix in `Software Architecture.md` and the explicit business rule below.

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `registerSeller(id, fullName, email, username, password, warehouseName, warehouseAddress)` | Creates a new `Seller` and their first `Warehouse`. `id` is the person's identity document (DEC-02). | Caller is `Administrator`; `id`, `email`, `username` are unique (DEC-03); warehouse name and address are provided. | New `Seller` persisted with `status = ACTIVE` and `role = SELLER`; new `Warehouse` persisted with `owner` = the new `Seller`. If either cannot be created, neither is persisted. |

## Dependencies (Output Ports)

* `UserRepository`
* `WarehouseRepository`
* `PasswordHasher` — hashes the initial password before the `Seller` is built; only the hash is stored.
* [Authorization Service](Authorization%20Service.md)

## Business Rules Enforced

* "A Seller cannot self-register. Sellers are incorporated into the platform by an Administrator, together with their first Warehouse." (`Seller` Business Rule, `Domain Model.md`; DEC-09)
* "The identity document (id), the email, and the username of a User must be unique across the platform." (`User` Business Rule, `Domain Model.md`)

## Exceptions

* `DuplicateUserDataException`
* `SellerSelfRegistrationNotAllowedException` — raised when the caller is authenticated but is not an `Administrator` attempting to create a `Seller` (in particular, a user trying to register themselves as a seller).
* `UnauthorizedOperationException`
* `DomainValidationException` — raised by `Seller` or `Warehouse` itself when mandatory data is missing or invalid.
