package infrastructure.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class GeneralItemEnt extends ProductEnt{

    @Enumerated(EnumType.STRING)
    private ItemCategoryEnt category;

    protected GeneralItemEnt() { }

    public GeneralItemEnt(UUID id, float price, String name, ItemCategoryEnt category) {
        super(id, price, name);
        this.category = category;
    }

    public ItemCategoryEnt getCategory() {
        return category;
    }
}
