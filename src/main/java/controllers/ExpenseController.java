package controllers;

import dao.ExpenseDao;
import entities.Expense;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class ExpenseController {

    private final ExpenseDao expenseDao;

    public ExpenseController(ExpenseDao expenseDao) {
        this.expenseDao = expenseDao;
    }

    public void addRoutes(Javalin app) {
        app.post("/expenses/create", this::createExpense);
        app.get("/expenses", this::getAllExpenses);
        app.get("/expenses/{id}", this::getExpenseById);
        app.get("/expenses/car/{carId}", this::getExpensesByCarId);
        app.get("/expenses/car/{carId}/category/{category}", this::getExpensesByCarIdAndCategory);
        app.get("/expenses/car/{carId}/total", this::getTotalExpensesForCar);
        app.put("/expenses/update/{id}", this::updateExpense);
        app.delete("/expenses/delete/{id}", this::deleteExpense);
    }

    private void createExpense(Context context) {
        Expense expense =
                context.bodyAsClass(Expense.class);

        Expense createdExpense =
                expenseDao.createExpense(expense);

        context.status(201);
        context.json(createdExpense);
    }

    private void getAllExpenses(Context context) {
        List<Expense> expenses =
                expenseDao.findAllExpenses();

        context.json(expenses);
    }

    private void getExpenseById(Context context) {
        Integer id = Integer.parseInt(context.pathParam("id"));

        Expense expense =
                expenseDao.findExpenseById(id);

        if (expense == null) {
            context.status(404);
            context.result("Expense not found");
            return;
        }

        context.json(expense);
    }

    private void getExpensesByCarId(Context context) {
        Integer carId = Integer.parseInt(context.pathParam("carId"));

        List<Expense> expenses =
                expenseDao.findExpenseByCarId(carId);

        context.json(expenses);
    }

    private void getExpensesByCarIdAndCategory(Context context) {
        Integer carId = Integer.parseInt(context.pathParam("carId"));
        String category = context.pathParam("category");

        List<Expense> expenses =
                expenseDao.findExpenseByCarIdAndCategory(carId, category);

        context.json(expenses);
    }

    private void getTotalExpensesForCar(Context context) {
        Integer carId = Integer.parseInt(context.pathParam("carId"));

        double total =
                expenseDao.getTotalExpensesForCar(carId);

        context.json(total);
    }

    private void updateExpense(Context context) {
        Integer id = Integer.parseInt(context.pathParam("id"));

        Expense expense =
                context.bodyAsClass(Expense.class);

        expense.setId(id);

        Expense updatedExpense =
                expenseDao.updateExpense(expense);

        context.json(updatedExpense);
    }

    private void deleteExpense(Context context) {
        Integer id = Integer.parseInt(context.pathParam("id"));

        Expense expense =
                expenseDao.findExpenseById(id);

        if (expense == null) {
            context.status(404);
            context.result("Expense not found");
            return;
        }

        expenseDao.deleteExpense(id);

        context.status(204);
    }
}