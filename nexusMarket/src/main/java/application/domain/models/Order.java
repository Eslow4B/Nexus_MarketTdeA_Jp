package application.domain.models;

import application.domain.valueobjects.OrderStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a purchase commitment made by a buyer. Its lifecycle is the central
 * business process of NexusMarket.
 *
 * <p>Invariants validated at construction:</p>
 * <ul>
 *   <li>A product appears at most once among the items (DEC-17).</li>
 *   <li>Once the order leaves {@code CART}, it has at least one item; and if it contains
 *       physical products, it has a delivery address and every physical item has been
 *       reserved from an inventory record (DEC-14, DEC-18).</li>
 *   <li>An order composed only of digital products can never be {@code SHIPPED}.</li>
 * </ul>
 *
 * <p>Two orders are the same entity when they share the same {@code id}.</p>
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Order {

    /** Unique identifier of the order. */
    @EqualsAndHashCode.Include
    private final String id;

    /** Buyer who placed the order. */
    private final Buyer buyer;

    /** Products and quantities included in the order, as an unmodifiable list. */
    private final List<OrderItem> items;

    /** Current stage of the order lifecycle. */
    private final OrderStatus status;

    /** Date and time when the order was created. */
    private final LocalDateTime creationDate;

    /**
     * Address where the physical products are delivered, chosen by the buyer among their own
     * addresses at confirmation and kept as a snapshot (DEC-18). Absent ({@code null}) while
     * the order is a {@code CART} and for orders composed only of digital products.
     */
    private final String deliveryAddress;

    /**
     * Creates a new order.
     *
     * @param id              unique identifier of the order
     * @param buyer           buyer who placed the order
     * @param items           products and quantities included in the order; {@code null} means none
     * @param status          current stage of the order lifecycle
     * @param creationDate    date and time when the order was created
     * @param deliveryAddress address where the physical products are delivered, or {@code null}
     */
    public Order(String id, Buyer buyer, List<OrderItem> items, OrderStatus status,
                 LocalDateTime creationDate, String deliveryAddress) {
        this.id = DomainAssert.notBlank(id, "Order id");
        this.buyer = DomainAssert.notNull(buyer, "Order buyer");
        this.items = DomainAssert.immutableCopy(items, "Order items");
        this.status = DomainAssert.notNull(status, "Order status");
        this.creationDate = DomainAssert.notNull(creationDate, "Order creationDate");
        this.deliveryAddress = deliveryAddress;

        long distinctProducts = this.items.stream().map(OrderItem::getProduct).distinct().count();
        DomainAssert.check(distinctProducts == this.items.size(),
                "A product may appear only once among the items of an order.");

        if (!OrderStatus.CART.equals(status)) {
            DomainAssert.check(!this.items.isEmpty(), "A confirmed order must contain at least one item.");
            if (containsPhysicalProducts()) {
                DomainAssert.notBlank(deliveryAddress, "Order deliveryAddress");
                DomainAssert.check(getPhysicalItems().stream().allMatch(item -> item.getReservedInventory() != null),
                        "Every physical item of a confirmed order must be reserved from an inventory record.");
            }
        }

        DomainAssert.check(!OrderStatus.SHIPPED.equals(status) || containsPhysicalProducts(),
                "An order composed only of digital products can never be SHIPPED.");
    }

    /**
     * Indicates whether the order contains at least one physical product, which determines
     * whether it follows the physical fulfillment path through {@code SHIPPED}.
     *
     * @return {@code true} when at least one item is a {@link PhysicalProduct}
     */
    public boolean containsPhysicalProducts() {
        return items.stream().anyMatch(OrderItem::isPhysical);
    }

    /**
     * Returns the items that correspond to physical products.
     *
     * @return unmodifiable list of physical items
     */
    public List<OrderItem> getPhysicalItems() {
        return items.stream().filter(OrderItem::isPhysical).toList();
    }

    /**
     * Returns the item of the order that corresponds to the given product, if any.
     *
     * @param product product to look for
     * @return the matching item, or empty when the product is not part of the order
     */
    public Optional<OrderItem> findItem(Product product) {
        return items.stream().filter(item -> item.getProduct().equals(product)).findFirst();
    }

    /**
     * Returns the warehouses the physical items were reserved from. One shipment is created
     * per warehouse (DEC-21).
     *
     * @return set of origin warehouses
     */
    public Set<Warehouse> getOriginWarehouses() {
        return getPhysicalItems().stream()
                .filter(item -> item.getReservedInventory() != null)
                .map(item -> item.getReservedInventory().getWarehouse())
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Returns the total amount of the order: the sum of every item's subtotal (DEC-23).
     *
     * @return total amount of the order
     */
    public BigDecimal getTotalAmount() {
        return items.stream().map(OrderItem::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Indicates whether the order is finalized ({@code DELIVERED}) and therefore can no longer
     * be modified under any circumstance.
     *
     * @return {@code true} when the order status is {@code DELIVERED}
     */
    public boolean isFinalized() {
        return OrderStatus.DELIVERED.equals(status);
    }
}
