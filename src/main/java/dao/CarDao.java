package dao;

import config.HibernateConfig;
import entities.Car;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

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
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException(
                    "Could not create car in database", e
            );
        } finally {
            em.close();
        }
    }

    public Car findCarById(Integer id) {
        EntityManager em = emf.createEntityManager();

        try {
            Car car = em.find(Car.class, id);

            return car;

        } catch (Exception e) {
            throw new RuntimeException(
                    "Could not find car with id: " + id, e
            );
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

        } catch (Exception e) {
            throw new RuntimeException("Could not retrieve cars from database", e);

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
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException(
                    "Could not update car with id: " + car.getId(), e
            );
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
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException(
                    "Could not delete car with id: " + car.getId(), e
            );
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

        } catch (Exception e) {
            throw new RuntimeException(
                    "Could not retrieve cars for user with id: " + userId, e
            );
        } finally {
            em.close();
        }
    }
}
