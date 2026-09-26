# User Reporting Service

## Description

Produces a consolidated, read-only view of `User` records for the `Supervisor` role (OBJ-12). Split from a single `AdministrativeReportingService` because each report reads from an entirely disjoint aggregate — `User`, `Inventory`, `Order`, and `ReturnRequest`/`Refund` share nothing except the `Supervisor` role and the fact that none of them mutate state, which is the same reasoning already used to split `InventoryService` by `MovementType`: same actor, unrelated subject matter.

## Responsibilities

* Aggregate `User` records by `role` and `UserStatus` for administrative consultation.

## Authorized Roles

Supervisor only — per the Authorization Matrix ("Administrative reporting"); the only use case assigned to this role in the entire business specification, alongside its three sibling reporting services.

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `consultUserSummary(filter)` | Aggregated view of `User` records: count by `role` and `UserStatus`, and buyers by `CommercialStatus`. `filter` may restrict by role or status. | Caller is `Supervisor`. | Returns aggregated data (read-only; never exposes passwords). |

## Dependencies (Output Ports)

* `UserRepository` (read-only usage)
* [Authorization Service](Authorization%20Service.md)

## Business Rules Enforced

None of its own — pure consultation.

## Exceptions

* `UnauthorizedOperationException`
