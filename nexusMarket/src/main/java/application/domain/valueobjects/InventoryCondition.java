package application.domain.valueobjects;

import java.util.List;

/**
 * Represents the physical condition of the stock tracked by an inventory record.
 *
 * <p>Inventory marked as {@code DAMAGED} must never be reserved, regardless of the
 * available quantity.</p>
 */
public final class InventoryCondition extends DomainCatalog {

    /** Stock is in good condition and may be reserved or sold. */
    public static final InventoryCondition AVAILABLE =
            new InventoryCondition("AVAILABLE", "Available", "Stock is in good condition and may be reserved or sold.");

    /** Stock is damaged and must not be reserved or sold. */
    public static final InventoryCondition DAMAGED =
            new InventoryCondition("DAMAGED", "Damaged", "Stock is damaged and must not be reserved or sold.");

    private static final List<InventoryCondition> VALUES = List.of(AVAILABLE, DAMAGED);

    /**
     * Returns every allowed value of this catalog.
     *
     * @return unmodifiable list of values
     */
    public static List<InventoryCondition> values() {
        return VALUES;
    }

    /**
     * Returns the value whose business code matches the given one.
     *
     * @param code business code to look up
     * @return the matching value
     * @throws application.domain.exceptions.DomainValidationException if the code is unknown
     */
    public static InventoryCondition fromCode(String code) {
        return findByCode(VALUES, code, "InventoryCondition");
    }

    /**
     * Indicates whether stock in this condition may be reserved.
     *
     * @return {@code true} only when the condition is {@code AVAILABLE}
     */
    public boolean isReservable() {
        return AVAILABLE.equals(this);
    }

    private InventoryCondition(String code, String name, String description) {
        super(code, name, description);
    }
}
