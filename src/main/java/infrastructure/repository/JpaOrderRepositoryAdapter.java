package infrastructure.repository;

import domain.model.*;
import domain.repository.OrderRepository;
import infrastructure.model.*;
import jakarta.persistence.*;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

public class JpaOrderRepositoryAdapter implements OrderRepository{
    private final EntityManagerFactory factory;

    public JpaOrderRepositoryAdapter(EntityManagerFactory factory) {
        this.factory = factory;
    }

    @Override
    public void add(Order order){
        EntityManager em = factory.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(toEntity(order, em));
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Order findById(UUID id) {
        EntityManager em = factory.createEntityManager();
        try {
            OrderEnt entity = em.find(OrderEnt.class, id);
            if (entity == null) return null;
            return toDomain(entity);
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Order order) {
        EntityManager em = factory.createEntityManager();
        try {
            em.getTransaction().begin();
            OrderEnt entity = em.find(OrderEnt.class, order.getId());
            if (entity == null) throw new IllegalArgumentException("Order not found");

            entity.getProducts().removeIf(ope ->
                    order.getProducts().stream().noneMatch(op -> op.getId().equals(ope.getId())));

            for (OrderedProduct op : order.getProducts()) {
                boolean exists = entity.getProducts().stream()
                        .anyMatch(ope -> ope.getId().equals(op.getId()));
                if (!exists) {
                    ProductEnt product = em.getReference(ProductEnt.class, op.getProduct().getId());
                    entity.addProduct(new OrderedProductEnt(op.getId(), product, op.getQuantity(), op.getPriceWhenOrdered()));
                }
            }
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    private OrderEnt toEntity(Order order, EntityManager em) {
        OrderEnt e = new OrderEnt(order.getId());
        for (OrderedProduct op : order.getProducts()) {
            ProductEnt product = em.getReference(ProductEnt.class, op.getProduct().getId());
            e.addProduct(new OrderedProductEnt(op.getId(), product, op.getQuantity(), op.getPriceWhenOrdered()));
        }
        return e;
    }

    private Order toDomain(OrderEnt e) {
        List<OrderedProduct> items = new ArrayList<>();
        for (OrderedProductEnt ope : e.getProducts()) {
            items.add(new OrderedProduct(
                    ope.getId(),
                    productToDomain(ope.getProduct()),
                    ope.getQuantity(),
                    ope.getPriceWhenOrdered()));
        }
        return new Order(e.getId(), items);
    }

    private Product productToDomain(ProductEnt product) {
        if (product instanceof FuelEnt f) {
            return new Fuel(f.getId(), f.getPrice(), f.getName(), f.getStandard());
        } else if (product instanceof GeneralItemEnt g) {
            return new GeneralItem(g.getId(), g.getPrice(), g.getName(), g.getCategory());
        }
        throw new IllegalArgumentException("Invalid product type");
    }
}
