package app.dao;

import app.config.HibernateConfig;
import app.entities.InspectionRecord;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class InspectionRecordDao {
    EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    public InspectionRecord createServiceRecord(InspectionRecord serviceRecord) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(serviceRecord);

            em.getTransaction().commit();

            return serviceRecord;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public InspectionRecord findServiceRecordById(Integer id) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.find(InspectionRecord.class, id);
        } finally {
            em.close();
        }
    }

    public List<InspectionRecord> findAllServiceRecords() {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT s FROM InspectionRecord s",
                    InspectionRecord.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    public List<InspectionRecord> findServiceRecordByCarId(Integer carId) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                            "SELECT s FROM InspectionRecord s WHERE s.car.id = :carId",
                            InspectionRecord.class
                    )
                    .setParameter("carId", carId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public InspectionRecord updateServiceRecord(InspectionRecord serviceRecord) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            InspectionRecord updatedServiceRecord =
                    em.merge(serviceRecord);

            em.getTransaction().commit();

            return updatedServiceRecord;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void deleteServiceRecord(Integer id) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            InspectionRecord serviceRecord =
                    em.find(InspectionRecord.class, id);

            if (serviceRecord != null) {
                em.remove(serviceRecord);
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

    public List<InspectionRecord> findServiceRecordByCarIdOrderByDateDesc(
            Integer carId) {

        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                            "SELECT s FROM InspectionRecord s " +
                                    "WHERE s.car.id = :carId " +
                                    "ORDER BY s.date DESC",
                            InspectionRecord.class
                    )
                    .setParameter("carId", carId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
