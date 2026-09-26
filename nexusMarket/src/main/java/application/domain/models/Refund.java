package application.domain.models;

import application.domain.valueobjects.RefundStatus;
import application.domain.valueobjects.ReturnStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents the reimbursement of funds to a buyer as a result of a completed return.
 *
 * <p>A refund only exists for a {@code COMPLETED} return (the product was physically received),
 * and its amount never exceeds the return's refundable amount (Business Decisions, DEC-25).</p>
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Refund {

    /** Unique identifier of the refund. */
    @EqualsAndHashCode.Include
    private final String id;

    /** Return that originated the refund. */
    private final ReturnRequest returnRequest;

    /** Amount reimbursed to the buyer. Must not be negative nor exceed the refundable amount. */
    private final BigDecimal amount;

    /** Current status of the refund process. */
    private final RefundStatus status;

    /** Date and time when the refund was created. */
    private final LocalDateTime creationDate;

    /**
     * Creates a new refund.
     *
     * @param id            unique identifier of the refund
     * @param returnRequest return that originated the refund
     * @param amount        amount reimbursed to the buyer
     * @param status        current status of the refund process
     * @param creationDate  date and time when the refund was created
     */
    public Refund(String id, ReturnRequest returnRequest, BigDecimal amount, RefundStatus status,
                  LocalDateTime creationDate) {
        this.id = DomainAssert.notBlank(id, "Refund id");
        this.returnRequest = DomainAssert.notNull(returnRequest, "Refund returnRequest");
        this.amount = DomainAssert.notNegative(amount, "Refund amount");
        this.status = DomainAssert.notNull(status, "Refund status");
        this.creationDate = DomainAssert.notNull(creationDate, "Refund creationDate");

        DomainAssert.check(ReturnStatus.COMPLETED.equals(returnRequest.getStatus()),
                "A refund can only be created for a COMPLETED return.");
        DomainAssert.check(amount.compareTo(returnRequest.getRefundableAmount()) <= 0,
                "The refund amount cannot exceed the refundable amount of the return.");
    }
}
