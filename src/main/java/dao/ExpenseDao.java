package dao;

import config.HibernateConfig;
import entities.Expense;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class ExpenseDao implements IExpenseDao{

    EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    @Override
    public Expense createExpense(Expense expense) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            em.persist(expense);

            em.getTransaction().commit();

            return expense;
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
    public Expense findExpenseById(Integer id) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.find(Expense.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Expense> findAllExpenses() {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "SELECT e FROM Expense e",
                    Expense.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Expense> findExpenseByCarId(Integer carId) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                            "SELECT e FROM Expense e WHERE e.car.id = :carId",
                            Expense.class
                    )
                    .setParameter("carId", carId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Expense> findExpenseByCarIdAndCategory(
            Integer carId,
            String category
    ) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                            "SELECT e FROM Expense e " +
                                    "WHERE e.car.id = :carId " +
                                    "AND e.category = :category",
                            Expense.class
                    )
                    .setParameter("carId", carId)
                    .setParameter("category", category)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public double getTotalExpensesForCar(Integer carId) {
        EntityManager em = emf.createEntityManager();

        try {
            Double total = em.createQuery(
                            "SELECT COALESCE(SUM(e.amount), 0) " +
                                    "FROM Expense e WHERE e.car.id = :carId",
                            Double.class
                    )
                    .setParameter("carId", carId)
                    .getSingleResult();

            return total;
        } finally {
            em.close();
        }
    }

    @Override
    public Expense updateExpense(Expense expense) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Expense updatedExpense = em.merge(expense);

            em.getTransaction().commit();

            return updatedExpense;
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
    public void deleteExpense(Integer id) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Expense expense = em.find(Expense.class, id);

            if (expense != null) {
                em.remove(expense);
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
