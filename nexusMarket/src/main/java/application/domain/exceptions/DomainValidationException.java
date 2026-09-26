package application.domain.exceptions;

/**
 * Raised when a domain object is created with data that violates one of its own
 * invariants, such as a missing mandatory attribute, a blank full name, a negative
 * stock quantity, or an unknown catalog code.
 *
 * <p>Unlike the other business exceptions, this one does not depend on persisted state:
 * it is raised by the entity or value object itself, before any service logic runs.</p>
 */
public class DomainValidationException extends DomainException {

    /**
     * Creates a new domain validation exception.
     *
     * @param message description of the violated invariant
     */
    public DomainValidationException(String message) {
        super(message);
    }
}
