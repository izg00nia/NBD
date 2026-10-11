package infrastructure.model;

import domain.model.ItemCategory;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class GeneralItemEnt extends ProductEnt{

    @Enumerated(EnumType.STRING)
    private ItemCategory category;

    protected GeneralItemEnt() { }

    public GeneralItemEnt(UUID id, float price, String name, ItemCategory category) {
        super(id, price, name);
        this.category = category;
    }

    public ItemCategory getCategory() {
        return category;
    }

    public void setCategory(ItemCategory category) {
        this.category = category;
    }
}
