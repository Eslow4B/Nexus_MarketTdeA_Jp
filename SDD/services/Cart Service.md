# Cart Service

## Description

Manages the shopping cart (OBJ-07), the provisional selection stage of an `Order` before it is confirmed. The cart is not a separate entity — it is an `Order` in `CART` status (`Domain Model.md`, `Order`) — but OBJ-07 is a distinct objective from OBJ-08 (the order lifecycle from confirmation onward), so cart management and order-lifecycle management are documented, and implemented, as separate services.

## Responsibilities

* Add an item to the buyer's cart, creating the cart `Order` if none exists yet, capturing the product's current price.
* Change or remove an item of the cart.
* Consult the current contents of the cart.

## Authorized Roles

Buyer only — a cart is a strictly personal, pre-commercial-commitment concept; per the Authorization Matrix, order management (which includes the cart stage) also involves Seller and Logistics Operator, but only from `PENDING_PAYMENT` onward (DEC-05) — while an `Order` is still `CART`, only its owning `Buyer` can see or change it.

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `addItemToCart(productId, quantity)` | Adds a product to the calling buyer's cart. | Caller is a `Buyer` whose `commercialStatus` allows cart management (not `SUSPENDED`, DEC-07); the product is available for sale (DEC-08); `quantity > 0`; the buyer has no order in `PENDING_PAYMENT` (DEC-17). | If the product is already in the cart, its quantity is increased; otherwise a new `OrderItem` is added with `unitPrice = Product.price` (DEC-16). The cart `Order` is created with `status = CART` if none existed. |
| `updateCartItemQuantity(productId, quantity)` | Sets the quantity of a product already in the cart. | Same as `addItemToCart`; the product is in the cart. | `OrderItem.quantity` updated. |
| `removeItemFromCart(productId)` | Removes a product from the cart. | Caller is the `Buyer`; `commercialStatus` allows cart management; the product is in the cart. | `OrderItem` removed. An empty cart remains as an empty `CART` order. |
| `consultCart()` | Retrieves the calling buyer's current cart. | Caller is a `Buyer`. | Returns the cart `Order` and its `OrderItem` list, or an empty result if the buyer has no cart. |

## Dependencies (Output Ports)

* `OrderRepository`
* `ProductRepository`
* `UserRepository` — to obtain the calling `Buyer` and its current `commercialStatus`.
* [Authorization Service](Authorization%20Service.md)

## Business Rules Enforced

* DEC-07 — a `SUSPENDED` buyer cannot manage the cart (`CommercialStatus.allowsCartManagement()`).
* "A Product that is not available for sale must not be added to a new Order." (`Product` Business Rule, `Domain Model.md`; DEC-08)
* DEC-16 — the unit price is captured when the item is added; it is refreshed at confirmation.
* DEC-17 — one open purchase per buyer; one line per product.

## Exceptions

* `BuyerNotEligibleForOrderException`
* `ProductNotAvailableException`
* `InvalidOrderStatusTransitionException` — raised when the buyer still has an order in `PENDING_PAYMENT`.
* `UnauthorizedOperationException`
* `ResourceNotFoundException`
* `DomainValidationException` — raised by `OrderItem` (non-positive quantity).
