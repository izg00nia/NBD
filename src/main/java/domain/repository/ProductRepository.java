package domain.repository;

import domain.model.Product;
import java.util.UUID;

public interface ProductRepository {
    void add(Product product);
    Product findById(UUID id);
    void update(Product product);
    void delete(UUID id);
}
