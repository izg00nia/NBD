package domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Order {
    private final UUID id;
    private final List<OrderedProduct> products = new ArrayList<>();

    public Order(UUID id, List<OrderedProduct> products) {
        this.id = id;
        this.products.addAll(products);
    }

    public Order(UUID id) {
        this.id = id;
    }

    public void addProduct(Product product, float quantity) {
        products.add(new OrderedProduct(UUID.randomUUID(), product, quantity));
    }

    public float getTotalPrice() {
        float total = 0;
        for (OrderedProduct product : products) {
            total += product.getPriceWhenOrdered() * product.getQuantity();
        }
        return total;
    }

    public List<OrderedProduct> getProducts() {
        return Collections.unmodifiableList(products);
    }

    public UUID getId() {
        return id;
    }
}
