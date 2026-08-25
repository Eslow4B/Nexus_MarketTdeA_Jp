package application.domain.valueobjects;

import lombok.Getter;

/**
 * Represents the current logistics status of a shipment.
 */
@Getter
public final class ShipmentStatus extends DomainCatalog {

    /** Products are being packed at the origin warehouse. */
    public static final ShipmentStatus PREPARING =
            new ShipmentStatus("PREPARING", "Preparing", "Products are being packed at the origin warehouse.");

    /** Shipment has left the warehouse and is en route. */
    public static final ShipmentStatus IN_TRANSIT =
            new ShipmentStatus("IN_TRANSIT", "In Transit", "Shipment has left the warehouse and is en route.");

    /** Shipment has been delivered to the buyer. */
    public static final ShipmentStatus DELIVERED =
            new ShipmentStatus("DELIVERED", "Delivered", "Shipment has been delivered to the buyer.");

    private ShipmentStatus(String code, String name, String description) {
        super(code, name, description);
    }
}