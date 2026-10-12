package dao;

import config.HibernateConfig;
import entities.Role;
import entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import security.ISecurityDAO;
import security.EnumRoles;

public class UserDao implements IUserDao, ISecurityDAO {

    private final EntityManagerFactory emf =
            HibernateConfig.getEntityManagerFactory();

    //Henter bruger og rolle før før em lukker. role er lazyloaded, så roller skal i mens session stadig er åben.
    //Ellers giver user.getRolesAsString LazyInitializationException.
    //Henter derfor bruger sammen med user, og genbruger bcrypt verifikation. !user.verifyPassword(password)
    @Override
    public User getVerifiedUser(String username, String password) {
        //Opretter em som jeg bruger til forespørgsler til databasen.
        //Jeg bruger jpa og hibernate til at håndtere forbindelen mellem mine java objekter og databasen.
        EntityManager em = emf.createEntityManager();

        //Bruger try til at samle database operationer, så jeg kan sikre at em bliver lukket, også selvom koden fejler.
        try {
            //JPQL forespørgsel.
            User user = em.createQuery(
                            "SELECT DISTINCT u FROM User u " +
                                    "LEFT JOIN FETCH u.roles " +
                                    "WHERE u.username = :username", //Begrænser resultatet til bruger med angivet brugernavn.
                            User.class //Fortæller hibernate at resultat skal være af typen user.
                    )
                    .setParameter("username", username) //Binder username parameter til :username.
                    //Håndterer resultat.
                    .getResultStream()//Giver adgang til resultat med streams.
                    .findFirst()//Henter det første resultat der findes.
                    .orElse(null);

            if (user == null || !user.verifyPassword(password)) {
                return null;
            }

            return user;
        } finally {
            em.close();
        }
    }

    @Override
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

    @Override
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