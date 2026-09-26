package application.domain.models;

import application.domain.valueobjects.CommercialStatus;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import lombok.Getter;

import java.util.List;

/**
 * Represents a user who purchases products published on NexusMarket.
 *
 * <p>A buyer never manages information belonging to other buyers, warehouses,
 * or seller inventories.</p>
 *
 * <p>The role of a buyer is always {@link SystemRole#BUYER}; it is fixed by this class
 * rather than received as a parameter, so it can never be inconsistent (RG-02).</p>
 */
@Getter
public class Buyer extends User {

    /** Habitual address used for order deliveries. */
    private final String primaryAddress;

    /** Secondary delivery addresses, as an unmodifiable list. Empty by default. */
    private final List<String> additionalAddresses;

    /** Condition of the buyer for placing new orders. */
    private final CommercialStatus commercialStatus;

    /**
     * Creates a new buyer.
     *
     * @param id                   unique identity document number of the person
     * @param fullName             full name of the person
     * @param email                primary email address of the person
     * @param status               current operational status of the user
     * @param username             login name used during authentication
     * @param password             secure password hash stored by the system
     * @param primaryAddress       habitual address used for order deliveries
     * @param additionalAddresses  secondary delivery addresses; {@code null} means none
     * @param commercialStatus     condition of the buyer for placing new orders
     */
    public Buyer(String id, String fullName, String email, UserStatus status,
                 String username, String password,
                 String primaryAddress, List<String> additionalAddresses, CommercialStatus commercialStatus) {
        super(id, fullName, email, SystemRole.BUYER, status, username, password);
        this.primaryAddress = DomainAssert.notBlank(primaryAddress, "Buyer primaryAddress");
        this.additionalAddresses = DomainAssert.immutableCopy(additionalAddresses, "Buyer additionalAddresses");
        this.commercialStatus = DomainAssert.notNull(commercialStatus, "Buyer commercialStatus");
    }

    /**
     * Indicates whether the given address is one of the buyer's registered delivery addresses
     * (primary or additional), as required to confirm an order (Business Decisions, DEC-18).
     *
     * @param address address to check
     * @return {@code true} when the address is the primary address or one of the additional ones
     */
    public boolean hasAddress(String address) {
        return primaryAddress.equals(address) || additionalAddresses.contains(address);
    }
}
