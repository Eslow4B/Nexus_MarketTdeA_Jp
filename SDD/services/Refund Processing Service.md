# Refund Processing Service

## Description

Opens and resolves the `Refund` of a `COMPLETED` `ReturnRequest` (OBJ-11, refund half; *"Gestión de reembolsos"*). Kept as its own service — not folded into the Return-stage services — because a `Refund` is its own entity with its own lifecycle (`RefundStatus`: `PENDING`, `PROCESSED`, `REJECTED`) and represents a financial action, categorically different from the logistics/commercial decisions around a return.

Requires `COMPLETED`, not merely `APPROVED`: money is returned only after [Return Completion Service](Return%20Completion%20Service.md) confirms the product was physically received back — the same reasoning that keeps `StockReturnService` from reincorporating stock at approval time.

Its two operations share the same aggregate and reason to change but have different triggers (system and Administrator) — the same structure as `StockAdjustmentService`, which groups a manual correction with a system-triggered reservation release.

## Responsibilities

* Open the `Refund` of a return in `PENDING`, for the return's refundable amount, when the return is completed.
* Resolve a `PENDING` `Refund` as `PROCESSED` or `REJECTED` according to the Administrator's decision.

## Authorized Roles

* `openRefund`: not invoked by an external role — triggered internally by `ReturnCompletionService`.
* `resolveRefund`: Administrator only — per the Authorization Matrix ("Gestión Reembolsos": the Buyer participates by requesting the return and consulting the refund; the Administrator decides it).

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `openRefund(returnRequest)` | Opens the refund of a completed return. | `ReturnRequest.status = COMPLETED`; no `Refund` exists yet for the return; triggered by `ReturnCompletionService`. | New `Refund` persisted with `status = PENDING`, `amount = ReturnRequest.getRefundableAmount()` (unit price paid × returned quantity), and `creationDate` = now. |
| `resolveRefund(refundId, approve)` | Resolves a pending refund. | Caller is `Administrator`; `Refund.status = PENDING`. | `Refund.status = PROCESSED` (`approve = true`, funds returned) or `REJECTED` (`approve = false`). Both are terminal. |

## Dependencies (Output Ports)

* `RefundRepository`
* [Authorization Service](Authorization%20Service.md) — for `resolveRefund` only.

## Business Rules Enforced

* "A Refund belongs to exactly one ReturnRequest." (`Refund` Relationships, `Domain Model.md`)
* A `Refund` may only exist for a `COMPLETED` `ReturnRequest`, and its amount never exceeds the refundable amount (invariants of `Refund` itself).
* `RefundStatus` lifecycle: `PENDING → PROCESSED | REJECTED` (`Domain Value Objects.md`).
* DEC-25 — a rejected refund does not revert the stock reincorporated at completion, because the units are physically in the warehouse.

## Exceptions

* `InvalidReturnStatusTransitionException` — raised when the refund is not `PENDING`, or a refund already exists for the return.
* `UnauthorizedOperationException`
* `ResourceNotFoundException`
