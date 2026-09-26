package application.domain.models;

import application.domain.exceptions.DomainValidationException;
import application.domain.valueobjects.ReturnStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a buyer's request to return one or more products from a delivered order.
 *
 * <p>Named {@code ReturnRequest} because {@code Return} is a reserved keyword in Java.</p>
 *
 * <p>A return never modifies its order, which remains {@code DELIVERED} (Business Decisions,
 * DEC-19). Invariants validated at construction (DEC-24): the order is {@code DELIVERED};
 * the request contains at least one item; each returned product belongs to the order,
 * appears only once, and is returned in a quantity not greater than the purchased one.</p>
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ReturnRequest {

    /** Unique identifier of the return. */
    @EqualsAndHashCode.Include
    private final String id;

    /** Order associated with the return. */
    private final Order order;

    /** Physical products and quantities being returned, as an unmodifiable list. */
    private final List<ReturnItem> items;

    /** Reason provided by the buyer for the return. */
    private final String reason;

    /** Current status of the return process. */
    private final ReturnStatus status;

    /** Date and time when the buyer requested the return. */
    private final LocalDateTime requestDate;

    /**
     * Creates a new return request.
     *
     * @param id          unique identifier of the return
     * @param order       order associated with the return
     * @param items       physical products and quantities being returned
     * @param reason      reason provided by the buyer for the return
     * @param status      current status of the return process
     * @param requestDate date and time when the buyer requested the return
     */
    public ReturnRequest(String id, Order order, List<ReturnItem> items, String reason,
                         ReturnStatus status, LocalDateTime requestDate) {
        this.id = DomainAssert.notBlank(id, "ReturnRequest id");
        this.order = DomainAssert.notNull(order, "ReturnRequest order");
        this.items = DomainAssert.immutableCopy(items, "ReturnRequest items");
        this.reason = DomainAssert.notBlank(reason, "ReturnRequest reason");
        this.status = DomainAssert.notNull(status, "ReturnRequest status");
        this.requestDate = DomainAssert.notNull(requestDate, "ReturnRequest requestDate");

        DomainAssert.check(order.isFinalized(), "Only a DELIVERED order can be returned.");
        DomainAssert.check(!this.items.isEmpty(), "A return must include at least one item.");

        long distinctProducts = this.items.stream().map(ReturnItem::getProduct).distinct().count();
        DomainAssert.check(distinctProducts == this.items.size(),
                "A product may appear only once among the items of a return.");

        for (ReturnItem item : this.items) {
            OrderItem purchased = order.findItem(item.getProduct())
                    .orElseThrow(() -> new DomainValidationException(
                            "Product " + item.getProduct().getId() + " is not part of the returned order."));
            DomainAssert.check(item.getQuantity() <= purchased.getQuantity(),
                    "The returned quantity of product " + item.getProduct().getId()
                            + " exceeds the purchased quantity.");
        }
    }

    /**
     * Returns the amount to reimburse for this return: for each returned item, the unit price
     * paid in the order multiplied by the returned quantity (DEC-25).
     *
     * @return refundable amount
     */
    public BigDecimal getRefundableAmount() {
        return items.stream()
                .map(item -> order.findItem(item.getProduct())
                        .map(OrderItem::getUnitPrice)
                        .orElse(BigDecimal.ZERO)
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
