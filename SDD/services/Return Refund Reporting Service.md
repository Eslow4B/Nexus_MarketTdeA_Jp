# Return Refund Reporting Service

## Description

Produces a consolidated, read-only view of `ReturnRequest` and `Refund` activity for the `Supervisor` role (OBJ-12). Split from a single `AdministrativeReportingService` for the same reason as `User Reporting Service`: disjoint subject matter, same role.

## Responsibilities

* Aggregate `ReturnRequest`/`Refund` activity for administrative consultation.

## Authorized Roles

Supervisor only — per the Authorization Matrix ("Administrative reporting").

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `consultReturnRefundSummary(filter)` | Aggregated view of `ReturnRequest` count by `ReturnStatus` and `Refund` count and amount by `RefundStatus`. `filter` may restrict by status and a date range (`requestDate` / `creationDate`). | Caller is `Supervisor`. | Returns aggregated data. |

## Dependencies (Output Ports)

* `ReturnRequestRepository` (read-only usage)
* `RefundRepository` (read-only usage)
* [Authorization Service](Authorization%20Service.md)

## Business Rules Enforced

None of its own — pure consultation.

## Exceptions

* `UnauthorizedOperationException`
