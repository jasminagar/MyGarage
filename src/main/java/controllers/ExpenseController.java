package controllers;

import dao.ExpenseDao;
import entities.Expense;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import security.EnumRoles;

import java.util.List;

import static io.javalin.apibuilder.ApiBuilder.*;

public class ExpenseController {

    private final ExpenseDao expenseDao;

    public ExpenseController(ExpenseDao expenseDao) {
        this.expenseDao = expenseDao;
    }

    public EndpointGroup getRoutes() {
        return () -> {
            get("/expenses", this::getAllExpenses, EnumRoles.ADMIN);
            get("/expenses/{id}", this::getExpenseById, EnumRoles.USER, EnumRoles.ADMIN);
            get("/expenses/car/{carId}", this::getExpensesByCarId, EnumRoles.USER, EnumRoles.ADMIN);
            get("/expenses/car/{carId}/category/{category}", this::getExpensesByCarIdAndCategory, EnumRoles.USER, EnumRoles.ADMIN);
            get("/expenses/car/{carId}/total", this::getTotalExpensesForCar, EnumRoles.USER, EnumRoles.ADMIN);
            post("/expenses/{carId}/create", this::createExpense, EnumRoles.USER, EnumRoles.ADMIN);
            put("/expenses/update/{id}", this::updateExpense, EnumRoles.USER, EnumRoles.ADMIN);
            delete("/expenses/delete/{id}", this::deleteExpense, EnumRoles.USER, EnumRoles.ADMIN);
        };
    }

    private void createExpense(Context context) {
        Integer carId = Integer.parseInt(context.pathParam("carId"));

        Expense expense =
                context.bodyAsClass(Expense.class);

        Expense createdExpense =
                expenseDao.createExpense(carId, expense);

        if (createdExpense == null) {
            context.status(404);
            context.result("Car not found");
            return;
        }

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