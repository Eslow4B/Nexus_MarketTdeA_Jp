# Stock Inbound Service

## Description

Registers incoming stock into a warehouse (OBJ-06, `MovementType.INBOUND`; step 3 of the business flow, *"Se registran existencias iniciales en las bodegas asociadas"*). Split out of the former single `InventoryService` because each `MovementType` corresponds to a distinct, independently-triggered business event — an inbound delivery arriving at a warehouse has nothing in common, operationally, with a sale being confirmed or a manual correction being applied, beyond writing to the same `Inventory` row.

This is also the operation that creates the `Inventory` record of a product/warehouse pair: the first inbound of a product into a warehouse creates the record (DEC-13).

## Responsibilities

* Record an `INBOUND` movement when new stock arrives at a warehouse.
* Create the `Inventory` record, with `condition = AVAILABLE`, when the product has no record in that warehouse yet.

## Authorized Roles

Seller (owner of the product) and Logistics Operator — per the Authorization Matrix ("Inventory administration"). A Logistics Operator may operate any warehouse (DEC-06).

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `registerInboundStock(productId, warehouseId, quantity)` | Records an `INBOUND` movement, increasing `availableQuantity`. | Caller is the owning `Seller` or a `Logistics Operator`; the product is a `PhysicalProduct`; the warehouse is Marketplace-owned or owned by the product's seller (DEC-13); `quantity > 0`. | `Inventory.availableQuantity` increased (record created if it did not exist); `InventoryMovement` recorded with `movementType = INBOUND` and no order. |

## Dependencies (Output Ports)

* `InventoryRepository`
* `InventoryMovementRepository`
* `ProductRepository`
* `WarehouseRepository`
* [Authorization Service](Authorization%20Service.md)

## Business Rules Enforced

* "Available quantity must never become negative as a result of any inventory movement." (`Inventory` Business Rule, `Domain Model.md`) — trivially satisfied here since inbound only increases stock.
* Every stock change produces a traceable `InventoryMovement` (`InventoryMovement` Description, `Domain Model.md`).
* "Only PhysicalProduct participates in Inventory." (Domain Design Rules, `Domain Model.md`)
* DEC-13 — the warehouse must be Marketplace-owned or owned by the product's seller; this is an invariant of `Inventory` itself.
* Stock received into a `DAMAGED` record increases its quantity but stays non-reservable until the condition is changed (DEC-15).

## Exceptions

* `UnauthorizedOperationException`
* `ResourceNotFoundException`
* `DomainValidationException` — raised by `Inventory`/`InventoryMovement` (warehouse not allowed for the product, a digital product, or a non-positive quantity).
