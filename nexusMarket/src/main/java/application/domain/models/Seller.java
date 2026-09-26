package application.domain.models;

import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;

/**
 * Represents a user responsible for registering and managing their own products
 * on NexusMarket.
 *
 * <p>Sellers cannot self-register; they are incorporated into the platform by an
 * {@code Administrator}.</p>
 *
 * <p>This class has no attributes of its own. It exists as a distinct type so that
 * ownership relationships ({@code Product.seller}, {@code Warehouse.owner}) can only
 * reference a seller. The warehouses and products owned by a seller are not held here:
 * they are retrieved through their repositories, since holding them in both directions
 * would make immutable instances impossible to build.</p>
 *
 * <p>The role of a seller is always {@link SystemRole#SELLER}; it is fixed by this class
 * rather than received as a parameter, so it can never be inconsistent (RG-02).</p>
 */
public class Seller extends User {

    /**
     * Creates a new seller.
     *
     * @param id       unique identity document number of the person
     * @param fullName full name of the person
     * @param email    primary email address of the person
     * @param status   current operational status of the user
     * @param username login name used during authentication
     * @param password secure password hash stored by the system
     */
    public Seller(String id, String fullName, String email, UserStatus status,
                  String username, String password) {
        super(id, fullName, email, SystemRole.SELLER, status, username, password);
    }
}
