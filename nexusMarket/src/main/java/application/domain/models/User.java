package application.domain.models;

import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents any participant of NexusMarket who interacts with the platform according
 * to the responsibilities defined by their role.
 *
 * <p>Participants whose role does not require additional attributes or relationships
 * ({@code ADMINISTRATOR}, {@code LOGISTICS_OPERATOR}, {@code SUPERVISOR}) are represented
 * directly as {@code User} instances. Participants whose role requires additional
 * attributes or relationships ({@code BUYER}, {@code SELLER}) are represented by a
 * specialized subclass.</p>
 *
 * <p>{@code User} is the root of the person hierarchy in NexusMarket. Unlike systems
 * where a person may exist independently of a system identity, every participant of
 * NexusMarket interacts with the platform directly as a {@code User}.</p>
 */
@Getter
@AllArgsConstructor
public class User {

    /** Unique identity document number of the person. Must be unique across the platform. */
    private final String id;

    /** Full name of the person. */
    private final String fullName;

    /** Primary email address, used for access and communication. Must be unique. */
    private final String email;

    /** Defines the participant's responsibilities and permissions within the marketplace. */
    private final SystemRole role;

    /** Current operational status of the user within the marketplace. */
    private final UserStatus status;

    /** Login name used during authentication. Must be unique across the platform. */
    private final String username;

    /** Secure password hash stored by the system. */
    private final String password;
}