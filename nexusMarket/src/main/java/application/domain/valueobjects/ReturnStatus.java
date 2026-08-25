package application.domain.valueobjects;

import lombok.Getter;

/**
 * Represents the current status of a product return request.
 */
@Getter
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

    private ReturnStatus(String code, String name, String description) {
        super(code, name, description);
    }
}