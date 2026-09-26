# Shipment Consultation Service

## Description

Retrieves `Shipment` information (OBJ-10, consultation side). Split out because its audience is broader than either write-side service alone: the `Logistics Operator` who handles it, the `Buyer` awaiting delivery, and the `Administrator` overseeing operations.

## Responsibilities

* Retrieve a `Shipment`'s current status and details, including the items it carries.
* Retrieve every shipment of an order.
* List shipments by status, so a Logistics Operator can see pending work.

## Authorized Roles

Logistics Operator (every shipment, DEC-06), the owning `Buyer` (shipments of their own orders), and `Administrator`.

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `consultShipment(shipmentId)` | Retrieves `Shipment` information. | Caller is a `Logistics Operator`, the owning `Buyer`, or `Administrator`. | Returns `Shipment` data and its items. |
| `consultOrderShipments(orderId)` | Retrieves every shipment of an order. | Same as `consultShipment`. | Returns the order's shipments (empty for digital-only orders). |
| `listShipments(filter)` | Lists shipments filtered by status or origin warehouse. | Caller is a `Logistics Operator` or `Administrator`. | Returns matching shipments (possibly empty). |

## Dependencies (Output Ports)

* `ShipmentRepository` (read-only usage)
* [Authorization Service](Authorization%20Service.md)

## Business Rules Enforced

None of its own — pure consultation.

## Exceptions

* `UnauthorizedOperationException`
* `ResourceNotFoundException`
