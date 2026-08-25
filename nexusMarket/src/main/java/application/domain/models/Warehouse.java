package application.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a physical location where product inventory is stored and managed.
 *
 * <p>A warehouse may belong to the Marketplace itself or to a specific seller.</p>
 */
@Getter
@AllArgsConstructor
public class Warehouse {

    /** Unique identifier of the warehouse. */
    private final String id;

    /** Descriptive name of the warehouse. */
    private final String name;

    /** Physical location of the warehouse. */
    private final String address;

    /** Seller who owns the warehouse. Absent when the warehouse belongs to the Marketplace. */
    private final Seller owner;
}