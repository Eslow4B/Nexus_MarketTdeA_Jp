# Return Review Service

## Description

Approves or rejects a `ReturnRequest` (OBJ-11, review stage). The `Administrator`'s commercial decision point, distinct from the `Buyer`'s original request in [Return Request Service](Return%20Request%20Service.md) and from the physical receipt confirmation in [Return Completion Service](Return%20Completion%20Service.md).

## Responsibilities

* Approve or reject a `ReturnRequest`. Approval only authorizes the `Buyer` to ship the product back — it does not yet reincorporate stock or move money, since neither should happen before the returned product is physically verified (DEC-25).

## Authorized Roles

Administrator only — per the Authorization Matrix ("Return and refund management", DEC-04).

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `reviewReturn(returnId, approve)` | Approves or rejects a `ReturnRequest`. | Caller is `Administrator`; `ReturnRequest.status = REQUESTED`. | `ReturnRequest.status` updated to `APPROVED` or `REJECTED`. `REJECTED` is terminal. |

## Dependencies (Output Ports)

* `ReturnRequestRepository`
* [Authorization Service](Authorization%20Service.md)

## Business Rules Enforced

* `ReturnStatus` lifecycle: only a `REQUESTED` return can be reviewed (`Domain Value Objects.md`).
* "A ReturnRequest may generate one Refund." (`ReturnRequest` Relationships, `Domain Model.md`) — an approval is the precondition [Return Completion Service](Return%20Completion%20Service.md) later relies on before stock and refund are touched.

## Exceptions

* `InvalidReturnStatusTransitionException`
* `UnauthorizedOperationException`
* `ResourceNotFoundException`
