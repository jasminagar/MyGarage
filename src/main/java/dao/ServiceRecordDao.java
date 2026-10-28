package dao;

import config.HibernateConfig;
import entities.ServiceRecord;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class ServiceRecordDao {
    EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    public ServiceRecord createServiceRecord(ServiceRecord serviceRecord) {
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

    public ServiceRecord findServiceRecordById(Integer id) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.find(ServiceRecord.class, id);
        } finally {
            em.close();
        }
    }

    public List<ServiceRecord> findAllServiceRecords() {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT s FROM ServiceRecord s",
                    ServiceRecord.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    public List<ServiceRecord> findServiceRecordByCarId(Integer carId) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                            "SELECT s FROM ServiceRecord s WHERE s.car.id = :carId",
                            ServiceRecord.class
                    )
                    .setParameter("carId", carId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public ServiceRecord updateServiceRecord(ServiceRecord serviceRecord) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            ServiceRecord updatedServiceRecord =
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

            ServiceRecord serviceRecord =
                    em.find(ServiceRecord.class, id);

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

    public List<ServiceRecord> findServiceRecordByCarIdOrderByDateDesc(
            Integer carId) {

        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                            "SELECT s FROM ServiceRecord s " +
                                    "WHERE s.car.id = :carId " +
                                    "ORDER BY s.date DESC",
                            ServiceRecord.class
                    )
                    .setParameter("carId", carId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
