package application.domain.models;

import application.domain.valueobjects.RefundStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Represents the reimbursement of funds to a buyer as a result of an approved return.
 */
@Getter
@AllArgsConstructor
public class Refund {

    /** Unique identifier of the refund. */
    private final String id;

    /** Return that originated the refund. */
    private final ReturnRequest returnRequest;

    /** Amount reimbursed to the buyer. */
    private final BigDecimal amount;

    /** Current status of the refund process. */
    private final RefundStatus status;
}