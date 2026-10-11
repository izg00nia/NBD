import domain.model.*;
import infrastructure.model.ProductEnt;
import infrastructure.repository.JpaProductRepositoryAdapter;
import jakarta.persistence.*;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("default");

        UUID id = UUID.randomUUID();
        JpaProductRepositoryAdapter productRepo = new JpaProductRepositoryAdapter(factory);
        productRepo.add(new Fuel(id, 5.0f, "Benzyna", "95"));

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

            try {
                em2.getTransaction().commit();
                System.out.println("BŁĄD: konflikt nie został wykryty");
            } catch (RollbackException e) {
                if (e.getCause() instanceof OptimisticLockException) {
                    System.out.println("OK: wykryto konflikt (blokada optymistyczna): " + e.getCause().getMessage());
                } else {
                    throw e;
                }
            }
        } finally {
            em1.close();
            em2.close();
            factory.close();
        }
    }
}