package application.domain.models;

import application.domain.valueobjects.ProductStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

/**
 * Represents a good offered for sale on NexusMarket, published by a seller.
 *
 * <p>Physical products require inventory tracking and dispatch, while digital products
 * are delivered immediately after payment confirmation. This behavioral difference is
 * represented through specialization rather than through a type attribute.</p>
 *
 * <p>This class cannot be instantiated directly.</p>
 */
@Getter
@AllArgsConstructor
public abstract class Product {

    /** Unique identifier of the product. */
    private final String id;

    /** Commercial name of the product. */
    private final String name;

    /** Description of the product shown to buyers. */
    private final String description;

    /** Variations of the product, such as color, size, or model. Empty by default. */
    @Getter(AccessLevel.NONE)
    private final List<String> variants;

    /** Current status of the product within the catalog. */
    private final ProductStatus status;

    /** Seller who owns and publishes the product. */
    private final Seller seller;

    /**
     * Returns the product variants as an unmodifiable list.
     *
     * @return unmodifiable list of variants
     */
    public List<String> getVariants() {
        return Collections.unmodifiableList(variants);
    }
}