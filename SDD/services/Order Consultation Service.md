# Order Consultation Service

## Description

Retrieves `Order` information (OBJ-08, read side). Kept separate from the four write services (`Order Confirmation`, `Payment Confirmation`, `Order Fulfillment Tracking`, `Order Cancellation`) because its audience spans all three participant roles of the "Gestión de Pedidos" row simultaneously, each with a different scope (DEC-05), whereas each write service is scoped to a single actor or trigger.

## Responsibilities

* Retrieve a single `Order`'s information, restricted to what the caller may see.
* Retrieve the list of orders relevant to the calling `Buyer`, `Seller`, or `Logistics Operator`.

## Authorized Roles

Buyer, Seller, and Logistics Operator — per the Authorization Matrix ("Order management (cart→delivered)"), each with its own scope (DEC-05):

| Role | Orders visible | Content visible |
| --- | --- | --- |
| Buyer | Their own orders, from `PENDING_PAYMENT` onward (the cart is consulted through `CartService`) | The whole order |
| Seller | Orders from `PENDING_PAYMENT` onward that contain at least one of their products | Only their own items; never other sellers' items or prices |
| Logistics Operator | Orders with physical products in `PAID`, `SHIPPED`, or `DELIVERED` | The physical items, the delivery address, and the status |

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `consultOrder(orderId)` | Retrieves a single order's information. | Caller may see the order per the table above. | Returns the `Order` data restricted to what the caller may see. |
| `consultMyOrders(filter)` | Retrieves the orders relevant to the calling user, optionally filtered by status. | Caller is `Buyer`, `Seller`, or `Logistics Operator`. | Returns the matching `Order` records (possibly empty), each restricted as above. |

## Dependencies (Output Ports)

* `OrderRepository` (read-only usage)
* [Authorization Service](Authorization%20Service.md)

## Business Rules Enforced

* Scoped by RG-03 and DEC-05.
* DEC-28 — this is where digital delivery becomes visible: for an order in `PAID` or later, its digital items are shown to the buyer as delivered and available, regardless of the status of its physical items.

## Exceptions

* `UnauthorizedOperationException`
* `ResourceNotFoundException` — raised by `consultOrder` when the order does not exist.
