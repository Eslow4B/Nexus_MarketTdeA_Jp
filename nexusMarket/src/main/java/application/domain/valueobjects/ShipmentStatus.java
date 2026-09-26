package application.domain.valueobjects;

import java.util.List;

/**
 * Represents the current logistics status of a shipment.
 */
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

    private static final List<ShipmentStatus> VALUES = List.of(PREPARING, IN_TRANSIT, DELIVERED);

    /**
     * Returns every allowed value of this catalog.
     *
     * @return unmodifiable list of values
     */
    public static List<ShipmentStatus> values() {
        return VALUES;
    }

    /**
     * Returns the value whose business code matches the given one.
     *
     * @param code business code to look up
     * @return the matching value
     * @throws application.domain.exceptions.DomainValidationException if the code is unknown
     */
    public static ShipmentStatus fromCode(String code) {
        return findByCode(VALUES, code, "ShipmentStatus");
    }

    private ShipmentStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
