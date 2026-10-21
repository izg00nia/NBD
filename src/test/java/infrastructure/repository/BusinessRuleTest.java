package infrastructure.repository;

import domain.model.Fuel;
import domain.model.Order;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BusinessRuleTest {
    static EntityManagerFactory factory;

    @BeforeAll
    static void init() {
        factory = Persistence.createEntityManagerFactory("default");
    }

    @AfterAll
    static void close() {
        factory.close();
    }

    // test sprawdzający czy cena zamówienienia się zgadza, jeśli nie doszło do zmiany ceny w trakcie gdy był "w koszyku"
    @Test
    void orderStablePrice() {
        JpaProductRepositoryAdapter products = new JpaProductRepositoryAdapter(factory);
        JpaOrderRepositoryAdapter orders = new JpaOrderRepositoryAdapter(factory);

        Fuel fuel = new Fuel(UUID.randomUUID(), 5.0f, "Benzyna", "95");
        products.add(fuel);

        Order order = new Order(UUID.randomUUID());
        order.addProduct(fuel, 2);

        assertDoesNotThrow(() -> orders.add(order));
        assertEquals(10.0f, orders.findById(order.getId()).getTotalPrice(), 0.001f);
    }

    // test sprawdzający czy dojdzie do zakupu jeśli cena zamówienienia się nie zgadza, ponieważ doszło do zmiany ceny w trakcie gdy był "w koszyku"
    @Test
    void orderOutdatedPrice() {
        JpaProductRepositoryAdapter products = new JpaProductRepositoryAdapter(factory);
        JpaOrderRepositoryAdapter orders = new JpaOrderRepositoryAdapter(factory);

        Fuel fuel = new Fuel(UUID.randomUUID(), 5.0f, "Benzyna", "95");
        products.add(fuel);

        Order order = new Order(UUID.randomUUID());
        order.addProduct(fuel, 1);

        fuel.setPrice(6.0f);
        products.update(fuel);

        assertThrows(IllegalStateException.class, () -> orders.add(order));
        assertNull(orders.findById(order.getId()));
    }
}