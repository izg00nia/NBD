package infrastructure.repository;

import domain.model.*;
import domain.repository.ProductRepository;
import infrastructure.model.*;
import jakarta.persistence.*;
import java.util.UUID;

public class JpaProductRepositoryAdapter implements ProductRepository {
    private final EntityManagerFactory factory;

    public JpaProductRepositoryAdapter(EntityManagerFactory factory) {
        this.factory = factory;
    }

    @Override
    public void add(Product product) {
        EntityManager em = factory.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(toEntity(product));
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Product findById(UUID id) {
        EntityManager em = factory.createEntityManager();
        try {
            ProductEnt product = em.find(ProductEnt.class, id);
            if (product == null) return null;
            return toDomain(product);
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Product product) {
        EntityManager em = factory.createEntityManager();
        try {
            em.getTransaction().begin();
            ProductEnt entity = em.find(ProductEnt.class, product.getId());
            if (entity == null) throw new IllegalArgumentException("Product not found");
            entity.setPrice(product.getPrice());
            entity.setName(product.getName());
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(UUID id) {
        EntityManager em = factory.createEntityManager();
        try {
            em.getTransaction().begin();
            ProductEnt product = em.find(ProductEnt.class, id);
            if (product != null) {
                em.remove(product);
            }
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }
        finally {
            em.close();
        }
    }

    private ProductEnt toEntity(Product product) {
        if (product instanceof Fuel f) {
            return new FuelEnt(f.getId(), f.getPrice(), f.getName(), f.getStandard());
        } else if (product instanceof GeneralItem g) {
            return new GeneralItemEnt(g.getId(), g.getPrice(), g.getName(), g.getCategory());
        }
        throw new IllegalArgumentException("Invalid product type");
    }

    private Product toDomain(ProductEnt product) {
        if (product instanceof FuelEnt f) {
            return new Fuel(f.getId(), f.getPrice(), f.getName(), f.getStandard());
        } else if (product instanceof GeneralItemEnt g) {
            return new GeneralItem(g.getId(), g.getPrice(), g.getName(), g.getCategory());
        }
        throw new IllegalArgumentException("Invalid product type");
    }
}
