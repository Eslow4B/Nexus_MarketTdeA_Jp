package application.domain.valueobjects;

import lombok.Getter;

/**
 * Represents the current status of a refund associated with an approved return.
 */
@Getter
public final class RefundStatus extends DomainCatalog {

    /** Refund has been requested and awaits processing. */
    public static final RefundStatus PENDING =
            new RefundStatus("PENDING", "Pending", "Refund has been requested and awaits processing.");

    /** Refund has been completed and funds returned. */
    public static final RefundStatus PROCESSED =
            new RefundStatus("PROCESSED", "Processed", "Refund has been completed and funds returned.");

    /** Refund request has been denied. */
    public static final RefundStatus REJECTED =
            new RefundStatus("REJECTED", "Rejected", "Refund request has been denied.");

    private RefundStatus(String code, String name, String description) {
        super(code, name, description);
    }
}