package application.domain.valueobjects;

import lombok.Getter;

/**
 * Represents the commercial condition of a buyer for placing new orders.
 */
@Getter
public final class CommercialStatus extends DomainCatalog {

    /** Buyer can place new orders normally. */
    public static final CommercialStatus ACTIVE =
            new CommercialStatus("ACTIVE", "Active", "Buyer can place new orders normally.");

    /** Buyer has limitations due to a pending situation. */
    public static final CommercialStatus RESTRICTED =
            new CommercialStatus("RESTRICTED", "Restricted", "Buyer has limitations due to a pending situation.");

    /** Buyer is temporarily prevented from placing new orders. */
    public static final CommercialStatus SUSPENDED =
            new CommercialStatus("SUSPENDED", "Suspended", "Buyer is temporarily prevented from placing new orders.");

    private CommercialStatus(String code, String name, String description) {
        super(code, name, description);
    }
}