package application.domain.valueobjects;

import lombok.Getter;

/**
 * Represents the current operational status of a user within the marketplace.
 */
@Getter
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

    private UserStatus(String code, String name, String description) {
        super(code, name, description);
    }
}