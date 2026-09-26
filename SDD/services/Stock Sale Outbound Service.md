# Stock Sale Outbound Service

## Description

Confirms the definitive consumption of previously reserved stock once a sale is finalized (OBJ-06, `MovementType.SALE_OUTBOUND`). Invoked internally by [Payment Confirmation Service](Payment%20Confirmation%20Service.md) when an `Order` transitions to `PAID` — a fundamentally different trigger from a reservation being made or stock being manually adjusted.

## Responsibilities

* Record a `SALE_OUTBOUND` movement for each physical item of an order whose payment is confirmed, on the inventory record the item was reserved from.

## Authorized Roles

Not directly invoked by an external role — triggered internally by `PaymentConfirmationService`.

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `confirmSaleOutbound(order)` | Records the sale of every physical item of `order`. | `order` has just been confirmed as paid; each physical item has a `reservedInventory`, and a `RESERVATION` movement for that order and inventory exists and has not been released. | For each physical item, an `InventoryMovement` recorded with `movementType = SALE_OUTBOUND`, the item's quantity, its `reservedInventory`, and the `order`. `availableQuantity` is not changed: the units already left it at reservation time. |

## Dependencies (Output Ports)

* `InventoryMovementRepository`

## Business Rules Enforced

* Every stock change produces a traceable `InventoryMovement` (`InventoryMovement` Description, `Domain Model.md`) — this is the movement that closes the traceability chain opened by the earlier `RESERVATION`, matched through the order reference.

## Exceptions

None specific to this service — the preconditions it depends on (reservation existed, order is being paid) are already guaranteed by `PaymentConfirmationService` before this operation is invoked.
