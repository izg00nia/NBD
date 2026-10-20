package infrastructure.repository;

import domain.model.Fuel;
import domain.model.Order;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class ConcurrentOrderUpdateTest {
    static EntityManagerFactory factory;

    @BeforeAll
    static void init() {
        factory = Persistence.createEntityManagerFactory("default");
    }

    @AfterAll
    static void close() {
        factory.close();
    }

    @Test
    void concurrentAdditionsToSameOrderAreAllPreserved() throws Exception {
        JpaProductRepositoryAdapter products = new JpaProductRepositoryAdapter(factory);
        JpaOrderRepositoryAdapter orders = new JpaOrderRepositoryAdapter(factory);

        Fuel fuel = new Fuel(UUID.randomUUID(), 5.0f, "Benzyna", "95");
        products.add(fuel);
        UUID orderId = UUID.randomUUID();
        orders.add(new Order(orderId));

        int threads = 8;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch allRead = new CountDownLatch(threads);
        CountDownLatch go = new CountDownLatch(1);
        List<Future<Void>> results = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            results.add(pool.submit(() -> {
                Order o = orders.findById(orderId);
                o.addProduct(fuel, 1);
                allRead.countDown();
                go.await();
                orders.update(o);
                return null;
            }));
        }

        assertTrue(allRead.await(15, TimeUnit.SECONDS));
        go.countDown();
        for (Future<Void> f : results) f.get(30, TimeUnit.SECONDS);
        pool.shutdown();

        Order loaded = orders.findById(orderId);
        assertEquals(threads, loaded.getProducts().size());
        assertEquals(40.0f, loaded.getTotalPrice(), 0.001f);
        assertInstanceOf(Fuel.class, loaded.getProducts().get(0).getProduct());
    }
}