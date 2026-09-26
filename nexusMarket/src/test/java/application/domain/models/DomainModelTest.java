package application.domain.models;

import application.domain.exceptions.DomainValidationException;
import application.domain.valueobjects.CommercialStatus;
import application.domain.valueobjects.InventoryCondition;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the invariants of the domain entities without any infrastructure.
 */
class DomainModelTest {

    private static final Seller SELLER =
            new Seller("900", "Seller Name", "seller@nexus.com", UserStatus.ACTIVE, "seller", "hash");

    private static final Buyer BUYER = new Buyer("100", "Buyer Name", "buyer@nexus.com", UserStatus.ACTIVE,
            "buyer", "hash", "Main street 1", null, CommercialStatus.ACTIVE);

    private static final PhysicalProduct CHAIR = new PhysicalProduct("P1", "Chair", "Wooden chair",
            List.of("Red"), BigDecimal.TEN, ProductStatus.PUBLISHED, SELLER);

    private static final DigitalProduct EBOOK = new DigitalProduct("D1", "E-book", null, null,
            BigDecimal.ONE, ProductStatus.PUBLISHED, SELLER);

    private static final Warehouse MARKETPLACE_WAREHOUSE = new Warehouse("W1", "Main", "Street 5", null);

    private static final Inventory CHAIR_STOCK =
            new Inventory("I1", CHAIR, MARKETPLACE_WAREHOUSE, 10, InventoryCondition.AVAILABLE);

    @Test
    void buyerAndSellerAlwaysCarryTheirOwnRole() {
        assertEquals(SystemRole.BUYER, BUYER.getRole());
        assertEquals(SystemRole.SELLER, SELLER.getRole());
    }

    @Test
    void plainUserCannotCarryBuyerOrSellerRole() {
        assertThrows(DomainValidationException.class, () ->
                new User("1", "Name", "a@b.com", SystemRole.BUYER, UserStatus.ACTIVE, "user", "hash"));
    }

    @Test
    void emailAndUsernameAreNormalizedAndValidated() {
        User user = new User("1", "Name", " Ana@Nexus.COM ", SystemRole.SUPERVISOR, UserStatus.ACTIVE, "Ana", "hash");
        assertEquals("ana@nexus.com", user.getEmail());
        assertEquals("ana", user.getUsername());
        assertThrows(DomainValidationException.class, () ->
                new User("1", "Name", "not-an-email", SystemRole.SUPERVISOR, UserStatus.ACTIVE, "user", "hash"));
    }

    @Test
    void productOfInactiveSellerIsNotAvailableForSale() {
        Seller blocked = new Seller("901", "Blocked", "b@nexus.com", UserStatus.BLOCKED, "blocked", "hash");
        PhysicalProduct product = new PhysicalProduct("P2", "Lamp", null, null, BigDecimal.ONE,
                ProductStatus.PUBLISHED, blocked);
        assertTrue(CHAIR.isAvailableForSale());
        assertFalse(product.isAvailableForSale());
    }

    @Test
    void inventoryCannotBeStoredInAnotherSellersWarehouse() {
        Seller other = new Seller("902", "Other", "o@nexus.com", UserStatus.ACTIVE, "other", "hash");
        Warehouse othersWarehouse = new Warehouse("W2", "Other", "Street 9", other);
        assertThrows(DomainValidationException.class, () ->
                new Inventory("I2", CHAIR, othersWarehouse, 5, InventoryCondition.AVAILABLE));
        assertThrows(DomainValidationException.class, () ->
                new Inventory("I3", CHAIR, MARKETPLACE_WAREHOUSE, -1, InventoryCondition.AVAILABLE));
    }

    @Test
    void damagedInventoryIsNeverReservable() {
        Inventory damaged = new Inventory("I4", CHAIR, MARKETPLACE_WAREHOUSE, 50, InventoryCondition.DAMAGED);
        assertFalse(damaged.canReserve(1));
        assertTrue(CHAIR_STOCK.canReserve(10));
        assertFalse(CHAIR_STOCK.canReserve(11));
    }

    @Test
    void confirmedPhysicalOrderRequiresAddressAndReservation() {
        List<OrderItem> unreserved = List.of(new OrderItem(CHAIR, 1, BigDecimal.TEN, null));
        assertThrows(DomainValidationException.class, () ->
                new Order("O1", BUYER, unreserved, OrderStatus.PENDING_PAYMENT, LocalDateTime.now(), "Main street 1"));

        List<OrderItem> reserved = List.of(new OrderItem(CHAIR, 1, BigDecimal.TEN, CHAIR_STOCK));
        assertThrows(DomainValidationException.class, () ->
                new Order("O1", BUYER, reserved, OrderStatus.PENDING_PAYMENT, LocalDateTime.now(), null));
    }

    @Test
    void digitalOnlyOrderCanNeverBeShipped() {
        List<OrderItem> items = List.of(new OrderItem(EBOOK, 1, BigDecimal.ONE, null));
        assertThrows(DomainValidationException.class, () ->
                new Order("O1", BUYER, items, OrderStatus.SHIPPED, LocalDateTime.now(), null));
    }

    @Test
    void orderItemsCannotBeModifiedFromOutside() {
        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem(CHAIR, 1, BigDecimal.ONE, null));
        Order order = new Order("O1", BUYER, items, OrderStatus.CART, LocalDateTime.now(), null);

        items.add(new OrderItem(EBOOK, 1, BigDecimal.ONE, null));

        assertEquals(1, order.getItems().size());
        assertThrows(UnsupportedOperationException.class, () -> order.getItems().clear());
    }

    @Test
    void returnCannotExceedPurchasedQuantity() {
        Order delivered = new Order("O1", BUYER, List.of(new OrderItem(CHAIR, 2, BigDecimal.TEN, CHAIR_STOCK)),
                OrderStatus.DELIVERED, LocalDateTime.now(), "Main street 1");

        ReturnRequest valid = new ReturnRequest("R1", delivered, List.of(new ReturnItem(CHAIR, 2)), "Broken",
                application.domain.valueobjects.ReturnStatus.REQUESTED, LocalDateTime.now());
        assertEquals(new BigDecimal("20"), valid.getRefundableAmount());

        assertThrows(DomainValidationException.class, () ->
                new ReturnRequest("R2", delivered, List.of(new ReturnItem(CHAIR, 3)), "Broken",
                        application.domain.valueobjects.ReturnStatus.REQUESTED, LocalDateTime.now()));
    }
}
