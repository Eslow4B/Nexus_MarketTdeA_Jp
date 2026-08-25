package application.domain.valueobjects;

import lombok.Getter;

/**
 * Represents the responsibilities and permissions assigned to a person within NexusMarket.
 *
 * <p>The role is a characteristic of {@code Person} because it represents what the person
 * means within the system. Each participant has exactly one role.</p>
 */
@Getter
public final class SystemRole extends DomainCatalog {

    /** Person who purchases products published on the marketplace. */
    public static final SystemRole BUYER =
            new SystemRole("BUYER", "Buyer", "Person who purchases products published on the marketplace.");

    /** Person responsible for registering and managing products and warehouses. */
    public static final SystemRole SELLER =
            new SystemRole("SELLER", "Seller", "Person responsible for registering and managing products and warehouses.");

    /** Person responsible for incorporating sellers and managing warehouses. */
    public static final SystemRole ADMINISTRATOR =
            new SystemRole("ADMINISTRATOR", "Administrator", "Person responsible for incorporating sellers and managing warehouses.");

    /** Person responsible for the physical operation of warehouses and dispatches. */
    public static final SystemRole LOGISTICS_OPERATOR =
            new SystemRole("LOGISTICS_OPERATOR", "Logistics Operator", "Person responsible for the physical operation of warehouses and dispatches.");

    /** Person with a consultation and operational monitoring profile. */
    public static final SystemRole SUPERVISOR =
            new SystemRole("SUPERVISOR", "Supervisor", "Person with a consultation and operational monitoring profile.");

    private SystemRole(String code, String name, String description) {
        super(code, name, description);
    }
}