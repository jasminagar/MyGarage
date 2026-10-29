package app.dao;

import app.config.HibernateConfig;
import app.entities.Car;
import app.entities.Expense;
import io.javalin.http.NotFoundResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
public class ExpenseDAOTest {

    @BeforeAll
    static void setUpDatabase() {
        HibernateConfig.useTestDatabase(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );
    }

    @BeforeEach
    void setUp() {
        expenseDao = new ExpenseDao();

        EntityManager em = HibernateConfig.getEntityManagerFactory()
                .createEntityManager();

        try {
            em.getTransaction().begin();

            Car car = new Car();
            car.setMake("Toyota");
            car.setModel("Yaris");
            car.setYear(2020);
            car.setMileage(50000);
            car.setRegistrationNumber("TEST123");
            car.setFuelType("Petrol");

            em.persist(car);
            em.getTransaction().commit();

            carId = car.getId();
        } finally {
            em.close();
        }
    }

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    private static EntityManagerFactory emf;
    private ExpenseDao expenseDao;
    private Integer carId;

    @Test
    void createExpenseSuccessfully() {
        Expense expense = new Expense();
        expense.setCategory("Fuel");
        expense.setAmount(500.0);

        Expense created = expenseDao.createExpense(carId, expense);

        assertThat(created, is(notNullValue()));
        assertThat(created.getId(), is(notNullValue()));
        assertThat(created.getCategory(), is(equalTo("Fuel")));
        assertThat(created.getAmount(), is(equalTo(500.0)));
    }

    @Test
    void createExpenseFailsWhenCarDoesNotExist() {
        Expense expense = new Expense();
        expense.setCategory("Fuel");
        expense.setAmount(500.0);

        assertThrows(
                NotFoundResponse.class,
                () -> expenseDao.createExpense(-1, expense)
        );
    }

    @Test
    void findExpenseByIdReturnsExpenseWhenItExists() {
        Expense expense = new Expense();
        expense.setCategory("Repair");
        expense.setAmount(1200.0);

        Expense created = expenseDao.createExpense(carId, expense);

        Expense found = expenseDao.findExpenseById(created.getId());

        assertThat(found, is(notNullValue()));
        assertThat(found.getId(), is(equalTo(created.getId())));
        assertThat(found.getCategory(), is(equalTo("Repair")));
    }

    @Test
    void findExpenseByIdReturnsNullWhenExpenseDoesNotExist() {
        Expense found = expenseDao.findExpenseById(-1);

        assertThat(found, is(nullValue()));
    }

    @Test
    void findExpenseByCarIdReturnsExpensesForCar() {
        Expense expense = new Expense();
        expense.setCategory("Fuel");
        expense.setAmount(300.0);

        expenseDao.createExpense(carId, expense);

        var expenses = expenseDao.findExpenseByCarId(carId);

        assertThat(expenses, is(not(empty())));
        assertThat(expenses, hasItem(
                hasProperty("category", equalTo("Fuel"))
        ));
    }

    @Test
    void deleteExpenseRemovesExpense() {
        Expense expense = new Expense();
        expense.setCategory("Repair");
        expense.setAmount(800.0);

        Expense created = expenseDao.createExpense(carId, expense);

        expenseDao.deleteExpense(created.getId());

        Expense found = expenseDao.findExpenseById(created.getId());

        assertThat(found, is(nullValue()));
    }
}
