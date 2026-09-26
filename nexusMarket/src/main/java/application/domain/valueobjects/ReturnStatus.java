package application.domain.valueobjects;

import java.util.List;

/**
 * Represents the current status of a product return request.
 */
public final class ReturnStatus extends DomainCatalog {

    /** Return has been requested by the buyer. */
    public static final ReturnStatus REQUESTED =
            new ReturnStatus("REQUESTED", "Requested", "Return has been requested by the buyer.");

    /** Return has been reviewed and approved. */
    public static final ReturnStatus APPROVED =
            new ReturnStatus("APPROVED", "Approved", "Return has been reviewed and approved.");

    /** Return request has been denied. */
    public static final ReturnStatus REJECTED =
            new ReturnStatus("REJECTED", "Rejected", "Return request has been denied.");

    /** Returned product has been received and processed. */
    public static final ReturnStatus COMPLETED =
            new ReturnStatus("COMPLETED", "Completed", "Returned product has been received and processed.");

    private static final List<ReturnStatus> VALUES = List.of(REQUESTED, APPROVED, REJECTED, COMPLETED);

    /**
     * Returns every allowed value of this catalog.
     *
     * @return unmodifiable list of values
     */
    public static List<ReturnStatus> values() {
        return VALUES;
    }

    /**
     * Returns the value whose business code matches the given one.
     *
     * @param code business code to look up
     * @return the matching value
     * @throws application.domain.exceptions.DomainValidationException if the code is unknown
     */
    public static ReturnStatus fromCode(String code) {
        return findByCode(VALUES, code, "ReturnStatus");
    }

    private ReturnStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
