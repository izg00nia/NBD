package infrastructure.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class FuelEnt extends ProductEnt{

    private String standard;

    protected FuelEnt() { }

    public FuelEnt(UUID id, float price, String name, String standard) {
        super(id, price, name);
        this.standard = standard;
    }

    public String getStandard() {
        return standard;
    }

}
