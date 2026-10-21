package infrastructure.repository;

import domain.model.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JpaProductRepositoryAdapterTest {
    static EntityManagerFactory factory;
    JpaProductRepositoryAdapter products = new JpaProductRepositoryAdapter(factory);

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
    }

    //zapisuje fuel i sprawdza czy po odczycie nadal jest fuel
    @Test
    void isFuelLoadedAsFuel() {
        UUID id = UUID.randomUUID();
        products.add(new Fuel(id, 5.0f, "Benzyna", "95"));

        Product loaded = products.findById(id);

        assertInstanceOf(Fuel.class, loaded);
        assertEquals("Benzyna", loaded.getName());
        assertEquals("95", ((Fuel) loaded).getStandard());
    }

    //zapisuje generalitem i sprawdza czy po odczycie nadal jest generalitem
    @Test
    void isGeneralItemLoadedAsGeneralItem() {
        UUID id = UUID.randomUUID();
        products.add(new GeneralItem(id, 12.0f, "Kawa", ItemCategory.COFFEE));

        Product loaded = products.findById(id);

        assertInstanceOf(GeneralItem.class, loaded);
        assertEquals(ItemCategory.COFFEE, ((GeneralItem) loaded).getCategory());
    }

    // zmienia cene i nazwe produktu i sprawdza czy sie zmienila
    @Test
    void update() {
        UUID id = UUID.randomUUID();
        products.add(new Fuel(id, 5.0f, "Benzyna", "95"));

        Product p = products.findById(id);
        p.setPrice(6.5f);
        p.setName("Benzyna 95");
        products.update(p);

        Product reloaded = products.findById(id);
        assertEquals(6.5f, reloaded.getPrice(), 0.001f);
        assertEquals("Benzyna 95", reloaded.getName());
    }

    // usuwa produkt i sprawdza czy findbyid jest null
    @Test
    void delete() {
        UUID id = UUID.randomUUID();
        products.add(new GeneralItem(id, 3.0f, "Cola", ItemCategory.DRINK));

        products.delete(id);

        assertNull(products.findById(id));
    }

    // findbyid dla nieistniejacego id zwraca null
    @Test
    void findByIdReturnsNull() {
        assertNull(products.findById(UUID.randomUUID()));
    }
}