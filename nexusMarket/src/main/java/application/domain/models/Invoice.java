package application.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents the commercial and financial information associated with a confirmed order.
 */
@Getter
@AllArgsConstructor
public class Invoice {

    /** Unique identifier of the invoice. */
    private final String id;

    /** Order this invoice belongs to. */
    private final Order order;

    /** Date and time when the invoice was issued. */
    private final LocalDateTime issueDate;

    /** Total amount billed for the order. */
    private final BigDecimal totalAmount;
}