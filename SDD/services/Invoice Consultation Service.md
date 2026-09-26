# Invoice Consultation Service

## Description

Retrieves `Invoice` information (OBJ-09, consultation side). Split from [Invoice Generation Service](Invoice%20Generation%20Service.md) because its audience — the owning `Buyer` and the `Administrator` — is a proper role-based use case, unlike generation, which no role invokes directly at all.

## Responsibilities

* Retrieve an `Invoice` by its identifier or by its `Order`.

## Authorized Roles

The owning `Buyer` and `Administrator`.

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `consultInvoice(invoiceId)` | Retrieves `Invoice` information. | Caller is the `Buyer` of the invoiced order or `Administrator`. | Returns `Invoice` data. |
| `consultInvoiceByOrder(orderId)` | Retrieves the invoice of an order. | Same as `consultInvoice`. | Returns `Invoice` data. |

## Dependencies (Output Ports)

* `InvoiceRepository` (read-only usage)
* [Authorization Service](Authorization%20Service.md)

## Business Rules Enforced

None of its own — pure consultation.

## Exceptions

* `UnauthorizedOperationException`
* `ResourceNotFoundException` — raised when the invoice does not exist, including an order that has not been paid yet.
