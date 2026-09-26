# Stock Reservation Service

## Description

Reserves stock on behalf of an order in progress (OBJ-06, `MovementType.RESERVATION`). Invoked internally by [Order Confirmation Service](Order%20Confirmation%20Service.md) when a cart is confirmed — not directly by an end user — since reservation is triggered by a specific point in the order lifecycle, not by a Seller/Logistics Operator deciding to reserve stock on their own initiative.

## Responsibilities

* Choose, for a physical `OrderItem`, the single inventory record its units will be reserved from (DEC-14).
* Reserve that quantity, rejecting the reservation if the stock is non-existent, insufficient, or `DAMAGED`.
* Record the `RESERVATION` movement, linked to the order.

## Authorized Roles

Not directly invoked by an external role — triggered internally by `OrderConfirmationService` as part of `confirmOrder`. `Software Architecture.md`'s "Inventory administration" row (Seller, Logistics Operator) governs who may adjust the underlying `Inventory` records this service reads, but the reservation action itself is system-orchestrated.

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `reserveStock(order, orderItem)` | Reserves `orderItem.quantity` units of its product for `order`. | `orderItem` is a physical item of `order`. | Among the product's `AVAILABLE` records with `availableQuantity >= quantity`, the one with the highest `availableQuantity` is chosen; its `availableQuantity` is decreased; an `InventoryMovement` is recorded with `movementType = RESERVATION` and the `order`. Returns the chosen `Inventory`, which `OrderConfirmationService` stores in `OrderItem.reservedInventory`. |

Selection rule and outcomes (DEC-14):

| Situation | Outcome |
| --- | --- |
| At least one `AVAILABLE` record has enough units | Reserve from the one with the highest `availableQuantity` |
| No record has enough units (including no record at all) | `InsufficientInventoryException` |
| Only `DAMAGED` records have enough units | `DamagedInventoryReservationException` |

## Dependencies (Output Ports)

* `InventoryRepository`
* `InventoryMovementRepository`

## Business Rules Enforced

* "Inventory that is non-existent (no record, or not enough available units) or marked as DAMAGED must not be reserved under any circumstance." (`Inventory` Business Rule, `Domain Model.md`; business specification §11)
* "Available quantity must never become negative as a result of any inventory movement." (`Inventory` Business Rule, `Domain Model.md`)
* An item is never split across several inventory records (DEC-14).

## Exceptions

* `InsufficientInventoryException`
* `DamagedInventoryReservationException`
