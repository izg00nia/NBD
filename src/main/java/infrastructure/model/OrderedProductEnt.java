package infrastructure.model;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.util.UUID;

@Entity
public class OrderedProductEnt {
    @Id
    private UUID id;

    @ManyToOne (optional = false)
    private OrderEnt order;

    @ManyToOne (optional = false)
    private ProductEnt product;

    private float quantity;
    private float priceWhenOrdered;

    protected OrderedProductEnt() { }

    public OrderedProductEnt(UUID id, ProductEnt product, float quantity, float priceWhenOrdered) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.priceWhenOrdered = priceWhenOrdered;
    }

    public UUID getId() {
        return id;
    }

    public OrderEnt getOrder() {
        return order;
    }

    public ProductEnt getProduct() {
        return product;
    }

    public float getQuantity() {
        return quantity;
    }

    public float getPriceWhenOrdered() {
        return priceWhenOrdered;
    }

    public void setOrder(OrderEnt order) {
        this.order = order;
    }
}
