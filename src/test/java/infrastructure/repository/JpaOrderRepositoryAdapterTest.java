package infrastructure.repository;

import domain.model.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import domain.model.Order;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JpaOrderRepositoryAdapterTest {
    static EntityManagerFactory factory;
    JpaProductRepositoryAdapter products;
    JpaOrderRepositoryAdapter orders;

    @BeforeAll
    static void init() {
        factory = Persistence.createEntityManagerFactory("default");
    }

    @AfterAll
    static void close() {
        factory.close();
    }

    @BeforeEach
    void setUp() {
        products = new JpaProductRepositoryAdapter(factory);
        orders = new JpaOrderRepositoryAdapter(factory);
    }

    @Test
    void orderWithDifferentProductTypesIsStoredAndLoaded() {
        Fuel fuel = new Fuel(UUID.randomUUID(), 5.0f, "Benzyna", "95");
        GeneralItem coffee = new GeneralItem(UUID.randomUUID(), 12.0f, "Kawa", ItemCategory.COFFEE);
        products.add(fuel);
        products.add(coffee);

        Order order = new Order(UUID.randomUUID());
        order.addProduct(fuel, 10);
        order.addProduct(coffee, 2);
        orders.add(order);

        Order loaded = orders.findById(order.getId());
        assertEquals(2, loaded.getProducts().size());
        assertEquals(74.0f, loaded.getTotalPrice(), 0.001f);
        assertTrue(loaded.getProducts().stream().anyMatch(op -> op.getProduct() instanceof Fuel));
        assertTrue(loaded.getProducts().stream().anyMatch(op -> op.getProduct() instanceof GeneralItem));
    }

    @Test
    void updateAddsNewItemsAndKeepsExistingOnes() {
        Fuel fuel = new Fuel(UUID.randomUUID(), 5.0f, "Benzyna", "95");
        products.add(fuel);

        Order order = new Order(UUID.randomUUID());
        order.addProduct(fuel, 1);
        orders.add(order);

        Order loaded = orders.findById(order.getId());
        loaded.addProduct(fuel, 3);
        orders.update(loaded);

        Order reloaded = orders.findById(order.getId());
        assertEquals(2, reloaded.getProducts().size());
        assertEquals(20.0f, reloaded.getTotalPrice(), 0.001f);
    }

    @Test
    void priceChangeDoesNotAffectExistingOrder() {
        Fuel fuel = new Fuel(UUID.randomUUID(), 5.0f, "Benzyna", "95");
        products.add(fuel);

        Order order = new Order(UUID.randomUUID());
        order.addProduct(fuel, 2);
        orders.add(order);

        fuel.setPrice(9.0f);
        products.update(fuel);

        Order loaded = orders.findById(order.getId());
        assertEquals(5.0f, loaded.getProducts().get(0).getPriceWhenOrdered(), 0.001f);
        assertEquals(10.0f, loaded.getTotalPrice(), 0.001f);
        assertEquals(9.0f, loaded.getProducts().get(0).getProduct().getPrice(), 0.001f);
    }

    @Test
    void findByIdReturnsNullForUnknownOrder() {
        assertNull(orders.findById(UUID.randomUUID()));
    }
}