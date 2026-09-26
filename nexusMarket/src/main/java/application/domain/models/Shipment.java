package application.domain.models;

import application.domain.valueobjects.ShipmentStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents the logistics process required to deliver the physical products of an order
 * from a warehouse to the buyer.
 *
 * <p>An order generates one shipment per warehouse its physical items were reserved from
 * (Business Decisions, DEC-21). A shipment therefore only exists for an order with physical
 * products, and its origin warehouse is one of the order's origin warehouses.</p>
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Shipment {

    /** Unique identifier of the shipment. */
    @EqualsAndHashCode.Include
    private final String id;

    /** Order being shipped. */
    private final Order order;

    /** Warehouse from which the products are dispatched. */
    private final Warehouse originWarehouse;

    /** Current logistics status of the shipment. */
    private final ShipmentStatus status;

    /** Date and time when the shipment was created. */
    private final LocalDateTime creationDate;

    /**
     * Creates a new shipment.
     *
     * @param id              unique identifier of the shipment
     * @param order           order being shipped
     * @param originWarehouse warehouse from which the products are dispatched
     * @param status          current logistics status of the shipment
     * @param creationDate    date and time when the shipment was created
     */
    public Shipment(String id, Order order, Warehouse originWarehouse, ShipmentStatus status,
                    LocalDateTime creationDate) {
        this.id = DomainAssert.notBlank(id, "Shipment id");
        this.order = DomainAssert.notNull(order, "Shipment order");
        this.originWarehouse = DomainAssert.notNull(originWarehouse, "Shipment originWarehouse");
        this.status = DomainAssert.notNull(status, "Shipment status");
        this.creationDate = DomainAssert.notNull(creationDate, "Shipment creationDate");

        DomainAssert.check(order.containsPhysicalProducts(),
                "A shipment can only be created for an order that contains physical products.");
        DomainAssert.check(order.getOriginWarehouses().contains(originWarehouse),
                "The origin warehouse must be one of the warehouses the order items were reserved from.");
    }

    /**
     * Returns the physical items of the order dispatched from this shipment's origin warehouse.
     *
     * @return unmodifiable list of the items carried by this shipment
     */
    public List<OrderItem> getItems() {
        return order.getPhysicalItems().stream()
                .filter(item -> item.getReservedInventory() != null
                        && item.getReservedInventory().getWarehouse().equals(originWarehouse))
                .toList();
    }
}
