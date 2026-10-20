package infrastructure.repository;

import domain.model.Order;
import domain.model.OrderedProduct;
import domain.repository.OrderRepository;
import infrastructure.model.OrderEnt;
import infrastructure.model.OrderedProductEnt;
import infrastructure.model.ProductEnt;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class JpaOrderRepositoryAdapter implements OrderRepository {
    private final EntityManagerFactory factory;

    public JpaOrderRepositoryAdapter(EntityManagerFactory factory) {
        this.factory = factory;
    }

    @Override
    public void add(Order order) {
        inTransaction(em -> {
            JpaProductRepository products = new JpaProductRepository(em);
            OrderEnt entity = new OrderEnt(order.getId());
            for (OrderedProduct op : order.getProducts()) {
                entity.addProduct(toEntity(op, products));
            }
            new JpaOrderRepository(em).add(entity);
        });
    }

    @Override
    public Order findById(UUID id) {
        EntityManager em = factory.createEntityManager();
        try {
            OrderEnt entity = new JpaOrderRepository(em).findById(id);
            return entity == null ? null : toDomain(entity);
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Order order) {
        inTransaction(em -> {
            OrderEnt entity = new JpaOrderRepository(em).findByIdForUpdate(order.getId());
            if (entity == null) throw new IllegalArgumentException("Order not found: " + order.getId());

            Set<UUID> existing = new HashSet<>();
            for (OrderedProductEnt ope : entity.getProducts()) existing.add(ope.getId());

            JpaProductRepository products = new JpaProductRepository(em);
            for (OrderedProduct op : order.getProducts()) {
                if (!existing.contains(op.getId())) {
                    entity.addProduct(toEntity(op, products));
                }
            }
        });
    }

    private OrderedProductEnt toEntity(OrderedProduct op, JpaProductRepository products) {
        ProductEnt product = products.findByIdOptimistic(op.getProduct().getId());
        if (product == null)
            throw new IllegalArgumentException("Product not found: " + op.getProduct().getId());
        if (Float.compare(product.getPrice(), op.getPriceWhenOrdered()) != 0)
            throw new IllegalStateException("Product price changed since it was added to the order");
        return new OrderedProductEnt(op.getId(), product, op.getQuantity(), op.getPriceWhenOrdered());
    }

    private Order toDomain(OrderEnt entity) {
        List<OrderedProduct> items = new ArrayList<>();
        for (OrderedProductEnt ope : entity.getProducts()) {
            items.add(new OrderedProduct(
                    ope.getId(),
                    ProductMapper.toDomain(ope.getProduct()),
                    ope.getQuantity(),
                    ope.getPriceWhenOrdered()));
        }
        return new Order(entity.getId(), items);
    }

    private void inTransaction(Consumer<EntityManager> work) {
        EntityManager em = factory.createEntityManager();
        try {
            em.getTransaction().begin();
            work.accept(em);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}