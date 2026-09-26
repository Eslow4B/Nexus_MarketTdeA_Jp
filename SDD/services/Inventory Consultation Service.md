# Inventory Consultation Service

## Description

Retrieves current stock levels and movement history across warehouses (OBJ-06, read side). Kept separate from the five movement-writing services (`Stock Inbound`, `Stock Reservation`, `Stock Sale Outbound`, `Stock Adjustment`, `Stock Return`) because a consultation is not tied to any single movement type — it needs a consistent view regardless of which kind of movement produced the current state.

## Responsibilities

* Retrieve the inventory records of a product, a warehouse, or a specific product/warehouse pair, with their `availableQuantity` and `InventoryCondition`.
* Retrieve the `InventoryMovement` history of an inventory record, for traceability.

## Authorized Roles

Seller and Logistics Operator — same audience as the write side, per the Authorization Matrix ("Inventory administration"). A `Seller` only sees inventory of their own products; a `Logistics Operator` sees every warehouse (DEC-06).

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `consultInventory(filter)` | Retrieves inventory records by product and/or warehouse. | Caller is a `Seller` (results limited to their own products) or a `Logistics Operator`. | Returns the matching `Inventory` records (possibly empty). |
| `consultMovementHistory(inventoryId)` | Retrieves the `InventoryMovement` history for traceability. | Caller is the `Seller` who owns the product or a `Logistics Operator`. | Returns the record's movements, ordered by `movementDate`. |

## Dependencies (Output Ports)

* `InventoryRepository` (read-only usage)
* `InventoryMovementRepository` (read-only usage)
* [Authorization Service](Authorization%20Service.md)

## Business Rules Enforced

None of its own — pure consultation, scoped by RG-03.

## Exceptions

* `UnauthorizedOperationException`
* `ResourceNotFoundException` — raised by `consultMovementHistory` when the inventory record does not exist.
