package infrastructure.repository;

import infrastructure.model.ProductEnt;
import jakarta.persistence.EntityManager;
import java.util.UUID;

public class JpaProductRepository {
    private final EntityManager em;

    public JpaProductRepository(EntityManager em) {
        this.em = em;
    }

    public void add(ProductEnt product) {
        em.persist(product);
    }

    public ProductEnt findById(UUID id) {
        return em.find(ProductEnt.class, id);
    }

    public ProductEnt getReference(UUID id) {
        return em.getReference(ProductEnt.class, id);
    }

    public void remove(ProductEnt product) {
        em.remove(product);
    }
}