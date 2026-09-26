package application.domain.models;

import application.domain.exceptions.DomainValidationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * Guard clauses shared by the domain entities to validate their invariants at construction time.
 *
 * <p>Every violation raises {@link DomainValidationException}, never a generic exception.</p>
 */
final class DomainAssert {

    private DomainAssert() {
    }

    static void check(boolean condition, String message) {
        if (!condition) {
            throw new DomainValidationException(message);
        }
    }

    static BigDecimal positive(BigDecimal value, String field) {
        notNull(value, field);
        if (value.signum() <= 0) {
            throw new DomainValidationException(field + " must be greater than zero.");
        }
        return value;
    }

    static <T> T notNull(T value, String field) {
        if (value == null) {
            throw new DomainValidationException(field + " is mandatory.");
        }
        return value;
    }

    static String notBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException(field + " is mandatory and must not be blank.");
        }
        return value;
    }

    static int notNegative(int value, String field) {
        if (value < 0) {
            throw new DomainValidationException(field + " must never be negative.");
        }
        return value;
    }

    static int positive(int value, String field) {
        if (value <= 0) {
            throw new DomainValidationException(field + " must be greater than zero.");
        }
        return value;
    }

    static BigDecimal notNegative(BigDecimal value, String field) {
        notNull(value, field);
        if (value.signum() < 0) {
            throw new DomainValidationException(field + " must never be negative.");
        }
        return value;
    }

    /**
     * Returns an immutable copy of the given list, or an empty list when it is {@code null}.
     */
    static <T> List<T> immutableCopy(List<T> values, String field) {
        if (values == null) {
            return List.of();
        }
        if (values.stream().anyMatch(Objects::isNull)) {
            throw new DomainValidationException(field + " must not contain empty elements.");
        }
        return List.copyOf(values);
    }
}
