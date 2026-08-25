package application.domain.models;

import application.domain.valueobjects.SystemRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents any identifiable person interacting with NexusMarket.
 *
 * <p>This abstract class centralizes the identity information shared by every
 * participant of the marketplace, regardless of their role.</p>
 *
 * <p>The role assigned to a person represents their responsibilities and
 * permissions within the system.</p>
 *
 * <p>This class cannot be instantiated directly.</p>
 */
@Getter
@AllArgsConstructor
public abstract class Person {

    /** Unique identity document number of the person. Must be unique across the platform. */
    private final String id;

    /** Full name of the person. */
    private final String fullName;

    /** Primary email address, used for access and communication. Must be unique. */
    private final String email;

    /** Defines the person's responsibilities and permissions within the marketplace. */
    private final SystemRole role;
}