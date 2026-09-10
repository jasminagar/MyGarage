package dao;

import config.HibernateConfig;
import entities.Car;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class CarDao implements ICarDao{
    EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    @Override
    public Car createCar(Car car) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(car);

            em.getTransaction().commit();

            return car;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Car findCarById(Integer id) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.find(Car.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Car> findAllcars() {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT c FROM Car c",
                    Car.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Car updateCar(Car car) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Car updatedCar = em.merge(car);

            em.getTransaction().commit();

            return updatedCar;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteCar(Car car) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Car managedCar = em.merge(car);
            em.remove(managedCar);

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Car> findCarByUserId(Integer userId) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                            "SELECT c FROM Car c WHERE c.user.id = :userId",
                            Car.class
                    )
                    .setParameter("userId", userId)
                    .getResultList();
        } finally {
            em.close();
        }


    }}
