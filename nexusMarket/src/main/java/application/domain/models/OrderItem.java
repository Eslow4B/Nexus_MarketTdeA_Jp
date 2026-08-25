package application.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Represents a single product and quantity included within an order, together with
 * the unit price at the moment of purchase.
 */
@Getter
@AllArgsConstructor
public class OrderItem {

    /** Product included in the order. */
    private final Product product;

    /** Quantity of the product requested. */
    private final int quantity;

    /** Price of the product at the moment the order was placed. */
    private final BigDecimal unitPrice;
}