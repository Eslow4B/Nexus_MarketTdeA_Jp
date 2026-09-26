package application.domain.valueobjects;

import java.util.List;

/**
 * Represents the current status of a product within the catalog.
 */
public final class ProductStatus extends DomainCatalog {

    /** Product is visible and available in the public catalog. */
    public static final ProductStatus PUBLISHED =
            new ProductStatus("PUBLISHED", "Published", "Product is visible and available in the public catalog.");

    /** Product is temporarily hidden from the public catalog. */
    public static final ProductStatus SUSPENDED =
            new ProductStatus("SUSPENDED", "Suspended", "Product is temporarily hidden from the public catalog.");

    /** Product is permanently removed from commercialization. */
    public static final ProductStatus DISCONTINUED =
            new ProductStatus("DISCONTINUED", "Discontinued", "Product is permanently removed from commercialization.");

    private static final List<ProductStatus> VALUES = List.of(PUBLISHED, SUSPENDED, DISCONTINUED);

    /**
     * Returns every allowed value of this catalog.
     *
     * @return unmodifiable list of values
     */
    public static List<ProductStatus> values() {
        return VALUES;
    }

    /**
     * Returns the value whose business code matches the given one.
     *
     * @param code business code to look up
     * @return the matching value
     * @throws application.domain.exceptions.DomainValidationException if the code is unknown
     */
    public static ProductStatus fromCode(String code) {
        return findByCode(VALUES, code, "ProductStatus");
    }

    /**
     * Indicates whether a product in this status may be added to a new order.
     *
     * @return {@code true} only when the status is {@code PUBLISHED}
     */
    public boolean allowsNewOrders() {
        return PUBLISHED.equals(this);
    }

    private ProductStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
