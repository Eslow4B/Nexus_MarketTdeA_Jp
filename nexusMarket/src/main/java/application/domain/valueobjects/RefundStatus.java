package application.domain.valueobjects;

import java.util.List;

/**
 * Represents the current status of a refund associated with a completed return.
 */
public final class RefundStatus extends DomainCatalog {

    /** Refund has been opened for a completed return and awaits the Administrator's decision. */
    public static final RefundStatus PENDING =
            new RefundStatus("PENDING", "Pending",
                    "Refund has been opened for a completed return and awaits the Administrator's decision.");

    /** Refund has been completed and funds returned. */
    public static final RefundStatus PROCESSED =
            new RefundStatus("PROCESSED", "Processed", "Refund has been completed and funds returned.");

    /** Refund request has been denied. */
    public static final RefundStatus REJECTED =
            new RefundStatus("REJECTED", "Rejected", "Refund request has been denied.");

    private static final List<RefundStatus> VALUES = List.of(PENDING, PROCESSED, REJECTED);

    /**
     * Returns every allowed value of this catalog.
     *
     * @return unmodifiable list of values
     */
    public static List<RefundStatus> values() {
        return VALUES;
    }

    /**
     * Returns the value whose business code matches the given one.
     *
     * @param code business code to look up
     * @return the matching value
     * @throws application.domain.exceptions.DomainValidationException if the code is unknown
     */
    public static RefundStatus fromCode(String code) {
        return findByCode(VALUES, code, "RefundStatus");
    }

    private RefundStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
