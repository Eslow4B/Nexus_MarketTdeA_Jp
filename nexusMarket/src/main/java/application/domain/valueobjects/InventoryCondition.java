package application.domain.valueobjects;

import lombok.Getter;

/**
 * Represents the physical condition of the stock tracked by an inventory record.
 *
 * <p>Inventory marked as {@code DAMAGED} must never be reserved, regardless of the
 * available quantity.</p>
 */
@Getter
public final class InventoryCondition extends DomainCatalog {

    /** Stock is in good condition and may be reserved or sold. */
    public static final InventoryCondition AVAILABLE =
            new InventoryCondition("AVAILABLE", "Available", "Stock is in good condition and may be reserved or sold.");

    /** Stock is damaged and must not be reserved or sold. */
    public static final InventoryCondition DAMAGED =
            new InventoryCondition("DAMAGED", "Damaged", "Stock is damaged and must not be reserved or sold.");

    private InventoryCondition(String code, String name, String description) {
        super(code, name, description);
    }
}