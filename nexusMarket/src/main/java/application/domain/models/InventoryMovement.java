package application.domain.models;

import application.domain.valueobjects.MovementType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Represents a significant change applied to an inventory record, such as an incoming
 * stock entry, a reservation, a sale, an adjustment, or a return.
 *
 * <p>An inventory movement provides traceability for changes in stock, in the same way
 * an operation record provides traceability for actions performed on any business entity.</p>
 */
@Getter
@AllArgsConstructor
public class InventoryMovement {

    /** Unique identifier of the movement. */
    private final String id;

    /** Inventory record affected by the movement. */
    private final Inventory inventory;

    /** Category of the inventory movement. */
    private final MovementType movementType;

    /** Quantity involved in the movement. */
    private final int quantity;

    /** Date and time when the movement occurred. */
    private final LocalDateTime movementDate;
}