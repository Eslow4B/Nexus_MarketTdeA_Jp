package application.domain.valueobjects;

import java.util.List;

/**
 * Represents the commercial condition of a buyer for placing new orders.
 *
 * <p>Effects of each value (Business Decisions, DEC-07): {@code ACTIVE} may manage the
 * cart and confirm orders; {@code RESTRICTED} may manage the cart but not confirm new
 * orders; {@code SUSPENDED} may do neither. Orders already confirmed are never affected.</p>
 */
public final class CommercialStatus extends DomainCatalog {

    /** Buyer can manage the cart and place new orders normally. */
    public static final CommercialStatus ACTIVE =
            new CommercialStatus("ACTIVE", "Active", "Buyer can manage the cart and place new orders normally.");

    /** Buyer may manage the cart but cannot confirm new orders until a pending situation is resolved. */
    public static final CommercialStatus RESTRICTED =
            new CommercialStatus("RESTRICTED", "Restricted",
                    "Buyer may manage the cart but cannot confirm new orders until a pending situation is resolved.");

    /** Buyer can neither manage the cart nor confirm new orders. */
    public static final CommercialStatus SUSPENDED =
            new CommercialStatus("SUSPENDED", "Suspended", "Buyer can neither manage the cart nor confirm new orders.");

    private static final List<CommercialStatus> VALUES = List.of(ACTIVE, RESTRICTED, SUSPENDED);

    /**
     * Returns every allowed value of this catalog.
     *
     * @return unmodifiable list of values
     */
    public static List<CommercialStatus> values() {
        return VALUES;
    }

    /**
     * Returns the value whose business code matches the given one.
     *
     * @param code business code to look up
     * @return the matching value
     * @throws application.domain.exceptions.DomainValidationException if the code is unknown
     */
    public static CommercialStatus fromCode(String code) {
        return findByCode(VALUES, code, "CommercialStatus");
    }

    /**
     * Indicates whether a buyer in this status may add or remove items in their cart.
     *
     * @return {@code true} unless the status is {@code SUSPENDED}
     */
    public boolean allowsCartManagement() {
        return !SUSPENDED.equals(this);
    }

    /**
     * Indicates whether a buyer in this status may confirm a new order.
     *
     * @return {@code true} only when the status is {@code ACTIVE}
     */
    public boolean allowsOrderConfirmation() {
        return ACTIVE.equals(this);
    }

    private CommercialStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
