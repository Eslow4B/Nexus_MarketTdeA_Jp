# Authorization Service

## Description

Centralizes the enforcement of RG-03 ("No participant may manage information outside the scope of their role") against the Authorization Matrix defined in `Software Architecture.md`. Every other service depends on it as the first step of every operation, instead of re-implementing the same role check independently in each of the other 34 services.

This is the clearest instance of "more services for better logic" in this decomposition: the guard-clause logic already existed conceptually in every service's documentation — this service simply gives that repeated logic a single, testable home.

## Responsibilities

* Verify that the role of the currently authenticated `User` is allowed to perform a given use case, per the Authorization Matrix.
* Verify that a `Seller`, `Buyer`, or other role-scoped actor is acting on their own resource when the Authorization Matrix requires ownership (for example, a `Seller` updating only their own `Product`, or a `Buyer` confirming only their own `Order`).

## Authorized Roles

Not applicable — this service is not itself a use case invoked by an external role. It is a shared dependency of every other service.

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `checkPermission(user, useCase)` | Verifies that `user.role` is allowed to perform `useCase` per the Authorization Matrix. | `user` is authenticated. | Returns normally if allowed; raises `UnauthorizedOperationException` otherwise. |
| `checkOwnership(user, resourceOwnerId)` | Verifies that `user` is the owner of a resource, for use cases restricted to "own resource only" (e.g., a `Seller` managing their own `Product`, a `Buyer` managing their own `Order`). | `user` is authenticated. | Returns normally if `user.id = resourceOwnerId` (or `user` is `Administrator`, where the matrix allows it); raises `UnauthorizedOperationException` otherwise. |

## Dependencies (Output Ports)

None — this service depends only on the `User` already produced by `AuthenticationService` and the static Authorization Matrix; it does not persist or query anything on its own.

## Business Rules Enforced

* RG-03 — "No participant may manage information outside the scope of their role." (`Domain Model.md`, General Business Rules)
* The full Authorization Matrix (`Software Architecture.md`), including the rows assigned from the Participants table (DEC-04) and the per-role participation in order management (DEC-05).
* The authenticated `User` must still be `ACTIVE`; a user blocked after authenticating is rejected on their next operation.

## Exceptions

* `UnauthorizedOperationException`
