package application.domain.exceptions;

/**
 * Base type of every business exception raised by the NexusMarket domain.
 *
 * <p>Having a single supertype lets input adapters recognize any business failure and
 * translate it uniformly (for example, into an HTTP status), without confusing it with a
 * technical error. The domain itself never decides how an exception is presented.</p>
 */
public abstract class DomainException extends RuntimeException {

    /**
     * Creates a new domain exception.
     *
     * @param message description of the business rule that was violated
     */
    protected DomainException(String message) {
        super(message);
    }
}
