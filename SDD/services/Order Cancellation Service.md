# Order Cancellation Service

## Description

Reverts a `PENDING_PAYMENT` `Order` back to `CART` (OBJ-08, cancellation path). `OrderStatus` intentionally has no `CANCELLED` value — its five codes are the exact ones defined by the business specification's "Ciclo de Estados del Pedido," and this service does not introduce a sixth one, for the same reason `Domain Value Objects.md` reuses `ADJUSTMENT` instead of adding a dedicated reservation-release code (DEC-20). Abandoning a purchase before paying is therefore modeled as a return to the pre-commitment state, not as a new terminal status: the `Order` was never a commercial commitment until it left `CART`, so undoing that commitment simply undoes the transition.

A cart that was never confirmed needs no cancellation at all — its `Buyer` can just stop interacting with it, or empty it via [Cart Service](Cart%20Service.md). This service only applies once an `Order` has moved to `PENDING_PAYMENT` and reserved inventory that must be released, either because the `Buyer` backs out or because the payment was rejected.

## Responsibilities

* Revert an `Order` from `PENDING_PAYMENT` back to `CART`.
* Release the inventory reservations made at confirmation via `StockAdjustmentService.releaseReservation`.
* Clear the fields that only make sense for a confirmed order (each item's `reservedInventory` and the `deliveryAddress`), so the cart can be confirmed again later with fresh prices and reservations (DEC-16).

## Authorized Roles

* `cancelOrder`: Buyer only — per the Authorization Matrix ("Order management (cart→delivered)"), restricted to the owning `Buyer` backing out of their own pending purchase.
* `revertPendingOrder`: not invoked by an external role — triggered internally by `PaymentConfirmationService.rejectPayment` (DEC-22).

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `cancelOrder(orderId)` | The buyer cancels a pending order. | Caller is the owning `Buyer`; `Order.status = PENDING_PAYMENT`. | Same as `revertPendingOrder`. |
| `revertPendingOrder(orderId)` | Reverts a pending order after a rejected payment. | `Order.status = PENDING_PAYMENT`; triggered by `PaymentConfirmationService`. | Reservations released via `StockAdjustmentService` (`ADJUSTMENT` movements referencing the order); items' `reservedInventory` and `deliveryAddress` cleared; `Order.status = CART`. |

From `PAID` onward an order cannot be cancelled: the post-sale path is a return ([Return Request Service](Return%20Request%20Service.md)).

## Dependencies (Output Ports)

* `OrderRepository`
* [Stock Adjustment Service](Stock%20Adjustment%20Service.md)
* [Authorization Service](Authorization%20Service.md)

## Business Rules Enforced

* "An Order in DELIVERED status is finalized and must not be modified under any circumstance." (`Order` Business Rule, `Domain Model.md`)
* Reservation release is recorded as `ADJUSTMENT` (`MovementType` > Reservation Release, `Domain Value Objects.md`).
* DEC-20 — only `PENDING_PAYMENT` can be cancelled, and cancellation returns the order to `CART`.

## Exceptions

* `OrderAlreadyDeliveredException`
* `InvalidOrderStatusTransitionException` — raised when the order is not in `PENDING_PAYMENT`.
* `UnauthorizedOperationException`
* `ResourceNotFoundException`
