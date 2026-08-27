package application.domain.models;

import application.domain.valueobjects.CommercialStatus;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a user who purchases products published on NexusMarket.
 *
 * <p>A buyer never manages information belonging to other buyers, warehouses,
 * or seller inventories.</p>
 */
@Getter
public class Buyer extends User {

    /** Habitual address used for order deliveries. */
    private final String primaryAddress;

    /** Secondary delivery addresses. Empty by default. */
    @Getter(AccessLevel.NONE)
    private final List<String> additionalAddresses;

    /** Condition of the buyer for placing new orders. */
    private final CommercialStatus commercialStatus;

    /**
     * Creates a new buyer.
     *
     * @param id                   unique identity document number of the person
     * @param fullName             full name of the person
     * @param email                primary email address of the person
     * @param role                 role assigned to the person
     * @param status               current operational status of the user
     * @param username             login name used during authentication
     * @param password             secure password hash stored by the system
     * @param primaryAddress       habitual address used for order deliveries
     * @param additionalAddresses  secondary delivery addresses
     * @param commercialStatus     condition of the buyer for placing new orders
     */
    public Buyer(String id, String fullName, String email, SystemRole role, UserStatus status,
                 String username, String password,
                 String primaryAddress, List<String> additionalAddresses, CommercialStatus commercialStatus) {
        super(id, fullName, email, role, status, username, password);
        this.primaryAddress = primaryAddress;
        this.additionalAddresses = additionalAddresses != null
                ? new ArrayList<>(additionalAddresses)
                : new ArrayList<>();
        this.commercialStatus = commercialStatus;
    }

    /**
     * Returns the secondary delivery addresses as an unmodifiable list.
     *
     * @return unmodifiable list of additional addresses
     */
    public List<String> getAdditionalAddresses() {
        return Collections.unmodifiableList(additionalAddresses);
    }
}