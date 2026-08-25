package application.domain.valueobjects;

import lombok.Getter;

/**
 * Represents the current stage of an order within its lifecycle.
 */
@Getter
public final class OrderStatus extends DomainCatalog {

    /** Provisional selection of products, not yet confirmed. */
    public static final OrderStatus CART =
            new OrderStatus("CART", "Cart", "Provisional selection of products, not yet confirmed.");

    /** Order confirmed and awaiting payment validation. */
    public static final OrderStatus PENDING_PAYMENT =
            new OrderStatus("PENDING_PAYMENT", "Pending Payment", "Order confirmed and awaiting payment validation.");

    /** Payment confirmed; preparation process may begin. */
    public static final OrderStatus PAID =
            new OrderStatus("PAID", "Paid", "Payment confirmed; preparation process may begin.");

    /** Order has left the warehouse. */
    public static final OrderStatus SHIPPED =
            new OrderStatus("SHIPPED", "Shipped", "Order has left the warehouse.");

    /** Order has been successfully delivered to the buyer. */
    public static final OrderStatus DELIVERED =
            new OrderStatus("DELIVERED", "Delivered", "Order has been successfully delivered to the buyer.");

    private OrderStatus(String code, String name, String description) {
        super(code, name, description);
    }
}