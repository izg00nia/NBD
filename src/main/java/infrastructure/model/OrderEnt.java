package infrastructure.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderEnt {
    @Id
    private UUID id;

    @Version
    private long version;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderedProductEnt> products = new ArrayList<>();

    protected OrderEnt() { }

    public OrderEnt(UUID id) {
        this.id = id;
    }

    public List<OrderedProductEnt> getProducts() {
        return products;
    }

    public UUID getId() {
        return id;
    }

    public void addProduct(OrderedProductEnt p) {
        products.add(p);
        p.setOrder(this);
    }
}