# Shipment Creation Service

## Description

Creates the `Shipment` instances of a `PAID` `Order` containing physical items (OBJ-10, creation side). Triggered by an `Order` reaching `PAID` — a system event originating in `PaymentConfirmationService` — rather than by a `Logistics Operator` deciding, on their own initiative, to create a shipment. This trigger-origin difference is what separates it from [Shipment Tracking Service](Shipment%20Tracking%20Service.md), whose operations are genuinely manual, physical actions by the `Logistics Operator`.

Because each physical item is reserved from a single inventory record, and those records may be in different warehouses, one shipment is created **per origin warehouse** (DEC-21).

## Responsibilities

* Create one `Shipment` in `PREPARING` status for each warehouse the order's physical items were reserved from.

Creating shipments does **not** move the order to `SHIPPED`: `PREPARING` corresponds to the *alistamiento* of a `PAID` order (*"Pagado: Inicio de procesos de alistamiento"*), and the order only becomes `SHIPPED` when its goods physically leave (see [Order Fulfillment Tracking Service](Order%20Fulfillment%20Tracking%20Service.md)).

## Authorized Roles

Not directly invoked by an external role — triggered internally when `PaymentConfirmationService` confirms payment for an order containing physical items.

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `createShipments(order)` | Creates the shipments of a `PAID` order. | `Order.status = PAID`; the order contains at least one `PhysicalProduct`; no shipment exists yet for the order. | One new `Shipment` per `Order.getOriginWarehouses()`, each with `status = PREPARING` and `creationDate` = now. |

## Dependencies (Output Ports)

* `ShipmentRepository`

## Business Rules Enforced

* "A Shipment belongs to exactly one Order; an Order has one Shipment per origin warehouse." (`Shipment` Relationships, `Domain Model.md`; DEC-21)
* "A Shipment originates from one Warehouse", which must be one of the order's origin warehouses (invariant of `Shipment` itself).
* `SHIPPED` only applies to orders containing at least one physical item (Digital Fulfillment Path rule, `Order` Business Rule, `Domain Model.md`).

## Exceptions

None specific to this service — its preconditions (order `PAID` with physical items, created once) are guaranteed by `PaymentConfirmationService`.
