package domain.model;

import java.util.UUID;

public abstract class Product {
    private final UUID id;
    private float price;
    private String name;

    protected Product(UUID id, float price, String name) {
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
