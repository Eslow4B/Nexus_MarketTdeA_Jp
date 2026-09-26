# Stock Adjustment Service

## Description

Applies manual corrections to `Inventory.availableQuantity` (OBJ-06, `MovementType.ADJUSTMENT`). This is also the service that releases a previously made reservation when an order is cancelled or its payment is rejected, per the documented decision in `Domain Value Objects.md` ("Reservation Release"): the catalog has no dedicated release code, so a release is recorded as an `ADJUSTMENT`. Grouping both under one service keeps that decision consistent — a release is, mechanically, exactly the same kind of operation as a manual correction.

## Responsibilities

* Apply a manual correction to `availableQuantity`, positive or negative, always stating its reason.
* Release the reservations of an order when [Order Cancellation Service](Order%20Cancellation%20Service.md) returns it to `CART`.
* Update an `Inventory` record's `InventoryCondition` (`AVAILABLE`/`DAMAGED`) — no other service writes this field, and marking stock as damaged after a physical inspection is the same kind of manual, non-automatic intervention as a quantity correction.

## Authorized Roles

`adjustStock` and `updateInventoryCondition` are invoked by the Seller who owns the product or by a Logistics Operator, per the Authorization Matrix ("Inventory administration"). `releaseReservation` is triggered internally by `OrderCancellationService`, not by a direct end-user call.

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `adjustStock(inventoryId, quantity, reason)` | Manual correction of `availableQuantity`. `quantity` is signed and not zero. | Caller is the owning `Seller` or a `Logistics Operator`; `reason` is not blank; `availableQuantity + quantity >= 0`. | `availableQuantity` corrected; `InventoryMovement` recorded with `movementType = ADJUSTMENT`, the signed `quantity`, and the `reason`. |
| `releaseReservation(order)` | Reverses the reservations of every physical item of `order`. | Triggered by `OrderCancellationService`; each physical item has a `reservedInventory` with a matching, unreleased `RESERVATION` movement for this order. | For each item, `availableQuantity` of its `reservedInventory` restored; `InventoryMovement` recorded with `movementType = ADJUSTMENT`, the positive quantity, and the `order`. |
| `updateInventoryCondition(inventoryId, newCondition, reason)` | Marks an `Inventory` record as `AVAILABLE` or `DAMAGED` after a physical inspection. | Caller is the owning `Seller` or a `Logistics Operator`; `reason` is not blank. | `Inventory.condition` updated; `InventoryMovement` recorded with `movementType = ADJUSTMENT`, `quantity = 0`, and the `reason`. |

## Dependencies (Output Ports)

* `InventoryRepository`
* `InventoryMovementRepository`
* [Authorization Service](Authorization%20Service.md)

## Business Rules Enforced

* "Available quantity must never become negative as a result of any inventory movement." (`Inventory` Business Rule, `Domain Model.md`)
* Reservation release is recorded as `ADJUSTMENT` referencing the order, not a dedicated movement code (`MovementType` > Reservation Release, `Domain Value Objects.md`).
* A manual adjustment must state its reason (invariant of `InventoryMovement`).
* Partially damaged stock is removed with a negative adjustment rather than marking the whole record `DAMAGED` (DEC-15).
* Changing the condition never affects reservations already made from the record.

## Exceptions

* `UnauthorizedOperationException`
* `ResourceNotFoundException`
* `InsufficientInventoryException` — raised when a negative adjustment would leave `availableQuantity` below zero.
* `DomainValidationException`
