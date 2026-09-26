package application.domain.valueobjects;

import java.util.List;

/**
 * Represents the responsibilities and permissions assigned to a person within NexusMarket.
 *
 * <p>The role is a characteristic of {@code User} because it represents what the person
 * means within the system. Each participant has exactly one role (RG-02).</p>
 */
public final class SystemRole extends DomainCatalog {

    /** Person who purchases products published on the marketplace. */
    public static final SystemRole BUYER =
            new SystemRole("BUYER", "Buyer", "Person who purchases products published on the marketplace.");

    /** Person responsible for registering and managing their own products. */
    public static final SystemRole SELLER =
            new SystemRole("SELLER", "Seller", "Person responsible for registering and managing their own products.");

    /** Person responsible for administering sellers and warehouses. */
    public static final SystemRole ADMINISTRATOR =
            new SystemRole("ADMINISTRATOR", "Administrator", "Person responsible for administering sellers and warehouses.");

    /** Person responsible for the physical operation of warehouses and dispatches. */
    public static final SystemRole LOGISTICS_OPERATOR =
            new SystemRole("LOGISTICS_OPERATOR", "Logistics Operator", "Person responsible for the physical operation of warehouses and dispatches.");

    /** Person with a consultation and operational monitoring profile. */
    public static final SystemRole SUPERVISOR =
            new SystemRole("SUPERVISOR", "Supervisor", "Person with a consultation and operational monitoring profile.");

    private static final List<SystemRole> VALUES =
            List.of(BUYER, SELLER, ADMINISTRATOR, LOGISTICS_OPERATOR, SUPERVISOR);

    /**
     * Returns every allowed value of this catalog.
     *
     * @return unmodifiable list of values
     */
    public static List<SystemRole> values() {
        return VALUES;
    }

    /**
     * Returns the value whose business code matches the given one.
     *
     * @param code business code to look up
     * @return the matching value
     * @throws application.domain.exceptions.DomainValidationException if the code is unknown
     */
    public static SystemRole fromCode(String code) {
        return findByCode(VALUES, code, "SystemRole");
    }

    private SystemRole(String code, String name, String description) {
        super(code, name, description);
    }
}
