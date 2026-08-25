package application.domain.models;

import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import lombok.Getter;

/**
 * Represents a user responsible for registering and managing products and warehouses
 * on NexusMarket.
 *
 * <p>Sellers cannot self-register; they are incorporated into the platform by an
 * {@code Administrator}.</p>
 *
 * <p>A seller has no additional attributes of its own beyond those inherited from
 * {@code User}. What distinguishes a seller within the domain is the set of entities
 * it owns — its warehouses and its products — rather than additional scalar attributes.</p>
 */
@Getter
public class Seller extends User {

    /**
     * Creates a new seller.
     *
     * @param id       unique identity document number of the person
     * @param fullName full name of the person
     * @param email    primary email address of the person
     * @param role     role assigned to the person
     * @param status   current operational status of the user
     */
    public Seller(String id, String fullName, String email, SystemRole role, UserStatus status) {
        super(id, fullName, email, role, status);
    }
}