package application.domain.models;

import application.domain.valueobjects.InventoryCondition;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Represents the available stock of a physical product within a specific warehouse.
 *
 * <p>Inventory must always be linked to exactly one product and one warehouse.
 * Negative stock is never allowed under any circumstance.</p>
 *
 * <p>The warehouse must be Marketplace-owned or owned by the product's own seller
 * (Business Decisions, DEC-13). {@code condition} applies to every unit of the record
 * (DEC-15).</p>
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Inventory {

    /** Unique identifier of the inventory record. */
    @EqualsAndHashCode.Include
    private final String id;

    /** Physical product tracked by this inventory record. */
    private final PhysicalProduct product;

    /** Warehouse where the stock is stored. */
    private final Warehouse warehouse;

    /** Current quantity available for sale. Must never be negative. */
    private final int availableQuantity;

    /** Current condition of the stock tracked by this record. */
    private final InventoryCondition condition;

    /**
     * Creates a new inventory record.
     *
     * @param id                unique identifier of the inventory record
     * @param product           physical product tracked by this record
     * @param warehouse         warehouse where the stock is stored
     * @param availableQuantity current quantity available for sale; must not be negative
     * @param condition         current condition of the stock
     */
    public Inventory(String id, PhysicalProduct product, Warehouse warehouse,
                     int availableQuantity, InventoryCondition condition) {
        this.id = DomainAssert.notBlank(id, "Inventory id");
        this.product = DomainAssert.notNull(product, "Inventory product");
        this.warehouse = DomainAssert.notNull(warehouse, "Inventory warehouse");
        this.availableQuantity = DomainAssert.notNegative(availableQuantity, "Inventory availableQuantity");
        this.condition = DomainAssert.notNull(condition, "Inventory condition");

        DomainAssert.check(warehouse.isMarketplaceOwned() || warehouse.getOwner().equals(product.getSeller()),
                "Inventory of a product may only be stored in a Marketplace warehouse "
                        + "or in a warehouse owned by the product's seller.");
    }

    /**
     * Indicates whether the given quantity can be reserved from this record: the stock is not
     * {@code DAMAGED} and enough units are available (Business Decisions, DEC-14).
     *
     * @param quantity quantity to reserve
     * @return {@code true} when the reservation is possible
     */
    public boolean canReserve(int quantity) {
        return condition.isReservable() && quantity > 0 && availableQuantity >= quantity;
    }
}
