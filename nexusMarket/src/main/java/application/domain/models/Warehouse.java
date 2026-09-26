package application.domain.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Represents a physical location where product inventory is stored and managed.
 *
 * <p>A warehouse may belong to the Marketplace itself or to a specific seller.
 * Two warehouses are the same entity when they share the same {@code id}.</p>
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Warehouse {

    /** Unique identifier of the warehouse. */
    @EqualsAndHashCode.Include
    private final String id;

    /** Descriptive name of the warehouse. */
    private final String name;

    /** Physical location of the warehouse. */
    private final String address;

    /** Seller who owns the warehouse. Absent ({@code null}) when the warehouse belongs to the Marketplace. */
    private final Seller owner;

    /**
     * Creates a new warehouse.
     *
     * @param id      unique identifier of the warehouse
     * @param name    descriptive name of the warehouse
     * @param address physical location of the warehouse
     * @param owner   seller who owns the warehouse, or {@code null} for a Marketplace-owned warehouse
     */
    public Warehouse(String id, String name, String address, Seller owner) {
        this.id = DomainAssert.notBlank(id, "Warehouse id");
        this.name = DomainAssert.notBlank(name, "Warehouse name");
        this.address = DomainAssert.notBlank(address, "Warehouse address");
        this.owner = owner;
    }

    /**
     * Indicates whether the warehouse belongs to the Marketplace rather than to a seller.
     *
     * @return {@code true} when the warehouse has no seller owner
     */
    public boolean isMarketplaceOwned() {
        return owner == null;
    }
}
