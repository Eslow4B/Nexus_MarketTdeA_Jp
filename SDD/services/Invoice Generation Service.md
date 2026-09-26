# Invoice Generation Service

## Description

Generates the `Invoice` for an `Order` once payment is confirmed (OBJ-09, generation side). Triggered internally by [Payment Confirmation Service](Payment%20Confirmation%20Service.md) — no external role invokes it directly, which is the same structural signal already used to separate `Stock Sale Outbound Service` and `Stock Return Service` from their role-invoked siblings.

## Responsibilities

* Generate exactly one `Invoice` when an `Order` transitions to `PAID`, for both physical and digital orders.

## Authorized Roles

Not directly invoked by any external role — system-triggered by `PaymentConfirmationService`, per the Authorization Matrix note in `Software Architecture.md`.

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `generateInvoice(order)` | Creates the `Invoice` for a newly `PAID` `Order`. | `Order.status = PAID`; no `Invoice` already exists for this `Order`. | New `Invoice` persisted with `issueDate` = now and `totalAmount = Order.getTotalAmount()` — the sum of `unitPrice × quantity` of every item, with no taxes or shipping costs (DEC-23). |

## Dependencies (Output Ports)

* `InvoiceRepository`

## Business Rules Enforced

* "An Invoice belongs to exactly one Order, and an Order has at most one Invoice." (`Invoice` Relationships, `Domain Model.md`)
* "An Order generates one Invoice once payment is confirmed." (`Order` Relationships, `Domain Model.md`)
* The invoice total equals the order total (invariant of `Invoice` itself, DEC-23). Since prices are frozen at confirmation (DEC-16), the invoice always matches what the buyer confirmed.

## Exceptions

None specific to this service — its preconditions (`Order.status = PAID`, no previous invoice) are guaranteed by `PaymentConfirmationService`, which only reaches this step once per order.
