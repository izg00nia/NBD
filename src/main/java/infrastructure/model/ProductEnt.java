package infrastructure.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ProductEnt {
    @Id
    private UUID id;

    private float price;
    private String name;

    @Version
    private long version;

    protected ProductEnt() { }

    public ProductEnt(UUID id, float price, String name) {
        this.id = id;
        this.price = price;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public float getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setName(String name) {
        this.name = name;
    }
}
