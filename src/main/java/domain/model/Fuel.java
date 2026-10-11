package domain.model;

import java.util.UUID;

public class Fuel extends Product{
    final private String standard;

    public Fuel(UUID id, float price, String name, String standard) {
        super(id, price, name);
        this.standard = standard;
    }

    public String getStandard() {
        return standard;
    }
}
