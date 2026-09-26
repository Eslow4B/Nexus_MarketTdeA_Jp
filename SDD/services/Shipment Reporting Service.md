# Shipment Reporting Service

## Description

Produces a consolidated, read-only view of logistics activity for the `Supervisor` role (OBJ-12). The Supervisor is defined as a *"Perfil de consulta y seguimiento operativo"* (§5), and logistics — packing, dispatch, and transport (§6.1, step 7) — is the most operational part of the business; without this report the Supervisor could not follow whether paid orders are actually leaving the warehouses. Split from the other reporting services for the same reason they are split from each other: it reads a disjoint aggregate (`Shipment`) for the same role (DEC-29).

## Responsibilities

* Aggregate shipments by `ShipmentStatus` and origin warehouse.
* Highlight pending dispatches: shipments still `PREPARING` or `IN_TRANSIT`, with the time elapsed since their creation.

## Authorized Roles

Supervisor only — per the Authorization Matrix ("Administrative reporting").

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `consultShipmentSummary(filter)` | Aggregated view of shipments: count by `ShipmentStatus` and by origin warehouse, and the list of pending dispatches with their age. `filter` may restrict by origin warehouse, status, and a date range (`creationDate`). | Caller is `Supervisor`. | Returns aggregated data. |

## Dependencies (Output Ports)

* `ShipmentRepository` (read-only usage)
* [Authorization Service](Authorization%20Service.md)

## Business Rules Enforced

None of its own — pure consultation.

## Exceptions

* `UnauthorizedOperationException`
