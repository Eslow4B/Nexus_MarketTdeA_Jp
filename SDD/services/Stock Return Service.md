# Stock Return Service

## Description

Reincorporates returned physical stock into `Inventory` (OBJ-06, `MovementType.RETURN`). Invoked internally by [Return Completion Service](Return%20Completion%20Service.md) once physical receipt of the returned product is confirmed — not at mere commercial approval, since counting stock back into `availableQuantity` before it has actually arrived would let it be sold again before it exists in the warehouse.

## Responsibilities

* Record a `RETURN` movement for each returned item, increasing `availableQuantity` of the inventory record the units originally left from (DEC-25).

## Authorized Roles

Not directly invoked by an external role — triggered internally by `ReturnCompletionService` once physical receipt of a `ReturnRequest` is confirmed.

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `registerReturnStock(returnRequest)` | Reincorporates the returned units. | Triggered by `ReturnCompletionService` upon confirmed physical receipt. | For each `ReturnItem`, the `reservedInventory` of the matching `OrderItem` has its `availableQuantity` increased by the returned quantity; `InventoryMovement` recorded with `movementType = RETURN` and the order. |

## Dependencies (Output Ports)

* `InventoryRepository`
* `InventoryMovementRepository`

## Business Rules Enforced

* Every stock change produces a traceable `InventoryMovement` (`InventoryMovement` Description, `Domain Model.md`).
* Returned units go back to the same inventory record they were sold from, closing the traceability chain `RESERVATION → SALE_OUTBOUND → RETURN` for that order (DEC-25).
* Only physical products are returned (DEC-24), so every returned item has a `reservedInventory`.

## Exceptions

None specific to this service — receipt-confirmation preconditions are already validated by `ReturnCompletionService` before this operation is invoked.
