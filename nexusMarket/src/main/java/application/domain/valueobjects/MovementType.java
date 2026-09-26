package application.domain.valueobjects;

import java.util.List;

/**
 * Represents the category of a significant change applied to an inventory record.
 */
public final class MovementType extends DomainCatalog {

    /** Incoming stock registered into the warehouse. */
    public static final MovementType INBOUND =
            new MovementType("INBOUND", "Inbound", "Incoming stock registered into the warehouse.");

    /** Stock reserved as part of an order in progress. */
    public static final MovementType RESERVATION =
            new MovementType("RESERVATION", "Reservation", "Stock reserved as part of an order in progress.");

    /** Stock removed as a result of a completed sale. */
    public static final MovementType SALE_OUTBOUND =
            new MovementType("SALE_OUTBOUND", "Sale Outbound", "Stock removed as a result of a completed sale.");

    /** Manual correction of the available quantity. */
    public static final MovementType ADJUSTMENT =
            new MovementType("ADJUSTMENT", "Adjustment", "Manual correction of the available quantity.");

    /** Stock reincorporated as a result of a completed return. */
    public static final MovementType RETURN =
            new MovementType("RETURN", "Return", "Stock reincorporated as a result of a completed return.");

    private static final List<MovementType> VALUES =
            List.of(INBOUND, RESERVATION, SALE_OUTBOUND, ADJUSTMENT, RETURN);

    /**
     * Returns every allowed value of this catalog.
     *
     * @return unmodifiable list of values
     */
    public static List<MovementType> values() {
        return VALUES;
    }

    /**
     * Returns the value whose business code matches the given one.
     *
     * @param code business code to look up
     * @return the matching value
     * @throws application.domain.exceptions.DomainValidationException if the code is unknown
     */
    public static MovementType fromCode(String code) {
        return findByCode(VALUES, code, "MovementType");
    }

    private MovementType(String code, String name, String description) {
        super(code, name, description);
    }
}
