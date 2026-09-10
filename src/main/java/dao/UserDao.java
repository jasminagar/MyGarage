package dao;

import config.HibernateConfig;
import entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class UserDao implements IUserDao{
    EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    @Override
    public User createUser(User user) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(user);

            em.getTransaction().commit();

            return user;
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
    public User findUserById(Integer id) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public User updateUser(User user) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            User updatedUser = em.merge(user);

            em.getTransaction().commit();

            return updatedUser;
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
    public void deleteUser(User user) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            User managedUser = em.merge(user);
            em.remove(managedUser);

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
