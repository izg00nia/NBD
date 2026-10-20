package infrastructure.repository;

import domain.model.Product;
import domain.repository.ProductRepository;
import infrastructure.model.ProductEnt;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.UUID;
import java.util.function.Consumer;

public class JpaProductRepositoryAdapter implements ProductRepository {
    private final EntityManagerFactory factory;

    public JpaProductRepositoryAdapter(EntityManagerFactory factory) {
        this.factory = factory;
    }

    @Override
    public void add(Product product) {
        inTransaction(em -> new JpaProductRepository(em).add(ProductMapper.toEntity(product)));
    }

    @Override
    public Product findById(UUID id) {
        EntityManager em = factory.createEntityManager();
        try {
            ProductEnt entity = new JpaProductRepository(em).findById(id);
            return entity == null ? null : ProductMapper.toDomain(entity);
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Product product) {
        inTransaction(em -> {
            ProductEnt entity = new JpaProductRepository(em).findById(product.getId());
            if (entity == null) throw new IllegalArgumentException("Product not found: " + product.getId());
            entity.setPrice(product.getPrice());
            entity.setName(product.getName());
        });
    }

    @Override
    public void delete(UUID id) {
        inTransaction(em -> {
            JpaProductRepository repo = new JpaProductRepository(em);
            ProductEnt entity = repo.findById(id);
            if (entity != null) repo.remove(entity);
        });
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