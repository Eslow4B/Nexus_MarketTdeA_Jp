# Return And Refund Consultation Service

## Description

Retrieves `ReturnRequest` and `Refund` status information (OBJ-11, consultation side). Kept as one service, unlike the write side, because both reads share the exact same audience and carry no independent business logic of their own beyond fetching state already produced by the four write-side services (`Return Request`, `Return Review`, `Return Completion`, `Refund Processing`) — splitting the two reads further would be CQRS symmetry for its own sake, not a genuine difference in actor or trigger.

## Responsibilities

* Retrieve a `ReturnRequest` with its items and status.
* Retrieve a `Refund` with its amount and status.
* List returns and refunds, so the Administrator can see pending reviews, receipts, and refund decisions.

## Authorized Roles

The owning `Buyer` (their own returns and refunds) and `Administrator` (all).

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `consultReturn(returnId)` | Retrieves `ReturnRequest` data. | Caller is the owning `Buyer` or `Administrator`. | Returns `ReturnRequest` data. |
| `consultRefund(refundId)` | Retrieves `Refund` data. | Caller is the owning `Buyer` or `Administrator`. | Returns `Refund` data. |
| `listReturns(filter)` | Lists returns filtered by status. | Caller is a `Buyer` (only their own) or `Administrator`. | Returns matching returns (possibly empty). |
| `listRefunds(filter)` | Lists refunds filtered by status (for example, `PENDING` refunds awaiting a decision). | Caller is a `Buyer` (only their own) or `Administrator`. | Returns matching refunds (possibly empty). |

## Dependencies (Output Ports)

* `ReturnRequestRepository` (read-only usage)
* `RefundRepository` (read-only usage)
* [Authorization Service](Authorization%20Service.md)

## Business Rules Enforced

None of its own — pure consultation.

## Exceptions

* `UnauthorizedOperationException`
* `ResourceNotFoundException`
