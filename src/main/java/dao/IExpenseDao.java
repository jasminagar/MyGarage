package dao;

import entities.Expense;

import java.util.List;

public interface IExpenseDao {

    Expense createExpense(Expense expense);

    Expense findExpenseById(Integer id);

    List<Expense> findAllExpenses();

    List<Expense> findExpenseByCarId(Integer carId);

    List<Expense> findExpenseByCarIdAndCategory(Integer carId, String category);

    double getTotalExpensesForCar(Integer carId);

    Expense updateExpense(Expense expense);

    void deleteExpense(Integer id);
}
