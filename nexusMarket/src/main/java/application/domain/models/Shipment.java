package application.domain.models;

import application.domain.valueobjects.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents the logistics process required to deliver the physical products of an order
 * from a warehouse to the buyer.
 */
@Getter
@AllArgsConstructor
public class Shipment {

    /** Unique identifier of the shipment. */
    private final String id;

    /** Order being shipped. */
    private final Order order;

    /** Warehouse from which the products are dispatched. */
    private final Warehouse originWarehouse;

    /** Current logistics status of the shipment. */
    private final ShipmentStatus status;
}