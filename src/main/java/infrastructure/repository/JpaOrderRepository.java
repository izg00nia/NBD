package infrastructure.repository;

import infrastructure.model.OrderEnt;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import java.util.UUID;

public class JpaOrderRepository {
    private final EntityManager em;

    public JpaOrderRepository(EntityManager em) {
        this.em = em;
    }

    public void add(OrderEnt order) {
        em.persist(order);
    }

    public OrderEnt findById(UUID id) {
        return em.find(OrderEnt.class, id);
    }

    public OrderEnt findByIdForUpdate(UUID id) {
        return em.find(OrderEnt.class, id, LockModeType.PESSIMISTIC_WRITE);
    }
}