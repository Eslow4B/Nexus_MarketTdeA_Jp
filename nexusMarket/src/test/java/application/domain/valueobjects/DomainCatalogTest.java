package application.domain.valueobjects;

import application.domain.exceptions.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Verifies the lookup and representation behavior shared by every {@link DomainCatalog}.
 */
class DomainCatalogTest {

    @Test
    void fromCodeReturnsTheControlledInstance() {
        assertSame(OrderStatus.PAID, OrderStatus.fromCode("PAID"));
        assertSame(SystemRole.LOGISTICS_OPERATOR, SystemRole.fromCode("LOGISTICS_OPERATOR"));
        assertSame(MovementType.RETURN, MovementType.fromCode("RETURN"));
    }

    @Test
    void fromCodeRejectsUnknownCodes() {
        assertThrows(DomainValidationException.class, () -> OrderStatus.fromCode("CANCELLED"));
        assertThrows(DomainValidationException.class, () -> UserStatus.fromCode(null));
    }

    @Test
    void valuesListEveryCatalogEntry() {
        assertEquals(5, OrderStatus.values().size());
        assertEquals(5, SystemRole.values().size());
        assertEquals(3, UserStatus.values().size());
        assertEquals(3, CommercialStatus.values().size());
        assertEquals(3, ProductStatus.values().size());
        assertEquals(2, InventoryCondition.values().size());
        assertEquals(5, MovementType.values().size());
        assertEquals(3, ShipmentStatus.values().size());
        assertEquals(4, ReturnStatus.values().size());
        assertEquals(3, RefundStatus.values().size());
    }

    @Test
    void toStringReturnsTheBusinessCode() {
        assertEquals("PENDING_PAYMENT", OrderStatus.PENDING_PAYMENT.toString());
    }
}
