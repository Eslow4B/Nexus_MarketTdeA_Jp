package application.domain.valueobjects;

import lombok.Getter;

/**
 * Represents the category of a significant change applied to an inventory record.
 */
@Getter
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

    /** Stock reincorporated as a result of an approved return. */
    public static final MovementType RETURN =
            new MovementType("RETURN", "Return", "Stock reincorporated as a result of an approved return.");

    private MovementType(String code, String name, String description) {
        super(code, name, description);
    }
}