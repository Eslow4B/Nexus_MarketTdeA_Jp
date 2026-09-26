package application.domain.models;

import application.domain.valueobjects.ProductStatus;

import java.math.BigDecimal;
import java.util.List;

/**
 * Represents a product delivered electronically and immediately after payment confirmation,
 * without requiring inventory or physical dispatch.
 *
 * <p>Digital products are never stored in inventory and cannot be returned
 * (Business Decisions, DEC-24).</p>
 */
public class DigitalProduct extends Product {

    /**
     * Creates a new digital product.
     *
     * @param id          unique identifier of the product
     * @param name        commercial name of the product
     * @param description description of the product shown to buyers
     * @param variants    variations of the product, such as color, size, or model
     * @param price       current sale price; must be greater than zero
     * @param status      current status of the product within the catalog
     * @param seller      seller who owns and publishes the product
     */
    public DigitalProduct(String id, String name, String description, List<String> variants,
                          BigDecimal price, ProductStatus status, Seller seller) {
        super(id, name, description, variants, price, status, seller);
    }
}
