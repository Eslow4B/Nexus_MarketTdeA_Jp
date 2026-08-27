package application.domain.models;

import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a user responsible for registering and managing products and warehouses
 * on NexusMarket.
 *
 * <p>Sellers cannot self-register; they are incorporated into the platform by an
 * {@code Administrator}.</p>
 *
 * <p>{@code warehouses} and {@code products} are not populated by default. They are
 * loaded on demand by the corresponding consultation service, mirroring the lazy-loading
 * pattern used for aggregate relationships throughout the domain.</p>
 */
@Getter
public class Seller extends User {

    /** Warehouses managed by the seller. Empty by default. */
    @Getter(AccessLevel.NONE)
    private final List<Warehouse> warehouses;

    /** Products published by the seller. Empty by default. */
    @Getter(AccessLevel.NONE)
    private final List<Product> products;

    /**
     * Creates a new seller.
     *
     * @param id          unique identity document number of the person
     * @param fullName    full name of the person
     * @param email       primary email address of the person
     * @param role        role assigned to the person
     * @param status      current operational status of the user
     * @param username    login name used during authentication
     * @param password    secure password hash stored by the system
     * @param warehouses  warehouses managed by the seller
     * @param products    products published by the seller
     */
    public Seller(String id, String fullName, String email, SystemRole role, UserStatus status,
                  String username, String password,
                  List<Warehouse> warehouses, List<Product> products) {
        super(id, fullName, email, role, status, username, password);
        this.warehouses = warehouses != null ? new ArrayList<>(warehouses) : new ArrayList<>();
        this.products = products != null ? new ArrayList<>(products) : new ArrayList<>();
    }

    /**
     * Returns the warehouses managed by the seller as an unmodifiable list.
     *
     * @return unmodifiable list of warehouses
     */
    public List<Warehouse> getWarehouses() {
        return Collections.unmodifiableList(warehouses);
    }

    /**
     * Returns the products published by the seller as an unmodifiable list.
     *
     * @return unmodifiable list of products
     */
    public List<Product> getProducts() {
        return Collections.unmodifiableList(products);
    }
}