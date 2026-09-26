# Return Request Service

## Description

Registers a buyer's request to return products from a `DELIVERED` order (OBJ-11, request stage). The first of three genuinely distinct actors in the return process — the `Buyer` requesting, the `Administrator` reviewing, and the `Administrator` later confirming physical receipt — which is why the former `ReturnAndRefundService` is split by stage rather than kept as one class.

A return never modifies the order, which remains `DELIVERED` (finalized) forever; the return is a separate entity that references it (DEC-19).

## Responsibilities

* Create a `ReturnRequest` for a `DELIVERED` `Order`, listing the physical products and quantities returned.

## Authorized Roles

Buyer only — per the Authorization Matrix ("Return and refund management", DEC-04), restricted to the `Buyer` who placed the original order.

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `requestReturn(orderId, reason, items)` | Creates a `ReturnRequest`. `items` is a list of (`productId`, `quantity`). | Caller is the `Buyer` who placed the `Order`; `Order.status = DELIVERED`; the order has no previous `ReturnRequest`; every product is a physical product of the order, listed once, with `1 <= quantity <= purchased quantity`. | New `ReturnRequest` persisted with `status = REQUESTED` and `requestDate` = now. |

## Dependencies (Output Ports)

* `ReturnRequestRepository`
* `OrderRepository`
* [Authorization Service](Authorization%20Service.md)

## Business Rules Enforced

* "A ReturnRequest belongs to exactly one Order; an Order has at most one ReturnRequest." (`ReturnRequest` Relationships, `Domain Model.md`)
* A return may only be requested for an `Order` whose status is `DELIVERED`.
* DEC-24 — only physical products; quantities between 1 and the purchased quantity; no time limit, since the business specification defines none. The item rules are invariants of `ReturnRequest` itself.
* The buyer's `commercialStatus` does not restrict returns: it only governs new purchases (DEC-07).

## Exceptions

* `InvalidOrderStatusTransitionException` — raised when a return is requested for an `Order` that is not `DELIVERED`.
* `InvalidReturnStatusTransitionException` — raised when the order already has a return.
* `UnauthorizedOperationException`
* `ResourceNotFoundException`
* `DomainValidationException` — raised by `ReturnRequest` itself (digital or foreign product, repeated product, invalid quantity, no items, blank reason).
