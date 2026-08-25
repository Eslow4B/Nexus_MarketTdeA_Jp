package application.domain.models;

import application.domain.valueobjects.OrderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Represents a purchase commitment made by a buyer. Its lifecycle is the central
 * business process of NexusMarket.
 */
@Getter
@AllArgsConstructor
public class Order {

    /** Unique identifier of the order. */
    private final String id;

    /** Buyer who placed the order. */
    private final Buyer buyer;

    /** Products and quantities included in the order. */
    @Getter(AccessLevel.NONE)
    private final List<OrderItem> items;

    /** Current stage of the order lifecycle. */
    private final OrderStatus status;

    /** Date and time when the order was created. */
    private final LocalDateTime creationDate;

    /**
     * Returns the order items as an unmodifiable list.
     *
     * @return unmodifiable list of order items
     */
    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }
}