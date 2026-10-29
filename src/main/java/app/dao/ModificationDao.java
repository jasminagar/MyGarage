package app.dao;

import app.config.HibernateConfig;
import app.entities.Car;
import app.entities.Modification;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class ModificationDao {
    EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    public List<Modification> getAllModifications(){
        EntityManager em = emf.createEntityManager();

        try{
            return em.createQuery("select m from Modification m",
                            Modification.class)
                    .getResultList();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Double getTotalCostByCarId(Integer carId) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                            "SELECT COALESCE(SUM(m.cost), 0) " +
                                    "FROM Modification m " +
                                    "WHERE m.car.id = :carId",
                            Double.class
                    )
                    .setParameter("carId", carId)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    public Modification createModification(Integer carId, Modification modification) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Car car = em.find(Car.class, carId);

            if (car == null){
                return null;
            }

            modification.setCar(car);

            em.persist(modification);

            em.getTransaction().commit();

            return modification;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Modification findModificationById(Integer id) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.find(Modification.class, id);
        } finally {
            em.close();
        }
    }

    public List<Modification> findModificationByCarId(Integer carId) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                            "SELECT m FROM Modification m WHERE m.car.id = :carId",
                            Modification.class
                    )
                    .setParameter("carId", carId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Modification updateModification(Modification modification) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Modification updatedModification =
                    em.merge(modification);

            em.getTransaction().commit();

            return updatedModification;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void deleteModification(Modification modification) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Modification foundModification =
                    em.find(Modification.class, modification.getId());

            if (foundModification != null) {
                em.remove(foundModification);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
