package application.domain.models;

import application.domain.valueobjects.InventoryCondition;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents the available stock of a physical product within a specific warehouse.
 *
 * <p>Inventory must always be linked to exactly one product and one warehouse.
 * Negative stock is never allowed under any circumstance.</p>
 */
@Getter
@AllArgsConstructor
public class Inventory {

    /** Unique identifier of the inventory record. */
    private final String id;

    /** Physical product tracked by this inventory record. */
    private final PhysicalProduct product;

    /** Warehouse where the stock is stored. */
    private final Warehouse warehouse;

    /** Current quantity available for sale. Must never be negative. */
    private final int availableQuantity;

    /** Current condition of the stock tracked by this record. */
    private final InventoryCondition condition;
}