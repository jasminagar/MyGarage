package app.dao;

import app.config.HibernateConfig;
import app.entities.Role;
import app.entities.User;
import app.exceptions.UserAlreadyExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import app.security.EnumRoles;
import jakarta.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDao {

    private final Logger logger = LoggerFactory.getLogger(UserDao.class);
    private final EntityManagerFactory emf =
            HibernateConfig.getEntityManagerFactory();

    public User getVerifiedUser(String username, String password) {
        EntityManager em = emf.createEntityManager();

        try {
            User user = em.createQuery(
                            "SELECT DISTINCT u FROM User u " +
                                    "LEFT JOIN FETCH u.roles " +
                                    "WHERE u.username = :username",
                            User.class
                    )
                    .setParameter("username", username)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (user == null || !user.verifyPassword(password)) {
                return null;
            }

            return user;
        } finally {
            em.close();
        }
    }

    public Role createRole(EnumRoles role) {

        EntityManager em = emf.createEntityManager();

        try {
            Role newRole = new Role(role);

            em.getTransaction().begin();
            em.persist(newRole);
            em.getTransaction().commit();

            return newRole;

        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw e;

        } finally {
            em.close();
        }
    }

    public User addUserRole(String username, EnumRoles role) {

        EntityManager em = emf.createEntityManager();

        try {
            User user = em.createQuery(
                            "SELECT u FROM User u WHERE u.username = :username",
                            User.class
                    ).setParameter("username", username)
                    .getSingleResult();

            Role userRole = em.createQuery(
                            "SELECT r FROM Role r WHERE r.role = :role",
                            Role.class
                    ).setParameter("role", role)
                    .getSingleResult();

            user.addRole(userRole);

            em.getTransaction().begin();
            em.merge(user);
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

    public User findUserByUsername(String username) {

        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                            "SELECT u FROM User u WHERE u.username = :username",
                            User.class
                    ).setParameter("username", username)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;

        } finally {
            em.close();
        }
    }

    public User createUser(String username, String password) {

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Long count = em.createQuery(
                            "select count(u) from User u where u.username = :username",
                            Long.class
                    ).setParameter("username", username)
                    .getSingleResult();

            if (count > 0) {
                throw new UserAlreadyExistsException();
            }

            User user = new User(username, password);

            Role userRole = em.createQuery(
                            "select r from Role r where r.role = :role",
                            Role.class
                    )
                    .setParameter("role", EnumRoles.USER)
                    .getSingleResult();

            user.addRole(userRole);

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

    public User findUserById(Integer id) {

        EntityManager em = emf.createEntityManager();

        try {
            return em.find(User.class, id);

        } finally {
            em.close();
        }
    }

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