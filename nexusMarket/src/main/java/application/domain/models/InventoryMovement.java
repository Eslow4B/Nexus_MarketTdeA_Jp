package application.domain.models;

import application.domain.valueobjects.MovementType;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Represents a significant change applied to an inventory record, such as an incoming
 * stock entry, a reservation, a sale, an adjustment, or a return.
 *
 * <p>An inventory movement provides traceability for changes in stock, in the same way
 * an operation record provides traceability for actions performed on any business entity.</p>
 *
 * <p>Movements caused by an order ({@code RESERVATION}, {@code SALE_OUTBOUND}, {@code RETURN},
 * and the {@code ADJUSTMENT} that releases a reservation) reference that order, so that each
 * one can be matched with the reservation that originated it. A manual {@code ADJUSTMENT}
 * references no order and must state its reason instead.</p>
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class InventoryMovement {

    /** Unique identifier of the movement. */
    @EqualsAndHashCode.Include
    private final String id;

    /** Inventory record affected by the movement. */
    private final Inventory inventory;

    /** Category of the inventory movement. */
    private final MovementType movementType;

    /**
     * Quantity involved in the movement. It is greater than zero for every type except
     * {@code ADJUSTMENT}, where it is the signed correction applied to the available quantity
     * (zero when only the condition of the stock changed).
     */
    private final int quantity;

    /** Date and time when the movement occurred. */
    private final LocalDateTime movementDate;

    /** Order that caused the movement. Absent ({@code null}) for inbound stock and manual adjustments. */
    private final Order order;

    /** Reason stated for the movement. Mandatory for manual adjustments; optional otherwise. */
    private final String reason;

    /**
     * Creates a new inventory movement.
     *
     * @param id           unique identifier of the movement
     * @param inventory    inventory record affected by the movement
     * @param movementType category of the inventory movement
     * @param quantity     quantity involved in the movement
     * @param movementDate date and time when the movement occurred
     * @param order        order that caused the movement, or {@code null}
     * @param reason       reason stated for the movement, or {@code null}
     */
    public InventoryMovement(String id, Inventory inventory, MovementType movementType,
                             int quantity, LocalDateTime movementDate, Order order, String reason) {
        this.id = DomainAssert.notBlank(id, "InventoryMovement id");
        this.inventory = DomainAssert.notNull(inventory, "InventoryMovement inventory");
        this.movementType = DomainAssert.notNull(movementType, "InventoryMovement movementType");
        this.movementDate = DomainAssert.notNull(movementDate, "InventoryMovement movementDate");
        this.order = order;
        this.reason = reason;

        if (MovementType.ADJUSTMENT.equals(movementType)) {
            DomainAssert.check(order != null || (reason != null && !reason.isBlank()),
                    "A manual ADJUSTMENT movement must state its reason.");
            this.quantity = quantity;
        } else {
            this.quantity = DomainAssert.positive(quantity, "InventoryMovement quantity");
        }

        boolean requiresOrder = MovementType.RESERVATION.equals(movementType)
                || MovementType.SALE_OUTBOUND.equals(movementType)
                || MovementType.RETURN.equals(movementType);
        DomainAssert.check(!requiresOrder || order != null,
                "A " + movementType.getCode() + " movement must reference the order that caused it.");
    }
}
