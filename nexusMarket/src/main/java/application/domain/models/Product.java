package application.domain.models;

import application.domain.valueobjects.ProductStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Represents a good offered for sale on NexusMarket, published by a seller.
 *
 * <p>Physical products require inventory tracking and dispatch, while digital products
 * are delivered immediately after payment confirmation. This behavioral difference is
 * represented through specialization rather than through a type attribute, so the type
 * of a product can never change after it is registered (Business Decisions, DEC-12).</p>
 *
 * <p>This class cannot be instantiated directly. Two products are the same entity when
 * they share the same {@code id}.</p>
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class Product {

    /** Unique identifier of the product. */
    @EqualsAndHashCode.Include
    private final String id;

    /** Commercial name of the product. */
    private final String name;

    /** Description of the product shown to buyers. Optional. */
    private final String description;

    /** Variations of the product, such as color, size, or model, as an unmodifiable list. Empty by default. */
    private final List<String> variants;

    /**
     * Current sale price of the product. Must be greater than zero (Business Decisions, DEC-11).
     * A later change never affects orders already confirmed, which keep their own unit price.
     */
    private final BigDecimal price;

    /** Current status of the product within the catalog. */
    private final ProductStatus status;

    /** Seller who owns and publishes the product. */
    private final Seller seller;

    /**
     * Creates a new product.
     *
     * @param id          unique identifier of the product
     * @param name        commercial name of the product
     * @param description description of the product shown to buyers
     * @param variants    variations of the product; {@code null} means none
     * @param price       current sale price; must be greater than zero
     * @param status      current status of the product within the catalog
     * @param seller      seller who owns and publishes the product
     */
    protected Product(String id, String name, String description, List<String> variants,
                      BigDecimal price, ProductStatus status, Seller seller) {
        this.id = DomainAssert.notBlank(id, "Product id");
        this.name = DomainAssert.notBlank(name, "Product name");
        this.description = description;
        this.variants = DomainAssert.immutableCopy(variants, "Product variants");
        this.price = DomainAssert.positive(price, "Product price");
        this.status = DomainAssert.notNull(status, "Product status");
        this.seller = DomainAssert.notNull(seller, "Product seller");
    }

    /**
     * Indicates whether the product may be shown in the public catalog and added to a new order:
     * its status is {@code PUBLISHED} and its seller is active (Business Decisions, DEC-08).
     *
     * @return {@code true} when the product is available for sale
     */
    public boolean isAvailableForSale() {
        return status.allowsNewOrders() && seller.isActive();
    }
}
