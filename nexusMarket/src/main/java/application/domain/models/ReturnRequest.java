package application.domain.models;

import application.domain.valueobjects.ReturnStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a buyer's request to return one or more products from a delivered order.
 *
 * <p>Named {@code ReturnRequest} because {@code Return} is a reserved keyword in Java.</p>
 */
@Getter
@AllArgsConstructor
public class ReturnRequest {

    /** Unique identifier of the return. */
    private final String id;

    /** Order associated with the return. */
    private final Order order;

    /** Reason provided by the buyer for the return. */
    private final String reason;

    /** Current status of the return process. */
    private final ReturnStatus status;
}