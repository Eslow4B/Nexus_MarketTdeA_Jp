package application.domain.models;

import lombok.Getter;

/**
 * Represents a physical product and the quantity of it that a buyer returns as part of a
 * {@link ReturnRequest}.
 *
 * <p>Only physical products can be returned, since a digital product has no physical receipt
 * to confirm and no stock to reincorporate (Business Decisions, DEC-24).</p>
 */
@Getter
public class ReturnItem {

    /** Physical product being returned. */
    private final PhysicalProduct product;

    /** Quantity returned. Must be greater than zero and not exceed the purchased quantity. */
    private final int quantity;

    /**
     * Creates a new return item.
     *
     * @param product  physical product being returned
     * @param quantity quantity returned; must be greater than zero
     */
    public ReturnItem(PhysicalProduct product, int quantity) {
        this.product = DomainAssert.notNull(product, "ReturnItem product");
        this.quantity = DomainAssert.positive(quantity, "ReturnItem quantity");
    }
}
