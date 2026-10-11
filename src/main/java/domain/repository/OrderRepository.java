package domain.repository;

import domain.model.Order;
import java.util.UUID;

public interface  OrderRepository {
    void add(Order order);
    Order findById(UUID id);
    void update(Order order);
}
