package domain.model;

import java.util.UUID;

public class GeneralItem extends Product{
    private ItemCategory category;

    public GeneralItem(UUID id, float price, String name, ItemCategory category) {
        super(id, price, name);
        this.category = category;
    }

    public ItemCategory getCategory() {
        return category;
    }
}
