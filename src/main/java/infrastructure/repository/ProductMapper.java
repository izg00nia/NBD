package infrastructure.repository;

import domain.model.*;
import infrastructure.model.*;
import org.hibernate.Hibernate;

final class ProductMapper {
    private ProductMapper() { }

    static ProductEnt toEntity(Product product) {
        if (product instanceof Fuel f) {
            return new FuelEnt(f.getId(), f.getPrice(), f.getName(), f.getStandard());
        } else if (product instanceof GeneralItem g) {
            return new GeneralItemEnt(g.getId(), g.getPrice(), g.getName(),
                    ItemCategoryEnt.valueOf(g.getCategory().name()));
        }
        throw new IllegalArgumentException("Invalid product type: " + product.getClass());
    }

    static Product toDomain(ProductEnt entity) {
        ProductEnt real = (ProductEnt) Hibernate.unproxy(entity);
        if (real instanceof FuelEnt f) {
            return new Fuel(f.getId(), f.getPrice(), f.getName(), f.getStandard());
        } else if (real instanceof GeneralItemEnt g) {
            return new GeneralItem(g.getId(), g.getPrice(), g.getName(),
                    ItemCategory.valueOf(g.getCategory().name()));
        }
        throw new IllegalArgumentException("Invalid product type: " + real.getClass());
    }
}