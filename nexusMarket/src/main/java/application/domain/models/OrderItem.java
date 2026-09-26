package application.domain.models;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * Represents a single product and quantity included within an order, together with
 * the unit price at the moment of purchase.
 *
 * <p>Once the order is confirmed, each physical item records the inventory record its
 * units were reserved from. An item is always reserved from a single inventory record,
 * never split across warehouses (Business Decisions, DEC-14).</p>
 */
@Getter
public class OrderItem {

    /** Product included in the order. */
    private final Product product;

    /** Quantity of the product requested. Must be greater than zero. */
    private final int quantity;

    /**
     * Price of the product at the moment the order was placed. Must not be negative.
     * Captured when the item is added to the cart and frozen at confirmation (DEC-16).
     */
    private final BigDecimal unitPrice;

    /**
     * Inventory record the units were reserved from. Absent ({@code null}) for digital products
     * and while the order is still a {@code CART}.
     */
    private final Inventory reservedInventory;

    /**
     * Creates a new order item.
     *
     * @param product           product included in the order
     * @param quantity          quantity requested; must be greater than zero
     * @param unitPrice         price of the product at the moment the order was placed
     * @param reservedInventory inventory record the units were reserved from, or {@code null}
     */
    public OrderItem(Product product, int quantity, BigDecimal unitPrice, Inventory reservedInventory) {
        this.product = DomainAssert.notNull(product, "OrderItem product");
        this.quantity = DomainAssert.positive(quantity, "OrderItem quantity");
        this.unitPrice = DomainAssert.notNegative(unitPrice, "OrderItem unitPrice");
        this.reservedInventory = reservedInventory;

        if (reservedInventory != null) {
            DomainAssert.check(isPhysical(), "Only physical products can be reserved from inventory.");
            DomainAssert.check(reservedInventory.getProduct().equals(product),
                    "The reserved inventory must belong to the same product as the order item.");
        }
    }

    /**
     * Indicates whether the item corresponds to a physical product.
     *
     * @return {@code true} when the product is a {@link PhysicalProduct}
     */
    public boolean isPhysical() {
        return product instanceof PhysicalProduct;
    }

    /**
     * Returns the subtotal of this item ({@code unitPrice × quantity}).
     *
     * @return subtotal of the item
     */
    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
