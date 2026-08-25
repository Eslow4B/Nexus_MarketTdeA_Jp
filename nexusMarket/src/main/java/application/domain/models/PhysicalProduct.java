package application.domain.models;

import application.domain.valueobjects.ProductStatus;

import java.util.List;

/**
 * Represents a tangible product that requires inventory tracking and physical dispatch
 * to be delivered to the buyer.
 */
public class PhysicalProduct extends Product {

    /**
     * Creates a new physical product.
     *
     * @param id          unique identifier of the product
     * @param name        commercial name of the product
     * @param description description of the product shown to buyers
     * @param variants    variations of the product, such as color, size, or model
     * @param status      current status of the product within the catalog
     * @param seller      seller who owns and publishes the product
     */
    public PhysicalProduct(String id, String name, String description, List<String> variants,
                           ProductStatus status, Seller seller) {
        super(id, name, description, variants, status, seller);
    }
}