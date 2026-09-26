# Warehouse Management Service

## Description

Controls the registration and administration of `Warehouse` records (OBJ-04), both Marketplace-owned and Seller-owned. A seller's first warehouse is registered together with the seller by [Seller Registration Service](Seller%20Registration%20Service.md) (DEC-09); this service registers every additional warehouse.

This service manages `Warehouse` metadata only. Day-to-day physical stock operation within a `Warehouse` is a separate concern handled by the Inventory services (see the "Inventory" group in `Domain Services.md`, starting with [Stock Inbound Service](Stock%20Inbound%20Service.md) and [Inventory Consultation Service](Inventory%20Consultation%20Service.md)), open to `Seller` and `Logistics Operator`.

## Responsibilities

* Register a new `Warehouse`, either Marketplace-owned or assigned to a `Seller`.
* Update `Warehouse` information (`name`, `address`). The owner never changes (DEC-10).
* Consult `Warehouse` records.

## Authorized Roles

* Registration and update: Administrator only — per the Participants table of the business specification ("Administrador: Responsable de la administración de vendedores y bodegas"), DEC-04.
* Consultation: Administrator (all warehouses); Logistics Operator (all warehouses, DEC-06); Seller (Marketplace-owned warehouses and their own, which are exactly the warehouses where they may store stock, DEC-13). Consultation is read-only and needed by the roles that administer inventory.

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `registerWarehouse(name, address, ownerSellerId)` | Creates a new `Warehouse`. `ownerSellerId` is optional — absent for a Marketplace-owned warehouse. | Caller is `Administrator`; if `ownerSellerId` is provided, it identifies an existing `Seller`. | New `Warehouse` persisted. |
| `updateWarehouse(warehouseId, name, address)` | Updates a `Warehouse`'s name and address. | Caller is `Administrator`; the `Warehouse` exists. | `Warehouse` updated; `owner` unchanged. |
| `consultWarehouse(warehouseId)` | Retrieves a single `Warehouse`'s information. | Caller is `Administrator`, `Logistics Operator`, or a `Seller` allowed to see it. | Returns `Warehouse` data. |
| `listWarehouses(filter)` | Retrieves `Warehouse` records matching a filter (e.g., by owner, or Marketplace-owned only). | Same as `consultWarehouse`; a `Seller` only receives the warehouses they are allowed to see. | Returns matching `Warehouse` records (possibly empty). |

## Dependencies (Output Ports)

* `WarehouseRepository`
* `UserRepository` — to validate the owning `Seller` when one is assigned.
* [Authorization Service](Authorization%20Service.md)

## Business Rules Enforced

* "A Warehouse may belong to zero or one Seller. When absent, the warehouse belongs to the Marketplace." (`Warehouse` Relationships, `Domain Model.md`)
* DEC-10 — the owner of a warehouse is fixed at registration.

## Exceptions

* `UnauthorizedOperationException`
* `ResourceNotFoundException`
* `DomainValidationException`
