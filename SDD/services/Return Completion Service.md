# Return Completion Service

## Description

Marks a `ReturnRequest` as `COMPLETED` once the returned product has been physically received (OBJ-11, completion stage). This is a distinct, later event from the `Administrator`'s approval decision in [Return Review Service](Return%20Review%20Service.md) — approval is a commercial decision, completion is a physical/logistics confirmation, which is why they are not the same operation even though both are exercised by the `Administrator`.

## Responsibilities

* Mark an `APPROVED` `ReturnRequest` as `COMPLETED` once the returned product is physically received.
* Trigger `StockReturnService.registerReturnStock` now that receipt is verified — reincorporating stock any earlier (at mere approval) would let inventory count a product that has not actually come back yet.
* Trigger `RefundProcessingService.openRefund`, so the refund is opened in `PENDING` as soon as the product is back (DEC-25).

## Authorized Roles

Administrator only — per the Authorization Matrix ("Return and refund management"). The business specification's Responsibility Matrix does not grant the Logistics Operator any role in returns or refunds, so physical receipt of a returned product is confirmed by the `Administrator`, not inferred as a Logistics Operator task by analogy (DEC-04).

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `completeReturn(returnId)` | Marks a `ReturnRequest` as `COMPLETED`, reincorporates stock, and opens the refund. | Caller is `Administrator` confirming physical receipt; `ReturnRequest.status = APPROVED`. | `ReturnRequest.status = COMPLETED`; each returned item's units back in the inventory record they left from (DEC-25); a `Refund` opened in `PENDING`. Atomic: if any step fails, the return stays `APPROVED` and nothing else changes. |

## Dependencies (Output Ports)

* `ReturnRequestRepository`
* [Stock Return Service](Stock%20Return%20Service.md)
* [Refund Processing Service](Refund%20Processing%20Service.md)
* [Authorization Service](Authorization%20Service.md)

## Business Rules Enforced

* A `ReturnRequest` reaches `COMPLETED` only after `APPROVED` (`ReturnStatus` lifecycle, `Domain Value Objects.md`).
* Stock is only reincorporated once physical receipt is verified, not at commercial approval (DEC-25).
* Every completed return has exactly one refund, opened at completion (DEC-25).

## Exceptions

* `InvalidReturnStatusTransitionException`
* `UnauthorizedOperationException`
* `ResourceNotFoundException`
