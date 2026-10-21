package infrastructure.repository;

import domain.model.Fuel;
import infrastructure.model.ProductEnt;
import jakarta.persistence.*;
import org.junit.jupiter.api.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OptimisticLockingTest {
    static EntityManagerFactory factory;

    @BeforeAll
    static void init() {
        factory = Persistence.createEntityManagerFactory("default");
    }

    @AfterAll
    static void close() {
        factory.close();
    }

    // testuje optymistyczne blokowanie, dwie transakcje wczytuja ten sam produkt i obie zmieniaja cene ale tylko jedna z nich zostaje zatwierdzona
    @Test
    void concurrentPriceChangeIsRejected() {
        UUID id = UUID.randomUUID();
        new JpaProductRepositoryAdapter(factory).add(new Fuel(id, 5.0f, "Benzyna", "95"));

        EntityManager em1 = factory.createEntityManager();
        EntityManager em2 = factory.createEntityManager();
        try {
            em1.getTransaction().begin();
            em2.getTransaction().begin();

            ProductEnt p1 = em1.find(ProductEnt.class, id);
            ProductEnt p2 = em2.find(ProductEnt.class, id);
            p1.setPrice(6.0f);
            p2.setPrice(7.0f);

            em1.getTransaction().commit();

            RollbackException ex = assertThrows(RollbackException.class,
                    () -> em2.getTransaction().commit());
            assertInstanceOf(OptimisticLockException.class, ex.getCause());
        } finally {
            em1.close();
            em2.close();
        }

        assertEquals(6.0f, new JpaProductRepositoryAdapter(factory).findById(id).getPrice());
    }
}