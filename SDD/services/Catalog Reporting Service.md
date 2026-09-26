# Catalog Reporting Service

## Description

Produces a consolidated, read-only view of the product catalog for the `Supervisor` role (OBJ-12). Unlike [Catalog Consultation Service](Catalog%20Consultation%20Service.md), which only shows products available for sale, this report covers the whole catalog — including `SUSPENDED` and `DISCONTINUED` products and products of non-active sellers — which is the administrative information the Supervisor needs to follow the state of the offer. Split from the other reporting services for the same reason they are split from each other: it reads a disjoint aggregate (`Product`) for the same role (DEC-29).

## Responsibilities

* Aggregate products by `ProductStatus`, by type (physical or digital), and by seller.
* Count the products that are published but not available for sale because their seller is not active (DEC-08).

## Authorized Roles

Supervisor only — per the Authorization Matrix ("Administrative reporting").

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `consultCatalogSummary(filter)` | Aggregated view of the catalog: count by `ProductStatus`, by type, and by seller, and the number of products not available for sale. `filter` may restrict by seller, status, and type. | Caller is `Supervisor`. | Returns aggregated data. |

## Dependencies (Output Ports)

* `ProductRepository` (read-only usage)
* [Authorization Service](Authorization%20Service.md)

## Business Rules Enforced

None of its own — pure consultation.

## Exceptions

* `UnauthorizedOperationException`
