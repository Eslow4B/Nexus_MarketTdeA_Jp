package application.domain.valueobjects;

import application.domain.exceptions.DomainValidationException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

/**
 * Represents a generic business catalog used throughout the NexusMarket domain.
 *
 * <p>Provides a consistent structure for controlled business values that require
 * a code, a human-readable name, and a business description.</p>
 *
 * <p>Equality and hashCode are computed exclusively from the {@code code} attribute.
 * {@code name} and {@code description} are descriptive metadata and must not
 * participate in equality comparisons — two catalog values with the same {@code code}
 * are the same value regardless of any difference in their descriptive text.</p>
 *
 * <p>Every concrete catalog exposes {@code values()} and {@code fromCode(String)}, so that
 * adapters can translate a persisted or transported code back into the controlled value
 * without resorting to arbitrary strings.</p>
 *
 * <p>This class cannot be instantiated directly.</p>
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class DomainCatalog {

    /** Unique business identifier of the catalog value. */
    @EqualsAndHashCode.Include
    private final String code;

    /** Human-readable name displayed within the application. */
    private final String name;

    /** Business definition of the catalog value. */
    private final String description;

    /**
     * Creates a new catalog value.
     *
     * @param code        unique business identifier of the catalog value
     * @param name        human-readable name displayed within the application
     * @param description business definition of the catalog value
     */
    protected DomainCatalog(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    /**
     * Finds the catalog value whose {@code code} matches the given one.
     *
     * @param values      every allowed value of the catalog
     * @param code        business code to look up
     * @param catalogName name of the catalog, used in the error message
     * @param <T>         concrete catalog type
     * @return the matching catalog value
     * @throws DomainValidationException if no value of the catalog has the given code
     */
    protected static <T extends DomainCatalog> T findByCode(List<T> values, String code, String catalogName) {
        return values.stream()
                .filter(value -> value.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new DomainValidationException(
                        "Unknown " + catalogName + " code: " + code));
    }

    /**
     * Returns the business code of the catalog value.
     *
     * @return the business code
     */
    @Override
    public String toString() {
        return code;
    }
}
