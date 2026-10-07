package dao;

import config.HibernateConfig;
import dto.UserDTO;
import entities.Role;
import entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import security.ISecurityDAO;

public class UserDao implements IUserDao, ISecurityDAO {

    private final EntityManagerFactory emf =
            HibernateConfig.getEntityManagerFactory();

    @Override
    public User getVerifiedUser(String username, String password) {

        User user = findUserByUsername(username);

        if (user == null) {
            return null;
        }

        if (!user.verifyPassword(password)) {
            return null;
        }

        return user;
    }

    @Override
    public Role createRole(String role) {

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

    @Override
    public User addUserRole(String username, String role) {

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

        } catch (jakarta.persistence.NoResultException e) {
            return null;

        } finally {
            em.close();
        }
    }
    @Override
    public User createUser(String username, String password) {

        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            User user = new User(username, password);

            Role userRole = em.createQuery(
                            "SELECT r FROM Role r WHERE r.role = :role",
                            Role.class
                    )
                    .setParameter("role", "USER")
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