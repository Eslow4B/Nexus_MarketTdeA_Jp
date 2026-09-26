# Order Reporting Service

## Description

Produces a consolidated, read-only view of `Order` volume and status distribution for the `Supervisor` role (OBJ-12). Split from a single `AdministrativeReportingService` for the same reason as `User Reporting Service`: disjoint subject matter, same role.

## Responsibilities

* Aggregate `Order` records by `OrderStatus` for administrative consultation.

## Authorized Roles

Supervisor only — per the Authorization Matrix ("Administrative reporting").

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `consultOrderSummary(filter)` | Aggregated view of `Order` records: count by `OrderStatus` and invoiced amount (sum of `Invoice.totalAmount`). `filter` may restrict by status and a date range (`creationDate`). | Caller is `Supervisor`. | Returns aggregated data. |

## Dependencies (Output Ports)

* `OrderRepository` (read-only usage)
* `InvoiceRepository` (read-only usage)
* [Authorization Service](Authorization%20Service.md)

## Business Rules Enforced

None of its own — pure consultation.

## Exceptions

* `UnauthorizedOperationException`
