package infrastructure.repository;

import domain.model.Order;
import domain.model.OrderedProduct;
import infrastructure.model.OrderEnt;
import infrastructure.model.OrderedProductEnt;
import infrastructure.model.ProductEnt;

import java.util.ArrayList;
import java.util.List;

final class OrderMapper {
    private OrderMapper() { }

    static Order toDomain(OrderEnt entity) {
        List<OrderedProduct> items = new ArrayList<>();
        for (OrderedProductEnt ope : entity.getProducts()) {
            items.add(toDomain(ope));
        }
        return new Order(entity.getId(), items);
    }

    static OrderedProduct toDomain(OrderedProductEnt ope) {
        return new OrderedProduct(
                ope.getId(),
                ProductMapper.toDomain(ope.getProduct()),
                ope.getQuantity(),
                ope.getPriceWhenOrdered());
    }

    static OrderedProductEnt toEntity(OrderedProduct op, ProductEnt product) {
        return new OrderedProductEnt(
                op.getId(),
                product,
                op.getQuantity(),
                op.getPriceWhenOrdered());
    }
}