package application.domain.valueobjects;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Represents a generic business catalog used throughout the NexusMarket domain.
 *
 * <p>Provides a consistent structure for controlled business values that require
 * a code, a human-readable name, and a business description.</p>
 *
 * <p>This class cannot be instantiated directly.</p>
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public abstract class DomainCatalog {

    /** Unique business identifier of the catalog value. */
    private final String code;

    /** Human-readable name displayed within the application. */
    private final String name;

    /** Business definition of the catalog value. */
    private final String description;
}