package app.dao;

import app.config.HibernateConfig;
import app.entities.Car;
import app.entities.Expense;
import io.javalin.http.NotFoundResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class ExpenseDao {

    EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    public Expense createExpense(Integer carId, Expense expense) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Car car = em.find(Car.class, carId);

            if (car == null) {
                throw new io.javalin.http.NotFoundResponse(
                        "Car not found with ID: " + carId
                );
            }

            expense.setCar(car);
            em.persist(expense);
            em.getTransaction().commit();

            return expense;

        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            System.err.println(
                    "Failed to create expense for car ID " + carId
                            + ": " + e.getMessage()
            );

            throw e;
        } finally {
            em.close();
        }
    }

    public Expense findExpenseById(Integer id) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.find(Expense.class, id);
        } finally {
            em.close();
        }
    }

    public List<Expense> findAllExpenses() {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                    "select e from Expense e",
                    Expense.class
            ).getResultList();
        } catch (RuntimeException e) {
            System.err.println("Failed to retrieve all expenses: " + e.getMessage());
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Expense> findExpenseByCarId(Integer carId) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                            "SELECT e FROM Expense e WHERE e.car.id = :carId",
                            Expense.class
                    )
                    .setParameter("carId", carId)
                    .getResultList();

        } catch (RuntimeException e) {
            System.err.println(
                    "Failed to find expenses for car ID " + carId
                            + ": " + e.getMessage()
            );
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Expense> findExpenseByCarIdAndCategory(
            Integer carId,
            String category) {

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

        } catch (RuntimeException e) {
            System.err.println(
                    "Failed to find expenses for car ID " + carId +
                            " and category '" + category + "': " + e.getMessage()
            );
            throw e;
        } finally {
            em.close();
        }
    }

    public double getTotalExpensesForCar(Integer carId) {
        EntityManager em = emf.createEntityManager();

        try {
            Double total = em.createQuery(
                            "select coalesce(sum(e.amount), 0) " +
                                    "from Expense e where e.car.id = :carId",
                            Double.class
                    )
                    .setParameter("carId", carId)
                    .getSingleResult();

            return total != null ? total : 0.0;

        } catch (RuntimeException e) {
            System.err.println(
                    "Failed to retrieve total expenses for car ID "
                            + carId + ": " + e.getMessage()
            );
            throw e;
        } finally {
            em.close();
        }
    }

    public Expense updateExpense(Expense expense) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            if (expense == null) {
                throw new NotFoundResponse("Expense not found ");
            }

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

    public void deleteExpense(Integer id) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Expense expense = em.find(Expense.class, id);


            if (expense == null) {
                throw new NotFoundResponse("Expense not found with ID: " + id);
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
