package application.domain.valueobjects;

import java.util.List;

/**
 * Represents the current stage of an order within its lifecycle.
 */
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

    private static final List<OrderStatus> VALUES = List.of(CART, PENDING_PAYMENT, PAID, SHIPPED, DELIVERED);

    /**
     * Returns every allowed value of this catalog.
     *
     * @return unmodifiable list of values
     */
    public static List<OrderStatus> values() {
        return VALUES;
    }

    /**
     * Returns the value whose business code matches the given one.
     *
     * @param code business code to look up
     * @return the matching value
     * @throws application.domain.exceptions.DomainValidationException if the code is unknown
     */
    public static OrderStatus fromCode(String code) {
        return findByCode(VALUES, code, "OrderStatus");
    }

    private OrderStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
