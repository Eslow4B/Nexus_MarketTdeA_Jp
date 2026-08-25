package application.domain.valueobjects;

import lombok.Getter;

/**
 * Represents the current status of a product within the catalog.
 */
@Getter
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

    private ProductStatus(String code, String name, String description) {
        super(code, name, description);
    }
}