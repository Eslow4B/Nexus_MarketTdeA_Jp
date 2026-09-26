package application.domain.models;

import application.domain.exceptions.DomainValidationException;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Represents any participant of NexusMarket who interacts with the platform according
 * to the responsibilities defined by their role.
 *
 * <p>Participants whose role does not require additional attributes or relationships
 * ({@code ADMINISTRATOR}, {@code LOGISTICS_OPERATOR}, {@code SUPERVISOR}) are represented
 * directly as {@code User} instances. Participants whose role requires additional
 * attributes or relationships ({@code BUYER}, {@code SELLER}) are represented by a
 * specialized subclass, which fixes its own role. A plain {@code User} can therefore never
 * carry the {@code BUYER} or {@code SELLER} role (RG-02).</p>
 *
 * <p>{@code User} is the root of the person hierarchy in NexusMarket. Unlike systems
 * where a person may exist independently of a system identity, every participant of
 * NexusMarket interacts with the platform directly as a {@code User}.</p>
 *
 * <p>Two users are the same entity when they share the same {@code id}.</p>
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    /** Unique identity document number of the person. Must be unique across the platform. */
    @EqualsAndHashCode.Include
    private final String id;

    /** Full name of the person. */
    private final String fullName;

    /**
     * Primary email address, used for access and communication. Must be unique.
     * Stored in lowercase so that uniqueness is case-insensitive (Business Decisions, DEC-03).
     */
    private final String email;

    /** Defines the participant's responsibilities and permissions within the marketplace. */
    private final SystemRole role;

    /** Current operational status of the user within the marketplace. */
    private final UserStatus status;

    /** Login name used during authentication. Must be unique across the platform. Stored in lowercase. */
    private final String username;

    /** Secure password hash stored by the system. */
    private final String password;

    /**
     * Creates a new user.
     *
     * @param id       unique identity document number of the person
     * @param fullName full name of the person
     * @param email    primary email address of the person
     * @param role     role assigned to the person
     * @param status   current operational status of the user
     * @param username login name used during authentication
     * @param password secure password hash stored by the system
     * @throws DomainValidationException if a mandatory attribute is missing, or if a plain
     *                                   {@code User} is given the {@code BUYER} or {@code SELLER} role
     */
    public User(String id, String fullName, String email, SystemRole role, UserStatus status,
                String username, String password) {
        this.id = DomainAssert.notBlank(id, "User id");
        this.fullName = DomainAssert.notBlank(fullName, "User fullName");
        this.email = DomainAssert.notBlank(email, "User email").trim().toLowerCase(Locale.ROOT);
        this.role = DomainAssert.notNull(role, "User role");
        this.status = DomainAssert.notNull(status, "User status");
        this.username = DomainAssert.notBlank(username, "User username").trim().toLowerCase(Locale.ROOT);

        DomainAssert.check(EMAIL_PATTERN.matcher(this.email).matches(), "User email has an invalid format.");
        this.password = DomainAssert.notBlank(password, "User password");

        if (getClass() == User.class
                && (SystemRole.BUYER.equals(role) || SystemRole.SELLER.equals(role))) {
            throw new DomainValidationException(
                    "Role " + role.getCode() + " must be represented by its specialized class.");
        }
    }

    /**
     * Indicates whether the user may currently access and operate on the platform.
     *
     * @return {@code true} when the user status is {@code ACTIVE}
     */
    public boolean isActive() {
        return status.allowsAccess();
    }
}
