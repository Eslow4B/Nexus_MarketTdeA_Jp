package application.domain.models;

import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import lombok.Getter;

/**
 * Represents a participant of NexusMarket who interacts with the platform according
 * to the responsibilities defined by their role.
 *
 * <p>Participants whose role does not require additional attributes or relationships
 * ({@code ADMINISTRATOR}, {@code LOGISTICS_OPERATOR}, {@code SUPERVISOR}) are represented
 * directly as {@code User} instances. Participants whose role requires additional
 * attributes or relationships ({@code BUYER}, {@code SELLER}) are represented by a
 * specialized subclass.</p>
 */
@Getter
public class User extends Person {

    /** Current operational status of the user within the marketplace. */
    private final UserStatus status;

    /**
     * Creates a new user.
     *
     * @param id       unique identity document number of the person
     * @param fullName full name of the person
     * @param email    primary email address of the person
     * @param role     role assigned to the person
     * @param status   current operational status of the user
     */
    public User(String id, String fullName, String email, SystemRole role, UserStatus status) {
        super(id, fullName, email, role);
        this.status = status;
    }
}