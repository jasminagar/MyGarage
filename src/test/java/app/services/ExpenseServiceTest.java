package app.services;

import app.entities.Car;
import app.entities.Expense;
import app.dao.ExpenseDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseDao expenseDao;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private ExpenseSecurityService expenseService;

    private Expense expense;
    private Car car;

    @BeforeEach
    void setUp() {
        car = new Car();
        car.setId(10);

        expense = new Expense();
        expense.setId(1);
        expense.setCar(car);
    }

    @Test
    void deleteReturnsFalseWhenExpenseDoesNotExist() {
        when(expenseDao.findExpenseById(1)).thenReturn(null);

        boolean result =
                expenseService.deleteExpenseForCar(1, "alice", false);

        assertThat(result, is(false));
        verify(expenseDao, never()).deleteExpense(anyInt());
        verifyNoInteractions(securityService);
    }

    @Test
    void adminCanDeleteExpense() {
        when(expenseDao.findExpenseById(1)).thenReturn(expense);

        boolean result =
                expenseService.deleteExpenseForCar(1, "admin", true);

        assertThat(result, is(true));
        verify(expenseDao).deleteExpense(1);
        verifyNoInteractions(securityService);
    }

    @Test
    void userCanDeleteExpenseForTheirOwnCar() {
        when(expenseDao.findExpenseById(1)).thenReturn(expense);
        when(securityService.findCarForUser(10, "alice")).thenReturn(car);

        boolean result =
                expenseService.deleteExpenseForCar(1, "alice", false);

        assertThat(result, is(true));
        verify(securityService).findCarForUser(10, "alice");
        verify(expenseDao).deleteExpense(1);
    }

    @Test
    void userCannotDeleteExpenseForAnotherUsersCar() {
        when(expenseDao.findExpenseById(1)).thenReturn(expense);
        when(securityService.findCarForUser(10, "alice")).thenReturn(null);

        boolean result =
                expenseService.deleteExpenseForCar(1, "alice", false);

        assertThat(result, is(false));
        verify(expenseDao, never()).deleteExpense(anyInt());
    }
}