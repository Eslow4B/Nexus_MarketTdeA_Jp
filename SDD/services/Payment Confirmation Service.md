# Payment Confirmation Service

## Description

Receives the result of the payment validation for an `Order` in `PENDING_PAYMENT` (OBJ-08, central transaction step; corresponds to step 6, "Transacción", of the business flow: *"Se valida el pago y se inicia el flujo de preparación"*). The payment technology is out of scope (§3.2), so the result arrives as an external signal (DEC-22).

When the payment is confirmed, this is the single most complex orchestration point in the whole domain: it coordinates sale outbound, billing, digital fulfillment, and either shipment creation (physical orders) or immediate delivery (digital-only orders) in one atomic business event, which alone justifies isolating it from the simpler, single-purpose transitions around it. When the payment is rejected, it delegates to the cancellation path.

## Responsibilities

* On confirmed payment:
  * Move the `Order` from `PENDING_PAYMENT` to `PAID`.
  * Trigger `StockSaleOutboundService.confirmSaleOutbound` for the order's physical items.
  * Trigger `InvoiceGenerationService.generateInvoice`.
  * Make the order's `DigitalProduct` items available to the buyer immediately: from `PAID` onward they are shown as delivered when the buyer consults the order (DEC-28).
  * If the order contains physical products, trigger `ShipmentCreationService.createShipments` (the order stays `PAID` — *alistamiento* — until its goods physically leave, DEC-21).
  * If the order contains only digital products, move it directly to `DELIVERED` (Digital Fulfillment Path).
* On rejected payment: return the order to `CART` and release its reservations through `OrderCancellationService` (DEC-20).

## Authorized Roles

Not invoked by a Buyer, Seller, or Logistics Operator — it represents the system receiving the payment validation result (per PDF §6.1, "Se valida el pago"). This service's Input Port is exposed to whichever caller integrates the payment signal, which is outside the scope of the business specification.

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `confirmPayment(orderId)` | Applies a confirmed payment. | `Order.status = PENDING_PAYMENT`. | Reserved stock consumed (`SALE_OUTBOUND`); invoice generated; digital items made available; then either shipments created in `PREPARING` with `Order.status = PAID` (physical products), or `Order.status = DELIVERED` (digital-only). |
| `rejectPayment(orderId)` | Applies a rejected payment. | `Order.status = PENDING_PAYMENT`. | Delegates to `OrderCancellationService.revertPendingOrder`: reservations released; `Order.status = CART`. |

Both operations are atomic: if any step fails, none of the order's changes are kept.

## Dependencies (Output Ports)

* `OrderRepository`
* [Stock Sale Outbound Service](Stock%20Sale%20Outbound%20Service.md)
* [Invoice Generation Service](Invoice%20Generation%20Service.md)
* [Shipment Creation Service](Shipment%20Creation%20Service.md)
* [Order Cancellation Service](Order%20Cancellation%20Service.md)

## Business Rules Enforced

* "Digital Fulfillment Path" (`Order` Business Rule, `Domain Model.md`): a purely digital order skips `SHIPPED` and goes directly from `PAID` to `DELIVERED`; a mixed order follows the full physical cycle while its digital items are made available as soon as `PAID` is reached.
* "Physical Fulfillment Path" (DEC-21): shipments are created at `PAID`; the order does not become `SHIPPED` at this point.
* DEC-22 — payment is validated by an external signal; a rejected payment follows DEC-20.
* "An Order generates one Invoice once payment is confirmed." (`Order` Relationships, `Domain Model.md`)

## Exceptions

* `InvalidOrderStatusTransitionException` — raised when the order is not in `PENDING_PAYMENT`.
* `OrderAlreadyDeliveredException`
* `ResourceNotFoundException`
