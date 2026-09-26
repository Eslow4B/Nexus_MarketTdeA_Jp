package application.domain.valueobjects;

import java.util.List;

/**
 * Represents the current operational status of a user within the marketplace.
 */
public final class UserStatus extends DomainCatalog {

    /** User can access and operate on the platform normally. */
    public static final UserStatus ACTIVE =
            new UserStatus("ACTIVE", "Active", "User can access and operate on the platform normally.");

    /** User exists but is not currently active on the platform. */
    public static final UserStatus INACTIVE =
            new UserStatus("INACTIVE", "Inactive", "User exists but is not currently active on the platform.");

    /** User access has been suspended. */
    public static final UserStatus BLOCKED =
            new UserStatus("BLOCKED", "Blocked", "User access has been suspended.");

    private static final List<UserStatus> VALUES = List.of(ACTIVE, INACTIVE, BLOCKED);

    /**
     * Returns every allowed value of this catalog.
     *
     * @return unmodifiable list of values
     */
    public static List<UserStatus> values() {
        return VALUES;
    }

    /**
     * Returns the value whose business code matches the given one.
     *
     * @param code business code to look up
     * @return the matching value
     * @throws application.domain.exceptions.DomainValidationException if the code is unknown
     */
    public static UserStatus fromCode(String code) {
        return findByCode(VALUES, code, "UserStatus");
    }

    /**
     * Indicates whether a user in this status may access and operate on the platform.
     *
     * @return {@code true} only when the status is {@code ACTIVE}
     */
    public boolean allowsAccess() {
        return ACTIVE.equals(this);
    }

    private UserStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
