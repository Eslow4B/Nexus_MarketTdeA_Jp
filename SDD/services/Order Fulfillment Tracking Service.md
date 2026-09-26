# Order Fulfillment Tracking Service

## Description

Advances an `Order` through its physical fulfillment stages, `PAID` → `SHIPPED` → `DELIVERED` (OBJ-08, physical fulfillment). Unlike every other Order-related write service, this one is not triggered by the Buyer or by a payment event — it is triggered entirely by [Shipment Tracking Service](Shipment%20Tracking%20Service.md) reporting progress on the order's shipments, which makes its "reason to change" fundamentally different from `OrderConfirmationService`, `PaymentConfirmationService`, or `OrderCancellationService`.

Because an order may have one shipment per origin warehouse (DEC-21), the order's status reflects **all** its shipments, not a single one.

## Responsibilities

* Move an `Order` from `PAID` to `SHIPPED` when all its shipments have physically left their warehouses (`IN_TRANSIT` or `DELIVERED`).
* Move an `Order` from `SHIPPED` to `DELIVERED` when all its shipments are `DELIVERED`.

## Authorized Roles

Not directly invoked by an external role — triggered internally by `ShipmentTrackingService` every time one of the order's shipments advances.

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `onShipmentAdvanced(orderId)` | Re-evaluates the order's status from the current status of all its shipments. | Called by `ShipmentTrackingService`; the order contains physical products. | If `Order.status = PAID` and every shipment is `IN_TRANSIT` or later → `Order.status = SHIPPED`. If `Order.status = SHIPPED` and every shipment is `DELIVERED` → `Order.status = DELIVERED`. Otherwise, no change. Both steps may apply in sequence (e.g., the last shipment goes directly from `IN_TRANSIT` to `DELIVERED`). |

## Dependencies (Output Ports)

* `OrderRepository`
* `ShipmentRepository` (read-only usage) — to obtain every shipment of the order.

## Business Rules Enforced

* "SHIPPED ... only applies to an Order that contains at least one PhysicalProduct." (`Order` Business Rule — Digital Fulfillment Path, `Domain Model.md`)
* DEC-21 — *"Despachado: Salida física de la bodega"*: the order is `SHIPPED` only when all its goods have left their warehouses, and `DELIVERED` only when all shipments are delivered.
* "An Order in DELIVERED status is finalized and must not be modified under any circumstance." (`Order` Business Rule, `Domain Model.md`) — enforced here as the terminal write to `Order.status`.

## Exceptions

* `InvalidOrderStatusTransitionException`
* `OrderAlreadyDeliveredException`
