package dao;

import config.HibernateConfig;
import entities.Modification;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class ModificationDao implements IModificationDao{
    EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    @Override
    public Modification createModification(Modification modification) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

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

    @Override
    public Modification findModificationById(Integer id) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.find(Modification.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Modification> findAllModifications() {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT m FROM Modification m",
                    Modification.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
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

    @Override
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

    @Override
    public void deleteModification(Integer id) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Modification modification =
                    em.find(Modification.class, id);

            if (modification != null) {
                em.remove(modification);
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
