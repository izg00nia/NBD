package domain.model;

import java.util.UUID;

public class OrderedProduct {
    private final UUID id;
    private final Product product;
    private final float quantity;
    private final float priceWhenOrdered;

    public OrderedProduct(UUID id, Product product, float quantity, float priceWhenOrdered) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.priceWhenOrdered = priceWhenOrdered;
    }

    public OrderedProduct(UUID id, Product product, float quantity) {
        this(id, product, quantity, product.getPrice());
    }

    public UUID getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public float getQuantity() {
        return quantity;
    }

    public float getPriceWhenOrdered() {
        return priceWhenOrdered;
    }
}
