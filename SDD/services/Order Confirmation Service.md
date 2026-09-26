# Order Confirmation Service

## Description

Confirms a cart, moving an `Order` from `CART` to `PENDING_PAYMENT` (OBJ-08, first transition; step 5 of the business flow, *"el comprador ... confirma el pedido"*). This is the moment the order becomes a commercial commitment: prices are frozen, the delivery address is fixed, and inventory is reserved. It is Buyer-initiated and depends only on inventory reservation — a narrower and simpler dependency set than [Payment Confirmation Service](Payment%20Confirmation%20Service.md), which additionally orchestrates billing and fulfillment. Separating them keeps each service's dependency list honest about what it actually needs.

## Responsibilities

* Verify again that every product in the cart is still available for sale, and refresh each item's `unitPrice` to the product's current price (DEC-16).
* Fix the order's `deliveryAddress` when it contains physical products (DEC-18).
* Reserve inventory for every physical item via `StockReservationService`, all or nothing.
* Move the `Order` to `PENDING_PAYMENT`.

## Authorized Roles

Buyer only — per the Authorization Matrix ("Order management (cart→delivered)"), restricted here to the owning `Buyer` since confirmation is the act of committing one's own cart.

## Operations

| Operation | Description | Preconditions | Result |
| --- | --- | --- | --- |
| `confirmOrder(orderId, deliveryAddress)` | Moves the order from `CART` to `PENDING_PAYMENT`. `deliveryAddress` is ignored for digital-only orders. | Caller is the owning `Buyer`; `Order.status = CART`; the order has at least one item; `Buyer.commercialStatus = ACTIVE` (DEC-07); every product is available for sale; for orders with physical products, `deliveryAddress` is one of the buyer's addresses. | Each item's `unitPrice` refreshed; each physical item reserved from a single inventory record and linked to it (`OrderItem.reservedInventory`); `Order.deliveryAddress` set; `Order.status = PENDING_PAYMENT`. |

The operation is atomic: if any validation or any reservation fails, no reservation made during this attempt is kept, and the order stays in `CART` unchanged.

## Dependencies (Output Ports)

* `OrderRepository`
* `UserRepository` — to obtain the buyer's current `commercialStatus` and addresses.
* [Stock Reservation Service](Stock%20Reservation%20Service.md)
* [Authorization Service](Authorization%20Service.md)

## Business Rules Enforced

* DEC-07 — only an `ACTIVE` buyer may confirm new orders (`CommercialStatus.allowsOrderConfirmation()`).
* DEC-08, DEC-16 — availability is verified again at confirmation, and prices are frozen from here on.
* DEC-14 — each physical item is reserved from a single inventory record; `DAMAGED` or non-existent stock is never reserved.
* DEC-18 — the delivery address must be one of the buyer's addresses and is kept as a snapshot.
* The `Order` invariants (at least one item; physical items reserved; delivery address present) are validated by `Order` itself.

## Exceptions

* `BuyerNotEligibleForOrderException`
* `ProductNotAvailableException`
* `InsufficientInventoryException`
* `DamagedInventoryReservationException`
* `InvalidDeliveryAddressException`
* `InvalidOrderStatusTransitionException` — raised when the order is not a `CART` or has no items.
* `UnauthorizedOperationException`
* `ResourceNotFoundException`
