package application.domain.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents the commercial and financial information associated with a confirmed order.
 *
 * <p>Its total is the sum of the order items' subtotals; no taxes, discounts, or shipping
 * costs are applied, since the business specification defines none (Business Decisions, DEC-23).</p>
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Invoice {

    /** Unique identifier of the invoice. */
    @EqualsAndHashCode.Include
    private final String id;

    /** Order this invoice belongs to. */
    private final Order order;

    /** Date and time when the invoice was issued. */
    private final LocalDateTime issueDate;

    /** Total amount billed for the order. Must not be negative. */
    private final BigDecimal totalAmount;

    /**
     * Creates a new invoice.
     *
     * @param id          unique identifier of the invoice
     * @param order       order this invoice belongs to
     * @param issueDate   date and time when the invoice was issued
     * @param totalAmount total amount billed for the order
     */
    public Invoice(String id, Order order, LocalDateTime issueDate, BigDecimal totalAmount) {
        this.id = DomainAssert.notBlank(id, "Invoice id");
        this.order = DomainAssert.notNull(order, "Invoice order");
        this.issueDate = DomainAssert.notNull(issueDate, "Invoice issueDate");
        this.totalAmount = DomainAssert.notNegative(totalAmount, "Invoice totalAmount");

        DomainAssert.check(totalAmount.compareTo(order.getTotalAmount()) == 0,
                "The invoice total must equal the sum of the order items' subtotals.");
    }
}
