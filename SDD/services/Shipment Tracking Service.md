# Shipment Tracking Service

## Description

Advances a `Shipment` through its physical logistics stages, `PREPARING` → `IN_TRANSIT` → `DELIVERED` (OBJ-10, tracking side; step 7 of the business flow, *"Se realiza el empaque, despacho y transporte del pedido"*). Unlike [Shipment Creation Service](Shipment%20Creation%20Service.md), which is system-triggered, this is a genuinely manual action performed by the `Logistics Operator` as they physically pack, dispatch, and deliver the shipment — the Participants table describes exactly this role ("Encargado de la operación física de bodegas y despachos").

## Responsibilities

* Advance `ShipmentStatus` exactly one step forward: `PREPARING` → `IN_TRANSIT` (the goods physically leave the warehouse), `IN_TRANSIT` → `DELIVERED` (delivery confirmed).
* Notify `OrderFulfillmentTrackingService` after every advance, so the order's status reflects all its shipments (DEC-21).

## Authorized Roles

Logistics Operator only — per the Authorization Matrix ("Shipment execution", DEC-04). Any Logistics Operator may advance any shipment (DEC-06).

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `advanceShipmentStatus(shipmentId, newStatus)` | Moves the `Shipment` one step forward. | Caller is a `Logistics Operator`; `newStatus` is exactly the next status of the shipment's current one. | `Shipment.status` updated; `OrderFulfillmentTrackingService.onShipmentAdvanced` invoked for the associated `Order`. |

## Dependencies (Output Ports)

* `ShipmentRepository`
* [Order Fulfillment Tracking Service](Order%20Fulfillment%20Tracking%20Service.md)
* [Authorization Service](Authorization%20Service.md)

## Business Rules Enforced

* "A Shipment belongs to exactly one Order." (`Shipment` Relationships, `Domain Model.md`)
* `ShipmentStatus` lifecycle: forward only, one step at a time (`Domain Value Objects.md`). A `DELIVERED` shipment cannot change.

## Exceptions

* `InvalidShipmentStatusTransitionException`
* `UnauthorizedOperationException`
* `ResourceNotFoundException`
