# Authentication Service

## Description

Provides the functional verification of a `User`'s credentials required by RG-01 ("Every operation on the platform must be executed by an authenticated user"). It confirms *who* is acting before any other service runs. The concrete technical mechanism used to validate credentials and issue a session (password hashing, tokens) is explicitly out of scope for this service and belongs to `infrastructure/security`, per `Domain Model.md`'s note on `User.username`/`User.password`.

## Responsibilities

* Verify that a submitted login and password match a registered `User`. The login may be the user's `email` — the *"medio principal de acceso"* of the business specification — or their `username` (DEC-03).
* Reject authentication for a `User` whose `status` is not `ACTIVE`.
* Return the authenticated `User` so that every other service can check it against the Authorization Matrix.

## Authorized Roles

Open to any unauthenticated caller presenting credentials. This use case is one of the two entry points exempt from RG-01, since it is the operation that produces an authenticated user (DEC-01).

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `authenticate(login, password)` | Verifies the given credentials and returns the corresponding `User` if valid. `login` is compared case-insensitively against `email` and `username`. | `login` and `password` are non-empty. | Returns the authenticated `User`. Raises `InvalidCredentialsException` if no match is found or the password does not match, or `UserNotActiveException` if the matching `User`'s status is `INACTIVE` or `BLOCKED`. |

## Dependencies (Output Ports)

* `UserRepository` — read-only lookup by `email` or `username`.
* `PasswordHasher` — verifies the submitted password against the stored hash. Its implementation (the hashing algorithm) belongs to `infrastructure/security`, so the domain never knows how passwords are hashed.

## Business Rules Enforced

* RG-01 — every operation must be executed by an authenticated user; this service is what produces that authentication for every other service to rely on.
* A `User` whose `status` is not `ACTIVE` must not be authenticated (`UserStatus.allowsAccess()`).
* DEC-01, DEC-03 (`Business Decisions.md`).

## Exceptions

* `InvalidCredentialsException` — the same exception is raised whether the login does not exist or the password is wrong, so the response never reveals which registered logins exist.
* `UserNotActiveException`
