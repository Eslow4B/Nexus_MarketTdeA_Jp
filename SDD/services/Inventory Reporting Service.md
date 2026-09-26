# Inventory Reporting Service

## Description

Produces a consolidated, read-only view of stock levels and `InventoryMovement` traceability across warehouses for the `Supervisor` role (OBJ-12). Split from a single `AdministrativeReportingService` for the same reason as `User Reporting Service`: disjoint subject matter, same role.

## Responsibilities

* Aggregate stock levels and movement traceability across warehouses for administrative consultation.

## Authorized Roles

Supervisor only — per the Authorization Matrix ("Administrative reporting").

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `consultInventorySummary(filter)` | Aggregated view of stock levels by warehouse, product, and `InventoryCondition`, and of `InventoryMovement` totals by `MovementType`. `filter` may restrict by warehouse, product, and a date range (`movementDate`). | Caller is `Supervisor`. | Returns aggregated data. |

## Dependencies (Output Ports)

* `InventoryRepository` (read-only usage)
* `InventoryMovementRepository` (read-only usage)
* [Authorization Service](Authorization%20Service.md)

## Business Rules Enforced

None of its own — pure consultation.

## Exceptions

* `UnauthorizedOperationException`
